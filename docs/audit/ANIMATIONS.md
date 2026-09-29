# Animations Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

Motion is **already centralized and already on-spec**. `utils/animation/` is the
single motion surface, and the spec's forbidden easing families (bounce, elastic,
spring) are confined to **two named, owner-acknowledged divergences** rather than
scattered across the app.

| File | Lines | Role |
|---|---|---|
| `utils/animation/AnimationUtils.kt` | 245 | The motion token source — durations, easings, speed scaling |
| `utils/animation/_Easing.kt` | — | Custom easing curves, incl. a `bounce` curve (D-05) |
| `utils/animation/TransitionAnimationType.kt` | — | Enum of transition kinds, incl. `BOUNCE` / `JELLY_BOUNCE` (D-05) |
| `ui/components/Shimmer.kt` | — | Loading shimmer, the one infinite animation in a control |

| Metric | Count |
|---|---|
| `kroxTween(` call sites | **66** |
| Files declaring a hand-rolled `FiniteAnimationSpec<` | **3** |
| `rememberInfiniteTransition` sites | **4** |

**This is the app's best-adopted token family.** `KroxMotion` / `kroxTween` have
66 call sites against `KroxSpacing`'s sparse adoption (`docs/audit/THEME.md`).
Phase 5's motion work is therefore not "adopt tokens" — it is "do not disturb
what already works."

## `AnimationUtils.kt` — the motion tokens

| Symbol | Line | Spec |
|---|---|---|
| `KroxMotion` | 38 | `INSTANT 80` / `FAST 120` / `BASE 180` / `SLOW 240` / `PAGE 220` |
| `KroxEaseOut` | 56 | `CubicBezierEasing(0f, 0f, 0.2f, 1f)` — Material 3 standard curve |
| `KroxPageEasing` | 59 | Page-transition curve |
| `fun <E> kroxTween(durationMillis: Int)` | 68 | The factory every call site uses |
| `getAnimateSpeed()` | 76 | Reads the user's animation-speed setting |
| `getAdjustedDelayMillis` | 85 | Speed-scaled delay |
| `isSwapAnimateClosed()` | 97 | Whether the user disabled swap animation |
| `getAnimateTween` | 99 / 106 | Two overloads |
| `getAnimateTweenBounce` | 114 / 121 | **D-05** |
| `getAnimateTweenJellyBounce` | 130 / 137 | **D-05** |
| `getSwapAnimateTween` | 149 | Swap transition spec |
| `getTargetValueByAmplitude` | 174 | Amplitude scaling helper |
| `swapAnimateDpAsState` | 196 / 217 | Two overloads |
| `calculateAnimationTime(speed, baseTime, minFactor = 0.25f)` | 243 | Speed clamp — never below 25% of base |

`KroxMotion.PAGE = 220` sits between `BASE 180` and `SLOW 240`. It is a distinct
token, not a duplicate; do not collapse the two.

`calculateAnimationTime`'s `minFactor = 0.25f` is a **floor**, not a
multiplier cap. A user who sets the slowest animation speed still sees 25% of
the base duration. This is what keeps motion perceptible at every setting, and
it is why §7.6's "no spring/bounce" rule does not mean "motion may be disabled
outright."

## The one place §7.6 is knowingly violated — D-05 / R-05

§7.6 forbids spring, bounce, and elastic easing. Three artifacts contradict it,
all of them pre-existing and all of them recorded rather than fixed:

| Artifact | Line | What it is |
|---|---|---|
| `getAnimateTweenBounce` | `AnimationUtils.kt:114` / `:121` | A bounce-eased tween factory |
| `getAnimateTweenJellyBounce` | `AnimationUtils.kt:130` / `:137` | A jelly-bounce tween factory |
| `bounce(x)` curve | `_Easing.kt:39-46` | The `bounce` easing function itself |
| `TransitionAnimationType.BOUNCE` | `TransitionAnimationType.kt:27` | Enum entry selecting it |
| `TransitionAnimationType.JELLY_BOUNCE` | `TransitionAnimationType.kt:29` | Enum entry selecting it |

