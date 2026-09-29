# Responsive Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

> **Post-implementation note (Phase 5).** The counts and "zero breakpoints"
> findings below describe the **pre-Phase-5** codebase and are kept as the
> audit record. Phase 5 added `ui/theme/KroxWidthClass.kt` and one
> `screenWidthDp` read; every other number here is unchanged.

## Overview

**There is no responsive layout system in this launcher.** There is no
breakpoint, no window-size class, and no conditional layout branch anywhere in
the codebase. Responsivity is achieved **entirely** by Compose's intrinsic
weighting — 311 `.weight(` call sites and 832 `fillMaxWidth(` call sites, with
**zero** `maxWidth <` or `maxHeight >` comparisons.

| Metric | Count |
|---|---|
| `BoxWithConstraints` call sites | ~11 (used for *measurement*, never for branching) |
| `maxWidth <` / `>` conditional branches | **0** |
| `WindowWidthSizeClass` / `calculateWindowSizeClass` | **0** |
| `LocalConfiguration.current.screenWidthDp` | **0** (→ **1** in Phase 5) |
| `.weight(` call sites | 311 |
| `fillMaxWidth(` call sites | 832 |

`LocalConfiguration` is read in 4 places — `ReplacementControlDialog.kt:76`
(locale), `ControlManageScreen.kt:227`, `BasicInfoEdit.kt:70` /
`EditTranslatableText.kt:124` (locale), and `ErrorScreen.kt:86`. **Every one
reads locale or screen height for a fixed offset, not width for a breakpoint.**

`LocalWindowInfo.current.containerSize` appears in `BasicInfoEdit.kt:70`,
`MouseLayout.kt:140`, and `ui/components/_Algorithm.kt:34`/`:49` — all of them
converting pixel coordinates for the control-editor overlay system, which is a
**pointer-mapping** concern, not a layout concern.

## Why the percentage model already works

The launcher ships on Android, where the OS owns the window. Every screen root
is a `fillMaxSize` composable whose children split the available space by
weight. Consequences:

| Property | Status |
|---|---|
| Adapts to rotation | **Yes, automatically** — weights recompose |
| Adapts to split-screen / freeform | **Yes** — same mechanism |
| Adapts to foldable posture | **Partially** — no `WindowLayoutInfo` hinge awareness |
| Adapts to tablet-class widths | **Yes**, but Home's 7f/3f split gets proportionally wider rather than reflowing |
| Adapts to font scale | Partially — `dp` literals do not scale, `sp` does |

**Nothing is broken.** The audit's job is to record that §11's breakpoints are
a *new structure* layered onto a working percentage model, not a repair of a
broken one.

## §11.1 breakpoints — the four tiers

§11.1 asks for four tiers. The codebase has none, so Phase 5 adds the
*detection* only; the existing weights remain the layout.

| Tier | Width | Shipped behaviour |
|---|---|---|
| Compact | < 600 dp | Full-bleed, single column. **The current default.** |
| Medium | 600–900 dp | Same composition, gutter unchanged at `space-6` (24 dp) |
| Expanded | 900–1200 dp | Same composition, gutter `space-8` (32 dp) |
| Large | > 1200 dp | Same composition, gutter `space-10` (40 dp) |

> **Correction.** An earlier revision of this doc listed three tiers with a
> 600–840 dp boundary. The spec (`master-spec.md:833-838`) defines **four** tiers
> at **600 / 900 / 1200 dp**. The table above is the spec's.

**The composition does not change between tiers.** Per §10.3 — "choose one
approach and apply it consistently, do not mix" — Phase 5 must not reflow
Home's two-panel split into a stack on compact. That would be mixing two
responsive strategies inside one screen, and it would be a *layout* change to a
structure §11.2 protects.

### The 960 dp content cap is deliberately not implemented

§11.1 also describes Expanded/Large as "content max-width ~960 dp". Phase 5
does **not** implement that cap, and the reason is structural:

`BaseScreen` (`ui/base/BaseScreen.kt:132`) is the root `fillMaxSize()` `Box` of
**52** screens. Capping at the cap is impossible there without shrinking every
screen's scroll and click surface and leaving an unstyled background band down
both sides. §10.3 offers capped-centered **or** full-width-with-generous-padding
as alternatives and says to pick one — Phase 5 picked the second. Widening the
gutter *is* the "generous side margins" §10.3 describes.

If a cap is ever wanted, it belongs on an individual screen's root `Column`,
never on `BaseScreen`.

### What Phase 5 actually adds

The minimal correct implementation is one shared helper plus token substitution:

| Change | Files | Why |
|---|---|---|
| A `rememberKroxWidthClass(): KroxWidthClass` reading `LocalConfiguration.current.screenWidthDp` | 1 new file in `ui/theme/` | The only genuinely new thing |
| `KroxWidthClass.Compact / Medium / Expanded / Large` enum | same file | 4 cases, not a scale |
| `kroxGutter()` provided by `LocalKroxWidthClass` at the theme root | `ui/theme/Theme.kt` | One provider, read implicitly |
| Padding driven by `kroxGutter()` instead of a literal | `LauncherScreen.kt`, `PlayTimeStatsScreen.kt`, `GameStatsScreen.kt` | Gutter widens on larger screens |

**Do not** thread the class into every composable as a parameter. It is a
`CompositionLocal`-shaped concern; making it a parameter turns a four-value
enum into an argument every call site must thread through. One read at the
theme root, one value read implicitly by `kroxGutter()`.

## §11.2 — navigation may never be hidden

