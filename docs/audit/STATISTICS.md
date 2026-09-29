# Statistics Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

The §9.3 Statistics feature spans **two routed screens, one Home card, and one
repository**. In the pre-Phase-5 codebase **both routed screens were
unreachable**: no call site anywhere navigated to them. Phase 5 added the two
missing entry points — see "Resolved in Phase 5" below.

| Surface | File | Lines | Role |
|---|---|---|---|
| Per-version play-time | `ui/screens/content/GameStatsScreen.kt` | 149 | One bar per installed version, sorted |
| Aggregate play-time | `ui/screens/content/PlayTimeStatsScreen.kt` | 184 | 2×2 grid of today / month / all-time / most-played |
| Home summary card | `ui/screens/content/LauncherScreen.kt:277` | — | Today-only, **clickable → PlayTimeStats** since Phase 5 |
| Data source | `game/version/installed/PlayTimeRepository.kt` | — | Shared-preference backed |
| Formatting | `utils/PlayTimeUtils.kt` | — | `getPlayHours`, `formatPlayTime`, `getRankName` |

## §9.3 compliance — and the reachability defect

| Spec requirement | Code reality | Verdict |
|---|---|---|
| Show play time | `GameStatsScreen.kt:157`, `PlayTimeStatsScreen.kt:144` | **Met** — but unreachable |
| Per-version breakdown | `VersionStat` at `GameStatsScreen.kt:72` | **Met** — but unreachable |
| Totals (today / month / all-time) | `:65`, `:68`, `:71` in `PlayTimeStatsScreen.kt` | **Met** — but unreachable |
| Accessible from the UI | **No navigation call site exists** | **D-08 — FIXED in Phase 5** |

### D-08 — both screens are dead destinations

`NormalNavKey.kt:212` (`GameStats`) and `:217` (`PlayTimeStats`) are declared,
and `MainScreen.kt:723` / `:728` render them in the nav graph. Nothing ever
*navigates* to either key.

`PlayTimeStatsCard()` at `LauncherScreen.kt:269` is the only statistics surface
a user can actually see, and it renders today's total with **no `clickable`
modifier and no `onClick` parameter** — it is a display-only card. The comment
at `:313` records that its `32.sp` heading was already moved onto `KroxDisplay`.

This is the same class of finding as the Logs gap (`docs/audit/LOGS.md`), but
worse: Logs has two `navigateToLogView(...)` entry points, Statistics has zero.

**Phase 5 added navigation to both screens** — see "Resolved in Phase 5" below.
Per §1.2 this is not a new
feature — the destinations, screens, and data all exist; only the entry was
missing. Per §11.2 navigation may never be *hidden*; wiring up an existing
dead destination is the opposite. The fix was one `clickable` on the
existing Home card routing to `PlayTimeStats`, plus a second target for
`GameStats` — **not** a new statistics hub screen, which would be a feature.

### Resolved in Phase 5

Two `clickable` modifiers, no new component, no new string key, no new screen:

| Entry point | File | Target |
|---|---|---|
| Home's today card | `LauncherScreen.kt:285` — `BackgroundCard(modifier = Modifier.clickable(role = Role.Button, onClick = onClick), …)` | `NormalNavKey.PlayTimeStats` |
| PlayTime's "most played" card | `PlayTimeStatsScreen.kt:130` — `StatCard(modifier = … .clickable { … })` | `NormalNavKey.GameStats` |

`PlayTimeStatsCard()` gained an `onClick: () -> Unit` parameter, threaded
through `ContentMenu(onPlayTimeStatsScreen = …)`, which builds the lambda from
`backStackViewModel.mainScreen.navigateTo(...)` at `LauncherScreen.kt:144` — the
same `navigateTo` form already used at `:152` for the account screen. The
second target reuses `StatCard`, which already accepted a `modifier` applied
to its `BackgroundCard`, so no signature change was needed.

Both targets use strings that already existed (`stats_today_header`,
`stats_most_played`), so **no new string key and no new translation** was
required. Chaining `PlayTime → GameStats` is deliberate: it puts the second
destination one level deeper than the first, so a user who never taps the
Home card still cannot reach `GameStats` from a dead end.

## `GameStatsScreen.kt` — declarations

| Symbol | Line | Visibility | Role |
|---|---|---|---|
| `GameStatsScreen(backStackViewModel)` | 62 | **public** | Screen root, `BaseScreen`-wrapped |

**One** public declaration. `data class VersionStat` is a **local** declaration
inside the composable body at `:72` — it is not top-level and is not importable.

