package org.openathar.core

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Reference values generated from the web frontend's `lib/hijri.ts` logic
 * (ICU `islamic-umalqura`) — the spec this port must match exactly.
 */
class HijriTest {

    @Test
    fun `forward conversion matches ICU islamic-umalqura`() {
        assertEquals(HijriDate(3, 4, 1448), Hijri.gregorianToHijri(LocalDate.of(2026, 9, 14)))
        assertEquals(HijriDate(1, 9, 1448), Hijri.gregorianToHijri(LocalDate.of(2027, 2, 8)))
        assertEquals(HijriDate(1, 10, 1448), Hijri.gregorianToHijri(LocalDate.of(2027, 3, 9)))
        assertEquals(HijriDate(10, 12, 1448), Hijri.gregorianToHijri(LocalDate.of(2027, 5, 16)))
        assertEquals(HijriDate(11, 1, 1448), Hijri.gregorianToHijri(LocalDate.of(2026, 6, 26)))
        assertEquals(HijriDate(1, 9, 1445), Hijri.gregorianToHijri(LocalDate.of(2024, 3, 11)))
        assertEquals(HijriDate(24, 9, 1420), Hijri.gregorianToHijri(LocalDate.of(2000, 1, 1)))
        assertEquals(HijriDate(21, 11, 1410), Hijri.gregorianToHijri(LocalDate.of(1990, 6, 15)))
    }

    @Test
    fun `backward conversion matches ICU islamic-umalqura`() {
        assertEquals(LocalDate.of(2027, 2, 8), Hijri.hijriToGregorian(1448, 9, 1))
        assertEquals(LocalDate.of(2027, 3, 9), Hijri.hijriToGregorian(1448, 10, 1))
        assertEquals(LocalDate.of(2027, 5, 16), Hijri.hijriToGregorian(1448, 12, 10))
        assertEquals(LocalDate.of(2026, 6, 16), Hijri.hijriToGregorian(1448, 1, 1))
        assertEquals(LocalDate.of(2026, 2, 18), Hijri.hijriToGregorian(1447, 9, 1))
        assertEquals(LocalDate.of(2004, 2, 21), Hijri.hijriToGregorian(1425, 1, 1))
        assertEquals(LocalDate.of(1882, 11, 12), Hijri.hijriToGregorian(1300, 1, 1))
    }

    @Test
    fun `round trip`() {
        for (date in listOf(
            LocalDate.of(2026, 9, 14),
            LocalDate.of(2027, 2, 8),
            LocalDate.of(2024, 3, 11),
            LocalDate.of(2000, 1, 1),
        )) {
            val h = Hijri.gregorianToHijri(date)
            assertEquals(date, Hijri.hijriToGregorian(h.year, h.month, h.day))
        }
    }

    @Test
    fun `invalid hijri date returns null`() {
        assertNull(Hijri.hijriToGregorian(1448, 9, 30))
    }
}