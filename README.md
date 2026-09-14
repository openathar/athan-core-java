# athan-core-java

> **Status: first calculation code landed.** Prayer times (PrayTimes.org v3.2
> port) and Hijri conversion (Umm al-Qura) with unit tests against reference
> values. Qibla bearing still to come.

The Core SDK (Java/Kotlin) for precise, offline-capable prayer time, Qibla
direction, and Hijri calendar calculation. Part of the Athar platform
(Sadaqah Jariyah, 100% open source) — see [openathar](https://github.com/openathar).

## Why this matters

This library is meant to become the **single source of truth** for Athar's
calculation logic — embedded offline in the mobile app, and wrapped by the
public API on the server. One engine, used twice, instead of two engines
that quietly drift apart over time.

Until this repo has code, the [web frontend](https://github.com/openathar/athar-web)
computes Hijri dates itself in JavaScript and calls the external Aladhan API
for prayer times, as a deliberate placeholder. This library's first code
(prayer times + Hijri) was ported to match the web's existing logic and
parameters exactly — the web stays the reference until the API and web are
switched over to this engine.

## Building

```sh
./gradlew build   # compiles JVM target and runs the unit tests
```

## License

MIT/Apache-2.0 (final choice pending) — deliberately permissive, so other
developers can freely embed this library in their own projects.
