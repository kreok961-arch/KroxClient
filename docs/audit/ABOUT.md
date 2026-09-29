# About Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

§9.9 About is split across **two surfaces**: a full settings-nested page and a
small launch-dialog.

| Surface | File | Lines | Role |
|---|---|---|---|
| About page | `ui/screens/content/settings/AboutInfoScreen.kt` | 568 | Full credit/link/library list, nested under Settings |
| About dialog | `ui/screens/content/elements/AboutDialog.kt` | 119 | Compact dialog, opened from Home |

Both are **KEEP** structurally and **REDESIGN**-only in presentation. §2.2 lists
"About entry" as a preservation contract, and §19.4 covers the link targets.

## `AboutInfoScreen.kt` — declarations

| Symbol | Line | Visibility | Role |
|---|---|---|---|
| `AboutInfoScreen(` | 83 | **public** | Page root, `BaseScreen`-wrapped |
| `ChunkLayout(` | 267 | private | Titled section container |
| `LinkIconItem(` | 297 | private | Icon + title + text + link button |
| `ButtonIconItem(` | 382 | private | Icon + title + text + button slot |
| `PluginInfoItem(` | 434 | private | Installed-plugin credit row |
| `LibraryInfoItem(` | 506 | private | Third-party library credit row |

One public declaration, five private row/section composables. The signature is
entirely callback-driven — the page opens **no** data of its own:

```kotlin
fun AboutInfoScreen(
    key: NestedNavKey.Settings,
    settingsScreenKey: TitledNavKey?,
    mainScreenKey: TitledNavKey?,
    checkUpdate: () -> Unit,
    openLicense: (raw: Int) -> Unit,
    openLink: (url: String) -> Unit
)
```

**All three callbacks are §19.4 no-touches.**

| Callback | Consumer | What it does |
|---|---|---|
| `checkUpdate` | `MainScreen.kt:250` region | Starts the existing update check |
| `openLicense(raw)` | Settings host | Opens a `raw` license resource by id |
| `openLink(url)` | 6 call sites inside this file | Hands a URL to the system browser |

## Composition

`AboutInfoScreen` (`:83`) → `BaseScreen` (`:92`) → `AnimatedLazyColumn` (`:98`)
→ `animatedItem` (`:103`) → `ChunkLayout` (`:105`) → `ButtonIconItem` /
`LinkIconItem` / `PluginInfoItem` / `LibraryInfoItem`.

| Element | Line | Note |
|---|---|---|
| `BaseScreen(Triple(key, mainScreenKey, false), …)` | 92-94 | The `NestedNavKey.Settings` 3-tuple form — see `docs/audit/BASE_SCREEN.md` |
| `AnimatedLazyColumn(contentPadding = PaddingValues(all = 12.dp))` | 98-101 | §7.6-compliant; `isVisible` drives the transition |
| `animatedItem(scope) { yOffset -> … Modifier.offset { … } }` | 103-104 | Per-item slide-in driven by scroll — **do not replace with `animateItem()`** |
| `BuildKeys.LAUNCHER_NAME` | 108 | Generated constant — see the R-01 caveat in `docs/REGRESSIONS.md` |
| `BuildConfig.VERSION_NAME` | 109 | Build-time version string |

**`contentPadding = 12.dp` and the internal `spacedBy(12.dp)` are literal dp**,
not `KroxSpacing.md`. `grep -c KroxSpacing AboutInfoScreen.kt` → **0**. This is
the app-wide pattern (`docs/spec/tokens.md` §3 permits literals in untouched
code). Phase 5 edits adopt `KroxSpacing`; untouched regions keep their literals.

## The six `openLink` targets

| Line | Constant | Destination |
|---|---|---|
| 117 | `URL_PROJECT` | The KROX/Zalith GitHub repository |
| 130 | `URL_SUPPORT` | Sponsor / donate |
| 143 | `URL_STAR1XR` | Upstream author profile |
| 189 | `URL_MCMOD` | Chinese mod site |
| 220 | `URL_COMMUNITY` | Community link |
| 227 | `URL_WEBLATE` | Translation platform |