| Element | Line | Note |
|---|---|---|
| `BaseScreen` | 65 | Swap-in/out transition contract |
| `context` | 69 | `LocalContext.current`, for `formatPlayTime` |
| `versions` | 70 | `VersionsManager.versions.value` — `remember`ed without a key |
| `VersionStat(name, version, totalMs)` | 72 | Local data class |
| `stats` | 74-78 | `map` → `sortedByDescending { it.totalMs }` |
| `maxMs` | 79 | `stats.firstOrNull()?.totalMs?.takeIf { it > 0 } ?: 1L` — the divide-by-zero guard |
| `BackgroundCard(... padding(kroxGutter()))` | 81-87 | Card shell, `extraLarge` shape |
| `PageHeader(title = NormalNavKey.GameStats.title!!)` | 93-96 | **No overline** — see the duplicated-title defect below |
| Empty branch | 99-127 | `Box(fillMaxSize, contentAlignment = Center)` + bounded `Column` → icon + title + description |
| `LazyColumn` | 129-133 | `itemsIndexed(stats, key = { _, s -> s.name })` |
| `VersionIconImage(32.dp)` | 140-143 | Version icon |
| `PlayTimeUtils.formatPlayTime(...)` | 157 | Right-aligned duration |
| `LinearProgressIndicator` | 162-166 | `progress = { totalMs / maxMs }`, `color = primary` |

### The duplicated-title header — fixed in Phase 6 (R-39)

The `PageHeader` rollout initially passed **both** `overline` and `title` as
`androidText(R.string.stats_game_stats)`. But `NormalNavKey.GameStats.title` *is*
that same resource (`NormalNavKey.kt:212-214`), so the header rendered the same
words twice.

The overline is now dropped entirely — this page has no category above the
title, and there is no second string that would be an honest overline without
inventing one (§1.3). This matches the sibling `PlayTimeStatsScreen.kt:90`
exactly. The now-unused `CardTitleLayout` import was deleted with it.

That edit also exposed a **third compile break of the R-19 class**: the
rollout's `androidText(` call had no `import com.movtery.zalithlauncher.ui.androidText`,
because `androidText` is a top-level function in `ui/AndroidStringText.kt:103`,
not a class. Sweeping all 13 `PageHeader` call sites confirmed this was the only
one missing it. The import was added and then removed again with the argument
it served; the file's imports now match its body exactly.

### The empty branch's §8.15 gap — closed in Phase 6

**This entry previously recorded a third §8.15 gap. Two of its three claims
were wrong and the third is now fixed.**

The empty branch did render a single centred `Text` reading
`R.string.stats_no_data` — no icon, no title, no supporting copy. That much was
accurate. But the file manager's empty-folder branch was **not** an identical
defect (it already had the full treatment — see
`docs/audit/FILE_MANAGER.md`), and the fix is no longer pending.

**Phase 6 applied the treatment** (`docs/REGRESSIONS.md` R-35). The branch is now
a `Box(fillMaxSize, contentAlignment = Center)` wrapping a centred `Column` with
a 48 dp `ic_dashboard_outlined` at `onSurfaceVariant.copy(alpha = 0.35f)`, the
existing `stats_no_data` string as a `titleSmall` title, and a new
`stats_no_data_desc` as `bodySmall` at 0.6 alpha — matching
`BuiltInFileManager.kt:848-871` and `AccountManageScreen.kt` exactly.

`maxMs`, the `stats.all { it.totalMs == 0L }` guard, `sortedByDescending`, and
`itemsIndexed` keying are untouched; no statistic was added, computed, or
relabelled (§1.3, §19.4). No action button — none of the existing destinations
offers a relevant one from this state.

R-38 then bounded the description column:
`Modifier.widthIn(max = 320.dp)` per `docs/audit/RESPONSIVE.md`. See that file's
"Resolved" section for why the bound is a plain `320.dp` and not a
`KroxSpacing` step.

## `PlayTimeStatsScreen.kt` — declarations

| Symbol | Line | Visibility | Role |
|---|---|---|---|
| `PlayTimeStatsScreen(backStackViewModel)` | 55 | **public** | Screen root, `BaseScreen`-wrapped |
| `StatCard(label, timeMs, iconRes, subtitle, modifier)` | 136 | private | One aggregate tile |

| Element | Line | Note |
|---|---|---|
| `todayMs` | 65-67 | `getDailyTotalPlayTime(today(), versionNames)` |
| `monthMs` | 68-70 | `getLastNDaysTotal(versionNames, 30)` — **30 days, hardcoded** |
| `allTimeMs` | 71-73 | `AllSettings.playTime.getValue()` — the config-schema read |
| `mostPlayed` | 74-76 | `getMostPlayedVersion(versionNames)` → `Pair<String, Long>?` |
| Layout | 78-131 | `Column` of two `Row`s, four `StatCard(weight = 1f, fillMaxHeight = true)` |
| `getPlayHours` / `"%.1f h"` | 143-144 | Locale-insensitive via `String.format` |
| `KroxDisplay` | 172 | `ui/theme/Type.kt:49` = `kroxStyle(28, 36, FontWeight.SemiBold)` |
| `subtitle` branch | 174-181 | Only the most-played tile uses it |

**Note the two different duration formats.** `GameStatsScreen` uses
`formatPlayTime(context, ms)` (localized, likely `h`/`m`); `PlayTimeStatsScreen`
uses `"%.1f h"` (hardcoded, not localized). This inconsistency is **pre-existing**
and must be preserved — unifying it would change what a number means to a user
in a non-English locale.

## `PlayTimeRepository.kt` — out of scope, documented

`game/version/installed/PlayTimeRepository.kt:26`, an `object`. §19.4 forbids
touching statistics calculation; this is a map of what must not change.

