package org.openathar.core

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.tan

/** The five daily prayers plus sunrise/sunset/midnight. */
enum class Prayer { FAJR, SUNRISE, DHUHR, ASR, SUNSET, MAGHRIB, ISHA, MIDNIGHT }

/** Twilight parameter: either a solar depression angle or fixed minutes. */
sealed interface Twilight {
    data class Angle(val degrees: Double) : Twilight
    data class Minutes(val minutes: Double) : Twilight
}

enum class Midnight { STANDARD, JAFARI }

/**
 * Calculation methods (praytime.js v3.2 `methods` table, with the shared
 * `defaults` — isha 14°, maghrib 1 min — already merged in).
 */
enum class Method(
    val fajr: Double,
    val isha: Twilight,
    val maghrib: Twilight,
    val midnight: Midnight,
) {
    MWL(18.0, Twilight.Angle(17.0), Twilight.Minutes(1.0), Midnight.STANDARD),
    ISNA(15.0, Twilight.Angle(15.0), Twilight.Minutes(1.0), Midnight.STANDARD),
    EGYPT(19.5, Twilight.Angle(17.5), Twilight.Minutes(1.0), Midnight.STANDARD),
    MAKKAH(18.5, Twilight.Minutes(90.0), Twilight.Minutes(1.0), Midnight.STANDARD),
    KARACHI(18.0, Twilight.Angle(18.0), Twilight.Minutes(1.0), Midnight.STANDARD),
    TEHRAN(17.7, Twilight.Angle(14.0), Twilight.Angle(4.5), Midnight.JAFARI),
    JAFARI(16.0, Twilight.Angle(14.0), Twilight.Angle(4.0), Midnight.JAFARI),
    FRANCE(12.0, Twilight.Angle(12.0), Twilight.Minutes(1.0), Midnight.STANDARD),
    RUSSIA(16.0, Twilight.Angle(15.0), Twilight.Minutes(1.0), Midnight.STANDARD),
    MALAYSIA(20.0, Twilight.Angle(18.0), Twilight.Minutes(1.0), Midnight.STANDARD),
    SINGAPORE(20.0, Twilight.Angle(18.0), Twilight.Minutes(1.0), Midnight.STANDARD),
}

enum class AsrMethod { STANDARD, HANAFI }

enum class HighLatMethod { NIGHT_MIDDLE, ONE_SEVENTH, ANGLE_BASED, NONE }

enum class Rounding { NEAREST, UP, DOWN, NONE }

/** Prayer times as UTC epoch milliseconds for the given date. */
data class PrayerTimesResult(
    val fajr: Long,
    val sunrise: Long,
    val dhuhr: Long,
    val asr: Long,
    val sunset: Long,
    val maghrib: Long,
    val isha: Long,
    val midnight: Long,
) {
    operator fun get(prayer: Prayer): Long = when (prayer) {
        Prayer.FAJR -> fajr
        Prayer.SUNRISE -> sunrise
        Prayer.DHUHR -> dhuhr
        Prayer.ASR -> asr
        Prayer.SUNSET -> sunset
        Prayer.MAGHRIB -> maghrib
        Prayer.ISHA -> isha
        Prayer.MIDNIGHT -> midnight
    }
}

/**
 * Prayer time calculation, a faithful port of praytime.js v3.2
 * (https://praytimes.org, MIT). Pure Kotlin (no java.*) so it stays
 * Kotlin-Multiplatform-capable. Results are UTC epoch milliseconds and must
 * be shifted to the location's local time for display.
 */
