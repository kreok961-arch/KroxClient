# Changelog Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

**There is no changelog feature in this launcher.** §9.4 asks for one; the
codebase does not contain it in any form. This document exists to record that
absence precisely, because §2.2 lists "changelog entry" among the things that
must be preserved — and the honest finding is that there is nothing to preserve.

Every relevant search returned empty or irrelevant results:

| Search | Command | Result |
|---|---|---|
| Any file named changelog | `find . -iname "*changelog*"` | **empty** |
| Any changelog in a `.kt` | `grep -rni "changelog" --include=*.kt .` | 3 files, **all irrelevant** |
| A `Changelog` nav destination | `NormalNavKey.kt` full enumeration | **absent** |
| A changelog string resource | `R.string.changelog*` | **absent** |

### The three grep hits, and why none of them is a changelog

| Hit | Line | What it actually is |
|---|---|---|
| `JVMScreen.kt` | 113, 146, 203 | `changeLogOutput` — a **callback name** for the JVM's stdout/stderr console. "Change log" in the sense of *mutation log of output*, unrelated to release notes. |
| `ModrinthSearcher.kt` | 78 | `include_changelog=false` — a **Modrinth REST query parameter**. The app asks a third-party API to omit mod changelogs from a search response. |
| `ModrinthVersion.kt` | 50, 52, 100, 101 | **Modrinth API response models** carrying per-mod changelog fields. Consumed by the mod search/download path, not the launcher. |

**None of these is a launcher changelog surface.** A Phase 5 reader scanning for
"changelog" will hit all three and conclude the feature exists. It does not.

## §9.4 compliance

| Spec requirement | Code reality | Verdict |
|---|---|---|
| Show what changed between versions | **No code** | **Not present in code** |
| A changelog screen or destination | **No code** | **Not present in code** |
| Link to release notes | `UrlManager.URL_PROJECT_RELEASES_LATEST` at `:56` exists but has **no launcher-side call site** | **Pre-existing gap — D-07** |
| Preserve existing changelog entries (§2.2) | There are none | **Satisfied vacuously** |

### D-07 — the gap

§9.4 is unmet. The redesign **must not close it**: adding a changelog screen, a
release-notes fetch, a "what's new" dialog, or an update-history list is a new
feature under §1.2 and is explicitly out of scope.

The one adjacent asset worth recording:

`path/UrlManager.kt:56` defines `URL_PROJECT_RELEASES_LATEST`. A GitHub
"latest release" URL exists as a constant, but grepping for it finds **no UI
call site** — the constant is defined and unused, like `getRankName` in
`docs/audit/STATISTICS.md`. Exposing it through an About row would be the
smallest possible closure of D-07, but it is still *adding a destination*, so
it needs an owner decision rather than a Phase 5 pass. Recorded, not acted on.

## What §9.4's absence means for the redesign

- **No changelog screen to restyle.** Phase 5 has zero work here.
- **No changelog destination to preserve.** §11.2's "never hide existing
  navigation" is not engaged — there is nothing to hide.
- **No changelog to regress.** §19.2's "changelog renders" check has no target
  and must be struck from the QA matrix rather than marked passing. See
  `docs/QA.md` and QA-14.
- **§2.2's changelog clause** is satisfied by not doing anything, which is the
  correct outcome under §1.3.

## Relationship to Logs and Statistics

`CHANGELOG.md` is the third feature area documented as
**"not present in code"** alongside Logs' absent list surface
(`docs/audit/LOGS.md`) and Statistics' unreachable screens
(`docs/audit/STATISTICS.md`).

The distinction matters for Phase 5:

| Area | Status | Phase 5 action |
|---|---|---|
| Logs list | Absent, but the *viewer* exists and has 2 entry points | Do not add a list (§1.3) |
| Statistics screens | Present and rendering, but **unreachable** | **FIX** — add navigation (D-08) |
| Changelog | **Entirely absent** | Do not add anything (§1.2) |

Only Statistics gets an entry-point fix, and only because both the destination
and the screen already exist. That is the line: wiring up what ships is not the
same as shipping what does not.

## Classification

| Class | Items |
|---|---|
| **KEEP** | Nothing. No changelog code exists to keep. |
| **REDESIGN** | Nothing. |
| **FIX** | **D-07 recorded, deliberately not fixed.** Building a changelog surface is a new feature under §1.2. |

## Notes

- `URL_PROJECT_RELEASES_LATEST` (`:56`) is defined and unreferenced. Do **not**
  delete it — it is a config constant, and §19.4 covers the config schema.
- `JVMScreen.kt`'s `changeLogOutput` is a **callback** the JVM console passes
  output deltas through. Do not rename it to avoid a future grep collision; it
  is unrelated to release notes and renaming it is churn with a compile risk and
  no payoff.
- All conclusions here are unverified by a compiler until R-01 clears — see the
  verification-status section in `docs/REGRESSIONS.md`.
