# AGENTS.md — athan-core-java

Public Core SDK of the Athar platform (openathar). Pure, stateless
calculation logic: prayer times (MWL/ISNA/Umm Al-Qura/...), Qibla bearing,
Hijri calendar conversion. No DB access, no state.

## Links

- Architecture/roadmap: `../../AGENTS.md` (superproject `business/athar`)
- Repo conventions: `~/Development/harness/agents/business-repo.md`

## Ground rule

This library is meant to be the ONE source of truth for calculation
logic — used by both the backend (`api-service`) and the mobile app
(`athar-mobile-app`, embedded/offline). Never reimplement the logic a
second time.

Reference algorithm: the PrayTimes.org specification, cross-checked against
what the web frontend (`athar-web`) already computes/shows today — the web
is the de-facto reference implementation until this library exists. Unit
tests against known reference values are required before every merge.

## Current state

Calculation code landed: prayer times (PrayTimes.org v3.2 port,
`PrayerTimes.java`, pure Java), Hijri conversion (`Hijri.java` via
`HijrahChronology` = Umm al-Qura), and Qibla bearing (`Qibla.java`), each
with unit tests against reference values generated from the official
praytime.js v3.2 library, the web's ICU-based hijri logic, and the
independent Adhan library. Plus invariant tests, a Duha window
(sunrise+15min … dhuhr−10min, best = midpoint), a CI pipeline, and
`known-drift-risks.md` documenting the edge cases.

Build with `mvn verify` (Java 25, Maven, JUnit 6). The prayer-time math is
pure Java (no Spring, no state); Hijri leans on
`java.time.chrono.HijrahChronology` — the migration point for the mobile
target is replacing that with an embedded Umm al-Qura table.

## APM (Agent Package Manager)

Projekt-lokale Skills/Agents/Commands werden über `apm.yaml` verwaltet
(Registry-Quelle: `~/Development/harness/registry/`).
- `apm install --local` — installiert die in `apm.yaml` gelisteten Packages
- `apm status --local` — prüft Installations-Stand gegen die Registry
