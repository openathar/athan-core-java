package org.openathar.core;

import java.util.EnumMap;
import java.util.Map;

/**
 * Prayer time calculation, a faithful port of praytime.js v3.2
 * (https://praytimes.org, MIT). Results are UTC epoch milliseconds and must
 * be shifted to the location's local time for display.
 */
public final class PrayerTimes {

    private final Method method;
    private final AsrMethod asr;
    private final HighLatMethod highLats;
    private final double dhuhrMinutes;
    private final Map<Prayer, Double> tune;
    private final Rounding rounding;

    public PrayerTimes() {
        this(Method.MWL, AsrMethod.STANDARD, HighLatMethod.NIGHT_MIDDLE, 0.0, Map.of(), Rounding.NEAREST);
    }

    public PrayerTimes(Method method) {
        this(method, AsrMethod.STANDARD, HighLatMethod.NIGHT_MIDDLE, 0.0, Map.of(), Rounding.NEAREST);
    }

    public PrayerTimes(Method method, AsrMethod asr, HighLatMethod highLats,
                       double dhuhrMinutes, Map<Prayer, Double> tune, Rounding rounding) {
        this.method = method;
        this.asr = asr;
        this.highLats = highLats;
        this.dhuhrMinutes = dhuhrMinutes;
        this.tune = tune;
        this.rounding = rounding;
    }

    private record Ctx(double lat, double lng, long utcTime) {}
    private record SunPosition(double declination, double equation) {}
    private record AdjustedTime(double time, boolean adjusted) {}

    public PrayerTimesResult getTimes(int year, int month, int day, double lat, double lng) {
        Ctx ctx = new Ctx(lat, lng, utcMillisOfDate(year, month, day));
        Map<Prayer, Double> times = new EnumMap<>(Prayer.class);
        times.put(Prayer.FAJR, 5.0);
        times.put(Prayer.SUNRISE, 6.0);
        times.put(Prayer.DHUHR, 12.0);
        times.put(Prayer.ASR, 13.0);
        times.put(Prayer.SUNSET, 18.0);
        times.put(Prayer.MAGHRIB, 18.0);
        times.put(Prayer.ISHA, 18.0);
        times.put(Prayer.MIDNIGHT, 24.0);
        times = processTimes(times, ctx);
        boolean adjusted = adjustHighLats(times, ctx);
        updateTimes(times, ctx, adjusted);
        tuneTimes(times);
        return convertTimes(times, ctx);
    }

    private Map<Prayer, Double> processTimes(Map<Prayer, Double> times, Ctx ctx) {
        Twilight horizon = new Twilight.Angle(0.833);
        Map<Prayer, Double> result = new EnumMap<>(Prayer.class);
        result.put(Prayer.FAJR, angleTime(new Twilight.Angle(method.fajr), times.get(Prayer.FAJR), ctx, -1.0));
        result.put(Prayer.SUNRISE, angleTime(horizon, times.get(Prayer.SUNRISE), ctx, -1.0));
        result.put(Prayer.DHUHR, midDay(times.get(Prayer.DHUHR), ctx));
        result.put(Prayer.ASR, angleTime(new Twilight.Angle(asrAngle(times.get(Prayer.ASR), ctx)), times.get(Prayer.ASR), ctx));
        result.put(Prayer.SUNSET, angleTime(horizon, times.get(Prayer.SUNSET), ctx));
        result.put(Prayer.MAGHRIB, angleTime(method.maghrib, times.get(Prayer.MAGHRIB), ctx));
        result.put(Prayer.ISHA, angleTime(method.isha, times.get(Prayer.ISHA), ctx));
        result.put(Prayer.MIDNIGHT, midDay(times.get(Prayer.MIDNIGHT), ctx) + 12);
        return result;
    }

