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
| `SidebarSlideEnterSpec` | 118 | Enter transition spec |
| `SidebarSlideExitSpec` | 120 | Exit transition spec |
| `CollapsibleSidebarSection` | 1181 | Collapsible section container |
| `SidebarNavItem` | 1242 | Single nav row |
| `SidebarStorageFooter` | 1317 | Storage summary pinned to sidebar bottom |

This is the sidebar users actually see, on the File Manager screen. It is **KEEP** —
structure and behaviour unchanged; **REDESIGN** for surface, spacing, and selection state.

**Hover now ships (R-42).** `SidebarNavItem` had the §6.4 Selected background and the
3 dp accent bar but no Hover layer at all. It now observes its own
`MutableInteractionSource` and layers `KroxHoverOverlay` over the selected background at
120 ms, so hovering a selected row shows both — the same behaviour as
`_SimpleRail.TextRailItem`. Line numbers in this table are post-edit.

---

## 3. `_SimpleRail.kt` — the shared rail primitive

`ui/components/_SimpleRail.kt` has exactly **one** public declaration:

| Symbol | Line |
|---|---|
| `fun TextRailItem(` | 70 |

Padding lives at `:139`. Everything else in the file is private.

**Correction:** an earlier draft claimed `SettingsScreen.TabMenu` is a caller. It is
not — `SettingsScreen.kt:164-180` instantiates Material3's `NavigationRailItem`
directly. `TextRailItem` has exactly **one** call site:
`ui/screens/content/elements/VersionsManageElements.kt:330`. See
`docs/audit/SETTINGS.md` for the verified tab-rail call site.

**KEEP** the primitive; **REDESIGN** the item's selected/hover/disabled presentation to
match the §7.7 hairline and §4 radius tokens.

---

## 4. `RightMenu` — the Home right-hand panel

Not a sidebar, but structurally adjacent and the other half of Home's composition.

| Symbol | Line | Source |
|---|---|---|
| `RightMenuContent(` | 309 | current `LauncherScreen.kt` |
| `RightMenu(` | 462 | current `LauncherScreen.kt` |
| call site | 161 | `LauncherScreen.kt` |

Restored **verbatim** from codespace export `3d9ab76`, not re-derived — see R-03 in
`docs/audit/RISKS.md`. The original line numbers in that risk entry (`:762`, `:616`,
`:799`) refer to the 1029-line original file; the restored file is 577 lines, hence the
drift. **KEEP** the structure exactly; **REDESIGN** surfaces and spacing only.

---

## Classification summary

| Class | Items |
|---|---|
| **KEEP** | `CollapsibleSidebarSection` + its 4 siblings, `_SimpleRail.TextRailItem`, `RightMenu`/`RightMenuContent` structure. |
| **REDESIGN** | All live sidebar and rail *presentation*: surface, padding, selection indicator, dividers. |
| **FIX** | `SideBar.kt` — dead. Delete or record; either way it should not ship. |
