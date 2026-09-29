# KROX Design Tokens (Phase 1)

Frozen source of truth for the redesign. Code that contradicts this file is a bug, not a
preference. Every value here comes from the master spec §3; nothing was invented.

## 1. Where each token lives in code

| Token group | Implementation | Reaches |
|---|---|---|
| Colour roles | `ui/theme/Color.kt` + `ui/theme/Theme.kt` | `MaterialTheme.colorScheme.*` |
| Semantic colour accessors | `ui/theme/Palette.kt` | 79k LOC, unchanged |
| Typography | `ui/theme/Type.kt` → `AppTypography` | `MaterialTheme.typography.*` (479 refs) |
| Shape/radius | `ui/theme/Type.kt` → `KroxShapes` | `MaterialTheme.shapes.*` (201 refs) |
| Spacing | `ui/theme/Palette.kt` → `KroxSpacing` | new call sites only |
| Motion | `utils/animation/AnimationUtils.kt` | existing `getAnimateTween*` helpers |

`MaterialExpressiveTheme` (`Theme.kt:756-763`) is the single injection point. Adding
`shapes = KroxShapes` there re-points all 201 `MaterialTheme.shapes` references at once, with
zero per-call-site edits.

## 2. Colour

| Token | Value | Note |
|---|---|---|
| bg | `#08080A` | already `backgroundDark` (`Color.kt:75`) |
| surface | `#121216` | already `surfaceDark` (`:77`) |
| elevated | `#24242C` | already `surfaceVariantDark` (`:79`) |
| accent | `#DC2626` | already `primaryDark` (`:59`) |
| accent hover | `#E73030` | new — hover only |
| accent pressed | `#C11F1F` | new — pressed only |
| success | `#10B981` | new |
| warning | `#EAB308` | new |
| danger | `#EF4444` | distinct from accent on purpose |
| disabled | `#374151` | new — see §5 |
| text-primary | `#F9FAFB` | new |
| text-secondary | `#9CA3AF` | new |
| text-muted | `#6B7280` | new |
| border-hairline | `rgba(255,255,255,0.06)` | new |
| border-divider | `rgba(255,255,255,0.04)` | new |
| border-accent | `rgba(220,38,38,0.60)` | new |
| overlay | `rgba(0,0,0,0.55)` | new |

Four tokens were already correct in the dark scheme; the rest did not exist and are added in
`Palette.kt` as named constants so call sites read as intent, not as raw hex.

## 3. Spacing

`0, 4, 8, 12, 16, 20, 24, 32, 40, 48, 64` (dp).

`KroxSpacing` exposes them. Existing literal `dp` values are **not** mass-rewritten — that
would be a 79k-LOC diff with no visual payoff. New and rewritten code uses the scale; old code
keeps its literals until it is touched for other reasons.

## 4. Radius

| Token | Value | M3 `Shapes` slot |
|---|---|---|
| xs | 4dp | `extraSmall` |
| sm | 6dp | `small` |
| md | 8dp | `medium` |
| lg | 12dp | `large` |
| xl | 16dp | `extraLarge` |
| full | 999dp | — (pill, used ad hoc) |

201 call sites read `MaterialTheme.shapes.*`; mapping the 5 radius steps onto the 5 M3 slots
converts them all with one line of wiring. `largeIncreased` / `extraLargeIncreased` /
`extraExtraLarge` are left at M3 defaults — no call site uses them.

## 5. Typography