    private void updateTimes(Map<Prayer, Double> times, Ctx ctx, boolean adjusted) {
        if (method.maghrib instanceof Twilight.Minutes)
            times.put(Prayer.MAGHRIB, times.get(Prayer.SUNSET) + value(method.maghrib) / 60);
        if (method.isha instanceof Twilight.Minutes)
            times.put(Prayer.ISHA, times.get(Prayer.MAGHRIB) + value(method.isha) / 60);
        if (method.midnight == Midnight.JAFARI) {
            double nextFajr = angleTime(new Twilight.Angle(method.fajr), 29.0, ctx, -1.0) + 24;
            times.put(Prayer.MIDNIGHT,
                (times.get(Prayer.SUNSET) + (adjusted ? times.get(Prayer.FAJR) + 24 : nextFajr)) / 2);
        }
        times.put(Prayer.DHUHR, times.get(Prayer.DHUHR) + dhuhrMinutes / 60);
    }

    private void tuneTimes(Map<Prayer, Double> times) {
        for (Map.Entry<Prayer, Double> e : tune.entrySet())
            times.put(e.getKey(), times.get(e.getKey()) + e.getValue() / 60);
    }

    private PrayerTimesResult convertTimes(Map<Prayer, Double> times, Ctx ctx) {
        return new PrayerTimesResult(
            convert(times.get(Prayer.FAJR), ctx),
            convert(times.get(Prayer.SUNRISE), ctx),
            convert(times.get(Prayer.DHUHR), ctx),
            convert(times.get(Prayer.ASR), ctx),
            convert(times.get(Prayer.SUNSET), ctx),
            convert(times.get(Prayer.MAGHRIB), ctx),
            convert(times.get(Prayer.ISHA), ctx),
            convert(times.get(Prayer.MIDNIGHT), ctx));
    }

    private long convert(double t, Ctx ctx) {
        double timestamp = ctx.utcTime() + Math.floor((t - ctx.lng() / 15) * 3_600_000.0);
        double oneMinute = 60_000.0;
        return switch (rounding) {
            case UP -> (long) Math.ceil(timestamp / oneMinute) * 60_000L;
            case DOWN -> (long) Math.floor(timestamp / oneMinute) * 60_000L;
            case NEAREST -> Math.round(timestamp / oneMinute) * 60_000L;
            case NONE -> (long) timestamp;
        };
    }

    private boolean adjustHighLats(Map<Prayer, Double> times, Ctx ctx) {
        if (highLats == HighLatMethod.NONE) return false;
        boolean adjusted = false;
        double night = 24 + times.get(Prayer.SUNRISE) - times.get(Prayer.SUNSET);
        AdjustedTime fajr = adjustTime(times.get(Prayer.FAJR), times.get(Prayer.SUNRISE),
            new Twilight.Angle(method.fajr), night, -1.0);
        AdjustedTime isha = adjustTime(times.get(Prayer.ISHA), times.get(Prayer.SUNSET), method.isha, night);
        AdjustedTime maghrib = adjustTime(times.get(Prayer.MAGHRIB), times.get(Prayer.SUNSET), method.maghrib, night);
        adjusted = adjusted || fajr.adjusted() || isha.adjusted() || maghrib.adjusted();
        times.put(Prayer.FAJR, fajr.time());
        times.put(Prayer.ISHA, isha.time());
        times.put(Prayer.MAGHRIB, maghrib.time());
        return adjusted;
    }

    private AdjustedTime adjustTime(double time, double base, Twilight angle, double night) {
        return adjustTime(time, base, angle, night, 1.0);
    }

    private AdjustedTime adjustTime(double time, double base, Twilight angle, double night, double direction) {
        double portion = switch (highLats) {
            case NIGHT_MIDDLE -> night / 2;
            case ONE_SEVENTH -> night / 7;
            case ANGLE_BASED -> value(angle) / 60 * night;
            case NONE -> 0.0;
        };
        double timeDiff = (time - base) * direction;
        return (Double.isNaN(time) || timeDiff > portion)
            ? new AdjustedTime(base + portion * direction, true)
            : new AdjustedTime(time, false);
    }

