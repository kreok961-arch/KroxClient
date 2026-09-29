# Home Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

`LauncherScreen.kt`, **578 lines**, is the launcher root. It hosts the §9.1 Home
specification: play area, content menu, and right-hand panel.

## Declarations

All composables in the file, with current line numbers:

| Symbol | Line | Visibility | Role |
|---|---|---|---|
| `LauncherScreen(` | 105 | public | Screen root, `BaseScreen`-wrapped |
| `ContentMenu(` | 179 | private | Left/content column: PlayTime card + list |
| `PlayTimeStatsCard()` | 269 | private | Session play-time card |
| `RightMenuContent(` | 309 | private | Right panel body |
| `RightMenu(` | 462 | private | Right panel container |
| `VersionManagerLayout(` | 503 | private | Version-management block |

### Composition chain

`LauncherScreen` (`:105`) → `RightMenu` (`:462`) → `RightMenuContent` (`:309`).
`RightMenu` is invoked at `LauncherScreen.kt:161`.

`RightMenu` and `RightMenuContent` were **restored verbatim** from codespace export
`3d9ab76` per the locked "Reconstruct, then you review" decision (R-03 in
`docs/audit/RISKS.md`). The original line numbers cited in that risk entry
(`:762`, `:616`, `:799`) refer to the 1029-line pre-restore file; the restored
file is 578 lines, hence the drift. This is documented, not a discrepancy.

## §9.1 Home compliance

| Spec requirement | Code reality | Verdict |
|---|---|---|
| §9.1 Home is the root destination | `LauncherScreen` is the `BaseScreen` root | **Met** |
| Content region + right panel | `ContentMenu` + `RightMenu` | **Met** |
| Play-time visibility | `PlayTimeStatsCard()` (`:269`) | **Met** |
| §10.3 "choose one and apply consistently" | Home is the only place the 7f/3f split appears; no competing split exists anywhere else in `ui/` | **Met — reference implementation** |

### The 7f/3f split

`LauncherScreen.kt:161` allocates the row between the content column and the
right menu. It is the codebase's **only** weighted split. §10.3 requires one
proportion system applied consistently; because nothing else in `ui/` uses a
competing ratio, Home is the reference implementation rather than an outlier.
Phase 5 responsive work should derive breakpoints **from** this split, not invent
a second one.

## What was removed and must not come back (R-02)

The original `LauncherScreen` carried a second rail plus a 2×2 statistics grid.
Both were removed:

| Removed | Reason |
|---|---|
| `SideBar(` — second left rail | The selective `RightMenu` restore kept one rail, not two |
| `StatsGrid` (2×2) | Composed of `WeeklyPlayTimeChart`, `DailyPlayTimeCard`, `LastLogCard`, `ChangelogCard` |
| `WeeklyPlayTimeChart` | Statistics surface |
| `DailyPlayTimeCard` | Statistics surface |
| `LastLogCard` | Logs surface |
| `ChangelogCard` | Changelog surface |
| 3 associated navigation lambdas | With them `CHANGELOGS_URL` / `CHANGELOGS_UPDATE_TR` |

**These destinations still exist in `NormalNavKey`** but nothing on Home navigates
to them. This is a **pre-existing defect**, not a redesign regression — see R-02.
The redesign does not restore the grid and does not re-point Home navigation;
`docs/REGRESSIONS.md` D-06 and D-07 record that the Changelog and Logs *list*
surfaces have no host.

## §9.2 Play Button — divergences

One §9.2 requirement is not implementable without behavioural change, and is
recorded as an **accepted divergence** rather than silently dropped:

- **Launching state** (`LaunchGame.isLaunching` is a plain field, not Compose
  state; no `launching` string resource exists) — D-01 in `docs/REGRESSIONS.md`.

The other half of §9.2's state list now ships. `ScalingActionButton`
(`ui/components/Buttons.kt:105-131`) drives its container colour through
`Color.shift()` — `+0.08` on hover, `-0.08` on press, `KroxMotion.INSTANT` —
alongside the 0.98 press scale it already had. D-02 is closed.

## Classification

| Class | Items |
|---|---|
| **KEEP** | `LauncherScreen` composition, `ContentMenu`, `RightMenu`/`RightMenuContent` structure, `VersionManagerLayout` structure, the 7f/3f split. |
| **REDESIGN** | All presentation: surfaces, padding, dividers, selection state, the play-time card's internal layout, right-panel surface treatment. |
| **FIX** | None on this screen. R-02's missing Home navigation is pre-existing and out of redesign scope (§1.2 forbids new features). |

## Notes

- `PlayTimeStatsCard` is **presentation only**. §19.4 forbids touching statistics
  *calculation*; the card may be restyled, the aggregation behind it may not.
- `RightMenu` was restored verbatim and has therefore never been visually
  audited against §7.3/§7.7. Its surfaces and spacing are REDESIGN work in
  Phase 5, and any edit there is unverified until R-01 clears.
