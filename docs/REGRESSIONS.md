# Regression Log — KROX Launcher Redesign

Governed by `docs/spec/master-spec.md` §19 (lines 1166–1205). Step 4 of §19.3
("Log in `docs/REGRESSIONS.md`") is the only thing that file is for.

**This is a divergence log, not an incident log.** No functional regression has been
detected and no revert has been performed. Everything below is a *deliberate* recorded
difference between the spec and the code, or a change made during the redesign that
could not be compile-verified. That distinction matters: §19.3's protocol (identify →
revert → re-verify → log) has not been triggered.

**This file covers the launcher only.** The Fabric mod under `Krox/client` keeps its own
register at [`Krox/client/docs/DEFECTS.md`](../Krox/client/docs/DEFECTS.md). The two do
not share a numbering scheme: `D-10` below is an off-table corner radius in the Compose
theme, while `D-10` in the mod's register is the `Can only blur once per frame` crash.
Both are real and unrelated — cite the file, not the number.

---

## Verification status — read this first

**No entry in this document is backed by a compile.** The CI compile gate is blocked by
R-01 (`docs/audit/RISKS.md`): the two signing passwords cannot be created from this
environment, so `.github/workflows/launcher-build.yml` fails at `build.gradle.kts:26-27`
before it reaches Kotlin compilation. There is also no local Android SDK and no local
Material3 artifacts, and the local JDK is 25 where the build requires 17.

Consequences, stated plainly:

- Every code change recorded below is **unverified by a compiler**.
- **Three** compile breaks introduced during the redesign were caught and repaired by
  inspection rather than by a build (entries R-19, R-39). All three are the same failure:
  a symbol used without the import that supplies it. There is no reason to expect a fourth
  to be any different, and no automated check in this environment that would catch it.
- §19.1 "verify the current build works" and the baseline screenshot capture are
  **blocked**, not skipped. Neither was performed.
- §19.2's post-phase re-verification list has never been executed, because there is no
  runnable artifact to execute it against.

### What has replaced the compiler

Since a build is impossible here, three static checks stand in for it. They are the
only evidence behind any "repaired" claim in this file:

| Check | What it catches | What it cannot catch |
|---|---|---|
| Import-resolution sweep across all touched files | Project symbols used with no import that supplies them — the exact R-19/R-39 failure | Wrong argument order, wrong overload, type mismatch, missing Android framework import |
| `R.string.*` / `R.drawable.*` existence grep | Unresolved resource references | Wrong resource *type* (e.g. a colour used as a drawable) |
| Brace-balance per edited file | A mis-placed or missing brace from a structural edit | Any other structural problem |

**None of these is a compile.** They raise confidence; they do not establish it. R-01
still gates the real answer.

**The first action after R-01 clears is a clean CI build.** If it fails, entries
R-19 and every entry below it are suspect until it passes.

---

## Divergences — spec rule vs. code reality

These are cases where the spec states a rule the code cannot satisfy without a
functional change. The one-line rule (`master-spec.md` §1.3, line 99) resolves all of
them the same way: *presentation-only rules yield to the preservation contract; the
functionality is preserved and the rule is recorded as unmet.*

