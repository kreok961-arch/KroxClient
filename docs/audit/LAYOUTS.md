# Layouts Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview
Layout primitives are split across two layers. `ui/components/Layouts.kt` holds generic
composables; `ui/screens/content/settings/layouts/` holds the settings-row contract that
§9.8 binds to.

## Generic primitives — `ui/components/Layouts.kt`

| Layer | Responsibility |
|---|---|
| Containers | `BackgroundCard` (`BackgroundCard.kt`) is the dominant surface — used by dialogs, settings cards, and most content chunks. |
| Rows / columns | `_AnimatedRow.kt`, `_AnimatedColumn.kt` provide staggered-entent reveal on list mount. |
| Scroll helpers | `_Scroll.kt` (`fadeEdge` used at `SettingsScreen.kt:146`) applies a soft mask at scroll extremes. |
| Menus | `Menu.kt` — dropdown/menu surfaces, `MenuListHeader` at `:479`. |
| Chips | `_Chips.kt` — filter/tag chips. |
| Fake shadow | `_FakeShadow.kt` — synthetic depth for surfaces that cannot take a real elevation. |

## Settings row contract — `ui/screens/content/settings/layouts/`

This is the home of the §9.8 rule *"rows = label + optional description + control,
dividers between rows"*. Every settings row in the app is built from one of these files.

| File | Lines | Row kind |
|---|---|---|
| `_SettingsCard.kt` | 297 | The card wrapper + section grouping + dividers. |
| `Enum.kt` | 149 | Label + description + segmented/dropdown enum control. |
| `List.kt` | 432 | Label + description + list/selector control. Largest of the set. |
| `Slider.kt` | 215 | Label + description + slider with value readout. |
| `Switch.kt` | 138 | Label + description + switch. Mirrors the §7.6 track tokens. |
| `TextInput.kt` | 144 | Label + description + text field. |

**Total 1375 lines across 6 files.** No settings row is hand-built outside this directory.

## Padding and spacing reality

`KroxSpacing` (`ui/theme/Palette.kt:34-46`) defines 11 steps: `none` 0, `xs` 4, `sm` 8,
`md` 12, `lg` 16, `xl` 20, `xxl` 24, `xxxl` 32, `huge` 40, `giant` 48, `max` 64.

**Adoption is near-zero.** Per `docs/spec/tokens.md` §3, existing `dp` literals are
explicitly *not* mass-rewritten — *"that would be a 79k-LOC diff with no visual payoff."*
Only the two About surfaces use `KroxSpacing` today. This is by design, not an oversight.

## Screen layout sizes

| Screen | Lines | Layout note |
|---|---|---|
| `BuiltInFileManager.kt` | 1715 | Largest screen. Collapsible sidebar + file list + storage footer. |
| `AccountManageScreen.kt` | 1159 | Card-per-account + 4 operation groups. |
| `VersionSettingsScreen.kt` | 701 | Long settings form. |
| `VersionExportScreen.kt` | 691 | Form + preview. |
| `VersionsManageScreen.kt` | 679 | List + per-item actions. |
| `LauncherScreen.kt` | 577 | §9.1 Home. Content menu + right menu. |
| `MultiplayerScreen.kt` | 494 | Terracotta integration. |
| `RecordingsScreen.kt` | 470 | Grid of recordings. |

## Classification

| Class | Items |
|---|---|
| **KEEP** | `Layouts.kt`, all 6 `settings/layouts/` files, `_Scroll.kt`, `_AnimatedRow/Column.kt` — structure and behaviour unchanged. |
| **REDESIGN** | `BackgroundCard` (elevation + border already tokenized), all `settings/layouts/` rows (label/description/control spacing, dividers), `Menu.kt` surfaces. |
| **FIX** | None. |

## Notes

- The shared `PageHeader` (`ui/components/PageHeader.kt`) now hosts §8.4's
  "overline + H1" page header on all 13 §9.7/§9.8/§9.9 pages — divergence D-09
  resolved; see `docs/audit/SETTINGS.md`. The only other `Header` symbol is
  `private fun <E> MenuListHeader(` at `ui/components/Menu.kt:479`, which is a
  list-section header, not a page header.
- `_FakeShadow.kt` exists because Compose `Card` elevation renders poorly on some Android
  versions at these alphas. §7.3 permits exactly two elevation levels; do not add a third
  synthetic level.
