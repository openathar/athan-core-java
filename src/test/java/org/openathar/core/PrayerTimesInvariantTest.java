package org.openathar.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Structural invariants that must hold for every method/location, independent
 * of the exact reference values: chronological order of the prayers, sane
 * day boundaries, and the dhuhr/midnight relation.
 *
 * <p>High-latitude midnight sun / polar night cases are EXCLUDED here: the
 * reference library (praytime.js v3.2) itself returns "-----" (uncomputable)
 * for those, and our port yields 0 (epoch) via Math.round(NaN) — see
 * known-drift-risks.md. Only computable date/location combos are asserted.
 */
class PrayerTimesInvariantTest {

    private static final List<Method> METHODS = List.of(
        Method.MWL, Method.ISNA, Method.EGYPT, Method.MAKKAH, Method.KARACHI,
        Method.TEHRAN, Method.JAFARI, Method.FRANCE, Method.RUSSIA,
        Method.MALAYSIA, Method.SINGAPORE);

    /** (lat, lng, [y, m, d]) — all computable in praytime.js v3.2. */
    private static final double[][] LOCATIONS = {
        {52.52, 13.405, 2026, 9, 14},       // Berlin
        {21.4225, 39.8262, 2026, 9, 14},    // Mecca
        {-33.8688, 151.2093, 2026, 9, 14},  // Sydney
        {1.3521, 103.8198, 2026, 9, 14},    // Singapore
        {69.6492, 18.9553, 2026, 9, 14},    // Tromsø (high latitude, computable date)
        {52.52, 13.405, 2026, 6, 21},       // Berlin summer solstice
        {52.52, 13.405, 2026, 12, 21},      // Berlin winter solstice
        {52.52, 13.405, 2024, 2, 29},       // Berlin leap day
    };

    @Test
    void prayersAreChronologicallyOrdered() {
        for (Method method : METHODS) {
            for (double[] loc : LOCATIONS) {
                PrayerTimesResult r = new PrayerTimes(method).getTimes(
                    (int) loc[2], (int) loc[3], (int) loc[4], loc[0], loc[1]);
                assertTrue(r.fajr() < r.sunrise(), method + " " + loc[0] + ": fajr < sunrise");
                assertTrue(r.sunrise() < r.dhuhr(), method + " " + loc[0] + ": sunrise < dhuhr");
                assertTrue(r.dhuhr() < r.asr(), method + " " + loc[0] + ": dhuhr < asr");
                assertTrue(r.asr() < r.sunset(), method + " " + loc[0] + ": asr < sunset");
                assertTrue(r.sunset() < r.maghrib(), method + " " + loc[0] + ": sunset < maghrib");
                assertTrue(r.maghrib() < r.isha(), method + " " + loc[0] + ": maghrib < isha");
            }
        }
    }

    @Test
    void timesAreWithinOneDay() {
        PrayerTimesResult r = new PrayerTimes(Method.MWL).getTimes(2026, 9, 14, 52.52, 13.405);
        long dayStart = PrayerTimes.utcMillisOfDate(2026, 9, 14);
        long dayEnd = dayStart + 86_400_000L;
        for (long t : List.of(r.fajr(), r.sunrise(), r.dhuhr(), r.asr(), r.sunset(), r.maghrib(), r.isha())) {
            assertTrue(t >= dayStart && t < dayEnd, "time " + t + " within day [" + dayStart + "," + dayEnd + ")");
        }
    }

    @Test
    void midnightIsBetweenSunsetAndFajr() {
        for (Method method : METHODS) {
            PrayerTimesResult r = new PrayerTimes(method).getTimes(2026, 9, 14, 52.52, 13.405);
            assertTrue(r.midnight() > r.sunset(), method + ": midnight > sunset");
            assertTrue(r.midnight() < r.fajr() + 86_400_000L, method + ": midnight < next fajr");
        }
    }

    @Test
    void dhuhrIsNearSolarNoon() {
        // Solar noon at longitude 13.405°E (Berlin) ≈ 11:26 UTC; dhuhr must be within ±2h.
        PrayerTimesResult r = new PrayerTimes(Method.MWL).getTimes(2026, 9, 14, 52.52, 13.405);
        long solarNoonUtc = PrayerTimes.utcMillisOfDate(2026, 9, 14) + (long) (11.5 * 3_600_000);
        assertTrue(Math.abs(r.dhuhr() - solarNoonUtc) < 2 * 3_600_000L);
    }

    @Test
    void sameDateDifferentMethodsDifferOnlyInTwilightTimes() {
        // Sunrise and sunset are pure astronomy — identical across methods.
        PrayerTimesResult mwl = new PrayerTimes(Method.MWL).getTimes(2026, 9, 14, 52.52, 13.405);
        PrayerTimesResult jafari = new PrayerTimes(Method.JAFARI).getTimes(2026, 9, 14, 52.52, 13.405);
        assertEquals(mwl.sunrise(), jafari.sunrise());
        assertEquals(mwl.sunset(), jafari.sunset());
    }

    @Test
    void duhaWindowIsBetweenSunriseAndDhuhr() {
        for (Method method : METHODS) {
            for (double[] loc : LOCATIONS) {
                PrayerTimesResult r = new PrayerTimes(method).getTimes(
                    (int) loc[2], (int) loc[3], (int) loc[4], loc[0], loc[1]);
                assertTrue(r.duhaStart() > r.sunrise(), method + " " + loc[0] + ": duhaStart > sunrise");
                assertTrue(r.duhaEnd() < r.dhuhr(), method + " " + loc[0] + ": duhaEnd < dhuhr");
                assertTrue(r.duhaStart() < r.duhaEnd(), method + " " + loc[0] + ": duhaStart < duhaEnd");
                assertTrue(r.duhaBest() >= r.duhaStart() && r.duhaBest() <= r.duhaEnd(),
                    method + " " + loc[0] + ": duhaBest within window");
            }
        }
    }

    @Test
    void utcMillisOfDateMatchesLocalDateEpoch() {
        // 2026-09-14 UTC midnight — cross-check against java.time.
        long expected = java.time.LocalDate.of(2026, 9, 14)
            .atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli();
        assertEquals(expected, PrayerTimes.utcMillisOfDate(2026, 9, 14));
    }
}