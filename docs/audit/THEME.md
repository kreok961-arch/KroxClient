# Theme Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

`ui/theme/` is **1351 lines across 6 files** and is the single most
load-bearing surface in the redesign. Everything visual flows through it.

| File | Lines | Role |
|---|---|---|
| `Theme.kt` | 768 | 14 `ColorScheme` definitions + `ZalithLauncherTheme` root |
| `NativeThemeUtils.kt` | 244 | System bar / edge-to-edge plumbing |
| `Color.kt` | 92 | Legacy color helpers |
| `Palette.kt` | 110 | `KroxSpacing` + the KROX color accessor functions |
| `Type.kt` | 94 | `KroxDisplay`…`KroxMono`, `AppTypography`, `KroxShapes` |
| `ColorTheme.kt` | 43 | Color-scheme selection plumbing |

Plus two sub-packages: `ui/theme/components/` and `ui/theme/feativals/`
(spelling as found — do not "fix" it; a rename is churn with zero visual payoff).

## `Theme.kt` — 14 color schemes

| Scheme | Lines | Family |
|---|---|---|
| `embermireLight` / `embermireDark` | 48 / 86 | Ember |
| `velvetRoseLight` / `velvetRoseDark` | 124 / 162 | Rose |
| `mistwaveLight` / `mistwaveDark` | 200 / 238 | Mist |
| `glacierLight` / `glacierDark` | 276 / 314 | Glacier |
| `verdantFieldLight` / `verdantFieldDark` | 352 / 390 | Verdant |
| `urbanAshLight` / `urbanAshDark` | 428 / 466 | Ash |
| `verdantDawnLight` / `verdantDawnDark` | 504 / 542 | Verdant Dawn |
| **`kroxLight` / `kroxDark`** | **582 / 620** | **KROX** |

`customLight(...)` at `:658` and `customDark(...)` at `:669` are the two private
helpers the 14 schemes are built from. `ZalithLauncherTheme` at `:681` is the
root composable and the file's **only public declaration**.

### The 13 other schemes are the app's real theme surface

**This is the single most under-appreciated fact in the audit.** The KROX
palette is *one of fourteen*. A user on a Pixel can be on `embermireDark`, a
user on a tablet on `kroxLight`, and both are correct shipped behaviour.

| Rule | Consequence |
|---|---|
| §1.3 — must not remove what exists | **None of the 13 other schemes may be deleted, renamed, or reordered.** |
| §1.3 — must not add what doesn't exist | **No 15th scheme** may be added. |
| §1.1 — colors are in scope | Phase 5 may **retune** the KROX tokens; the *mechanism* (14 schemes) is **KEEP**. |

Phase 5 therefore restyles **one** scheme pair and leaves thirteen untouched.

### `kroxDark` at `:620-656` — the frozen KROX tokens

| Role | Token | Value | Line |
|---|---|---|---|
| background | `#08080A` | `0xFF08080A` | 621 |
| surface | `#121216` | `0xFF121216` | 622 |
| surfaceVariant / elevated | `#24242C` | `0xFF24242C` | 644 area |
| primary (accent) | `#DC2626` | `0xFFDC2626` | — |
| outline | — | `0xFF6F5B59` | 643 |
| **outlineVariant** | — | **`0xFF4A3B3A`** | **644** |

**`outlineVariant` is deliberately NOT rewritten.** §7.6 defines the control
track tokens (36×20 dp track, 16 dp knob) and the file-manager's progress /
selection track uses `Color.White.copy(alpha = 0.12f)`. Rewriting
`outlineVariant` to a hairline gray would repaint the progress tracks on every
screen that reads it. Logged as **R-25**-adjacent; see `docs/REGRESSIONS.md`.

## `Palette.kt` — the token accessors

| Symbol | Line | Role |
|---|---|---|
| `KroxSpacing` | 34-46 | 11-step scale: `none`/`xs 4`/`sm 8`/`md 12`/`lg 16`/`xl 20`/`xxl 24`/`xxxl 32`/`huge 40`/`giant 48`/`max 64` |
| `backgroundColor()` | 50 | `surfaceContainer` |
| `onBackgroundColor()` | 52 | `onSurfaceVariant` |
| `cardColor(...)` | 62 | Parameterized card surface |
| `onCardColor()` | 69 | `onSurface` |
| `cardTitleColor(...)` | 74 | Parameterized card title |
| `itemColor(...)` | 83 | Parameterized list-item surface |
| `onItemColor()` | 97 | `onSurface` |
| `buttonColor(...)` | 103 | Parameterized button surface |
| `onButtonColor(...)` | 108 | Parameterized button foreground |

**Every one of these is a thin delegation to `MaterialTheme.colorScheme`.**
There is no hard-coded KROX hex in `Palette.kt` — the accent lives in
`kroxDark`/`kroxLight`, and the accessors read it.

**That indirection is the design system's main strength and must be preserved.**
Screens read `cardColor()`, not `Color(0xFF121216)`. Phase 5 restyling a card
means changing the *token*, which then propagates to every consumer — that is
the point. Do not inline literals in screens to "tune one card"; that breaks
the fan-out and is how the 13 non-KROX schemes get bypassed.