| # | Spec rule | Reality in code | Resolution | Status |
|---|-----------|-----------------|------------|--------|
| D-01 | §9.2 Play Button — five states including **launching** | `LaunchGame.isLaunching` is a plain field, not Compose state. No `LaunchedEffect`/snapshot state observes it, so a "launching" state cannot be rendered. There is no `launching` string resource. | Omitted. Wiring `isLaunching` into Compose state is behaviour, not presentation. | Accepted |
| D-02 | §9.2 Play Button — **hover +8% / pressed −8%** | Was unmet: `ScalingActionButton` exposed no `interactionSource` and had no colour layer to shift. | **Resolved** in Phase 6 (R-40). It now creates its own `MutableInteractionSource`, observes press + hover, and shifts the container colour ±0.08 through the new `Color.shift()`. The **signature is unchanged** — the `interactionSource` is private, not a new parameter. | **Resolved** — R-40 |
| D-03 | §9.9 About — "Version, build, **date**" | `BuildKeys` is generated and no `buildConfigField` timestamp exists anywhere in the Gradle config. `AboutInfoScreen` has no build-date string resource. | Version and author implemented; **build date not implementable** without adding a build-time field. | Accepted |
| D-04 | §11.1 Responsive **breakpoints** | Zero breakpoint branches exist tree-wide: `grep` for `maxWidth <` / `maxWidth >` / `maxHeight <` / `maxHeight >` / `<=` / `>=` across `ui/` returns **no hits**. | No breakpoint system to preserve. §11 work is purely additive. §11.2 ("never hide existing navigation") is satisfied by construction. | Accepted — see `docs/audit/RESPONSIVE.md` |
| D-05 | §7.6 "no animation longer than 240 ms" | Two **shipped user settings** violate it: `AllSettings.launcherSwapAnimateType` exposes `BOUNCE` / `JELLY_BOUNCE`, and `getAnimateSpeed()` derives durations from a 1500 ms ceiling. | Preserved. Removing a shipped setting is a functional removal, forbidden by §1.3. See R-05 in `RISKS.md`. | **Owner decision outstanding** |
| D-06 | §9.4 Changelog screen | **No changelog screen exists.** `find -iname "*changelog*"` is empty; `grep -rn "changelog" ui/` is empty. | Recorded. §9.4 and §19.2's "re-verify changelog renders" both assume a screen that is not in the codebase. The audit does not invent one. | Accepted — see `docs/audit/CHANGELOG.md` |
| D-07 | §9.5 Logs — states "list, empty, viewer" | Only the **viewer** exists. `LogViewScreen.kt` (102 lines) is viewer-only. There is no log *list* screen. Entry points are `MainActivity.kt:296` and `:444`, which name a log file directly. | Recorded. The "list" and "empty" states have no host surface. | Accepted — see `docs/audit/LOGS.md` |
| D-08 | §9.2 / §7.7 spacing scale | `56.dp` and `1.sp` are off-scale literals, but §8.1 and §9.2 **mandate those exact values** for the Play button. | Kept literal. The spec contradicts its own scale; the specific rule wins. | Accepted |
| D-09 | §8.4 Page header — overline + H1 | No shared `PageHeader` component existed. The only `Header` symbol in the codebase was `private fun <E> MenuListHeader(` at `ui/components/Menu.kt:479`, a list-section header. `KroxOverline` (`Type.kt:65`) and `KroxH1` (`Type.kt:51`) were used on **none** of the §9.7/§9.8/§9.9 pages. | `ui/components/PageHeader.kt` created and applied to all 13 §9.7/§9.8/§9.9 pages. Every screen sources its title from its existing nav key's `.title`, so no string resource was added and `BaseScreen`'s signature is unchanged. | **Resolved** — R-37 |
| D-10 | §4 Radius scale (xs 4 / sm 6 / md 8) | `KroxShapes.large = 12.dp` and `extraLarge = 16.dp` (`ui/theme/Type.kt:88-94`) exceed the documented table. | Code is the source of truth; `tokens.md` §4 table is incomplete. Doc gap recorded rather than changing shape values, which would alter every existing card's corner. | Accepted — see `THEME.md` |
| D-11 | §3 Spacing — "new and rewritten code uses the scale" | The long tail of off-scale `dp` literals across `ui/` is not tokenized. | Deliberate. `docs/spec/tokens.md` §3 explicitly forbids mass rewriting: *"that would be a 79k-LOC diff with no visual payoff."* | By design |
| D-12 | §8.15 / §8.17 state components | `ScalingLabel`'s 3 overloads (`ui/components/Layouts.kt:74`/`:104`/`:127`) lost **every** caller to `StateLabel` in Phase 6. Grep: 0 external references. | **Kept, not deleted.** It is public API in an untracked directory with no revert target — the same reasoning as R-04. Nothing renders it, so it costs nothing but 3 dead declarations. | **Owner decision** |

---

## Changes made during the redesign that are not compile-verified

Each of these is a real edit to a real file. Any of them could be a compile break for the
same reason the two in R-19 were.

