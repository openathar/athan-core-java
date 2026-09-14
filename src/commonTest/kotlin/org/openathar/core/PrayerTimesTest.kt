package org.openathar.core

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Reference values generated from the official praytime.js v3.2 library
 * (npm `praytime@3.2.0`) — the exact algorithm this port is based on.
 */
class PrayerTimesTest {

    private fun localTimes(
        method: Method,
        y: Int, mo: Int, d: Int,
        lat: Double, lng: Double,
        utcOffsetHours: Double,
    ): Map<Prayer, String> {
        val result = PrayerTimes(method = method).getTimes(y, mo, d, lat, lng)
        return Prayer.entries.associateWith { PrayerTimes.formatLocalTime(result[it], utcOffsetHours) }
    }

    @Test
    fun `Berlin MWL`() {
        val t = localTimes(Method.MWL, 2026, 9, 14, 52.52, 13.405, 2.0)
        assertEquals("04:38", t[Prayer.FAJR])
        assertEquals("06:39", t[Prayer.SUNRISE])
        assertEquals("13:02", t[Prayer.DHUHR])
        assertEquals("16:30", t[Prayer.ASR])
        assertEquals("19:24", t[Prayer.SUNSET])
        assertEquals("19:25", t[Prayer.MAGHRIB])
        assertEquals("21:17", t[Prayer.ISHA])
        assertEquals("01:02", t[Prayer.MIDNIGHT])
    }

    @Test
    fun `Mecca Makkah method`() {
        val t = localTimes(Method.MAKKAH, 2026, 9, 14, 21.4225, 39.8262, 3.0)
        assertEquals("04:51", t[Prayer.FAJR])
        assertEquals("06:07", t[Prayer.SUNRISE])
        assertEquals("12:16", t[Prayer.DHUHR])
        assertEquals("15:42", t[Prayer.ASR])
        assertEquals("18:25", t[Prayer.SUNSET])
        assertEquals("18:26", t[Prayer.MAGHRIB])
        assertEquals("19:56", t[Prayer.ISHA])
        assertEquals("00:16", t[Prayer.MIDNIGHT])
    }

    @Test
    fun `New York ISNA`() {
        val t = localTimes(Method.ISNA, 2026, 9, 14, 40.7128, -74.006, -4.0)
        assertEquals("05:19", t[Prayer.FAJR])
        assertEquals("06:36", t[Prayer.SUNRISE])
        assertEquals("12:52", t[Prayer.DHUHR])
        assertEquals("16:23", t[Prayer.ASR])
        assertEquals("19:07", t[Prayer.SUNSET])
        assertEquals("19:08", t[Prayer.MAGHRIB])
        assertEquals("20:23", t[Prayer.ISHA])
        assertEquals("00:51", t[Prayer.MIDNIGHT])
    }

    @Test
    fun `Karachi method`() {
        val t = localTimes(Method.KARACHI, 2026, 9, 14, 24.8607, 67.0011, 5.0)
        assertEquals("05:01", t[Prayer.FAJR])
        assertEquals("06:18", t[Prayer.SUNRISE])
        assertEquals("12:28", t[Prayer.DHUHR])
        assertEquals("15:55", t[Prayer.ASR])
        assertEquals("18:37", t[Prayer.SUNSET])
        assertEquals("18:38", t[Prayer.MAGHRIB])
        assertEquals("19:54", t[Prayer.ISHA])
        assertEquals("00:27", t[Prayer.MIDNIGHT])
    }

    @Test
    fun `Tromso high latitude NightMiddle`() {
        val t = localTimes(Method.MWL, 2026, 9, 14, 69.6492, 18.9553, 2.0)
        assertEquals("00:39", t[Prayer.FAJR])
        assertEquals("05:53", t[Prayer.SUNRISE])
        assertEquals("12:40", t[Prayer.DHUHR])
        assertEquals("15:47", t[Prayer.ASR])
        assertEquals("19:24", t[Prayer.SUNSET])
        assertEquals("19:25", t[Prayer.MAGHRIB])
        assertEquals("00:14", t[Prayer.ISHA])
        assertEquals("00:40", t[Prayer.MIDNIGHT])
    }

    @Test
    fun `Tehran Jafari midnight`() {
        val t = localTimes(Method.TEHRAN, 2026, 9, 14, 35.6892, 51.389, 3.5)
        assertEquals("04:21", t[Prayer.FAJR])
        assertEquals("05:46", t[Prayer.SUNRISE])
        assertEquals("12:00", t[Prayer.DHUHR])
        assertEquals("15:32", t[Prayer.ASR])
        assertEquals("18:13", t[Prayer.SUNSET])
        assertEquals("18:32", t[Prayer.MAGHRIB])
        assertEquals("19:19", t[Prayer.ISHA])
        assertEquals("23:18", t[Prayer.MIDNIGHT])
    }

    @Test
    fun `Berlin winter solstice`() {
        val t = localTimes(Method.MWL, 2026, 12, 21, 52.52, 13.405, 1.0)
        assertEquals("06:07", t[Prayer.FAJR])
        assertEquals("08:15", t[Prayer.SUNRISE])
        assertEquals("12:04", t[Prayer.DHUHR])
        assertEquals("13:39", t[Prayer.ASR])
        assertEquals("15:54", t[Prayer.SUNSET])
        assertEquals("15:55", t[Prayer.MAGHRIB])
        assertEquals("17:55", t[Prayer.ISHA])
        assertEquals("00:05", t[Prayer.MIDNIGHT])
    }

    @Test
    fun `Damascus Jafari`() {
        val t = localTimes(Method.JAFARI, 2026, 9, 14, 33.5138, 36.2765, 3.0)
        assertEquals("05:03", t[Prayer.FAJR])
        assertEquals("06:17", t[Prayer.SUNRISE])
        assertEquals("12:30", t[Prayer.DHUHR])
        assertEquals("16:02", t[Prayer.ASR])
        assertEquals("18:43", t[Prayer.SUNSET])
        assertEquals("18:58", t[Prayer.MAGHRIB])
        assertEquals("19:47", t[Prayer.ISHA])
        assertEquals("23:54", t[Prayer.MIDNIGHT])
    }
}