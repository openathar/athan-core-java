package org.openathar.core;

/**
 * Qibla direction (bearing from true north, in degrees) from any location
 * to the Kaaba in Mecca, using the great-circle initial bearing formula.
 * Kaaba coordinates match the Adhan library (21.4225241°N, 39.8261818°E)
 * so reference values agree.
 */
public final class Qibla {

    private static final double KAABA_LAT = 21.4225241;
    private static final double KAABA_LNG = 39.8261818;

    private Qibla() {
    }

    /**
     * @param latitude  location latitude in degrees (−90..90)
     * @param longitude location longitude in degrees (−180..180)
     * @return bearing from true north in degrees (0..360)
     */
    public static double bearing(double latitude, double longitude) {
        double lat1 = Math.toRadians(latitude);
        double lat2 = Math.toRadians(KAABA_LAT);
        double dLng = Math.toRadians(KAABA_LNG - longitude);
        double y = Math.sin(dLng);
        double x = Math.cos(lat1) * Math.tan(lat2) - Math.sin(lat1) * Math.cos(dLng);
        double bearing = Math.toDegrees(Math.atan2(y, x));
        return (bearing + 360) % 360;
    }
}