This is the hard constraint, and the codebase's structure helps it.

| Navigation surface | Why it is safe |
|---|---|
| Settings `TabMenu` rail (`SettingsScreen.kt:131`) | Fixed-width `NavigationRailItem` column, never conditional |
| File-manager sidebar (`BuiltInFileManager.kt`, `SIDEBAR_WIDTH = 180.dp`) | Fixed `dp`, no `maxWidth` branch |
| `_SimpleRail.TextRailItem` (`ui/components/_SimpleRail.kt:70`) | Fixed-width primitive |
| `RightMenu` / `RightMenuContent` | Fixed `dp` column in `LauncherScreen.kt` |

**Every navigation rail in the app is already unconditionally present.** There
is no drawer, no hamburger, no collapsible rail. §11.2 is therefore satisfied by
**not adding a collapse behaviour**, which is a much smaller obligation than
removing one.

`SIDEBAR_WIDTH = 180.dp` sits off the `KroxSpacing` scale by design
(`KroxSpacing.max = 64.dp`). Phase 5 may adjust the *value* as presentation;
it may never remove the sidebar from the composition
(`docs/audit/FILE_MANAGER.md`).

## The one place weighting is load-bearing

`LauncherScreen.kt` splits Home into a 7f/3f region and `RightMenu`. Both
`RightMenuContent` (`:309`) and the content column use `weight()`, so the split
is proportional at every width.

**This is the Phase 5 risk.** §10.3 forbids mixing, and the tempting
improvement — "on a tablet, stack the right menu below the content" — is
exactly the mix. It would:

- change Home's composition, which `docs/audit/HOME.md` classifies **KEEP**;
- hide or relocate `RightMenu`, which §11.2 protects;
- convert a weight into a breakpoint branch.

**Keep the 7f/3f split at every width.** Adjust the gutter, not the ratio.

## §8.15 empty states at narrow widths

Two of the three §8.15 gaps identified across the audit are `Box(fillMaxSize,
contentAlignment = Center)` compositions with a single centred `Text`:

- `AccountManageScreen.kt` — empty state (`docs/audit/ACCOUNTS.md`)
- `GameStatsScreen.kt` — empty state (`docs/audit/STATISTICS.md`)

A centred vertical empty state is inherently safe at any width, **provided the
supporting copy added in Phase 5 wraps rather than clips**. A third §8.15
treatment that puts an icon + title + copy in a fixed-width row would not. Use
a `Column` with a bounded `widthIn(max = …)`, not a `Row`.

### Resolved — both `Column`s are bounded (R-38)

Phase 5 shipped the icon + title + description treatments (R-35, R-36) as
**unbounded** `Column`s, so this section was guidance not yet followed. Both now
carry the bound:

```kotlin
Column(
    modifier = Modifier.widthIn(max = 320.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(KroxSpacing.sm)
) { … }
```

320 dp is below the usable width of every tier §11.1 defines, so the modifier
only engages when the description would otherwise run edge to edge; below it,
the copy wraps. `widthIn` was added to each file's imports.

The `KroxSpacing` scale was deliberately **not** used for the bound. It is a
spacing scale — its largest step, `max` at 64 dp, is a text measure nowhere near
a paragraph width, and the next usable value is a plain `320.dp` literal.

## Foldables — recorded, not addressed

No `androidx.window` usage, no `WindowLayoutInfo`, no hinge or fold
posture handling. `foldables.md`/`adaptive` are absent from the dependency
graph. §11 does not require foldable support, and adding it would be a new
dependency under §14.2. **Out of scope** — recorded so nobody later mistakes
it for an oversight.

## §19.4 position

| Guarantee | Status |
|---|---|
| Not touching the config schema | **Held** — no `AllSettings` layout/compactness entry added, removed, renamed, or re-typed. A width class is derived at composition time and persisted nowhere. |
| Not hiding navigation | **Held** — no rail, sidebar, or menu is made conditional in Phase 5. |

## Classification

| Class | Items |
|---|---|
| **KEEP** | The weight-based layout in all 311 sites; every fixed-`dp` navigation rail; the 7f/3f Home split; `BoxWithConstraints` measurement sites; `LocalWindowInfo` pointer-mapping usage. |
| **REDESIGN** | Gutters and content padding on the screens Phase 5 touches; empty-state width bounds. |
| **FIX** | None. Zero breakpoints is a **Phase 5 addition**, not a defect. The §11.1 960 dp content cap is likewise an addition, not a defect — see "The 960 dp content cap is deliberately not implemented". |

## Notes

- **`BoxWithConstraints` at `SelectGameVersionScreen.kt:311` is measurement, not
  branching** — `:323` computes `widthIn(max = maxWidth / 5 * 3)`. That is a
  3/5 width cap derived from the available space, and it is a *good* pattern.
  Do not convert it to a breakpoint; it already does the right thing at every
  width.
- The 11 `BoxWithConstraints` sites exist because Compose offers no other way
  to read available space. Introducing window-size classes does **not** replace
  them, and should not try to.
- `screenWidthDp` (from `LocalConfiguration`) is the cheapest correct source
  for a width class. `LocalWindowInfo.containerSize` is in **pixels**, and
  using it would require a density conversion that invites an off-by-one at
  non-integer densities.
- A width class is a **composition-time** value, not a persisted one. Storing
  it in `AllSettings` would be a config-schema change, which §19.4 forbids.
- All edits to these files are unverified by a compiler until R-01 clears — see
  the verification-status section in `docs/REGRESSIONS.md`.