These are **reachable** — the enum entries are a user-selectable transition
setting. Removing them would delete a shipped user preference and violate §1.3
("if it has it, the redesign must not remove it"). Changing them to a compliant
curve would be a visible motion change, which is a restyle of a *behaviour*
rather than of presentation.

**Phase 5 must preserve all five byte-identical.** This is a standing owner
decision (R-05), not an oversight. If the owner later rules for conformance,
it is a separate change with its own regression pass — see `docs/REGRESSIONS.md`.

Note the contrast with `ui/components/Buttons.kt:103`, which carries a Chinese
comment reading *"不得使用 spring / bounce —— 见 docs/spec/master-spec.md §7.6"*
("must not use spring / bounce — see §7.6"). **The app's own newest component
already complies.** The bounce paths are legacy, not a live pattern. New Phase 5
motion follows the comment's rule; the legacy paths stay.

`AnimationUtils.kt:64-65` also carries a comment recording that the no-argument
`fadeIn()` / `slideInVertically()` overloads resolve to a `spring()` internally
and therefore violate §7.6. **Always pass `kroxTween(...)` or an explicit
`animationSpec`** rather than relying on the no-arg overload.

## The three hand-rolled specs

Exactly three files declare their own `FiniteAnimationSpec<`. Everything else
goes through `kroxTween` or a `getAnimateTween*` overload.

| File | Specs | Verdict |
|---|---|---|
| `BuiltInFileManager.kt:118-128` | `SidebarSlideEnterSpec` :118, `SidebarSlideExitSpec` :120, `SectionExpandSpec` :122, `SectionShrinkSpec` :124, `SectionFadeInSpec` :126, `SectionFadeOutSpec` :128 | **KEEP byte-identical** |
| `ui/screens/_Navigation.kt` | The screen transition spec | **KEEP** |
| `utils/animation/AnimationUtils.kt` | The token definitions | **KEEP** |

**The six `BuiltInFileManager.kt` specs are the live sidebar's motion.** Phase 5
restyles that file's presentation (`docs/audit/SIDEBAR.md`), and the temptation
is to "harmonize" these six with `KroxMotion`. Do not. They are already
§7.6-compliant (`tween` + `CubicBezierEasing`, no spring), they are the
animation vocabulary the sidebar's enter/exit and section-collapse behaviour is
written against, and swapping them changes when the sidebar finishes moving —
which is motion *behaviour*, not style.

**If a new sidebar animation is needed, it reuses one of these six or a
`KroxMotion` token. It does not add a seventh spec.**

## The four infinite animations

`rememberInfiniteTransition` appears in exactly four places. All four are
decorative or ambient, and all four are on a repeating loop by design.

| Site | Role | Verdict |
|---|---|---|
| `ui/theme/feativals/TitleTexts.kt:52` | Seasonal festival title animation | **KEEP** |
| `ui/screens/content/elements/AccountElements.kt:268` | Account "Chroma" effect | **KEEP** |
| `ui/control/mouse/HotspotEditor.kt:188` | Control-editor hotspot pulse | **KEEP** |
| `ui/components/Shimmer.kt:42` | Loading shimmer | **KEEP** — this is the §8.16 loading state's motion |

**`Shimmer.kt` is load-bearing for §8.16.** The loading state is not a spinner;
it is a shimmer sweep, and Phase 5's loading treatment inherits it rather than
replacing it. The loading **appearance** (colors, radius, speed) is Phase 5
work; the mechanism is not.

None of the four is a `spring`. None violates §7.6.

## §12.4 motion rules — Phase 5's obligations

| Rule | Code reality | Phase 5 action |
|---|---|---|
| Consistent durations via tokens | 66 `kroxTween(` sites | **Met** — do not reintroduce literals |
| Easing from the approved set | `KroxEaseOut` / `KroxPageEasing` | **Met** — new motion uses these |
| No spring / bounce / elastic | Violated in 5 legacy spots (D-05) | **Preserve**; never add a sixth |
| Respect the user's speed setting | `getAnimateSpeed()` + `calculateAnimationTime` | **Met** — never hardcode a duration |
| Transitions are interruptible | Compose default | **Met** |

