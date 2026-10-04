# Sidebar Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview
Two independent "sidebar" systems exist in the codebase, and only one of them is live.
Conflating them produces wrong conclusions, so they are documented separately.

---

## 1. `SideBar.kt` — DEAD CODE, 0 references

`ui/screens/content/elements/SideBar.kt`, **337 lines**, confirmed dead.

| Declaration | Line | Visibility |
|---|---|---|
| `CollapsedWidth = 56.dp` | 81 | private val |
| `ExpandedWidth = 110.dp` | 82 | private val |
| `SideBar(` | 85 | **public** |
| `SideBarMenuContent` | 148 | private |
| `StaggeredItem` | 217 | private |
| `SideBarToggle` | 244 | private |
| `SideBarShortcut` | 279 | private |

### Proof of deadness

- `grep -rn "\bSideBar(" --include=*.kt .` excluding `elements/SideBar.kt` → **empty**.
- `grep -rn "elements.SideBar"` → **empty**. No import anywhere.
- `grep` for each of the 4 private sub-component names outside the file → **empty**.

The single public `SideBar` composable has zero call sites. The file was orphaned by the
selective `RightMenu` restore described in `docs/audit/RISKS.md` R-02/R-03 — the original
`LauncherScreen` had a second rail that was intentionally removed, and `SideBar.kt` was
its companion.

### Why it still violates §7.6

It animates with `animateDpAsState` at `:95-96` and uses
`spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)`
— §7.6 permits no bounce / elastic / spring. Those springs were converted to `kroxTween`
during the redesign (logged as R-22), which is churn on a file nothing calls.

### Classification

| Class | Verdict |
|---|---|
| **KEEP** | — |
| **REDESIGN** | — |
| **FIX** | **Candidate for deletion.** Zero references, 337 lines, no compile dependency. Deletion is a scope decision, not a correctness one. |

**Not deleted.** The spec's one-line rule governs features, not dead files, and deleting
337 lines is a reviewable change that should be its own commit rather than a footnote to
a design pass. Recorded as R-04 in `docs/audit/RISKS.md`.

---

## 2. `CollapsibleSidebarSection` — the live sidebar

`BuiltInFileManager.kt` has its own, unrelated sidebar. These are private locals, not the
file above:

| Symbol | Line | Role |
|---|---|---|
| `SidebarSlideEnterSpec` | 122 | Enter transition spec |
| `SidebarSlideExitSpec` | 124 | Exit transition spec |
| `CollapsibleSidebarSection` | 1185 | Collapsible section container |
| `SidebarNavItem` | 1244 | Single nav row |
| `SidebarStorageFooter` | 1324 | Storage summary pinned to sidebar bottom |

This is one of **two** live sidebars in the app, not the only one — see §4. It is
**KEEP** — structure and behaviour unchanged; **REDESIGN** for surface, spacing, and
selection state.

**Hover now ships (R-42).** `SidebarNavItem` had the §6.4 Selected background and the
3 dp accent bar but no Hover layer at all. It now observes its own
`MutableInteractionSource` and layers `KroxHoverOverlay` over the selected background at
120 ms, so hovering a selected row shows both. The same pass closed the rest of the §6.4
Hover row on this surface: unselected rows now lift their text and icon to `#F9FAFB`
on hover, and the two pre-existing tweens were retimed to §6.5's per-surface values
(background 120 ms, left bar 140 ms) with `KroxEaseOut`. Line numbers in this table are
post-edit.

§6.4's **Focus** and **Disabled** rows are still unimplemented here — the composable has
no `enabled` parameter and no `focusable` modifier. See D-13 in `docs/REGRESSIONS.md`.

**Motion corrected in R-45.** 10 `tween(...)` calls in this file carried no `easing`
argument and were therefore running on Compose's default `FastOutSlowInEasing`, which
§3.6 permits nowhere. All 10 now pass `easing = KroxEaseOut`; durations and
`delayMillis` are unchanged. The two `Section*Spec` fade vals (`:130-133`) are consumed
by `CollapsibleSidebarSection` at `:1230`/`:1234`, so they were live rather than dead
defaults. Full row in `docs/REGRESSIONS.md`.

---

## 3. `_SimpleRail.kt` — the shared rail primitive

`ui/components/_SimpleRail.kt` has exactly **one** public declaration:

| Symbol | Line |
|---|---|
| `fun TextRailItem(` | 75 |