| # | File | Change | Risk |
|---|------|--------|------|
| R-19 | `ui/screens/content/settings/SettingsScreen.kt`, `ui/screens/content/elements/AboutDialog.kt` | **Two compile breaks were introduced and repaired.** A §7.7 divider edit added a `Color.White.copy(alpha = 0.04f)` reference; neither file imported `androidx.compose.ui.graphics.Color`. A wildcard `material3.*` import does **not** cover it. Both repaired. | **Repaired, unverified.** This is the failure mode to watch for in any future divider/token edit. |
| R-20 | `ui/components/BackgroundCard.kt` | `elevation` is forwarded to `Card`; border falls back to the §7.7 hairline `1px rgba(255,255,255,0.06)`. | Low — signature unchanged. |
| R-21 | `ui/screens/content/BuiltInFileManager.kt` | Divider → `rgba(255,255,255,0.04)`; `LinearProgressIndicator` track → `Color.White.copy(alpha = 0.12f)`. | Low — token swap only. |
| R-22 | `ui/screens/content/elements/SideBar.kt` | 5 × `DampingRatioMediumBouncy` springs and `tween(250/150/200/100)` → `kroxTween`. | **Low value.** The file is confirmed **0-reference** (R-04). Editing dead code is churn. |
| R-23 | `ui/components/_Chips.kt`, `ui/screens/content/GameBall.kt`, `ui/components/RecordingPlayerOverlay.kt` | 1 × `motionScheme.fastSpatialSpec()` and bare `expandIn`/`shrinkOut`/`scaleIn` given explicit `kroxTween` specs. | Low. |
| R-24 | `ui/components/Menu.kt` (×3), `…/OpenFolder.kt` (×1) | Ungated `getAnimateTweenJellyBounce()` call sites left intact (see D-05) — **no change**, recorded so the omission is deliberate and visible. | None. |
| R-25 | `ui/theme/Theme.kt:644`, `ui/theme/Color.kt:82` | `kroxDark.outlineVariant = Color(0xFF4A3B3A)` **deliberately not rewritten.** It doubles as the §7.6 progress-track color; changing it would restyle every track. | None — recorded as a decision, not a change. |
| R-26 | `ui/theme/Palette.kt` `KroxSpacing` | Token object created (11 steps: `none` 0 → `max` 64). Call sites: the two About surfaces use it. The rest of `ui/` does not. | None — additive. |
| R-27 | `ui/screens/content/elements/CommonElements.kt:113` | `DisabledAlpha = 0.38f`. 13 call sites across 8 files. | None — extracted constant, no value changed. |
| R-28 | `ui/screens/content/AccountManageScreen.kt:505-509` | A drag `animateDpAsState` animating 0→6 dp sits **outside** §7.3's two-level elevation rule. | None. Existing behaviour, not touched. |
| R-29 | `ui/screens/content/elements/AccountElements.kt:273`, `…/HotspotEditor.kt:193` | `tween(10000)` and `tween(1000)` left as `infiniteRepeatable` ambient loops. §7.6 governs discrete transitions, not ambient loops. | None — recorded so the long duration is not mistaken for a violation. |
| R-30 | `ui/theme/KroxWidthClass.kt` (new) | §11.1 four-tier width class, `LocalKroxWidthClass`, `rememberKroxWidthClass()`, `kroxGutter()`. Five call sites: `LauncherScreen.kt:168`/`:199`, `PlayTimeStatsScreen.kt:84`, `GameStatsScreen.kt:83`. | Low. `LocalConfiguration.screenWidthDp` is API 13 and minSdk is 26, so no version guard. `Compact` and `Medium` both return `KroxSpacing.xxl` (24 dp), which is the **pre-existing literal at every one of the five sites** — rendering below 900 dp is provably byte-identical to before. |
| R-31 | `ui/screens/content/LauncherScreen.kt:285` | `PlayTimeStatsCard` gained `onClick: () -> Unit` and `BackgroundCard(modifier = Modifier.clickable(role = Role.Button, onClick = onClick))`. Threaded through `ContentMenu(onPlayTimeStatsScreen = …)` → `navigateTo(NormalNavKey.PlayTimeStats)`. | **Behaviour added** (D-08). `androidx.compose.foundation.clickable` added at `:22`; `Role` was already imported at `:67`. Both nav keys were already declared and already rendered in `MainScreen.kt:723`/`:728` — no screen, no string key, and no `AllSettings` entry was added. |
| R-32 | `ui/screens/content/PlayTimeStatsScreen.kt:130` | `.clickable { navigateTo(NormalNavKey.GameStats) }` on the existing "most played" `StatCard`. `StatCard` already accepted a `modifier` applied to its `BackgroundCard`, so no signature change. `androidx.compose.foundation.clickable` added at `:21`. | **Behaviour added** (D-08, second target). Reused `stats_most_played`, an existing string — no new string key, no new translation. |
| R-33 | `ui/theme/KroxWidthClass.kt` | The §11.1 "content max-width ~960 dp" cap is **deliberately not implemented.** `BaseScreen` is the root `fillMaxSize()` `Box` of 52 screens; capping there would shrink every screen's scroll and click surface and leave an unstyled band down both sides. `contentMaxWidth`, `contentCentered`, and `kroxContentWidth()` were written, then **deleted** rather than shipped. | None — removal. §10.3 offers capped-centered **or** full-width-with-generous-padding and says to pick one; Phase 5 picked the second, so exactly one strategy exists in the codebase. Rationale in the enum's KDoc and in `docs/audit/RESPONSIVE.md`. |
| R-34 | `ui/theme/Theme.kt:759` | One comment word: `内容最大宽度` was stale after R-33 dropped the cap. Now reads `只加宽外边距`. | None — comment only. |
| R-35 | `ui/screens/content/GameStatsScreen.kt` | §8.15 empty state. The branch that renders when `stats.all { it.totalMs == 0L }` went from a single centred `Text` to a `Box(fillMaxSize, contentAlignment = Center)` wrapping a centred `Column` with a 48 dp `ic_dashboard_outlined` `Icon` at `onSurfaceVariant.copy(alpha = 0.35f)`, the existing `stats_no_data` string as `titleSmall`, and a new `stats_no_data_desc` as `bodySmall` at 0.6 alpha. Imports added: `material3.Icon`, `material3.LinearProgressIndicator`, `material3.MaterialTheme`, `material3.Text`, `runtime.Composable`, `runtime.remember`, `ui.Alignment`, `ui.Modifier`, `ui.platform.LocalContext`, `ui.res.painterResource`, `ui.res.stringResource`, `ui.unit.dp`. | Low–medium. Every added symbol was grep-verified present, and every referenced drawable/string key was confirmed to exist. The risk profile matches **R-19** exactly: `material3.*` and `ui.res.*` do not come from the wildcard imports, so an unresolved reference is the failure mode to watch for. `maxMs`, the zero-guard, `sortedByDescending`, and `itemsIndexed` keying are byte-identical. No statistic added or relabelled; no `AllSettings` entry. |
| R-36 | `ui/screens/content/AccountManageScreen.kt` | §8.15 empty state. The no-accounts branch went from a `Box(fillMaxSize)` centring one `ScalingLabel` to the same centred `Column` shape as R-35, with `ic_person_outlined` in place of `ic_dashboard_outlined`, the existing `account_no_account` string as `titleSmall`, and a new `account_no_account_desc` as `bodySmall` at 0.6 alpha. The now-unused `import com.movtery.zalithlauncher.ui.components.ScalingLabel` was removed (grep-confirmed 0 remaining references). Literal `8.dp` used instead of `KroxSpacing.sm` to match the file's existing convention — it has 0 `KroxSpacing` occurrences and 0 `ui.theme` imports. | Low–medium, same R-19 profile. The auth overlays (`MicrosoftLoginOperation`, `LocalLoginOperation`, `OtherLoginOperation`) and the "Add Account" `ScalingActionButton` at `:398-409` are untouched. **No action button was added to the empty state** — §8.15 allows one only if an existing action is relevant, and the left-panel one already covers it; duplicating it would put the same login path on screen twice (§9.7 prohibition 1, §1.2). |

