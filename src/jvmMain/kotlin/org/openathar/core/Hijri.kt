package org.openathar.core

import java.time.DateTimeException
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import kotlin.math.ceil

/** A Hijri (Umm al-Qura) calendar date. */
data class HijriDate(val day: Int, val month: Int, val year: Int)

/**
 * Hijri calendar conversion, a port of the web frontend's `lib/hijri.ts`
 * (which uses ICU's `islamic-umalqura`). Forward conversion relies on
 * `java.time.chrono.HijrahChronology` — the JVM's Umm al-Qura calendar, the
 * same reference values ICU uses, so web and engine never drift apart.
 *
 * JVM-only for now (java.time); the KMP migration point is replacing
 * `HijrahChronology` with kotlinx-datetime or an embedded Umm al-Qura table
 * when the mobile target gets built.
 */
object Hijri {

    /** Gregorian → Hijri (Umm al-Qura). */
    fun gregorianToHijri(date: LocalDate): HijriDate {
        val h = HijrahDate.from(date)
        return HijriDate(
            h.getLong(ChronoField.DAY_OF_MONTH).toInt(),
            h.getLong(ChronoField.MONTH_OF_YEAR).toInt(),
            h.getLong(ChronoField.YEAR).toInt(),
        )
    }

    /**
     * Hijri → Gregorian. The tabular estimate (kbisa/al-Beruni) can deviate
     * from Umm al-Qura by up to ±2 days, so the estimate is corrected against
     * the real Umm al-Qura output within a small window — same approach as
     * the web. Returns null if no match is found.
     */
    fun hijriToGregorian(year: Int, month: Int, day: Int): LocalDate? {
        val guess = jdnToGregorian(hijriToJdnApprox(year, month, day))
        for (offset in -4..4) {
            val candidate = guess.plusDays(offset.toLong())
            // Java's Umm al-Qura data starts at 1300 AH; candidates before that
            // are out of range and must be skipped, not thrown.
            val got = try {
                gregorianToHijri(candidate)
            } catch (e: DateTimeException) {
                continue
            }
            if (got.day == day && got.month == month && got.year == year) return candidate
        }
        return null
    }

    private fun hijriToJdnApprox(y: Int, m: Int, d: Int): Int =
        d + ceil(29.5 * (m - 1)).toInt() + (y - 1) * 354 + (3 + 11 * y) / 30 + 1948440 - 1

    private fun jdnToGregorian(jdn: Int): LocalDate {
        var l = jdn + 68569
        val n = (4 * l) / 146097
        l -= (146097 * n + 3) / 4
        val i = (4000 * (l + 1)) / 1461001
        l -= (1461 * i) / 4 - 31
        val j = (80 * l) / 2447
        val day = l - (2447 * j) / 80
        l = j / 11
        val month = j + 2 - 12 * l
        val year = 100 * (n - 49) + i + l
        return LocalDate.of(year, month, day)
    }
}

/** Localized names of the 12 Hijri months for the platform's three languages. */
object HijriMonths {
    val names: Map<String, List<String>> = mapOf(
        "ar" to listOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة",
            "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة",
        ),
        "en" to listOf(
            "Muharram", "Safar", "Rabi' al-awwal", "Rabi' al-thani",
            "Jumada al-awwal", "Jumada al-thani", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah",
        ),
        "de" to listOf(
            "Muharram", "Safar", "Rabi' al-awwal", "Rabi' al-thani",
            "Jumada al-awwal", "Jumada al-thani", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah",
        ),
    )
}