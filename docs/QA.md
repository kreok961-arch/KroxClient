# QA Matrix — KROX Launcher

Generated from codebase analysis on 2026-09-29.

Source: `docs/spec/master-spec.md` Part 16 (`:1063-1099`).
Companion: `docs/REGRESSIONS.md` (the §19.3 log).

## How to run this matrix

Every row is a **manual, on-device** check unless marked otherwise. There is no
test suite in this repository — `ZalithLauncher/src/test/` does not exist — so
the matrix is the verification surface, and QA-22 (the compile) is its only
automatable row.

| Execution gate | Status |
|---|---|
| §19.1 baseline captured | **Blocked on R-01** |
| Any row below can be honestly marked PASS | **No** — until R-01 clears |

**No row in this matrix may be marked PASS from code reading alone.** A compile
proves the code builds; it does not prove a screen opens. Until the CI compile
gate clears and the APK runs on a device, every row is either FAIL or
NOT-RUN.

## The matrix

| ID | Test | Pass criteria | Audit basis | Status |
|---|---|---|---|---|
| QA-01 | Navigation | Every existing navigation item opens the same destination as before | `NAVIGATION.md`, `RESPONSIVE.md` §11.2 | NOT-RUN |
| QA-02 | Home | Existing Home behavior unchanged | `HOME.md` | NOT-RUN |
| QA-03 | Play | Play launches Minecraft exactly as before | `HOME.md`, `docs/audit/` §2.3 | NOT-RUN |
| QA-04 | Accounts | Existing account operations unchanged | `ACCOUNTS.md` | NOT-RUN |
| QA-05 | Settings | Existing settings still work | `SETTINGS.md` | NOT-RUN |
| QA-06 | File Manager | Existing filesystem behavior unchanged | `FILE_MANAGER.md` | NOT-RUN |
| QA-07 | Logs | Existing logs remain accessible | `LOGS.md` | NOT-RUN |
| QA-08 | Statistics | Existing statistics remain accurate | `STATISTICS.md` | NOT-RUN |
| ~~QA-09~~ | ~~Changelog~~ | **STRUCK — see below** | `CHANGELOG.md` | **N/A** |
| QA-10 | About | Existing About information remains available | `ABOUT.md` | NOT-RUN |
| QA-11 | Responsive | No text / button overlap at any breakpoint | `RESPONSIVE.md` | NOT-RUN |
| QA-12 | Theme | Visual consistency across all screens | `THEME.md` | NOT-RUN |
| QA-13 | Regression | No launch-engine regression caused by UI changes | `RISKS.md` | NOT-RUN |
| QA-14 | No new features | No feature was added that was not present before | all | NOT-RUN |
| QA-15 | No player preview | No 3D skin / cape / cosmetic preview was added | §1.2 | NOT-RUN |
| QA-16 | No new auth | No new authentication flow was added | §1.2 | NOT-RUN |
| QA-17 | No engine rewrite | Launch engine files show no unrelated changes | §2.3 | NOT-RUN |
| QA-18 | No backend | No new backend or online service was added | §1.2 | NOT-RUN |
| QA-19 | Motion | No animation exceeds 240 ms | `ANIMATIONS.md` | NOT-RUN |
| QA-20 | Reduced motion | Platform reduced-motion respected where available | `ANIMATIONS.md` | NOT-RUN |
| QA-21 | Contrast | Text meets minimum contrast ratio against its background | `THEME.md` | NOT-RUN |
| QA-22 | Build | Project compiles with no UI-related errors | `DEPENDENCIES.md` | **BLOCKED (R-01)** |
| QA-23 | Config | No config schema change was introduced | `RESOURCES.md`, `DEPENDENCIES.md` | NOT-RUN |
| QA-24 | Logs format | No log format change was introduced | `LOGS.md` | NOT-RUN |
| QA-25 | Statistics source | No statistics source change was introduced | `STATISTICS.md` | NOT-RUN |
| QA-26 | File operations | No file-operation behavior change was introduced | `FILE_MANAGER.md` | NOT-RUN |
| QA-27 | Account persistence | Selected account persists across restarts as before | `ACCOUNTS.md` | NOT-RUN |
| QA-28 | Java runtime | Existing Java selection works as before | §2.2, §2.3 | NOT-RUN |
| QA-29 | Fabric path | Existing Fabric launch works as before | §2.2, §2.3 | NOT-RUN |
| QA-30 | Platform variants | Launcher runs on target platforms as before | `DEPENDENCIES.md` | NOT-RUN |

## QA-09 is struck, not passed

The spec's QA-09 reads *"Existing changelog content remains intact."*

`docs/audit/CHANGELOG.md` establishes that **§9.4 does not exist in this
codebase** — no changelog screen, no nav destination, no string resource, and
`find . -iname "*changelog*"` returns nothing. The three greps that do hit
"changelog" are a JVM stdout callback, a Modrinth query parameter, and Modrinth
API models.

Marking QA-09 PASS would assert that something was preserved that was never
there. **It is struck from the matrix** rather than given a pass, and the
original row is retained above struck-through so the deletion of the check is
itself auditable.

`URL_PROJECT_RELEASES_LATEST` (`path/UrlManager.kt:56`) is defined and
unreferenced. It stays that way — exposing it is a new destination under §1.2.

## The three rows that carry the most risk

**QA-22 (Build) is the gate on everything.** No Android SDK, no Compose
artifacts, and a JDK mismatch exist locally, so GitHub CI is the only compile
path. It is blocked on two CI secrets — `DEFAULT_STORE_PASSWORD` and
`DEFAULT_KEY_PASSWORD` — that must be added through the GitHub web UI. Until
then every claim in all 19 audit docs is **unverified by a compiler**, and that
caveat stands at the foot of each of them.

**QA-14 (No new features) is the row most likely to be violated by accident.**
Three separate audit findings are one small addition away from tripping it:
the missing Logs list, the absent changelog, and the missing §8.4 page header.
The first two must stay absent. The third is presentation on an existing
surface and is permitted.

**QA-19 (Motion ≤ 240 ms) is checkable by grep, not by eye.** `KroxMotion`'s
slowest value is 240. Any Phase 5 animation that bypasses `kroxTween` and
passes a literal duration is both a QA-19 failure and a token-adoption
regression.

## The one FIX in scope — D-08

`docs/audit/STATISTICS.md` finds that `NormalNavKey.GameStats` (`:212`) and
`NormalNavKey.PlayTimeStats` (`:217`) are declared and rendered but have **no
navigating call site anywhere in the repository**. `PlayTimeStatsCard()` in
`LauncherScreen.kt:269` has no `clickable` modifier and no `onClick` parameter.

This is a **FIX**, not a new feature: both destinations and both screens already
ship. The audit's line is that **wiring up what already exists is permitted;
building what does not exist is not.** QA-01 therefore has a dual obligation —
it must confirm no destination changed *and* that these two became reachable.

## Definition-of-Done gate

`docs/spec/master-spec.md` Part 17 is the gate. Until QA-22 clears and the
matrix runs on a device, the honest status is: **audit complete, implementation
in progress, nothing verified.** Every audit doc carries that caveat at its foot
and it is not a formality — it is the actual state of this work.
