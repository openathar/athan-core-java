package org.openathar.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Reference values generated from the web frontend's {@code lib/hijri.ts} logic
 * (ICU {@code islamic-umalqura}) — the spec this port must match exactly.
 */
class HijriTest {

    @Test
    void forwardConversionMatchesIcu() {
        assertEquals(new HijriDate(3, 4, 1448), Hijri.gregorianToHijri(LocalDate.of(2026, 9, 14)));
        assertEquals(new HijriDate(1, 9, 1448), Hijri.gregorianToHijri(LocalDate.of(2027, 2, 8)));
        assertEquals(new HijriDate(1, 10, 1448), Hijri.gregorianToHijri(LocalDate.of(2027, 3, 9)));
        assertEquals(new HijriDate(10, 12, 1448), Hijri.gregorianToHijri(LocalDate.of(2027, 5, 16)));
        assertEquals(new HijriDate(11, 1, 1448), Hijri.gregorianToHijri(LocalDate.of(2026, 6, 26)));
        assertEquals(new HijriDate(1, 9, 1445), Hijri.gregorianToHijri(LocalDate.of(2024, 3, 11)));
        assertEquals(new HijriDate(24, 9, 1420), Hijri.gregorianToHijri(LocalDate.of(2000, 1, 1)));
        assertEquals(new HijriDate(21, 11, 1410), Hijri.gregorianToHijri(LocalDate.of(1990, 6, 15)));
    }

    @Test
    void backwardConversionMatchesIcu() {
        assertEquals(LocalDate.of(2027, 2, 8), Hijri.hijriToGregorian(1448, 9, 1));
        assertEquals(LocalDate.of(2027, 3, 9), Hijri.hijriToGregorian(1448, 10, 1));
        assertEquals(LocalDate.of(2027, 5, 16), Hijri.hijriToGregorian(1448, 12, 10));
        assertEquals(LocalDate.of(2026, 6, 16), Hijri.hijriToGregorian(1448, 1, 1));
        assertEquals(LocalDate.of(2026, 2, 18), Hijri.hijriToGregorian(1447, 9, 1));
        assertEquals(LocalDate.of(2004, 2, 21), Hijri.hijriToGregorian(1425, 1, 1));
        assertEquals(LocalDate.of(1882, 11, 12), Hijri.hijriToGregorian(1300, 1, 1));
    }

    @Test
    void roundTrip() {
        for (LocalDate date : List.of(
            LocalDate.of(2026, 9, 14),
            LocalDate.of(2027, 2, 8),
            LocalDate.of(2024, 3, 11),
            LocalDate.of(2000, 1, 1))) {
            HijriDate h = Hijri.gregorianToHijri(date);
            assertEquals(date, Hijri.hijriToGregorian(h.year(), h.month(), h.day()));
        }
    }

    @Test
    void invalidHijriDateReturnsNull() {
        assertNull(Hijri.hijriToGregorian(1448, 9, 30));
    }

    @Test
    void monthNamesAreLocalized() {
        assertEquals("Muharram", Hijri.monthName(1, "en"));
        assertEquals("Rabi' al-thani", Hijri.monthName(4, "en"));
        assertEquals("رمضان", Hijri.monthName(9, "ar"));
        assertEquals("Ramadan", Hijri.monthName(9, "de"));
        assertEquals("Ramadan", Hijri.monthName(9, "xx"));
        assertEquals("Ramadan", Hijri.monthName(9, null));
    }
}