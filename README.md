# athan-core-java

> **Status: scaffold.** README and contributor notes only — no code yet.
> See [why](https://github.com/openathar/athar/blob/main/docs/architecture.md)
> and what comes first.

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
for prayer times, as a deliberate placeholder. When this library gets
built, the web's existing logic and parameters count as the spec to match —
not a clean-room reimplementation of PrayTimes.org.

## License

MIT/Apache-2.0 (final choice pending) — deliberately permissive, so other
developers can freely embed this library in their own projects.
