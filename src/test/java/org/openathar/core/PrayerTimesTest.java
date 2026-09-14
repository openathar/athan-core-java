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
}