| R-37 | `ui/components/PageHeader.kt` (new) + 13 call sites | §8.4 "overline + H1" page header (D-09). New file hosts `KroxOverline` + `headlineSmall` (mapped to `KroxH1` at `Type.kt:71`) + optional `actions` row + optional subtitle, spaced by `KroxSpacing`. Applied at `GameStatsScreen.kt:93`, `PlayTimeStatsScreen.kt:90`, and the 11 settings screens (`LauncherSettingsScreen.kt:173`, `GameSettingsScreen.kt:90`, `RendererSettingsScreen.kt:166`, `ControlSettingsScreen.kt:126`, `JavaManageScreen.kt:132`, `TurnipDriversScreen.kt:197`, `ControlManageScreen.kt:274`, `GamepadSettingsScreen.kt:179`, `AboutInfoScreen.kt:103`). Three placement variants, chosen per existing container: direct sibling before `AnimatedItem(scope)`; `item { }` inside `AnimatedLazyColumn`; new `Column(Modifier.fillMaxSize())` root with the `fillMaxSize()` child demoted to `fillMaxWidth().weight(1f)` in the 3 non-scrolling roots. Every title is sourced from the screen's existing `NestedNavKey.Settings` / `NormalNavKey` `.title`. | Low–medium, R-19/R-35 risk profile. **No string resource added, no `AllSettings` entry touched, no nav key declared, `BaseScreen`'s signature unchanged.** The `weight(1f)` demotion is the one behavioural edit: without it the header would push the body off-screen in the 3 non-scrolling roots. |

