# Screen Audit — KROX Launcher

Generated from codebase analysis on 2026-09-27.

---

## Main Navigation Screens (7 destinations)

| Screen | NavKey | Purpose | File |
|--------|--------|---------|------|
| **Home / Launcher Main** | `NormalNavKey.LauncherMain` | Primary launch screen with play time stats and custom home page | `LauncherScreen.kt` |
| **Account Manager** | `NormalNavKey.AccountManager` | Account management (Microsoft, offline, auth servers), skin/cape management | `AccountManageScreen.kt` |
| **File Manager** | `NormalNavKey.BuiltInFileManager` | Browse game directory, file operations, storage info | `BuiltInFileManager.kt` |
| **Download** | `NestedNavKey.Download` | Download games, mods, modpacks, resource packs, saves, shaders, search by ID | `DownloadScreen.kt` |
| **Settings** | `NestedNavKey.Settings` | 10 sub-screens (see SETTINGS.md) | `SettingsScreen.kt` |
| **About** | `NormalNavKey.Settings.AboutInfo` | Launcher info, acknowledgements, libraries, plugins | `AboutInfoScreen.kt` |
| **Task Menu** | (slide-in panel) | Background tasks progress | `TaskMenu` in `MainScreen.kt` |

---

## Nested Screens (Settings sub-navigation)

| Screen | NavKey | Purpose | File |
|--------|--------|---------|------|
| Renderer | `NormalNavKey.Settings.Renderer` | Graphics renderer settings | `RendererSettingsScreen.kt` |
| Turnip Drivers | `NormalNavKey.Settings.TurnipDrivers` | Turnip driver downloads | `TurnipDriversScreen.kt` |
| Game | `NormalNavKey.Settings.Game` | Game-related settings | `GameSettingsScreen.kt` |
| Control | `NormalNavKey.Settings.Control` | Input/control settings | `ControlSettingsScreen.kt` |
| Gamepad | `NormalNavKey.Settings.Gamepad` | Gamepad configuration | `GamepadSettingsScreen.kt` |
| Launcher | `NormalNavKey.Settings.Launcher` | Launcher appearance/behavior | `LauncherSettingsScreen.kt` |
| Java Manager | `NormalNavKey.Settings.JavaManager` | Java runtime management | `JavaManageScreen.kt` |
| Control Manager | `NormalNavKey.Settings.ControlManager` | Control scheme management | `ControlManageScreen.kt` |
| About Info | `NormalNavKey.Settings.AboutInfo` | Version, credits, licenses | `AboutInfoScreen.kt` |

---

## Version Management Screens

| Screen | NavKey | Purpose | File |
|--------|--------|---------|------|
| Versions Manager | `NormalNavKey.VersionsManager` | List/manage installed versions | `VersionsManageScreen.kt` |
| Version Settings | `NestedNavKey.VersionSettings` | Per-version configuration | `VersionSettingsScreen.kt` |
| Version Export | `NestedNavKey.VersionExport` | Export modpack | `VersionExportScreen.kt` |
| Version Overview | `NormalNavKey.Versions.OverView` | Version overview | `VersionOverViewScreen.kt` |
| Version Config | `NormalNavKey.Versions.Config` | Version configuration | `VersionConfigScreen.kt` |
| Update Loader | `NormalNavKey.Versions.UpdateLoader` | Update mod loader | `UpdateLoaderScreen.kt` |
| Mods Manager | `NormalNavKey.Versions.ModsManager` | Manage mods | `ModsManagerScreen.kt` |
| Saves Manager | `NormalNavKey.Versions.SavesManager` | Manage saves | `SavesManagerScreen.kt` |
| Resource Pack Manager | `NormalNavKey.Versions.ResourcePackManager` | Manage resource packs | `ResourcePackManageScreen.kt` |
| Screenshots Manager | `NormalNavKey.Versions.ScreenshotsManager` | Manage screenshots | `ScreenshotsManagerScreen.kt` |
| Shaders Manager | `NormalNavKey.Versions.ShadersManager` | Manage shaders | `ShadersManagerScreen.kt` |
| Server List | `NormalNavKey.Versions.ServerList` | Multiplayer servers | `ServerListScreen.kt` |

---

## Content Screens (from sidebar navigation)

| Screen | NavKey | Purpose | File |
|--------|--------|---------|------|
| Game Stats | `NormalNavKey.GameStats` | Per-version play time | `GameStatsScreen.kt` |
| Play Time Stats | `NormalNavKey.PlayTimeStats` | Daily/month/all-time stats | `PlayTimeStatsScreen.kt` |
| Log Viewer | `NormalNavKey.LogView` | View log files | `LogViewScreen.kt` |
| Multiplayer | `NormalNavKey.Multiplayer` | Multiplayer (Terracotta) | `MultiplayerScreen.kt` |
| Cape Gallery | `NormalNavKey.CapeGallery` | Browse capes | `CapeGalleryScreen.kt` |
| Recordings | `NormalNavKey.Recordings` | Screen recordings | `RecordingsScreen.kt` |
| License | `NormalNavKey.License` | License display | `LicenseScreen.kt` |
| Web View | `NormalNavKey.WebScreen` | In-app browser | `WebViewScreen.kt` |
| File Editor | `NormalNavKey.FileEditor` | Edit text files | `FileEditorScreen.kt` |
| Home Page Editor | `NormalNavKey.HomePageEditor` | Custom home page editor | `HomePageEditorScreen.kt` |
| File Selector | `NormalNavKey.FileSelector` | Pick files/folders | `FileSelectorScreen.kt` |