The last row of that table is the one most easily broken by a redesign: a
`kroxTween` that ignores `getAnimateSpeed()` is a hardcoded duration, and it
would make the app feel faster for users who asked it to be slower. **Every new
Phase 5 animation goes through `kroxTween` or a `getAnimateTween*` overload**,
never a bare `tween(200)`.

## §7.3 elevation — a Phase 5 addition, not an existing concern

§7.3 allows exactly two elevation levels (Base: none; Raised: the specified
two-part shadow). **There is no elevation or shadow token in `ui/theme/`**
(`docs/audit/THEME.md`).

That means elevation — and therefore the visual lift a Phase 5 card or panel
gets — is **new work**, and it lands as exactly two levels, not a scale. It is
recorded here because an elevation change is almost always paired with a motion
change: a card that lifts should do so with a `kroxTween(KroxMotion.FAST)`
over the two permitted shadows. **Two levels, one duration token, no shadow
stacking.**

## §19.4 position

| Guarantee | Status |
|---|---|
| Not touching the config schema | **Held** — no animation setting is added, removed, renamed, or re-typed. `TransitionAnimationType` keeps all its entries; `getAnimateSpeed()` keeps reading the existing setting. |
| Not changing what a control does | **Held** — motion is presentation. The `HotspotEditor` pulse and the account Chroma effect animate the same events they animated before. |
| Not changing when a transition completes | **Held** — the six `BuiltInFileManager` specs and `_Navigation.kt`'s spec are byte-identical. |

## Classification

| Class | Items |
|---|---|
| **KEEP** | `KroxMotion`'s 5 values; `KroxEaseOut`; `KroxPageEasing`; `kroxTween`; `getAnimateSpeed`; `getAdjustedDelayMillis`; `isSwapAnimateClosed`; `getSwapAnimateTween`; `getTargetValueByAmplitude`; `swapAnimateDpAsState`; `calculateAnimationTime` and its `minFactor = 0.25f`; all six `BuiltInFileManager.kt:118-128` specs; `_Navigation.kt`'s spec; the four `rememberInfiniteTransition` sites; `Shimmer.kt`'s sweep mechanism; the `Buttons.kt:103` §7.6 comment. |
| **REDESIGN** | The **appearance** of motion-adjacent surfaces: shimmer colors and radius, slide durations *only if* they already route through a token, and the pairing of new §7.3 elevation with `KroxMotion.FAST`. |
| **FIX** | **D-05 recorded, deliberately not fixed** — `getAnimateTweenBounce`, `getAnimateTweenJellyBounce`, `bounce(x)`, `TransitionAnimationType.BOUNCE`, `TransitionAnimationType.JELLY_BOUNCE` stay. Owner decision R-05 outstanding. |

## Notes

- **`Buttons.kt:103` is the app's own §7.6 enforcement comment.** It is the
  pattern to copy when writing new Phase 5 motion: an explicit spec argument,
  never the no-arg `fadeIn()` / `slideInVertically()` overloads, which resolve
  to `spring()` (`AnimationUtils.kt:64-65`).
- **Do not "harmonize" the six file-manager specs into `KroxMotion`.** They are
  compliant already. Changing them alters when the sidebar stops moving, which
  is behaviour.
- `KroxMotion.PAGE = 220` is not a duplicate of `BASE 180` or `SLOW 240`. It is
  the page-transition duration; collapsing the three would change either page
  transitions or general transitions to fix the other.
- `calculateAnimationTime` clamps with `minFactor = 0.25f`. That floor is what
  keeps motion visible at the slowest user setting; do not lower it while
  "tuning" speed.
- The four `rememberInfiniteTransition` sites are the complete set. A Phase 5
  addition of a fifth needs a reason beyond "it would look nice" — an infinite
  loop runs forever and is the most expensive motion in the app to leave
  running.
- All conclusions here are unverified by a compiler until R-01 clears — see the
  verification-status section in `docs/REGRESSIONS.md`.
