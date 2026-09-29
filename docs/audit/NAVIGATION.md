# Navigation Audit — KROX Launcher

Generated from codebase analysis on 2026-09-27.

## Navigation Model
The launcher uses a custom `Navigation3` implementation with `sealed interface NormalNavKey` to define all screen destinations. This approach ensures type-safe navigation and allows passing parameters (e.g., URLs, paths, UUIDs) directly in the navigation keys.

## Primary Navigation Structure

The launcher is organized into several main sections accessible via the sidebar/main menu:

| Category | Description | Key / Target |
|---|---|---|
| **Launcher** | Primary landing experience | `NormalNavKey.LauncherMain` |
| **Accounts** | Account & Cape management | `NormalNavKey.AccountManager` |
| **Versions** | Installed game versions | `NormalNavKey.VersionsManager` |
| **File Management** | Internal file browser & editor | `NormalNavKey.BuiltInFileManager`, `NormalNavKey.FileEditor` |
| **Settings** | Configuration sub-screens | `NormalNavKey.Settings.*` |
| **Download** | Content marketplace | `NestedNavKey.Download`, `NormalNavKey.Search*` |
| **Multiplayer** | Terracotta multiplayer | `NormalNavKey.Multiplayer` |
| **Statistics** | Play time & game stats | `NormalNavKey.GameStats`, `NormalNavKey.PlayTimeStats` |
| **About** | Info & Credits | `NormalNavKey.Settings.AboutInfo` |

## Settings Sub-navigation
Settings are organized under `NormalNavKey.Settings` and include:
- `Renderer`
- `TurnipDrivers`
- `Game`
- `Control`
- `Gamepad`
- `Launcher`
- `JavaManager`
- `ControlManager`
- `AboutInfo`

## Version Management Sub-navigation
Per-version settings are organized under `NormalNavKey.Versions` and include:
- `OverView`
- `Config`
- `UpdateLoader`
- `ModsManager`
- `SavesManager`
- `ResourcePackManager`
- `ShadersManager`
- `ScreenshotsManager`
- `ServerList`

## Navigation Implementation Patterns
- **Type-safe:** Every navigation target is a `Serializable data object` or `data class` implementing `NormalNavKey`.
- **Parametric:** Screens like `WebScreen(val url: String)` or `FileEditor(val filePath: String)` use constructor parameters for state initialization.
- **Nested:** `Settings` and `Versions` use `sealed interface` for grouping related sub-screens.