All six constants are defined in `path/UrlManager.kt:47-64`. **None of them may
be changed, reordered, relabelled, or re-targeted.** The redesign owns the
*button chrome* around them and nothing else — the URL a credit link points at
is a fact about the project, not a style choice.

### `UrlManager.kt:47-64` — the full constant inventory

| Constant | Line | Used by the About page? |
|---|---|---|
| `URL_MCMOD` | 47 | Yes (`:189`) |
| `URL_MINECRAFT_VERSION_REPOS` | 48 | No — version download path |
| `URL_MINECRAFT_ASSETS_INDEX` | 49 | No — asset download path |
| `URL_MINECRAFT_PURCHASE` | 50 | No — Mojang store link |
| `URL_PROJECT` | 51 | Yes (`:117`) |
| `URL_ORIGINAL_PROJECT` | 52 | No |
| `URL_STAR1XR` | 53 | Yes (`:143`) |
| `URL_PROJECT_INFO` | 54 | No |
| `URL_ORIGINAL_PROJECT_INFO` | 55 | No |
| `URL_PROJECT_RELEASES_LATEST` | 56 | **No — defined and unreferenced.** See `docs/audit/CHANGELOG.md` D-07 |
| `URL_COMMUNITY` | 57 | Yes (`:220`) |
| `URL_WEBLATE` | 58 | Yes (`:227`) |
| `URL_SUPPORT` | 59 | Yes (`:130`) |
| `URL_EASYTIER` | 60 | No |
| `URL_PLAYER_NOTICE` | 61 | No |
| `URL_GITHUB_RENDERER_PLUGINS` | 63 | No — plugin repo |
| `URL_GITHUB_DRIVER_PLUGINS` | 64 | No — driver repo |

**Do not delete `URL_PROJECT_RELEASES_LATEST`.** It is unused, but it is a
config constant and §19.4 covers the config schema. Removing it is a scope
decision, not a visual one.

## `LibraryInfoItem` / `PluginInfoItem` — the credit lists

These two render **runtime-discovered** data: installed renderer/driver plugins
and the third-party libraries the build embeds. They are therefore
data-driven, not a static list in source.

**Phase 5 may restyle the rows. It may not change how the lists are populated,
sorted, or filtered** — a library credit list that gains or loses an entry is a
behaviour change under §1.3, even though nothing about the *code* moved.

`PluginInfoItem` (`:434`) and `LibraryInfoItem` (`:506`) also receive their
version strings from the plugin/library objects. Preserve the exact string
formatting; do not reformat a version number.

## `AboutDialog.kt` — the compact surface

119 lines, one public declaration: `AboutDialog(` at `:44`.

| Property | Value |
|---|---|
| Call site | `LauncherScreen.kt:120` — `AboutDialog(onDismissRequest = { showAboutDialog = false })` |
| Trigger | A `showAboutDialog` state flag in Home |
| Contract | §8.11 dialog: scrim, dismiss-on-outside-tap, corner radius |

`LauncherScreen.kt:120` is a single-line call with an `onDismissRequest` lambda —
the **only** thing it passes. The dialog therefore owns its own content and its
own dismissal. Do not add parameters to it; a new parameter is a new
capability, not a restyle.

The two About surfaces are **independent** — the dialog is not a preview of the
page and does not link to it. Do not "unify" them; that would add a navigation
edge that does not exist (§1.2).

## Navigation

| Route | Mechanism |
|---|---|
| Enter About page | `settingsScreen.navigateTo(NormalNavKey.Settings.AboutInfo)` at `MainScreen.kt:250` |
| About page declared | `NormalNavKey.kt:125` — `@Serializable data object AboutInfo : Settings` |
| About page rendered | The 9-entry `NavDisplay` in `SettingsScreen.NavigationUI` (see `docs/audit/SETTINGS.md`) |
| Open About dialog | `showAboutDialog = true` in `LauncherScreen.kt` |

`MainScreen.kt:307` has `val inAboutScreen = mainScreenKey is NormalNavKey.Settings.AboutInfo`
— a state read used for Home layout. **KEEP** it: it is an existing behaviour
contract, and the About page's presence changes Home's composition.

## §9.9 compliance

