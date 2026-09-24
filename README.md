# athan-core-java

<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="docs/logo-dark.png" />
    <img src="docs/logo-light.png" alt="Athar — the word أثر" width="360" />
  </picture>
</p>

The Core SDK (Java 25 / Maven) for precise, offline-capable prayer time,
Qibla direction, and Hijri calendar calculation. Part of the Athar platform
(Sadaqah Jariyah, 100% open source) — see [openathar](https://github.com/openathar).

**Published to Maven Central:** `org.openathar:athan-core:0.1.0`

## What's inside

- **Prayer times** — a pure Java port of the PrayTimes.org v3.2
  specification, with all calculation methods (MWL, ISNA, EGYPT, MAKKAH,
  KARACHI, TEHRAN, JAFARI, FRANCE, RUSSIA, MALAYSIA, SINGAPORE), Asr
  (standard/Hanafi), high-latitude handling, rounding, and the Duha window.
- **Qibla direction** — bearing from true north to the Kaaba
  (21.4225241°N, 39.8261818°E).
- **Hijri calendar** — Gregorian ↔ Hijri conversion via
  `java.time.chrono.HijrahChronology` (Umm al-Qura), plus localized month
  names (en/ar).

All pure, stateless, no DB access, no framework dependencies. Each part has
unit tests against reference values generated from the official
praytime.js v3.2 library, the web's ICU-based Hijri logic, and the
independent Adhan library.

## Why this matters

This library is the **single source of truth** for Athar's calculation
logic — embedded offline in the mobile app, wrapped by the public API on
the server, and mirrored by the web frontend (a TypeScript port kept in
sync by reference tests). One engine, used everywhere, instead of several
engines that quietly drift apart over time.

## Usage

```xml
<dependency>
    <groupId>org.openathar</groupId>
    <artifactId>athan-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

```java
PrayerTimesResult times = new PrayerTimes(Method.MWL)
    .getTimes(2026, 9, 15, 52.52, 13.405);   // lat, lng
HijriDate hijri = Hijri.gregorianToHijri(LocalDate.of(2026, 9, 15));
double qibla = Qibla.bearingDegrees(52.52, 13.405);
```

## Building

```sh
mvn verify   # compiles and runs the unit tests (Java 25)
```

## Publishing

Releases are published to Maven Central via the `release` profile
(`mvn -Prelease clean deploy`), authenticated with the Central Portal user
token in `~/.m2/settings.xml` (server id `central`). See the POM for
details.

## License

MIT — deliberately permissive, so other developers can freely embed this
library in their own projects.