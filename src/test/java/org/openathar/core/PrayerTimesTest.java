package org.openathar.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Reference values generated from the official praytime.js v3.2 library
 * (npm {@code praytime@3.2.0}) — the exact algorithm this port is based on.
 */
class PrayerTimesTest {

    private Map<Prayer, String> localTimes(Method method, int y, int mo, int d,
                                           double lat, double lng, double utcOffsetHours) {
        PrayerTimesResult result = new PrayerTimes(method).getTimes(y, mo, d, lat, lng);
        Map<Prayer, String> map = new EnumMap<>(Prayer.class);
        for (Prayer p : Prayer.values()) map.put(p, PrayerTimes.formatLocalTime(result.get(p), utcOffsetHours));
        return map;
    }

    @Test
    void berlinMwl() {
        Map<Prayer, String> t = localTimes(Method.MWL, 2026, 9, 14, 52.52, 13.405, 2.0);
        assertEquals("04:38", t.get(Prayer.FAJR));
        assertEquals("06:39", t.get(Prayer.SUNRISE));
        assertEquals("13:02", t.get(Prayer.DHUHR));
        assertEquals("16:30", t.get(Prayer.ASR));
        assertEquals("19:24", t.get(Prayer.SUNSET));
        assertEquals("19:25", t.get(Prayer.MAGHRIB));
        assertEquals("21:17", t.get(Prayer.ISHA));
        assertEquals("01:02", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void meccaMakkahMethod() {
        Map<Prayer, String> t = localTimes(Method.MAKKAH, 2026, 9, 14, 21.4225, 39.8262, 3.0);
        assertEquals("04:51", t.get(Prayer.FAJR));
        assertEquals("06:07", t.get(Prayer.SUNRISE));
        assertEquals("12:16", t.get(Prayer.DHUHR));
        assertEquals("15:42", t.get(Prayer.ASR));
        assertEquals("18:25", t.get(Prayer.SUNSET));
        assertEquals("18:26", t.get(Prayer.MAGHRIB));
        assertEquals("19:56", t.get(Prayer.ISHA));
        assertEquals("00:16", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void newYorkIsna() {
        Map<Prayer, String> t = localTimes(Method.ISNA, 2026, 9, 14, 40.7128, -74.006, -4.0);
        assertEquals("05:19", t.get(Prayer.FAJR));
        assertEquals("06:36", t.get(Prayer.SUNRISE));
        assertEquals("12:52", t.get(Prayer.DHUHR));
        assertEquals("16:23", t.get(Prayer.ASR));
        assertEquals("19:07", t.get(Prayer.SUNSET));
        assertEquals("19:08", t.get(Prayer.MAGHRIB));
        assertEquals("20:23", t.get(Prayer.ISHA));
        assertEquals("00:51", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void karachiMethod() {
        Map<Prayer, String> t = localTimes(Method.KARACHI, 2026, 9, 14, 24.8607, 67.0011, 5.0);
        assertEquals("05:01", t.get(Prayer.FAJR));
        assertEquals("06:18", t.get(Prayer.SUNRISE));
        assertEquals("12:28", t.get(Prayer.DHUHR));
        assertEquals("15:55", t.get(Prayer.ASR));
        assertEquals("18:37", t.get(Prayer.SUNSET));
        assertEquals("18:38", t.get(Prayer.MAGHRIB));
        assertEquals("19:54", t.get(Prayer.ISHA));
        assertEquals("00:27", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void tromsoHighLatitudeNightMiddle() {
        Map<Prayer, String> t = localTimes(Method.MWL, 2026, 9, 14, 69.6492, 18.9553, 2.0);
        assertEquals("00:39", t.get(Prayer.FAJR));
        assertEquals("05:53", t.get(Prayer.SUNRISE));
        assertEquals("12:40", t.get(Prayer.DHUHR));
        assertEquals("15:47", t.get(Prayer.ASR));
        assertEquals("19:24", t.get(Prayer.SUNSET));
        assertEquals("19:25", t.get(Prayer.MAGHRIB));
        assertEquals("00:14", t.get(Prayer.ISHA));
        assertEquals("00:40", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void tehranJafariMidnight() {
        Map<Prayer, String> t = localTimes(Method.TEHRAN, 2026, 9, 14, 35.6892, 51.389, 3.5);
        assertEquals("04:21", t.get(Prayer.FAJR));
        assertEquals("05:46", t.get(Prayer.SUNRISE));
        assertEquals("12:00", t.get(Prayer.DHUHR));
        assertEquals("15:32", t.get(Prayer.ASR));
        assertEquals("18:13", t.get(Prayer.SUNSET));
        assertEquals("18:32", t.get(Prayer.MAGHRIB));
        assertEquals("19:19", t.get(Prayer.ISHA));
        assertEquals("23:18", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void berlinWinterSolstice() {
        Map<Prayer, String> t = localTimes(Method.MWL, 2026, 12, 21, 52.52, 13.405, 1.0);
        assertEquals("06:07", t.get(Prayer.FAJR));
        assertEquals("08:15", t.get(Prayer.SUNRISE));
        assertEquals("12:04", t.get(Prayer.DHUHR));
        assertEquals("13:39", t.get(Prayer.ASR));
        assertEquals("15:54", t.get(Prayer.SUNSET));
        assertEquals("15:55", t.get(Prayer.MAGHRIB));
        assertEquals("17:55", t.get(Prayer.ISHA));
        assertEquals("00:05", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void damascusJafari() {
        Map<Prayer, String> t = localTimes(Method.JAFARI, 2026, 9, 14, 33.5138, 36.2765, 3.0);
        assertEquals("05:03", t.get(Prayer.FAJR));
        assertEquals("06:17", t.get(Prayer.SUNRISE));
        assertEquals("12:30", t.get(Prayer.DHUHR));
        assertEquals("16:02", t.get(Prayer.ASR));
        assertEquals("18:43", t.get(Prayer.SUNSET));
        assertEquals("18:58", t.get(Prayer.MAGHRIB));
        assertEquals("19:47", t.get(Prayer.ISHA));
        assertEquals("23:54", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void berlinSpringEquinox() {
        Map<Prayer, String> t = localTimes(Method.MWL, 2026, 3, 20, 52.52, 13.405, 1.0);
        assertEquals("04:13", t.get(Prayer.FAJR));
        assertEquals("06:09", t.get(Prayer.SUNRISE));
        assertEquals("12:14", t.get(Prayer.DHUHR));
        assertEquals("15:30", t.get(Prayer.ASR));
        assertEquals("18:19", t.get(Prayer.SUNSET));
        assertEquals("18:20", t.get(Prayer.MAGHRIB));
        assertEquals("20:09", t.get(Prayer.ISHA));
        assertEquals("00:14", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void berlinSummerSolstice() {
        Map<Prayer, String> t = localTimes(Method.MWL, 2026, 6, 21, 52.52, 13.405, 2.0);
        assertEquals("01:08", t.get(Prayer.FAJR));
        assertEquals("04:43", t.get(Prayer.SUNRISE));
        assertEquals("13:08", t.get(Prayer.DHUHR));
        assertEquals("17:33", t.get(Prayer.ASR));
        assertEquals("21:33", t.get(Prayer.SUNSET));
        assertEquals("21:34", t.get(Prayer.MAGHRIB));
        assertEquals("01:08", t.get(Prayer.ISHA));
        assertEquals("01:08", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void sydneySouthernHemisphere() {
        Map<Prayer, String> t = localTimes(Method.MWL, 2026, 9, 14, -33.8688, 151.2093, 10.0);
        assertEquals("04:34", t.get(Prayer.FAJR));
        assertEquals("05:56", t.get(Prayer.SUNRISE));
        assertEquals("11:51", t.get(Prayer.DHUHR));
        assertEquals("15:13", t.get(Prayer.ASR));
        assertEquals("17:46", t.get(Prayer.SUNSET));
        assertEquals("17:47", t.get(Prayer.MAGHRIB));
        assertEquals("19:04", t.get(Prayer.ISHA));
        assertEquals("23:51", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void singaporeNearEquator() {
        Map<Prayer, String> t = localTimes(Method.ISNA, 2026, 9, 14, 1.3521, 103.8198, 8.0);
        assertEquals("06:00", t.get(Prayer.FAJR));
        assertEquals("06:57", t.get(Prayer.SUNRISE));
        assertEquals("13:00", t.get(Prayer.DHUHR));
        assertEquals("16:04", t.get(Prayer.ASR));
        assertEquals("19:04", t.get(Prayer.SUNSET));
        assertEquals("19:05", t.get(Prayer.MAGHRIB));
        assertEquals("20:01", t.get(Prayer.ISHA));
        assertEquals("01:00", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void cairoEgyptianMethod() {
        Map<Prayer, String> t = localTimes(Method.EGYPT, 2026, 9, 14, 30.0444, 31.2357, 2.0);
        assertEquals("04:11", t.get(Prayer.FAJR));
        assertEquals("05:39", t.get(Prayer.SUNRISE));
        assertEquals("11:51", t.get(Prayer.DHUHR));
        assertEquals("15:21", t.get(Prayer.ASR));
        assertEquals("18:02", t.get(Prayer.SUNSET));
        assertEquals("18:03", t.get(Prayer.MAGHRIB));
        assertEquals("19:20", t.get(Prayer.ISHA));
        assertEquals("23:50", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void reykjavikHighLatSummer() {
        Map<Prayer, String> t = localTimes(Method.MWL, 2026, 6, 21, 64.1466, -21.9426, 0.0);
        assertEquals("01:30", t.get(Prayer.FAJR));
        assertEquals("02:55", t.get(Prayer.SUNRISE));
        assertEquals("13:30", t.get(Prayer.DHUHR));
        assertEquals("18:22", t.get(Prayer.ASR));
        assertEquals("00:04", t.get(Prayer.SUNSET));
        assertEquals("00:05", t.get(Prayer.MAGHRIB));
        assertEquals("01:30", t.get(Prayer.ISHA));
        assertEquals("01:30", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void reykjavikHighLatWinter() {
        Map<Prayer, String> t = localTimes(Method.MWL, 2026, 12, 21, 64.1466, -21.9426, 0.0);
        assertEquals("07:54", t.get(Prayer.FAJR));
        assertEquals("11:22", t.get(Prayer.SUNRISE));
        assertEquals("13:26", t.get(Prayer.DHUHR));
        assertEquals("13:47", t.get(Prayer.ASR));
        assertEquals("15:30", t.get(Prayer.SUNSET));
        assertEquals("15:31", t.get(Prayer.MAGHRIB));
        assertEquals("18:48", t.get(Prayer.ISHA));
        assertEquals("01:26", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void berlinLeapDay() {
        Map<Prayer, String> t = localTimes(Method.MWL, 2024, 2, 29, 52.52, 13.405, 1.0);
        assertEquals("05:01", t.get(Prayer.FAJR));
        assertEquals("06:54", t.get(Prayer.SUNRISE));
        assertEquals("12:19", t.get(Prayer.DHUHR));
        assertEquals("15:07", t.get(Prayer.ASR));
        assertEquals("17:44", t.get(Prayer.SUNSET));
        assertEquals("17:45", t.get(Prayer.MAGHRIB));
        assertEquals("19:31", t.get(Prayer.ISHA));
        assertEquals("00:19", t.get(Prayer.MIDNIGHT));
    }

    @Test
    void duhaWindowBerlinMwl() {
        PrayerTimesResult r = new PrayerTimes(Method.MWL).getTimes(2026, 9, 14, 52.52, 13.405);
        // start = sunrise + 15 min, end = dhuhr − 10 min, best = midpoint (rounded)
        assertEquals(r.sunrise() + 15 * 60_000L, r.duhaStart());
        assertEquals(r.dhuhr() - 10 * 60_000L, r.duhaEnd());
        assertEquals("06:54", PrayerTimes.formatLocalTime(r.duhaStart(), 2.0));
        assertEquals("12:52", PrayerTimes.formatLocalTime(r.duhaEnd(), 2.0));
        assertEquals("09:51", PrayerTimes.formatLocalTime(r.duhaBest(), 2.0));
        assertEquals("06:39", PrayerTimes.formatLocalTime(r.sunrise(), 2.0));
        assertEquals("13:02", PrayerTimes.formatLocalTime(r.dhuhr(), 2.0));
    }
}