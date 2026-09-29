# Plan: Krox Launcher UI Redesign (ZalithLauncher base)

**Source PRD**: inline "KROX LAUNCHER V31.0" directive
**Complexity**: Medium

## Summary
Transform the visual identity of `ZalithLauncher` into "Krox Launcher" using a
single new color preset (Obsidian `#08080A` / Charcoal `#121216`+`#24242C` / Crimson
`#DC2626`), purged Zalith branding, and targeted structural tweaks. The app is
**100% Jetpack Compose (Material3 Expressive)** — there are no layout XML files to
overhaul (only one GL surface XML: `player_texture_view.xml`). The PRD's "XML
reconstruction" framing does not apply; the real lever is the centralized theme
system in `ui/theme/`.

## Key Reality Check (discovery findings)
- All screens pull `ColorScheme` from `ZalithLauncherTheme` → `ColorThemeType` preset.
  Adding one preset recolors every page at once (Home, Instance Mgr, File Mgr,
  Downloader, Skin/Cape, Mod Mgr, Account). This is the highest-leverage change.
- Branding lives in `gradle.properties` (`launcher_name` etc.) + `strings.xml`.
- Account is currently in the Home `RightMenu`, NOT the nav `SideBar`. PRD #7
  (account at bottom of nav dock) requires a new shortcut in `SideBar.kt`.
- File Manager already uses a left sidebar; PRD wants a top bar. The theme recolor
  covers the look; a full left->top re-architecture is larger (see Risks).

## Patterns to Mirror
| Category | Source | Pattern |
|---|---|---|
| Theme preset | `ui/theme/Theme.kt:48-578` | `private val <name>Dark = darkColorScheme(...)` wired in `when` at `Theme.kt:624-653` |
| Color set | `ui/theme/Color.kt` + `Palette.kt:23-93` | `ColorTheme(7 colors)` named field + `xxxLight`/`xxxDark = ColorTheme(...)` rows |
| Enum | `ui/theme/ColorTheme.kt:33-43` | `enum class ColorThemeType { ... }` |
| Native seed | `NativeThemeUtils.kt:204-212` | `getPredefinedSeedColor` maps theme->seed int |
| Setting label | `LauncherSettingsScreen.kt:245-261` | enum entry -> `stringResource(R.string.theme_color_*)` |

## Files to Change
| File | Action | Why |
|---|---|---|
| `ui/theme/ColorTheme.kt` | UPDATE | add `KROX` to `ColorThemeType` enum |
| `ui/theme/Theme.kt` | UPDATE | add `kroxLight`/`kroxDark` `ColorScheme`s (direct Obsidian/Charcoal/Crimson `Color` literals — NOT via Palette.kt, since its `ColorTheme(...)` calls are positional) + wire into both `when` branches |
| `ui/theme/NativeThemeUtils.kt` | UPDATE | add `KROX` seed color (crimson) |
| `setting/AllSettings.kt:376-380` | UPDATE | default `launcherColorTheme` -> `KROX` |
| `screens/content/settings/LauncherSettingsScreen.kt:247-266` | UPDATE | add KROX label + missing-check entry |
| `res/values/strings.xml` (+ tr/vi/zh-rCN/pt-rBR) | UPDATE | add `theme_color_krox`; purge Zalith branding strings |
| `gradle.properties` | UPDATE | `launcher_name`/`app_name`/`short_name` -> Krox |
| `screens/content/elements/SideBar.kt` | UPDATE | add Account shortcut anchored at bottom |
| `res/values/themes.xml` | UPDATE | `AppTheme.SplashScreen` icon if rebranded splash drawable |

## Tasks
### Task 1: Krox color preset (core visual change)
- Add `KROX` to `ColorThemeType` enum.
- In `Palette.kt`, add a `krox` field to `data class ColorTheme` and `kroxLight`/
  `kroxDark` `ColorTheme` rows built from the Obsidian/Charcoal/Crimson palette
  (single accent `#DC2626`; near-black surfaces `#08080A`–`#24242C`; on-colors white/grey).
- In `Theme.kt`, add `kroxLight` (`lightColorScheme`) and `kroxDark`
  (`darkColorScheme`) referencing the new rows, and add `ColorThemeType.KROX ->
  kroxDark/kroxLight` to both `when` branches (mirror embermire structure).
- Add `KROX -> 0xFFDC2626.toInt()` to `getPredefinedSeedColor`.
- **Validate**: `./gradlew assembleDebug --no-daemon` compiles (KROX reachable).

### Task 2: Make Krox the default theme
- `AllSettings.kt`: change default `launcherColorTheme` to `ColorThemeType.KROX`.
- `LauncherSettingsScreen.kt`: add `ColorThemeType.KROX -> stringResource(R.string.theme_color_krox)`
  to the label map and include it in the entries list.
- **Validate**: app launches into Obsidian/Crimson scheme by default.

### Task 3: Branding purge
- `gradle.properties`: set `launcher_name=Krox Launcher`, `launcher_app_name=Krox Launcher`,
  `launcher_short_name=Krox`, keep version.
- `strings.xml` (+ locales): add `theme_color_krox="Krox"`; soften/remove Zalith-only
  strings (`about_launcher_author_movtery_text`, `disclaimer_content`, `launcher_fork_subtitle`).

### Task 4: Account at bottom of nav dock (PRD #7)
- `SideBar.kt`: add a 4th `SideBarShortcut` (account icon) after `SideBarMenuContent`,
  anchored to the bottom (`Arrangement.Bottom` / `Spacer(Modifier.weight(1f))` above it),
  navigating to `AccountManager`. Wire a callback from `LauncherScreen`.
- **Validate**: account icon visible at the bottom of the left dock; tap opens accounts.

### Task 5 (optional / structural): File Manager top bar & Content Downloader modal
- File Manager (`BuiltInFileManager.kt`) already has the recolor via theme. A full
  sidebar->top-bar conversion is a larger structural rewrite; defer unless requested.
- Content Downloader floating version modal: the per-mod version list already exists in
  `download/DownloadModScreen.kt`; confirm "Install" shows version list. If a dedicated
  floating modal is required, add it there. Defer unless requested.

## Validation
```bash
cd /workspaces/KroxLauncherV2
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))  # ensure Java 17
./gradlew :ZalithLauncher:assembleDebug --no-daemon
```
Stage 5 (git push to `https://github.com/shettyudhey-glitch/DemoLauncher`) is held
pending your explicit go-ahead — pushing to a remote is a shared-state action.

## Risks
| Risk | Likelihood | Mitigation |
|---|---|---|
| Full XML "overhaul" from PRD is moot (Compose app) | Certain | Plan reframed to theme preset |
| File Manager left->top bar is large structural change | Medium | Deferred to optional Task 5 |
| Custom drawables/splash reference Zalith art | Low | Only rename strings; swap drawables only if assets exist |
| git push to external repo | — | Blocked until you confirm |

## Acceptance
- [ ] Krox preset compiles and is the default
- [ ] All 7 screens render Obsidian/Charcoal/Crimson
- [ ] Zalith branding removed from name strings
- [ ] Account shortcut at bottom of SideBar
- [ ] `assembleDebug` succeeds
