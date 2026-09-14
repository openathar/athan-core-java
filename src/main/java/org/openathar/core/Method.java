package org.openathar.core;

/**
 * Calculation methods (praytime.js v3.2 {@code methods} table, with the shared
 * {@code defaults} — isha 14°, maghrib 1 min — already merged in).
 */
public enum Method {
    MWL(18.0, new Twilight.Angle(17.0), new Twilight.Minutes(1.0), Midnight.STANDARD),
    ISNA(15.0, new Twilight.Angle(15.0), new Twilight.Minutes(1.0), Midnight.STANDARD),
    EGYPT(19.5, new Twilight.Angle(17.5), new Twilight.Minutes(1.0), Midnight.STANDARD),
    MAKKAH(18.5, new Twilight.Minutes(90.0), new Twilight.Minutes(1.0), Midnight.STANDARD),
    KARACHI(18.0, new Twilight.Angle(18.0), new Twilight.Minutes(1.0), Midnight.STANDARD),
    TEHRAN(17.7, new Twilight.Angle(14.0), new Twilight.Angle(4.5), Midnight.JAFARI),
    JAFARI(16.0, new Twilight.Angle(14.0), new Twilight.Angle(4.0), Midnight.JAFARI),
    FRANCE(12.0, new Twilight.Angle(12.0), new Twilight.Minutes(1.0), Midnight.STANDARD),
    RUSSIA(16.0, new Twilight.Angle(15.0), new Twilight.Minutes(1.0), Midnight.STANDARD),
    MALAYSIA(20.0, new Twilight.Angle(18.0), new Twilight.Minutes(1.0), Midnight.STANDARD),
    SINGAPORE(20.0, new Twilight.Angle(18.0), new Twilight.Minutes(1.0), Midnight.STANDARD);

    public final double fajr;
    public final Twilight isha;
    public final Twilight maghrib;
    public final Midnight midnight;

    Method(double fajr, Twilight isha, Twilight maghrib, Midnight midnight) {
        this.fajr = fajr;
        this.isha = isha;
        this.maghrib = maghrib;
        this.midnight = midnight;
    }
}