Padding is animated at `:141-160`; the default `PaddingValues` it reads is declared at
`:81`. Everything else in the file is private.

**Correction:** an earlier draft claimed `SettingsScreen.TabMenu` is a caller. It is
not — `SettingsScreen.kt:164-180` instantiates Material3's `NavigationRailItem`
directly. `TextRailItem` has exactly **one** call site:
`ui/screens/content/elements/VersionsManageElements.kt:330`. See
`docs/audit/SETTINGS.md` for the verified tab-rail call site.

**KEEP** the primitive; **REDESIGN** the item's selected/hover/disabled presentation to
match the §7.7 hairline and §4 radius tokens.

---

## 4. `MainScreen.SidebarNavButton` — the top-level rail

`ui/screens/main/MainScreen.kt` carries the launcher's own nav rail, separate from both
the File Manager sidebar and `_SimpleRail`. It is constructed by the app entry point
`ui/activities/MainActivity.kt:329`, so it is present on **every** screen.

| Symbol | Line | Role |
|---|---|---|
| `SidebarNavButton(` | 450 | Rail nav row (collapsible expanded/collapsed) |
| 6 call sites | 359, 367, 375, 383, 391, 399 | The six primary destinations |

This is the highest-traffic nav item in the codebase and it was, until R-42, the
navigation surface with no §6.4 Hover state whatsoever. R-42 gave it the same treatment
as §2: a `MutableInteractionSource` threaded into the existing `Surface(onClick = …)`,
`KroxHoverOverlay` composited over the container colour via `compositeOver` at `:493`,
and `#F9FAFB` for text and icon on hover. Its pre-existing timings were already
§6.5-compliant (background 120 ms, left bar 140 ms, both `KroxEaseOut`).

**Correction to the record:** R-42 originally described `SidebarNavItem` as "the
launcher's one live sidebar item." That was wrong — two live nav-item composables exist,
and this is the busier one. Both are now covered. R-44 completed `TextRailItem`; see
the §6.4 coverage table at the end of this file.

---

## 5. `RightMenu` — the Home right-hand panel

Not a sidebar, but structurally adjacent and the other half of Home's composition.

| Symbol | Line | Source |
|---|---|---|
| `RightMenuContent(` | 320 | current `LauncherScreen.kt` |
| `RightMenu(` | 473 | current `LauncherScreen.kt` |
| call site | 168 | `LauncherScreen.kt` |

Restored **verbatim** from codespace export `3d9ab76`, not re-derived — see R-03 in
`docs/audit/RISKS.md`. The original line numbers in that risk entry (`:762`, `:616`,
`:799`) refer to the 1029-line original file; the restored file is 577 lines, hence the
drift. **KEEP** the structure exactly; **REDESIGN** surfaces and spacing only.

---

## Classification summary

| Class | Items |
|---|---|
| **KEEP** | `CollapsibleSidebarSection` + its 4 siblings, `MainScreen.SidebarNavButton`, `_SimpleRail.TextRailItem`, `RightMenu`/`RightMenuContent` structure. |
| **REDESIGN** | All live sidebar and rail *presentation*: surface, padding, selection indicator, dividers. |
| **FIX** | `SideBar.kt` — dead. Delete or record; either way it should not ship. |

## §6.4 state coverage across the three live nav surfaces

Phase 6's "hover / focus / disabled state audit" resolved to this table. Hover is
complete on all three; Focus and Disabled are absent on all three, and that absence is
recorded once as D-13 rather than repeated per surface.

| Surface | Default | Hover | Selected | Focus | Disabled |
|---|---|---|---|---|---|
| `MainScreen.SidebarNavButton` | present | **complete** (R-42) | present | absent | absent |
| `BuiltInFileManager.SidebarNavItem` | present | **complete** (R-42) | present | absent | absent |
| `_SimpleRail.TextRailItem` | present | **complete** (R-40 background, R-44 text lift) | present | absent | present (`enabled` param → `DisabledAlpha`) |

All three now cover the whole §6.4 Hover row — background wash *and* the `#F9FAFB`
text/icon lift. `TextRailItem` reached the text lift in R-44; until then it drew the
`KroxHoverOverlay` canvas but its `contentColor` at `_SimpleRail.kt:172` branched on
`selected` alone, so the lift was missing. It is the only one of the three with a
disabled state, because it is the only one that takes an `enabled` parameter — the hover
branch there is gated on `isHovered && enabled` so a disabled row cannot light up.