class PrayerTimes(
    val method: Method = Method.MWL,
    val asr: AsrMethod = AsrMethod.STANDARD,
    val highLats: HighLatMethod = HighLatMethod.NIGHT_MIDDLE,
    val dhuhrMinutes: Double = 0.0,
    val tune: Map<Prayer, Double> = emptyMap(),
    val rounding: Rounding = Rounding.NEAREST,
) {
    private data class Ctx(val lat: Double, val lng: Double, val utcTime: Long)

    fun getTimes(year: Int, month: Int, day: Int, lat: Double, lng: Double): PrayerTimesResult {
        val ctx = Ctx(lat, lng, utcMillisOfDate(year, month, day))
        var times = mapOf(
            Prayer.FAJR to 5.0,
            Prayer.SUNRISE to 6.0,
            Prayer.DHUHR to 12.0,
            Prayer.ASR to 13.0,
            Prayer.SUNSET to 18.0,
            Prayer.MAGHRIB to 18.0,
            Prayer.ISHA to 18.0,
            Prayer.MIDNIGHT to 24.0,
        )
        times = processTimes(times, ctx)
        val mutable = times.toMutableMap()
        val adjusted = adjustHighLats(mutable, ctx)
        updateTimes(mutable, ctx, adjusted)
        tuneTimes(mutable)
        return convertTimes(mutable, ctx)
    }

    private fun processTimes(times: Map<Prayer, Double>, ctx: Ctx): Map<Prayer, Double> {
        val horizon = Twilight.Angle(0.833)
        return mapOf(
            Prayer.FAJR to angleTime(Twilight.Angle(method.fajr), times.getValue(Prayer.FAJR), ctx, -1.0),
            Prayer.SUNRISE to angleTime(horizon, times.getValue(Prayer.SUNRISE), ctx, -1.0),
            Prayer.DHUHR to midDay(times.getValue(Prayer.DHUHR), ctx),
            Prayer.ASR to angleTime(Twilight.Angle(asrAngle(times.getValue(Prayer.ASR), ctx)), times.getValue(Prayer.ASR), ctx),
            Prayer.SUNSET to angleTime(horizon, times.getValue(Prayer.SUNSET), ctx),
            Prayer.MAGHRIB to angleTime(method.maghrib, times.getValue(Prayer.MAGHRIB), ctx),
            Prayer.ISHA to angleTime(method.isha, times.getValue(Prayer.ISHA), ctx),
            Prayer.MIDNIGHT to midDay(times.getValue(Prayer.MIDNIGHT), ctx) + 12,
        )
    }

    private fun updateTimes(times: MutableMap<Prayer, Double>, ctx: Ctx, adjusted: Boolean) {
        if (method.maghrib is Twilight.Minutes)
            times[Prayer.MAGHRIB] = times.getValue(Prayer.SUNSET) + value(method.maghrib) / 60
        if (method.isha is Twilight.Minutes)
            times[Prayer.ISHA] = times.getValue(Prayer.MAGHRIB) + value(method.isha) / 60
        if (method.midnight == Midnight.JAFARI) {
            val nextFajr = angleTime(Twilight.Angle(method.fajr), 29.0, ctx, -1.0) + 24
            times[Prayer.MIDNIGHT] =
                (times.getValue(Prayer.SUNSET) + (if (adjusted) times.getValue(Prayer.FAJR) + 24 else nextFajr)) / 2
        }
        times[Prayer.DHUHR] = times.getValue(Prayer.DHUHR) + dhuhrMinutes / 60
    }

    private fun tuneTimes(times: MutableMap<Prayer, Double>) {
        for ((prayer, minutes) in tune) times[prayer] = times.getValue(prayer) + minutes / 60
    }

    private fun convertTimes(times: Map<Prayer, Double>, ctx: Ctx): PrayerTimesResult {
        fun convert(t: Double): Long {
            val timestamp = ctx.utcTime.toDouble() + floor((t - ctx.lng / 15) * 3_600_000.0)
            val oneMinute = 60_000.0
            return when (rounding) {
                Rounding.UP -> ceil(timestamp / oneMinute).toLong() * 60_000L
                Rounding.DOWN -> floor(timestamp / oneMinute).toLong() * 60_000L
                Rounding.NEAREST -> round(timestamp / oneMinute).toLong() * 60_000L
                Rounding.NONE -> timestamp.toLong()
            }
        }
        return PrayerTimesResult(
            fajr = convert(times.getValue(Prayer.FAJR)),
            sunrise = convert(times.getValue(Prayer.SUNRISE)),
            dhuhr = convert(times.getValue(Prayer.DHUHR)),
            asr = convert(times.getValue(Prayer.ASR)),
            sunset = convert(times.getValue(Prayer.SUNSET)),
            maghrib = convert(times.getValue(Prayer.MAGHRIB)),
            isha = convert(times.getValue(Prayer.ISHA)),
            midnight = convert(times.getValue(Prayer.MIDNIGHT)),
        )
    }

    private fun adjustHighLats(times: MutableMap<Prayer, Double>, ctx: Ctx): Boolean {
        if (highLats == HighLatMethod.NONE) return false
        var adjusted = false
        val night = 24 + times.getValue(Prayer.SUNRISE) - times.getValue(Prayer.SUNSET)
        val fajr = adjustTime(times.getValue(Prayer.FAJR), times.getValue(Prayer.SUNRISE), Twilight.Angle(method.fajr), night, -1.0)
        val isha = adjustTime(times.getValue(Prayer.ISHA), times.getValue(Prayer.SUNSET), method.isha, night)
        val maghrib = adjustTime(times.getValue(Prayer.MAGHRIB), times.getValue(Prayer.SUNSET), method.maghrib, night)
        adjusted = adjusted || fajr.adjusted || isha.adjusted || maghrib.adjusted
        times[Prayer.FAJR] = fajr.time
        times[Prayer.ISHA] = isha.time
        times[Prayer.MAGHRIB] = maghrib.time
        return adjusted
    }

    private fun adjustTime(time: Double, base: Double, angle: Twilight, night: Double, direction: Double = 1.0): AdjustedTime {
        val portion = when (highLats) {
            HighLatMethod.NIGHT_MIDDLE -> night / 2
            HighLatMethod.ONE_SEVENTH -> night / 7
            HighLatMethod.ANGLE_BASED -> value(angle) / 60 * night
            HighLatMethod.NONE -> 0.0
        }
        val timeDiff = (time - base) * direction
        return if (time.isNaN() || timeDiff > portion) {
            AdjustedTime(base + portion * direction, true)
        } else {
            AdjustedTime(time, false)
        }
    }

    private data class AdjustedTime(val time: Double, val adjusted: Boolean)

    private fun sunPosition(time: Double, ctx: Ctx): SunPosition {
        val d = ctx.utcTime / 864e5 - 10957.5 + time / 24 - ctx.lng / 360
        val g = mod(357.529 + 0.98560028 * d, 360.0)
        val q = mod(280.459 + 0.98564736 * d, 360.0)
        val l = mod(q + 1.915 * sinDeg(g) + 0.020 * sinDeg(2 * g), 360.0)
        val e = 23.439 - 0.00000036 * d
        val ra = mod(arctan2Deg(cosDeg(e) * sinDeg(l), cosDeg(l)) / 15, 24.0)
        return SunPosition(
            declination = arcsinDeg(sinDeg(e) * sinDeg(l)),
            equation = q / 15 - ra,
        )
    }

    private data class SunPosition(val declination: Double, val equation: Double)

    private fun midDay(time: Double, ctx: Ctx): Double = mod(12 - sunPosition(time, ctx).equation, 24.0)

    private fun angleTime(angle: Twilight, time: Double, ctx: Ctx, direction: Double = 1.0): Double {
        val decl = sunPosition(time, ctx).declination
        val angleDeg = when (angle) {
            is Twilight.Angle -> angle.degrees
            is Twilight.Minutes -> Double.NaN
        }
        val numerator = -sinDeg(angleDeg) - sinDeg(ctx.lat) * sinDeg(decl)
        val diff = arccosDeg(numerator / (cosDeg(ctx.lat) * cosDeg(decl))) / 15
        return midDay(time, ctx) + diff * direction
    }

    private fun asrAngle(time: Double, ctx: Ctx): Double {
        val shadowFactor = when (asr) {
            AsrMethod.STANDARD -> 1.0
            AsrMethod.HANAFI -> 2.0
        }
        val decl = sunPosition(time, ctx).declination
        return -arccotDeg(shadowFactor + tanDeg(abs(ctx.lat - decl)))
    }

    private fun value(t: Twilight): Double = when (t) {
        is Twilight.Angle -> t.degrees
        is Twilight.Minutes -> t.minutes
    }

    private fun mod(a: Double, b: Double): Double = ((a % b) + b) % b

    private fun dtr(d: Double) = d * Math.PI / 180
    private fun rtd(r: Double) = r * 180 / Math.PI
    private fun sinDeg(d: Double) = sin(dtr(d))
    private fun cosDeg(d: Double) = cos(dtr(d))
    private fun tanDeg(d: Double) = tan(dtr(d))
    private fun arcsinDeg(d: Double) = rtd(asin(d))
    private fun arccosDeg(d: Double) = rtd(acos(d))
    private fun arccotDeg(x: Double) = rtd(atan(1 / x))
    private fun arctan2Deg(y: Double, x: Double) = rtd(atan2(y, x))

    companion object {
        /** UTC epoch milliseconds of a Gregorian date at midnight (proleptic). */
        fun utcMillisOfDate(year: Int, month: Int, day: Int): Long = daysFromCivil(year, month, day) * 86_400_000L

        /** Days since 1970-01-01 for a proleptic Gregorian date (Howard Hinnant's algorithm). */
        fun daysFromCivil(y: Int, m: Int, d: Int): Long {
            val yy = if (m <= 2) y - 1 else y
            val era = (if (yy >= 0) yy else yy - 399) / 400
            val yoe = yy - era * 400
            val doy = (153 * (if (m > 2) m - 3 else m + 9) + 2) / 5 + d - 1
            val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
            return era * 146097L + doe - 719468
        }

        /** Format a UTC epoch millisecond as local "HH:mm" for a fixed UTC offset in hours. */
        fun formatLocalTime(utcMillis: Long, utcOffsetHours: Double): String {
            val localMillis = utcMillis + (utcOffsetHours * 3_600_000).toLong()
            val totalMinutes = floor(localMillis / 60_000.0).toLong()
            val minutesOfDay = Math.floorMod(totalMinutes, 24 * 60)
            val hh = minutesOfDay / 60
            val mm = minutesOfDay % 60
            return "${hh.toString().padStart(2, '0')}:${mm.toString().padStart(2, '0')}"
        }
    }
}