    private SunPosition sunPosition(double time, Ctx ctx) {
        double d = ctx.utcTime() / 864e5 - 10957.5 + time / 24 - ctx.lng() / 360;
        double g = mod(357.529 + 0.98560028 * d, 360.0);
        double q = mod(280.459 + 0.98564736 * d, 360.0);
        double l = mod(q + 1.915 * sinDeg(g) + 0.020 * sinDeg(2 * g), 360.0);
        double e = 23.439 - 0.00000036 * d;
        double ra = mod(arctan2Deg(cosDeg(e) * sinDeg(l), cosDeg(l)) / 15, 24.0);
        return new SunPosition(arcsinDeg(sinDeg(e) * sinDeg(l)), q / 15 - ra);
    }

    private double midDay(double time, Ctx ctx) {
        return mod(12 - sunPosition(time, ctx).equation(), 24.0);
    }

    private double angleTime(Twilight angle, double time, Ctx ctx) {
        return angleTime(angle, time, ctx, 1.0);
    }

    private double angleTime(Twilight angle, double time, Ctx ctx, double direction) {
        double decl = sunPosition(time, ctx).declination();
        double angleDeg = switch (angle) {
            case Twilight.Angle a -> a.degrees();
            case Twilight.Minutes m -> Double.NaN;
        };
        double numerator = -sinDeg(angleDeg) - sinDeg(ctx.lat()) * sinDeg(decl);
        double diff = arccosDeg(numerator / (cosDeg(ctx.lat()) * cosDeg(decl))) / 15;
        return midDay(time, ctx) + diff * direction;
    }

    private double asrAngle(double time, Ctx ctx) {
        double shadowFactor = switch (asr) {
            case STANDARD -> 1.0;
            case HANAFI -> 2.0;
        };
        double decl = sunPosition(time, ctx).declination();
        return -arccotDeg(shadowFactor + tanDeg(Math.abs(ctx.lat() - decl)));
    }

    private double value(Twilight t) {
        return switch (t) {
            case Twilight.Angle a -> a.degrees();
            case Twilight.Minutes m -> m.minutes();
        };
    }

    private double mod(double a, double b) {
        return ((a % b) + b) % b;
    }

    private double dtr(double d) { return d * Math.PI / 180; }
    private double rtd(double r) { return r * 180 / Math.PI; }
    private double sinDeg(double d) { return Math.sin(dtr(d)); }
    private double cosDeg(double d) { return Math.cos(dtr(d)); }
    private double tanDeg(double d) { return Math.tan(dtr(d)); }
    private double arcsinDeg(double d) { return rtd(Math.asin(d)); }
    private double arccosDeg(double d) { return rtd(Math.acos(d)); }
    private double arccotDeg(double x) { return rtd(Math.atan(1 / x)); }
    private double arctan2Deg(double y, double x) { return rtd(Math.atan2(y, x)); }

    /** UTC epoch milliseconds of a Gregorian date at midnight (proleptic). */
    public static long utcMillisOfDate(int year, int month, int day) {
        return daysFromCivil(year, month, day) * 86_400_000L;
    }

    /** Days since 1970-01-01 for a proleptic Gregorian date (Howard Hinnant's algorithm). */
    public static long daysFromCivil(int y, int m, int d) {
        int yy = (m <= 2) ? y - 1 : y;
        long era = ((yy >= 0) ? yy : yy - 399) / 400;
        int yoe = (int) (yy - era * 400);
        int doy = (153 * ((m > 2) ? m - 3 : m + 9) + 2) / 5 + d - 1;
        int doe = yoe * 365 + yoe / 4 - yoe / 100 + doy;
        return era * 146097L + doe - 719468;
    }

    /** Format a UTC epoch millisecond as local "HH:mm" for a fixed UTC offset in hours. */
    public static String formatLocalTime(long utcMillis, double utcOffsetHours) {
        long localMillis = utcMillis + (long) (utcOffsetHours * 3_600_000);
        long totalMinutes = (long) Math.floor(localMillis / 60_000.0);
        long minutesOfDay = Math.floorMod(totalMinutes, 24 * 60);
        long hh = minutesOfDay / 60;
        long mm = minutesOfDay % 60;
        return String.format("%02d:%02d", hh, mm);
    }
}