| Spec requirement | Code reality | Verdict |
|---|---|---|
| App name and version | `BuildKeys.LAUNCHER_NAME` + `BuildConfig.VERSION_NAME` at `:108-109` | **Met** |
| Upstream / project credit | `URL_PROJECT` (`:117`), `URL_STAR1XR` (`:143`), `URL_MCMOD` (`:189`) | **Met** |
| Support / sponsorship | `URL_SUPPORT` (`:130`) | **Met** |
| Community / translation | `URL_COMMUNITY` (`:220`), `URL_WEBLATE` (`:227`) | **Met** |
| Library and plugin credits | `PluginInfoItem` (`:434`), `LibraryInfoItem` (`:506`) | **Met** |
| Check for updates | `checkUpdate` callback (`:112`) | **Met** |
| Licenses | `openLicense(raw)` | **Met** |

§9.9 is the **most completely satisfied** section in the app. Phase 5's job here
is the smallest of anywhere: restyle six button rows and four section
containers.

## Token adoption

| Token | Status |
|---|---|
| `KroxSpacing` | **0 occurrences** — literal `12.dp` padding throughout |
| `KroxType` / `KroxDisplay` | Not used; the page reads `MaterialTheme.typography` for everything |
| §7.7 divider | Not present — `AnimatedLazyColumn` has no dividers between chunks |
| §8.4 page header | **Met** — `PageHeader` at `:103`; overline `about_launcher_title` ("About Launcher"), title `key.title` ("About"). `ChunkLayout` (`:267`) still titles each section below it, as it did before. |

The §8.4 gap that was the one substantive §9.9 omission is now closed by the
shared `PageHeader` (`ui/components/PageHeader.kt`) — D-09, resolved; see
`docs/audit/SETTINGS.md`. `KroxType` reach here is via that component
(`KroxOverline` named directly, `KroxH1` through `typography.headlineSmall`);
the section bodies below still read `MaterialTheme.typography` and their own
`dp` literals, unchanged.

## §19.4 position

| Guarantee | Status |
|---|---|
| Not touching About entries | **Held** — all six URLs unchanged; no credit row added, removed, or reordered. |
| Not touching config schema | **Held** — `UrlManager.kt` is read-only here; no constant added, removed, renamed, or re-targeted. |
| Not adding an About feature | **Held** — §1.3. `URL_PROJECT_RELEASES_LATEST` stays unused; exposing it is a new destination. |

## Classification

| Class | Items |
|---|---|
| **KEEP** | Both `BaseScreen` hosts, the six-URL set and its order, the `checkUpdate` / `openLicense` / `openLink` callback signatures, `PluginInfoItem` and `LibraryInfoItem` population logic, `AboutDialog`'s single-parameter signature, `inAboutScreen` at `MainScreen.kt:307`, the nav entry at `MainScreen.kt:250`. |
| **REDESIGN** | `ChunkLayout` section chrome, `LinkIconItem` / `ButtonIconItem` row surfaces and button styling, plugin/library row typography, `AboutDialog`'s §8.11 surface and scrim. The shared §8.4 `PageHeader` was part of this and **has landed** at `:103` (D-09 resolved). |
| **FIX** | None outstanding. D-09 (shared, tracked in `docs/audit/SETTINGS.md`) was the one item and it is resolved. |

## Notes

- **`BuildKeys.LAUNCHER_NAME` is a generated constant.** `find . -name "BuildKeys*"`
  returns nothing in source — it is produced by the Gradle build. Phase 5 must
  not attempt to "tidy" the reference, and must not hardcode the name as a
  fallback. This is one of the reasons R-01 (a green compile) gates Phase 5.
- The two About surfaces are visually independent by design. Unifying them, or
  adding a "view full credits" link from the dialog to the page, would be a new
  navigation edge — §1.2 forbids it and §11.2 is about hiding navigation, not
  adding it.
- `AnimatedLazyColumn` + `animatedItem` is the app's scroll-transition pattern
  and is §7.6-compliant. Do not swap it for `LazyColumn` + `animateItem()`; that
  would change the motion behaviour, not just its styling.
- All edits to these files are unverified by a compiler until R-01 clears — see
  the verification-status section in `docs/REGRESSIONS.md`.
