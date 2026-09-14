package org.openathar.core;

/**
 * Prayer times as UTC epoch milliseconds for the given date. The Duha
 * (forenoon) values are a derived time window, not an astronomical prayer
 * time: start = sunrise + 15 min, end = dhuhr − 10 min, best = midpoint
 * between sunrise and dhuhr (Ibn ʿUthaymin, Al-Sharh al-Mumtiʿ 4/122;
 * al-Nawawi, Al-Majmuʿ 4/36).
 */
public record PrayerTimesResult(
        long fajr,
        long sunrise,
        long dhuhr,
        long asr,
        long sunset,
        long maghrib,
        long isha,
        long midnight,
        long duhaStart,
        long duhaEnd,
        long duhaBest) {

    public long get(Prayer prayer) {
        return switch (prayer) {
            case FAJR -> fajr;
            case SUNRISE -> sunrise;
            case DHUHR -> dhuhr;
            case ASR -> asr;
            case SUNSET -> sunset;
            case MAGHRIB -> maghrib;
            case ISHA -> isha;
            case MIDNIGHT -> midnight;
        };
    }
}