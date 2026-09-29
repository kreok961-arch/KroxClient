# Settings Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

Settings is the largest surface in the app: **13 screen/dialog files totalling
7386 lines**, plus a **6-file row contract totalling 1375 lines** in
`ui/screens/content/settings/layouts/`. The `SettingsScreen` shell is separate
and small (278 lines).

## The shell — `ui/screens/content/SettingsScreen.kt`

| Symbol | Line | Role |
|---|---|---|
| `SettingsScreen(` | 79 | public root, `BaseScreen`-wrapped |
| `settingItems` | 119 | 8-entry tab list, 2 with `division = true` |
| `TabMenu(` | 131 | Left rail: `NavigationRailItem` per entry |
| `NavigationUI(` | 188 | `NavDisplay` host for the 9 nested destinations |

`TabMenu` uses `NavigationRailItem` directly (`:164-180`) — **not**
`TextRailItem` from `_SimpleRail.kt`. `docs/audit/SIDEBAR.md` §3 claims
`SettingsScreen.TabMenu` uses `_SimpleRail.TextRailItem`; that is **incorrect**,
corrected here.

The 8 tabs: Renderer, Game, Control, Gamepad, Launcher, Java Manager (divider),
Control Manager, About Info (divider). The dividers are `HorizontalDivider` at
`Color.White.copy(alpha = 0.04f)` (`:156-161`) — the §7.7 divider token.

## The row contract — `settings/layouts/`

This is where §9.8 binds: *"rows = label + optional description + control,
dividers between rows."* No settings row is hand-built outside this directory.

| File | Lines | Row kind |
|---|---|---|
| `_SettingsCard.kt` | 297 | Card wrapper + section grouping + dividers |
| `List.kt` | 432 | Label + description + list/selector — largest |
| `Slider.kt` | 215 | Label + description + slider + value readout |
| `Enum.kt` | 149 | Label + description + segmented/dropdown enum |
| `TextInput.kt` | 144 | Label + description + text field |
| `Switch.kt` | 138 | Label + description + switch |
| **Total** | **1375** | 6 files |

### §9.8 compliance

| Rule | Status |
|---|---|
| label | **Met** in all 6 |
| optional description | **Met** in all 6 |
| control | **Met** in all 6 |
| dividers between rows | **Partial** — dividers live in `_SettingsCard.kt`, not in the row files. A single-row card has no divider, which is correct. |

`Switch.kt` mirrors the §7.6 track tokens (36×20 dp track, 16 dp knob, off
`#374151`, on `#10B981`). These are **not** the Material defaults, so `Switch.kt`
carries a deliberate divergence from the theme's `outlineVariant` — see
`docs/REGRESSIONS.md` R-25.

## The 13 screens

| File | Lines | Notes |
|---|---|---|
| `LauncherSettingsScreen.kt` | 1110 | Largest settings form |
| `ControlManageScreen.kt` | 1091 | Control map management |
| `ControlSettingsScreen.kt` | 809 | Per-control binding config |
| `RendererSettingsScreen.kt` | 620 | |
| `AboutInfoScreen.kt` | 568 | §9.9 — see `docs/audit/ABOUT.md` |
| `GamepadSettingsScreen.kt` | 550 | |
| `MobileGluesSettingsDialog.kt` | 489 | Dialog, §8.11 |
| `RendererBenchmarkOverlay.kt` | 480 | Overlay, not a page |
| `TurnipDriversScreen.kt` | 414 | |
| `JavaManageScreen.kt` | 402 | §2.3 — Java **selection** UI only |
| `RendererV2ConfigDialog.kt` | 374 | Dialog, §8.11 |
| `GameSettingsScreen.kt` | 330 | |
| `BenchmarkGLRenderer.kt` | 149 | Benchmark, not a settings page |

### Screens vs. dialogs vs. overlays

Three of the 13 are not settings *pages*: two dialogs (`MobileGluesSettingsDialog`,
`RendererV2ConfigDialog`) and two overlays (`RendererBenchmarkOverlay`,
`BenchmarkGLRenderer`). §8.11 governs the dialogs; the overlays have no spec
section and are treated as surfaces.

