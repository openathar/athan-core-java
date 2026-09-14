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

Scaffold only — no code yet. See the superproject's `docs/architecture.md`
for the concrete sequence of what gets built first and why.

## APM (Agent Package Manager)

Projekt-lokale Skills/Agents/Commands werden über `apm.yaml` verwaltet
(Registry-Quelle: `~/Development/harness/registry/`).
- `apm install --local` — installiert die in `apm.yaml` gelisteten Packages
- `apm status --local` — prüft Installations-Stand gegen die Registry