`KroxSpacing` tops out at `max = 64.dp`. Anything larger (e.g. the file
manager's `SIDEBAR_WIDTH = 180.dp`) is a **container dimension**, not a
spacing step, and sits off-scale by design — see `docs/audit/FILE_MANAGER.md`.

## `Type.kt` — the typography scale

| Token | Line | Spec (size / lineHeight / weight) |
|---|---|---|
| `KroxFontFamily` | 35 | **`FontFamily.Default`** — the locked decision |
| `kroxStyle(...)` | 37 | private factory |
| `KroxDisplay` | 49 | 28 / 36 / SemiBold |
| `KroxH1` | 51 | 22 / 30 / SemiBold |
| `KroxH2` | 53 | 16 / 24 / SemiBold |
| `KroxH3` | 55 | 14 / 20 / SemiBold |
| `KroxBody` | 57 | 13 / 20 / Regular |
| `KroxBodyStrong` | 59 | 13 / 20 / SemiBold |
| `KroxCaption` | 61 | 12 / 16 / Regular |
| `KroxMeta` | 63 | 11 / 14 / Medium |
| `KroxOverline` | 65 | 10 / 14 / SemiBold |
| `KroxMono` | 67 | 12 / 18 / Regular |
| `AppTypography` | 69 | `Typography(displayMedium = KroxDisplay, …)` |
| `KroxShapes` | 88 | `Shapes(...)` |

**`KroxFontFamily = FontFamily.Default` is the locked typography decision**
(platform default family, confirmed by the owner). §14.2 forbids new
dependencies, so no font file may be added — and there is none: `res/font/`
**does not exist** (see `docs/audit/RESOURCES.md`). This is consistent, not a
gap.

`KroxMono` (`:67`) uses the same default family rather than a monospace face.
It is used by the code editor area, where true monospace would be preferable.
**Not a defect to fix** — changing it means shipping a font file, which §14.2
forbids. Recorded.

**Adoption.** `KroxDisplay` is used in `PlayTimeStatsScreen.kt:172`
and `LauncherScreen.kt` (`PlayTimeStatsCard`). `KroxOverline` and `KroxH1` now
reach every §9.7/§9.8/§9.9 page through the shared `PageHeader`
(`ui/components/PageHeader.kt`) — D-09 resolved, see `docs/audit/SETTINGS.md`.
`PageHeader` names `KroxOverline` directly and reaches `KroxH1` indirectly via
`MaterialTheme.typography.headlineSmall` (mapped at `Type.kt:71`). Most screens
still read `MaterialTheme.typography` directly rather than the KROX type tokens.

## `NativeThemeUtils.kt` / `ColorTheme.kt` / `Color.kt`

`NativeThemeUtils.kt` (244 lines) handles system bars, edge-to-edge, and status
bar icon contrast. `ColorTheme.kt` (43) and `Color.kt` (92) are the older
color plumbing. **All three are KEEP** — they are platform integration, not
design surface, and touching them risks the status bar and cutout handling that
§19.2's "every destination opens" check implicitly depends on.

## Token adoption across the app

| Token | Adoption |
|---|---|
| `KroxSpacing` | **Sparse.** 0 in `AccountManageScreen.kt`, 0 in `BuiltInFileManager.kt`, 0 in `AboutInfoScreen.kt`, 0 in `LogViewScreen.kt`. Present in `GameStatsScreen.kt` and `PlayTimeStatsScreen.kt`. |
| `KroxMotion` / `kroxTween` | **Good.** 66 `kroxTween(` call sites. |
| `KroxType` tokens | **Sparse.** Only `KroxDisplay` is actually used. |
| `KroxShapes` | Partial — most screens read `MaterialTheme.shapes.extraLarge`. |
| `Palette.kt` accessors | Used by the redesigned surfaces; not universal. |

`docs/spec/tokens.md` §3 governs this: literals are **not** mass-rewritten
(79k-LOC diff, no visual payoff). New and rewritten code uses the scale; old
code keeps its literals until touched. Phase 5 adopts tokens **in the lines it
edits** and does not do a tokenization sweep.

## §19.4 position

| Guarantee | Status |
|---|---|
| Not touching the config schema | **Held** — no `AllSettings` theme entry added, removed, renamed, or re-typed. The scheme-selection setting keeps its current shape. |
| Not removing a user-selected theme | **Held** — 14 schemes stay; a user on `embermireDark` keeps it after the redesign. This is the most user-visible §1.3 obligation in the codebase. |

## Classification

| Class | Items |
|---|---|
| **KEEP** | All 14 `ColorScheme` definitions and their order; `customLight`/`customDark`; `ZalithLauncherTheme`'s signature; `KroxSpacing`'s 11 values; every `Palette.kt` accessor's *delegation* behaviour; `AppTypography`; `KroxShapes`; `KroxFontFamily = FontFamily.Default`; `NativeThemeUtils.kt`, `ColorTheme.kt`, `Color.kt`; `outlineVariant = 0xFF4A3B3A`. |
| **REDESIGN** | The `kroxLight`/`kroxDark` **values** (Phase 5 tunes the frozen tokens to the spec); `KroxShapes` radii against the §4 radius tokens; screens not yet reading `KroxType`. |
| **FIX** | None. The sparse adoption is a Phase-5 scope item, not a correctness defect. The `KroxOverline`/`KroxH1` page-header gap (D-09) is closed by `ui/components/PageHeader.kt`. |

## Notes

- **Do not touch `outlineVariant`** (`Theme.kt:644`). It is the progress-track
  color and the file manager's selection track. Rewriting it to a neutral
  hairline is a cross-screen repaint disguised as a one-line theme change.
- The two sub-package names include a typo (`feativals`). Renaming it is churn
  with a compile risk and no visual payoff — leave it.
- `KroxSpacing.max = 64.dp` does not cover large container dimensions. Adding a
  bigger step to the scale to accommodate one sidebar is backwards; adjust the
  sidebar's value instead.
- §7.3's "only two elevation levels" is **not** implemented in `Theme.kt` —
  there is no elevation/shadow token. It is a Phase 5 addition, and it must
  land as exactly two levels, not a scale.
- All edits to these files are unverified by a compiler until R-01 clears — see
  the verification-status section in `docs/REGRESSIONS.md`.