## `BaseScreen` — `ui/base/BaseScreen.kt`, 166 lines

Four `@Composable` overloads at `:41`, `:70`, `:98`, `:127`. Every settings
screen and every other routed screen is wrapped in it. It supplies the
swap-in/swap-out visibility animation that `TabMenu` also reads via `isVisible`.

**KEEP** — it is the transition contract for all navigation, and Phase 5 must
not alter its signature or the screens that call it differently.

## §9.8 and §8.4 page headers

**D-09 (resolved).** The component now exists at
`ui/components/PageHeader.kt`:

```kotlin
@Composable
fun PageHeader(
    title: AndroidStringText,
    modifier: Modifier = Modifier,
    overline: AndroidStringText? = null,
    subtitle: AndroidStringText? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
)
```

It renders `KroxOverline` (optional) + `headlineSmall` title, an optional
actions `Row`, and an optional `bodyMedium` subtitle, spaced by `KroxSpacing`
and bottom-padded by `KroxSpacing.lg`. This is the §8.4 "overline + H1" host.

All 13 screens in this doc now call it, each carrying the marker comment
`// D-09：§9.8 页面统一用 PageHeader（overline + H1）`:

| Screen | Call site | Placement variant |
|---|---|---|
| `LauncherSettingsScreen.kt` | `:173` | inside `AnimatedItem(scope)` |
| `GameSettingsScreen.kt` | `:90` | direct sibling before `AnimatedItem(scope)` |
| `RendererSettingsScreen.kt` | `:166` | direct sibling before `AnimatedItem(scope)` |
| `ControlSettingsScreen.kt` | `:126` | direct sibling before `AnimatedItem(scope)` |
| `JavaManageScreen.kt` | `:132` | new `Column` root, card demoted to `weight(1f)` |
| `TurnipDriversScreen.kt` | `:197` | new `Column` root, `BackgroundCard` demoted to `weight(1f)` |
| `ControlManageScreen.kt` | `:274` | new `Column` root, `AnimatedRow` demoted to `weight(1f)` |
| `GamepadSettingsScreen.kt` | `:179` | `item { }` inside `AnimatedLazyColumn` |
| `AboutInfoScreen.kt` | `:103` | `item { }` |
| `PlayTimeStatsScreen.kt` | `:90` | direct sibling |
| `GameStatsScreen.kt` | `:93` | direct sibling |

Each screen sources its title from its existing `NestedNavKey.Settings` /
`NormalNavKey` `.title` (`AndroidStringText`), so no new string resource was
added and `BaseScreen`'s signature was not touched. Non-scrolling roots were
wrapped in `Column(Modifier.fillMaxSize())` and their `fillMaxSize()` child
demoted to `fillMaxWidth().weight(1f)` so the header does not push the body
off-screen.

## Classification

| Class | Items |
|---|---|
| **KEEP** | `SettingsScreen` shell, `settingItems` list, `BaseScreen` (all 4 overloads), all 6 `layouts/` row files' structure and behaviour, the 9 nested destinations and their navigation. |
| **REDESIGN** | All presentation: row spacing, label/description typography, divider treatment, switch and slider chrome, `TabMenu` rail presentation, card surfaces, the new shared `PageHeader`. |
| **FIX** | None. The missing `PageHeader` was a gap to fill, not a defect — filled in Phase 5 (D-09). |

## Constraints

- **§2.3 / §19.4.** `JavaManageScreen.kt` is a **Java selection** UI. It must not
  be extended to add Java detection, download, or validation. `ControlSettingsScreen.kt`
  / `ControlManageScreen.kt` are control-mapping surfaces — no new mapping types.
- **§19.4 config schema held.** No `AllSettings` entry is added, removed, renamed,
  or re-typed. `AllSettings` is read (`showSettingsTip.state` at
  `SettingsScreen.kt:206`) but never written by redesign code.
- **D-05.** `LauncherSettingsScreen.kt` hosts `launcherSwapAnimateType` (BOUNCE /
  JELLY_BOUNCE) and `launcherAnimateSpeed` — two shipped settings that violate
  §7.6. Preserved byte-identical. Owner decision outstanding (R-05).
- **Unverified.** Every edit to these files is unverified until R-01 clears.