| Member | Line | Note |
|---|---|---|
| `lastPlayedKey` | 29 | Pref key `pt_last_$versionName` |
| `totalPlayTimeKey` | 30 | Pref key `pt_total_$versionName` |
| `dailyKey` | 31 | Pref key `pt_day_${date}_$versionName` |
| `getLastPlayed` | 33 | |
| `getTotalPlayTime` | 36 | |
| `getDailyPlayTime` | 39 | |
| `getDailyTotalPlayTime` | 43 | |
| `today()` | 47 | `dateFormat.format(Date())` |
| `lastNDays(n)` | 50 | |
| `getLastNDaysTotal` | 60 | |
| `getMostPlayedVersion` | 64 | Returns `Pair<String, Long>?` |
| `recordSession` | 71 | Written at session end — **not** a UI path |

**`recordSession` (`:71`) is the single write path** into the play-time data.
No screen calls it; the launch flow does. Redesign must not introduce a second
writer or a reset/clear affordance — that would be a new statistics system
under §1.2.

## `PlayTimeUtils.kt`

`getPlayHours` `:28`, `getRankName(context, playTimeMs)` `:35`,
`formatPlayTime(context, playTimeMs)` `:50`.

**`getRankName` is unused** by both statistics screens. It is dead-ish but
public, in a shared util, and removing it is out of scope for a visual pass —
recorded, not acted on.

## Token adoption — the exception

`grep -c KroxSpacing` returns **non-zero on all three surfaces**: every pad,
gap, and inset in both screens and the Home card is already a `KroxSpacing`
token, and the headings already use `KroxDisplay`. This is the **opposite** of
the app-wide pattern (`AccountManageScreen.kt` → 0, `BuiltInFileManager.kt` → 0,
`LogViewScreen.kt` → 0).

**Record it; do not "fix" it.** These three files are the reference for what a
tokenized screen looks like, and Phase 5 edits here should follow their
conventions rather than introduce literals.

Two alpha literals remain: `Modifier.alpha(0.7f)` at `PlayTimeStatsScreen.kt:167`
and `alpha(0.6f)` at `:179`, plus `alpha(0.5f)` / `alpha(0.7f)` in
`PlayTimeStatsCard`. These are secondary-text opacities, not spacing; they map
onto the `text-secondary` / `text-muted` hierarchy and are **REDESIGN**-class.

## §19.4 position

| Guarantee | Status |
|---|---|
| Not touching statistics calculation | **Held** — `PlayTimeRepository` untouched; `maxMs` guard, `sortedByDescending`, and every `remember` key preserved. |
| Not touching config schema | **Held** — `AllSettings.playTime.getValue()` at `:72` is a read only; no entry added, removed, renamed, or re-typed. |
| Not adding a statistics feature | **Held** — §1.3. Wiring navigation to an existing dead destination is not a feature. Adding a chart library, a reset button, or a new stat would be. |

## Classification

| Class | Items |
|---|---|
| **KEEP** | Both `BaseScreen` hosts, the `VersionStat` model, `maxMs` guard, `sortedByDescending` ordering, `itemsIndexed` keying, all four `StatCard` instantiations and their icons, the `KroxDisplay` headings, the two distinct duration formats. |
| **REDESIGN** | Card surfaces and `extraLarge` shape treatment, row spacing, the `LinearProgressIndicator` track, the `alpha(0.7f)` / `alpha(0.6f)` secondary text. **The `GameStats` empty state is done** (Phase 6, R-35, bounded in R-38) and its header duplicate is gone (R-39); the rest remains. |
| **FIX** | **D-08 — done.** Navigation to `GameStats` and `PlayTimeStats` is wired via two `clickable` modifiers. Both screens render and both data paths are correct; the destinations were simply never entered. **The §8.15 empty-state gap is also closed** (Phase 6, R-35, bounded R-38), as is the duplicated header title (R-39). |

## Notes

- `LinearProgressIndicator(progress = { ... })` uses the **lambda overload**, not
  the deprecated positional one. Do not "modernise" or reorder it.
- `versions` is `remember`ed **without a key** at `GameStatsScreen.kt:66` and
  `PlayTimeStatsScreen.kt:62` — `remember { }` captures the value once per
  composition and will not observe later `VersionsManager` emissions. This is a
  pre-existing staleness bug. It is **not** in scope (it is behaviour, not
  presentation, and fixing it changes what the screen displays). Recorded only.
- `PlayTimeStatsCard()` is now a link target, so its surface takes a
  hover/pressed treatment. Compose's `clickable` supplies the ripple and
  `Role.Button` the semantics; no bespoke pressed state was added, because
  `BackgroundCard` has no existing selected variant to match.
- `GameStatsScreen`'s card gutter is `kroxGutter()` (§11.1 breakpoint helper),
  not the `KroxSpacing.xxl` this table previously cited. `grep -c KroxSpacing`
  is still non-zero in the file — the inner `Column` padding and both the
  `LazyColumn` content padding and the empty state's `spacedBy` are tokens.
- All edits to these files are unverified by a compiler until R-01 clears — see
  the verification-status section in `docs/REGRESSIONS.md`.