| R-38 | `ui/screens/content/GameStatsScreen.kt`, `ui/screens/content/AccountManageScreen.kt` | §11.1 width bound on the two new §8.15 empty states. Both `Column`s gained `Modifier.widthIn(max = 320.dp)`. `docs/audit/RESPONSIVE.md:156` prescribes exactly this shape for Phase-5 empty states — *"Use a `Column` with a bounded `widthIn(max = …)`, not a `Row`"* — because the supporting copy must wrap at narrow widths rather than clip. R-35 and R-36 shipped the unbounded `Column`, which is the same gap RESPONSIVE.md names. `androidx.compose.foundation.layout.widthIn` imported in both files. | Low. One modifier on a `Column` that is already centred; a 320 dp bound is below every width tier's usable width, so it only takes effect once the copy would otherwise run edge to edge. Neither file's `Box`, `Icon`, `Text`, or spacing changed. |
| R-39 | `ui/screens/content/GameStatsScreen.kt:93-96` | **A third compile break was introduced and repaired** (R-19 class). R-37's header rollout left `androidText(R.string.stats_game_stats)` on `:94` with **no `import com.movtery.zalithlauncher.ui.androidText`** — a top-level function in `ui/AndroidStringText.kt:103`, and this file has no wildcard import to cover it. Found by sweeping all 13 `PageHeader` call sites; `GameStatsScreen` was the only one missing it. **Also fixed in the same edit:** overline and title were both `R.string.stats_game_stats`, rendering the same words twice. The overline was dropped entirely — `stats_game_stats` and `NormalNavKey.GameStats.title` are the *same* string, so there is no distinct category to show, and the sibling `PlayTimeStatsScreen.kt:90` already sets no overline. Title now reads `NormalNavKey.GameStats.title!!`. The now-unused `CardTitleLayout` import was removed (grep-confirmed 0 remaining references). | **Repaired, unverified** — same risk profile as R-19. The import was added, then removed again once the overline argument it served was deleted; the file's final state imports neither `androidText` nor `CardTitleLayout`, matching its body. |
| R-40 | `ui/theme/Palette.kt`, `ui/components/Buttons.kt:105-131`, `ui/components/BackgroundCard.kt:105-128`, `ui/components/_SimpleRail.kt:88-138` | Phase 6 **hover/press** states. Two additions to `Palette.kt`: `KroxHoverOverlay = Color(0x0AF9FAFB)` (the §6.4 `rgba(255,255,255,0.04)` overlay, `#F9FAFB` at 4%) and `fun Color.shift(amount: Float)` (per-channel clamp — the §9.2 ±8%). `ScalingActionButton` now builds its own `MutableInteractionSource` and drives `containerColor` through `shift(-0.08f)` on press / `shift(+0.08f)` on hover at `KroxMotion.INSTANT`; `ClickableBackgroundCard` shifts `+0.06f` (§8.13 requires only "perceptible", no offset); `_SimpleRail` draws `KroxHoverOverlay` as a second `Canvas` layered over the selection capsule so hover and selected can coexist. | Low. **No public signature changed** — every `interactionSource` is private, so no call site and no caller had to change. `DisabledAlpha` stays the sole disabled signal, per the plan's freeze. All four animations use existing `KroxMotion` / `KroxEaseOut`; no spring. `shift()` is additive. Resolves D-02. |
| R-41 | `ui/components/Layouts.kt` (`StateLabel`) + 14 call sites across 14 files | Phase 6 **empty / error / loading** states. New `StateLabel(modifier, icon, tint, title, description, onRetry)` in `Layouts.kt` — a centred icon + title + optional description + optional `ScalingActionButton`, per §8.15/§8.17. It **replaces** the old `ScalingLabel` at every empty/error site: 14 empty states (9 files), 5 error states (4 files), and `CapeGalleryScreen`'s 3 branches. `CapeGalleryScreen`'s loading branch gained the §8.16 label, reusing the already-present-but-unused `account_capes_labynet_loading`. | Low–medium, R-19 risk profile. **Zero new string resources** — every site reuses an existing key, so nothing was added across the 18 locale folders. `onRetry` is passed only where a reload handler already existed; `CapeGalleryScreen`'s error branch omits it because no retry handler exists there (§8.17 allows retry only if the underlying system supports it). `ScalingLabel` is **left in place** and is now **dead**: a tree-wide grep returns only its 3 declarations and 1 self-recursive call inside `Layouts.kt`, 0 external callers. Same reasoning as R-04 (`SideBar.kt`) — `ZalithLauncher/` is untracked, so deleting public API has no revert target. **Owner decision**; see D-12. Three latent `Box` bugs were fixed in passing — `DownloadAssetsScreen.kt:493`, `SearchIdScreen.kt:325`, `_Search.Result.kt:201` used `Box(padding)` with no `fillMaxSize()`, so `contentAlignment = Center` would have centred in a zero-height box. |
| R-42 | `ui/screens/content/BuiltInFileManager.kt:1242-1297` (`SidebarNavItem`) | Phase 6 **hover** state for the launcher's one live sidebar item. Added `MutableInteractionSource` + `collectIsHoveredAsState`, threaded the source into the existing `combinedClickable`, and layered a second `background(KroxHoverOverlay)` over the selected background at `KroxMotion.FAST` (120 ms — §6.5's 120 ms for background). The two pre-existing `tween(220)` literals were folded into `KroxMotion.PAGE` (220, identical value, inside the §7.6 240 ms cap). | Low. **Signature unchanged and no call site changed** — `SidebarNavItem` is `private`, and all 4 call sites (`:393`, `:408`, `:417`, `:436`) pass only `icon`/`label`/`selected`/`onClick`. Three imports added (`MutableInteractionSource`, `collectIsHoveredAsState`, `KroxHoverOverlay`) — the `KroxMotion` import already existed at `:102`, so R-19's risk profile is not incurred. `KroxHoverOverlay` is the R-40 constant, reused rather than re-declared. Selection and accent-bar behaviour are untouched, so §6.4's Selected and Disabled rows still hold, and `DisabledAlpha` remains the sole disabled signal. |

