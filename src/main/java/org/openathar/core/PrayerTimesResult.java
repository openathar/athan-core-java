package org.openathar.core;

/** Prayer times as UTC epoch milliseconds for the given date. */
public record PrayerTimesResult(
        long fajr,
        long sunrise,
        long dhuhr,
        long asr,
        long sunset,
        long maghrib,
        long isha,
        long midnight) {

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