---

## Download Sub-screens

| Screen | NavKey | Purpose |
|--------|--------|---------|
| Download Game | `NestedNavKey.DownloadGame` | Download Minecraft versions |
| Download Mod Pack | `NestedNavKey.DownloadModPack` | Download modpacks |
| Download Mod | `NestedNavKey.DownloadMod` | Download mods |
| Download Resource Pack | `NestedNavKey.DownloadResourcePack` | Download resource packs |
| Download Saves | `NestedNavKey.DownloadSaves` | Download saves |
| Download Shaders | `NestedNavKey.DownloadShaders` | Download shaders |
| Search Assets | `NestedNavKey.DownloadAssets` | Search by platform/project |
| Search Mod Pack | `NormalNavKey.SearchModPack` | Search modpacks |
| Search Mod | `NormalNavKey.SearchMod` | Search mods |
| Search Resource Pack | `NormalNavKey.SearchResourcePack` | Search resource packs |
| Search Saves | `NormalNavKey.SearchSaves` | Search saves |
| Search Shaders | `NormalNavKey.SearchShaders` | Search shaders |
| Search ID | `NormalNavKey.SearchId` | Search by project ID |

---

## Dialog / Overlay Screens

| Screen | Purpose | File |
|--------|---------|------|
| About Dialog | App info dialog | `AboutDialog.kt` |
| Performance Settings | Performance options | `PerformanceSettingsDialog.kt` |
| Login Menu | Login type selection | `LoginMenuDialog.kt` |
| Microsoft Login Tip | Microsoft auth info | `MicrosoftLoginTipDialog.kt` |
| Local Login | Offline account creation | `LocalLoginDialog.kt` |
| Other Server Login | Auth server login | `OtherServerLoginDialog.kt` |
| Change Skin | Skin/cape management | `ChangeSkinDialog.kt` |
| Cape Selector | Cape gallery picker | `CapeSelectorDialog.kt` |
| Simple Alert | Generic alerts | `SimpleAlertDialog.kt` |
| Simple Edit | Text input dialog | `SimpleEditDialog.kt` |
| Simple List | List selection dialog | `SimpleListDialog.kt` |

---

## Classification

| Class | Screens |
|-------|---------|
| **KEEP** | All screens above — existing functionality preserved |
| **REDESIGN** | Visual/layout of all screens above |
| **FIX** | None outstanding. (`RightMenu` is defined at `LauncherScreen.kt:460`, called at `:157`.) |

---

## §9 Per-Screen State Audit (master-spec §9.1–§9.9)

Each subsection of §9 was checked against the prohibition rules it states. The §9 specs
are presentation-only and explicitly say "do not fabricate", so the audit asks two
questions per screen: *are the states the spec lists present?* and *does anything violate
a stated prohibition?*

| § | Screen | Prohibition rules | States required | Verdict |
|---|--------|------------------|-----------------|---------|
| 9.1 | Home / `LauncherScreen` | no new launch/feature logic | launch button, instance selector | KEEP — behaviour untouched |
| 9.2 | Play Button | presentation only | idle / hover / pressed / disabled / launching | PARTIAL — hover/pressed shipped in Phase 6 (R-40); `launching` still see D-01 |
| 9.3 | Statistics `GameStatsScreen` + `PlayTimeStatsScreen` | no new stats | data rows, empty, loading | KEEP — tokenized this pass |
| 9.4 | Changelog | no new entries | list, empty | KEEP |
| 9.5 | Logs | no new log capture | list, empty, viewer | KEEP |
| 9.6 | File Manager | no new file ops | list, empty, busy | KEEP — divider/track tokens fixed |
| 9.7 | Accounts `AccountManageScreen` | **no new auth / no auth change / no player preview** | list, empty state, actions | KEEP — empty state now the full §8.15 treatment (R-36, bounded R-38); all four action groups present; no prohibition violated |
| 9.8 | Settings `SettingsScreen` + 8 subscreens | **every setting maps to an existing setting / no invented settings / no semantic change / no config-format change** | rows = label + optional description + control, dividers between rows | KEEP — `SettingsScreen.kt` is a pure nav hub (8 pre-existing destinations, zero settings created or altered); row contract lives in `settings/layouts/` |
| 9.9 | About `AboutInfoScreen` (page) + `AboutDialog` (dialog) | **no unrelated content / no changed version strings / no new social links** | emblem + wordmark, version, credits, links, license | KEEP — all links are existing `path/URL_*` constants; KROX wordmark already present in `strings.xml`; build-date line not implementable, see D-03 |

### Notes on the two About surfaces

These are distinct and must not be conflated:

- `ui/screens/content/settings/AboutInfoScreen.kt` (569 lines) is the real §9.9 **page**,
  reached via `SettingsScreen.kt:259-272`. Six card chunks, zero KROX tokens
  (raw `34.dp`/`14.dp`/`12.dp`/`6.dp` literals), section headers at `typography.titleSmall`.
  Verdict KEEP on all three rules; tokenization is on the Task 4 sweep list.
- `ui/screens/content/elements/AboutDialog.kt` (118 lines) is a **launcher-level dialog**,
  reached from `LauncherScreen.kt:119`. This is where the §7.7 divider fix was applied.
  §9.9's "page header: overline + H1" rule does not bind to a dialog.