---

## §19.1 / §19.2 checklists — current state

| Step | Status |
|------|--------|
| §19.1 Verify the current build works | **BLOCKED** — R-01 |
| §19.1 Verify the current launcher launches | **BLOCKED** — no artifact |
| §19.1 Verify every existing destination opens | **BLOCKED** — no artifact |
| §19.1 Capture baseline screenshots | **BLOCKED** — no artifact |
| §19.2 Re-verify every destination opens | **NOT STARTED** — no artifact |
| §19.2 Re-verify Play works | **NOT STARTED** — no artifact |
| §19.2 Re-verify account selection works | **NOT STARTED** — no artifact |
| §19.2 Re-verify settings work | **NOT STARTED** — no artifact |
| §19.2 Re-verify file manager works | **NOT STARTED** — no artifact |
| §19.2 Re-verify logs open | **NOT STARTED** — no artifact |
| §19.2 Re-verify statistics render | **NOT STARTED** — no artifact |
| §19.2 Re-verify changelog renders | **NOT STARTED** — and **not possible** — no changelog screen exists (D-06) |
| §19.2 Re-verify About renders | **NOT STARTED** — no artifact |

---

## §19.4 — regression-free by design

Stated as the spec states it (`master-spec.md:1195-1205`), and confirmed against the
diff surface actually touched:

- Not touching launch engine code — **held.** No file under `path/java/`, `path/minecraft/`,
  `path/download/`, or the Fabric/auth/process-creation code was modified.
- Not touching authentication — **held.** `AccountManageScreen.kt` edits are visual-state
  only: `DisabledAlpha`, the divider token, and the §8.15 empty state (R-36). All three
  login overlays and the "Add Account" `ScalingActionButton` are byte-identical.
- Not touching file operations — **held.** `BuiltInFileManager.kt` edits are divider and
  progress-track tokens.
- Not touching logging — **held.** `log/` is untouched.
- Not touching statistics calculation — **held.** `GameStatsScreen.kt` /
  `PlayTimeStatsScreen.kt` edits are token swaps plus the §8.15 empty state (R-35);
  `32.dp` icon dimensions kept literal, and `PlayTimeRepository.kt` is untouched.
- Not touching config schema — **held.** No `AllSettings` entry added, removed, renamed,
  or re-typed.
- Only touching visual and layout layers — **held**, with one exception logged above as
  R-19 (two imports added to make a visual edit compile).

**The §19.4 claim is structurally true but empirically unverified.** It holds by
inspection of what was edited, not by a build and a run. That distinction is the whole
reason this file opens with a verification-status section.
