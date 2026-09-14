package org.openathar.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Reference values generated from the independent Adhan library
 * (npm {@code adhan}, {@code adhan.Qibla}) — the same locations used in
 * the prayer-time reference suite.
 */
class QiblaTest {

    private static final double EPSILON = 0.01;

    @Test
    void berlin() {
        assertEquals(136.68, Qibla.bearing(52.52, 13.405), EPSILON);
    }

    @Test
    void mecca() {
        assertEquals(324.89, Qibla.bearing(21.4225, 39.8262), EPSILON);
    }

    @Test
    void newYork() {
        assertEquals(58.48, Qibla.bearing(40.7128, -74.006), EPSILON);
    }

    @Test
    void sydney() {
        assertEquals(277.50, Qibla.bearing(-33.8688, 151.2093), EPSILON);
    }

    @Test
    void singapore() {
        assertEquals(293.02, Qibla.bearing(1.3521, 103.8198), EPSILON);
    }

    @Test
    void reykjavik() {
        assertEquals(106.12, Qibla.bearing(64.1466, -21.9426), EPSILON);
    }

    @Test
    void tokyo() {
        assertEquals(293.00, Qibla.bearing(35.6762, 139.6503), EPSILON);
    }

    @Test
    void capeTown() {
        assertEquals(23.35, Qibla.bearing(-33.9249, 18.4241), EPSILON);
    }

    @Test
    void bearingIsWithinZeroTo360() {
        for (double lat : new double[]{-90, -45, 0, 45, 90}) {
            for (double lng : new double[]{-180, -90, 0, 90, 180}) {
                double b = Qibla.bearing(lat, lng);
                assertEquals(true, b >= 0 && b < 360, "bearing " + b + " in [0,360) for " + lat + "," + lng);
            }
        }
    }
}