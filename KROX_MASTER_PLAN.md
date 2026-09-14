# Krox Launcher V31.0 — Master Implementation Plan

## Stage 1: Codebase Discovery and Analysis — COMPLETE

### Tech Stack Confirmed
- **Jetpack Compose** (Material3 + Material Expressive Theme) — no XML for main UI
- **Kotlin + Java 17** (JVM target 1.17 in build.gradle.kts)
- **Navigation3** (`androidx.navigation3`) — `NavDisplay` + `entryProvider` in MainScreen.kt
- **Hilt DI**, **Coil** for images, **Kotlinx Serialization**, **materialkolor** for dynamic colors
- **ndkBuild** JNI for native rendering (PlayerSkin uses AndroidView WebView for 3D)

### Color System — ALREADY IMPLEMENTED
- `Theme.kt`: `kroxLight` (light mode, crimson on pale surface) + `kroxDark` (dark mode: `#08080A` bg, `#121216` surface, `#24242C` surfaceVariant, `#DC2626` primary)
- `ColorThemeType.kt`: `KROX` enum added
- `NativeThemeUtils.kt`: `KROX -> 0xFFDC2626` seed
- `AllSettings.kt`: default theme = `ColorThemeType.KROX`
- `strings.xml` (all locales): `account` + `theme_color_krox` strings

### Current State of Each PRD Page

| # | PRD Page | Status | Key Files |
|---|----------|--------|-----------|
| 1 | Home Screen | Partially themed | `LauncherScreen.kt` — uses `BackgroundCard`, `SideBar` |
| 2 | Instance Manager | Not touched | `VersionsManageScreen.kt`, `VersionManagerLayout` |
| 3 | File Manager | DONE | `BuiltInFileManager.kt` — sidebar to top-bar conversion complete |
| 4 | Content Downloader | Partially themed | `DownloadScreen.kt`, sub-screens in `download/` |
| 5 | Skin & Cape Manager | Partially themed | `CapeGalleryScreen.kt`, `AccountManageScreen.kt` |
| 6 | Mod Manager | Partially themed | `ModsManagerScreen.kt` |
| 7 | Account Page | Partially themed | `AccountManageScreen.kt` |

### Key Architecture Patterns
- `BaseScreen` — wraps all screens, handles `isVisible` animation + `screenKey` back-stack
- `ScreenBackStackViewModel` — holds `mainScreen`, `downloadScreen`, version screen keys
- `swapAnimateDpAsState` — staggered entrance animations for screen content
- `VersionChunkBackground` — reusable card container with blur background
- `SideBar` — collapsible left-side shortcut rail (now includes Account shortcut)

---

## Stage 2: Master Implementation Checklist

### Tier 1: High Priority (Core visual polish)

- [x] T1.1 Color system — kroxDark/kroxLight defined and wired as default
- [x] T1.2 Launcher name — gradle.properties -> Krox Launcher
- [x] T1.3 User-Agent strings — updated to KroxLauncher/2.0
- [x] T1.4 FileManager top-bar — TopBarNavChip replacing sidebar
- [x] T1.5 SideBar account shortcut — onAccountClick callback + nav item
- [x] T1.6 Theme settings — KROX option in LauncherSettingsScreen
- [ ] T1.7 Mod Manager — Add +Add floating action button (FAB)
- [ ] T1.8 Content Downloader — Implement floating version modal

### Tier 2: Medium Priority (Visual consistency)

- [ ] T2.1 Home Screen — Adjust charts/cards for crimson accent (primary already maps to #DC2626)
- [ ] T2.2 Instance Manager / Versions screen — Verify VersionChunkBackground uses KROX palette
- [ ] T2.3 Account Page — Convert to bottom-nav dock layout (currently left/right panel split)
- [ ] T2.4 Skin & Cape Manager — Verify 3D preview styling; crimson buttons

### Tier 3: Low Priority (Nice-to-have polish)

- [ ] T3.1 Add ic_krox_logo launcher icon or ensure app icon reflects new branding
- [ ] T3.2 Verify all BackgroundCard components render with proper KROX surface colors

---

## Implementation Notes

### Mod Manager +Add Button
File: VersionsManagerScreen.kt (the versions list where users add Minecraft versions)
The +Add FAB should open DownloadGameWithAddonScreen.
Use FloatingActionButton with crimson tint (containerColor = MaterialTheme.colorScheme.primary).
Alternative: In ModsManagerScreen.kt, consolidate ImportMultipleFileButton + swapToDownload into a crimson FAB.

### Content Downloader Floating Version Modal
Files: DownloadGameWithAddonScreen.kt, DownloadModScreen.kt, SearchModScreen.kt
When user clicks a mod/game, a floating modal shows available versions.
Check existing AssetInfoDialog or ModVersionInfoDialog.

### Account Page Bottom Nav Dock
File: AccountManageScreen.kt
Currently uses Row with left panel (3D skin + add account) and right panel (account list).
PRD wants bottom navigation dock. Convert right panel to bottom NavigationBar with NavigationBarItem entries.

---

## Stage 3-5 Plan

- Stage 3: Implement T1.7, T1.8, T2.1-T2.4 using Jetpack Compose, reusing existing BackgroundCard, ScalingActionButton, SideBar patterns
- Stage 4: Compile with ./gradlew compileDebugKotlin after each change, fix errors
- Stage 5: ./gradlew assembleDebug, push to https://github.com/shettyudhey-glitch/DemoLauncher
