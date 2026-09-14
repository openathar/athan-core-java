# Known Drift Risks — athan-core-java

This library is the single source of truth for Athar's calculation logic.
"Drift" here means: cases where our output differs from another
implementation or where the algorithm degrades. Documented so nobody
discovers them as bugs later.

## 1. High-latitude midnight sun / polar night → 0 (epoch)

**Symptom:** For locations above the polar circle during midnight sun or
polar night (e.g. Tromsø 2026-06-21), `getTimes` returns `0` (1970-01-01
epoch) for the uncomputable prayers.

**Why:** The solar-position math produces `NaN` (arccos of a value outside
[-1, 1]); `Math.round(NaN)` in Java returns `0`, which then flows through
`convert` into the epoch millis.

**Reference behavior:** praytime.js v3.2 returns `"-----"` (uncomputable)
for the same cases — it explicitly guards NaN. Adhan returns a time anyway
(no guard).

**Impact:** A consumer must treat `0` as "not computable", not as a real
time. The web frontend and API must guard against this before display.

**Status:** Accepted for now (faithful to the Kotlin port). A `Double.NaN`
→ sentinel fix is a candidate improvement; the invariant tests deliberately
exclude these cases (see `PrayerTimesInvariantTest` javadoc).

## 2. Algorithm-level differences vs. Adhan (independent implementation)

**Symptom:** dhuhr/asr/maghrib/isha can differ by up to 1 minute from the
`adhan` npm library; fajr/sunrise/sunset match exactly.

**Why:** PrayTimes v3.2 and Adhan use different solar-position
approximations (equation of time / declination), which shifts the solar
noon anchor slightly.

**Measured (2026-09-14, UTC, 6 locations):** max deviation 1 minute on
dhuhr/asr/maghrib/isha, 0 on fajr/sunrise/sunset.

**Impact:** None for a single source of truth. Relevant only if someone
cross-checks against Adhan and expects byte-identical output.

## 3. Hijri backward conversion window (±4 days)

**Symptom:** `hijriToGregorian` corrects a tabular estimate against the real
Umm al-Qura output within a window of ±4 days. If the tabular estimate is
off by more than that (never observed for the tested range 1300–1448 AH),
the method returns `null`.

**Why:** The kbisa/al-Beruni tabular calendar deviates from Umm al-Qura by
up to ±2 days; the window is deliberately wider (±4) as a safety margin.

**Impact:** `null` must be handled by callers (the web does the same).

## 4. JVM `HijrahChronology` data range

**Symptom:** Java's Umm al-Qura data starts at 1300 AH (1882-11-12).
Candidates before that throw `DateTimeException` and are skipped by the
correction loop.

**Why:** The JDK ships a fixed Umm al-Qura table.

**Impact:** Dates before 1300 AH return `null` from `hijriToGregorian`.
The mobile target (embedded Umm al-Qura table) must match this range or
document its own.

## 5. Rounding mode: `NEAREST` ties

**Symptom:** `Rounding.NEAREST` uses `Math.round` (half-up). The Kotlin
port used `kotlin.math.round` (half-even). For real timestamps the
fractional part is continuous, so an exact `.5` tie is effectively
impossible — but if it ever occurred, the two would differ by 1 minute.

**Why:** Port fidelity trade-off; both were verified against the same
reference values.

**Impact:** None observed. Documented for completeness.