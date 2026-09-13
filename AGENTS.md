# AGENTS.md — athan-core-java

Public Core SDK der Athar-Plattform (openathar). Reine, zustandslose
Berechnungslogik: Gebetszeiten (MWL/ISNA/Umm Al-Qura/...), Qibla-Winkel,
Hijri-Kalender-Konvertierung. Kein DB-Zugriff, kein State.

## Verknuepfungen
- Architektur/Roadmap: `../../AGENTS.md` (Superproject `business/athar`)
- Repo-Regeln: `~/Development/harness/agents/business-repo.md`

## Grundsatz
Diese Library ist die EINZIGE Quelle der Wahrheit für die
Berechnungslogik — wird sowohl vom Backend (`api-service`) als auch von der
Mobile-App (`athar-mobile-app`, embedded/offline) genutzt. Niemals die Logik
an zweiter Stelle re-implementieren.

Referenz-Algorithmus: PrayTimes.org-Spezifikation. Unit-Tests gegen bekannte
Referenzwerte sind Pflicht vor jedem Merge.
