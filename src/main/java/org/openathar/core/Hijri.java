package org.openathar.core;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;

/**
 * Hijri calendar conversion, a port of the web frontend's {@code lib/hijri.ts}
 * (which uses ICU's {@code islamic-umalqura}). Forward conversion relies on
 * {@link HijrahDate} — the JVM's Umm al-Qura calendar, the same reference
 * values ICU uses, so web and engine never drift apart.
 */
public final class Hijri {

    private Hijri() {}

    /** Gregorian → Hijri (Umm al-Qura). */
    public static HijriDate gregorianToHijri(LocalDate date) {
        HijrahDate h = HijrahDate.from(date);
        return new HijriDate(
            (int) h.getLong(ChronoField.DAY_OF_MONTH),
            (int) h.getLong(ChronoField.MONTH_OF_YEAR),
            (int) h.getLong(ChronoField.YEAR));
    }

    /**
     * Hijri → Gregorian. The tabular estimate (kbisa/al-Beruni) can deviate
     * from Umm al-Qura by up to ±2 days, so the estimate is corrected against
     * the real Umm al-Qura output within a small window — same approach as
     * the web. Returns null if no match is found.
     */
    public static LocalDate hijriToGregorian(int year, int month, int day) {
        LocalDate guess = jdnToGregorian(hijriToJdnApprox(year, month, day));
        for (int offset = -4; offset <= 4; offset++) {
            LocalDate candidate = guess.plusDays(offset);
            // Java's Umm al-Qura data starts at 1300 AH; candidates before that
            // are out of range and must be skipped, not thrown.
            HijriDate got;
            try {
                got = gregorianToHijri(candidate);
            } catch (DateTimeException e) {
                continue;
            }
            if (got.day() == day && got.month() == month && got.year() == year) return candidate;
        }
        return null;
    }

    /** Localized name of a Hijri month (1–12); unknown locales fall back to English. */
    public static String monthName(int month, String locale) {
        String key = locale == null ? "en" : locale;
        List<String> names = HijriMonths.NAMES.getOrDefault(key, HijriMonths.NAMES.get("en"));
        return names.get(month - 1);
    }

    private static int hijriToJdnApprox(int y, int m, int d) {
        return d + (int) Math.ceil(29.5 * (m - 1)) + (y - 1) * 354 + (3 + 11 * y) / 30 + 1948440 - 1;
    }

    private static LocalDate jdnToGregorian(int jdn) {
        int l = jdn + 68569;
        int n = (4 * l) / 146097;
        l -= (146097 * n + 3) / 4;
        int i = (4000 * (l + 1)) / 1461001;
        l -= (1461 * i) / 4 - 31;
        int j = (80 * l) / 2447;
        int day = l - (2447 * j) / 80;
        l = j / 11;
        int month = j + 2 - 12 * l;
        int year = 100 * (n - 49) + i + l;
        return LocalDate.of(year, month, day);
    }
}

/** Localized names of the 12 Hijri months for the platform's three languages. */
final class HijriMonths {
    static final Map<String, List<String>> NAMES = Map.of(
        "ar", List.of(
            "محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة",
            "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"),
        "en", List.of(
            "Muharram", "Safar", "Rabi' al-awwal", "Rabi' al-thani",
            "Jumada al-awwal", "Jumada al-thani", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"),
        "de", List.of(
            "Muharram", "Safar", "Rabi' al-awwal", "Rabi' al-thani",
            "Jumada al-awwal", "Jumada al-thani", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"));
}