Platform default family (user decision — `res/font/` does not exist, so there is no bundled
typeface to use; the spec's Inter/Manrope is a deviation, recorded here deliberately).

Ten spec styles, mapped onto the M3 slots actually in use:

| Spec style | Size/Weight/LineHeight | M3 slot | live refs |
|---|---|---|---|
| display | 28sp/600/36 | `displayMedium` | 0 |
| h1 | 22sp/600/30 | `headlineSmall` | 2 |
| h2 | 16sp/600/24 | `titleMedium` | 48 |
| h3 | 14sp/600/20 | `titleSmall` | 56 |
| body | 13sp/400/20 | `bodyMedium` | 51 |
| body-strong | 13sp/600/20 | `bodyMedium` (weight only) | — |
| caption | 12sp/400/16 | `bodySmall` | 71 |
| meta | 11sp/500/14 | `labelMedium` | 94 |
| overline | 10sp/600/14 | `labelSmall` | 103 |
| mono | 12sp/400/18 | `labelLarge` | 29 |

`bodyStrong` is exposed as a named val in `Type.kt`; it has no distinct M3 slot and therefore
no independent reference count.

`DisabledAlpha = 0.38f` (`ui/screens/content/elements/SideBar.kt` consumers) is **kept** — it is
the existing disabled treatment, and §3 does not ask for a different one.

## 6. Motion

| Token | Value | Easing |
|---|---|---|
| instant | 80ms | linear |
| fast | 120ms | ease-out |
| base | 180ms | ease-out |
| slow | 240ms | ease-out |
| page | 220ms | `cubic-bezier(0.2,0,0,1)` |

Hard rule: **nothing above 240ms**, and no bounce / elastic / spring.

### 6.1 Existing bounce is user-selected, and stays

`utils/animation/AnimationUtils.kt:108-124` exposes `TransitionAnimationType.BOUNCE` and
`JELLY_BOUNCE` behind the pre-existing `launcherSwapAnimateType` setting. This is a **user
setting that already shipped**. §3.6 bans the *redesign* from introducing bounce; deleting a
shipped setting would violate the one-line rule ("if it has it, the redesign must not remove
it"). So the enforcement is directional, not absolute:

- The redesign **never writes** a new bounce/spring/elastic.
- The existing user-selectable option is left intact and left in QA scope as a known
  deliberate divergence.

**This is the one place the implementation knowingly diverges from a literal reading of §3.6,
and it is a scope decision, not an oversight.** Recorded in `docs/audit/RISKS.md`.

### 6.2 Existing violations found (audit)

| File:line | Value | Verdict |
|---|---|---|
| `ui/components/_SimpleRail.kt:85` | 300ms `FastOutSlowInEasing` | **fixed** → `KroxMotion.BASE` (180ms) + `KroxEaseOut` |
| `ui/components/_SimpleRail.kt:77` | default padding vertical `8.dp` | **fixed** → `6.dp` (1 live caller). Not "12dp" as first written — see note below |
| `ui/components/_SimpleRail.kt:79` | `shapes.extraLarge` default | **left alone** — 0 callers pass `shape`, so the default never executes |
| `ui/components/_SimpleRail.kt:118-152` | 5 × `animateDpAsState`/`animateColorAsState` with no `animationSpec` | **fixed** → `KroxMotion.FAST` (120ms) + `KroxEaseOut` |
| `ui/components/RecordingPlayerOverlay.kt:294,545` | 300ms, 250ms fade | out of scope — pre-existing overlay, not a redesigned surface |
| `ui/components/Shimmer.kt:49` | 1000ms | **keep** — a shimmer's period, not a transition |
| `ui/screens/content/elements/AccountElements.kt:273` | 10000ms | **keep** — infinite loop animation, not a transition |
| `ui/screens/content/BuiltInFileManager.kt:118,120,122` | 320 / 280 / 260ms `FastOutSlowInEasing` | **fixed** → `KroxMotion.SLOW` (240ms) + `KroxEaseOut` |
| `ui/screens/content/BuiltInFileManager.kt:124,745,749,1206` | 220 / 200 / 180 / 240ms `FastOutSlowInEasing` | **fixed** → durations kept, easing → `KroxEaseOut` (§3.6 permits only linear / ease-out / page curve) |
| `ui/control/Hotbar.kt:185` | `tween(800)` on the resize settle | **fixed** → `KroxMotion.SLOW` + `KroxEaseOut` |
| `ui/screens/content/download/assets/elements/_Search.Filter.kt:696,704` | 200ms `tween` | **fixed** → `KroxMotion.FAST` + `KroxEaseOut` |
| `ui/screens/content/AccountManageScreen.kt:505` | bare `spring()` on drag-lift elevation | **fixed** → `KroxMotion.FAST` + `KroxEaseOut`; `spring` import dropped |
| `ui/screens/content/elements/SideBar.kt:99,163,165,247,268,307` | `spring(DampingRatioMediumBouncy)`, `tween(250)` | dead code (R-04) — retime only if the file is revived |
| `utils/animation/AnimationUtils.kt:64-68` | `calculateAnimationTime(…, 1500, 0.1f)` | §3.6 conflict, see below and R-05 |

Remaining violations are exactly the two documented carve-outs: `SideBar.kt` (0 references,
R-04) and `RecordingPlayerOverlay.kt` (pre-existing, out of scope). Both are listed rather
than hidden, so a reviewer can disagree with the call.

`TextRailItem` has exactly one live call site —
`ui/screens/content/elements/VersionsManageElements.kt:330` (`VersionCategoryItem`). It
passes `shape` but not the padding params, so the padding default runs and the shape default
does not. Both defaults were re-checked against that call site rather than assumed; a fix
applied to a default that never executes would be a no-op dressed as work.

The `shapes.extraLarge` default at `:79` is nonetheless *value*-dead, not
call-dead-afterwards: once `Theme.kt` injects `KroxShapes` (§1), `extraLarge` resolves to
16dp instead of the M3 default. That is the intended xl radius, and it remains unreachable.

`getAnimateSpeed()` derives its duration from a user speed slider with a 1500ms base. At the
default setting that exceeds the 240ms ceiling. Re-clamping it would silently change how every
existing animation feels, which is behaviour, not design. Left alone; flagged for the owner
as a §3.6 trade-off to accept or reject.

## 7. Elevation

Two levels only (spec §3). Material3 `CardDefaults.cardElevation` defaults to 0dp, and the
launcher already draws its own depth through surface colour, so no elevation change is needed
to satisfy the rule. Where elevation is used it is limited to 0dp and 2dp.

## 8. How to verify

```bash
# No transition over 240ms (expect only Shimmer/AccountElements loops, per §6.2)
grep -rn "durationMillis = [3-9][0-9][0-9]\|durationMillis = 1000[0-9]" ZalithLauncher/src/main/java/com/movtery/zalithlauncher/ui/

# Every Shapes consumer picks up the theme automatically
grep -rc "MaterialTheme.shapes" ZalithLauncher/src/main/java/ | grep -v ':0' | wc -l
```

Compile gate: `.github/workflows/launcher-build.yml` (see `docs/audit/RISKS.md` R-01 for the
secrets that currently block it).
