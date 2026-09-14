package org.openathar.core;

/** Twilight parameter: either a solar depression angle or fixed minutes. */
public sealed interface Twilight permits Twilight.Angle, Twilight.Minutes {
    record Angle(double degrees) implements Twilight {}
    record Minutes(double minutes) implements Twilight {}
}