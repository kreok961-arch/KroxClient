# Dependencies Audit — KROX Launcher

Generated from codebase analysis on 2026-09-29.

## Overview

**§14.2 forbids new dependencies, and the build already satisfies everything
the redesign needs.** There is no missing library, no unversioned artifact, and
no repository that needs adding. Phase 5's entire dependency obligation is to
change **nothing**.

| Fact | Value | Location |
|---|---|---|
| Version catalog | `gradle/libs.versions.toml` | **Repo root**, not module root |
| Library aliases | **75** | same file |
| Version declarations | **49** | same file |
| Plugin aliases | **5** | same file |
| Gradle modules | **6** | `settings.gradle.kts` |
| Direct deps in app module | **70** | `ZalithLauncher/build.gradle.kts` |
| Gradle wrapper | 9.5.0 | `gradle/wrapper/gradle-wrapper.properties` |

## The six modules

`settings.gradle.kts` includes:

| Module | Role |
|---|---|
| `:ZalithLauncher` | The app — the only module Phase 5 touches |
| `:LWJGL` | LWJGL bridge; **flat local jars**, not Maven |
| `:LayerController` | Overlay layer control |
| `:ColorPicker` | Colour-picker UI |
| `:Terracotta` | Terracotta rules |
| `:InputMap` | Input-mapping model |

**Phase 5 edits only `:ZalithLauncher/src/main/java/...`.** The other five are
platform/graphics modules with no Compose UI of their own, and none of them
should be opened during the redesign.

`settings.gradle.kts` sets `repositoriesMode = PREFER_SETTINGS` with three
repositories: `google()`, `mavenCentral()`, and
`https://maven.fabricmc.net/`. **None may be added to or removed** — the Fabric
repository exists for the launch engine (§2.3) and is a §19.4 dependency, not a
styling choice.

## The pinned versions that matter

| Alias group | Version | Note |
|---|---|---|
| Android Gradle Plugin | **9.2.0** | |
| Kotlin | **2.4.0** | |
| Compose BOM | **2026.06.01** | |
| `material3` (Expressive) | **1.5.0-alpha20** | `MaterialExpressiveTheme`, `MotionScheme.expressive()` |
| Dagger | 2.60 | |
| Hilt | 1.4.0 | |
| `coreKtx` | 1.19.0 | |
| `lifecycleRuntimeKtx` | 2.11.0 | |
| `activityCompose` | 1.13.0 | |
| MMKV | 1.3.14 | The config store — §19.4 |
| Room | 2.8.4 | |
| Ktor | 3.5.1 | |
| `coilCompose` | 3.5.0 | Image loading |
| `kotlinxCoroutinesAndroid` | 1.11.0 | |

**`material3 1.5.0-alpha20` is the load-bearing pin.** The design system's
`kroxTween`, `CubicBezierEasing`, `MaterialExpressiveTheme`, and
`MotionScheme.expressive()` all come from it. Downgrading to a stable Material 3
would remove the expressive APIs the theme is built on — a §14.2 violation in
spirit (changing the shipped surface) and a guaranteed compile break.

## SDK and toolchain

| Setting | Value |
|---|---|
| `compileSdk` | 37 |
| `minSdk` | 26 |
| `targetSdk` | 34 |
| Java | `JavaVersion.VERSION_17` (`:120-121`) |
| `jvmTarget` | `JVM_17` (`:171`) |
| `applicationIdSuffix` | `.v2` |
| Package | `com.movtery.zalithlauncher` |

`minSdk 26` is why the responsive plan can use `LocalConfiguration.screenWidthDp`
— the API has existed far longer than 26, so no version guard is needed.

`targetSdk 34` with `compileSdk 37` is normal and must not be "aligned." Touching
`targetSdk` is a behaviour change under §1.2, not a styling one.

## `LWJGL`'s flat-jar `fileTree`

`LWJGL/build.gradle.kts:43` declares:

```kotlin
implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
```

These are **checked-in jars, not resolved artifacts.** There is no lockfile and
no version to bump. Do not attempt to "convert them to Maven coordinates" —
that would rewrite the graphics bridge, which is squarely inside §2.3's
do-not-touch list.

A sibling directory `Krox/` also exists at the repo root (AxolotlClient,
Flashback, OptixClient, jars, client). **It is not a Gradle module.** It is
reference material for the client, not a build input. Nothing in Phase 5
should read it.

There is no `.gitmodules` — nothing is pulled in by submodule, so there is no
submodule state to break.

## What §14.2 rules out, and why

§14.2 forbids new dependencies. Four tempting additions were considered and
**all four are blocked**:

| Tempting addition | Blocked because |
|---|---|
| `androidx.window` for fold/hinge posture | New dependency. `RESPONSIVE.md`'s width class uses `LocalConfiguration.screenWidthDp` instead, which is `minSdk 26`-safe. |
| An icon font / `res/font/` | New asset + new dependency. `KroxFontFamily = FontFamily.Default` is the locked decision, and `res/font/` does not exist. |
| A monospace font for `KroxMono` | Same. The default family is used, recorded not fixed. |
| A date/picker or chart library | New dependency. Statistics render with Compose primitives already. |

The general rule this produces: **if a design-system need arises, it is solved
by a composable, not a dependency.** §8.15's empty state, §11.1's width class,
and §7.3's elevation are all three solved in plain Compose.

`androidx.compose.material:material-icons-core` is worth a specific note: recent
versions ship **no bundled drawables**, so it is not a source of icons here. The
125 Material Symbol vectors in `res/drawable/` are the correct, already-migrated
approach (`docs/audit/RESOURCES.md`).

## Configuration surface

| Config | Value | Note |
|---|---|---|
| Repositories mode | `PREFER_SETTINGS` | Settings-level repos win |
| Repositories | `google()`, `mavenCentral()`, `maven.fabricmc.net` | Fabric's is §2.3's |
| Build types | debug + release | Release is signed in CI |
| Plugins | `android-application`, `kotlin-compose`, `ksp`, `android-library`, `hilt` | 5 |

Signing is the one build concern outside the app's code: the release build reads
`DEFAULT_STORE_PASSWORD` / `DEFAULT_KEY_PASSWORD` from CI secrets, which is the
subject of **R-01**. Its absence is why the compile gate is currently blocked.

## §19.4 position

| Guarantee | Status |
|---|---|
| Not touching the config schema | **Held** — no dependency added, removed, upgraded, or downgraded. `libs.versions.toml`, `settings.gradle.kts`, and all `build.gradle.kts` files are unmodified. |
| Not touching the launch engine | **Held** — `:LWJGL`'s flat jars, the Fabric repository, and the SDK levels are all untouched. |
| Not touching the config store | **Held** — MMKV stays at 1.3.14; no store-format change. |

## Classification

| Class | Items |
|---|---|
| **KEEP** | All 75 library aliases and 49 versions; all 5 plugin aliases; the 6 modules; the 3 repositories and `PREFER_SETTINGS`; `compileSdk 37` / `minSdk 26` / `targetSdk 34`; Java 17 / `jvmTarget` 17; `applicationIdSuffix = ".v2"`; `material3 1.5.0-alpha20`; `LWJGL`'s flat-jar `fileTree`; MMKV 1.3.14. |
| **REDESIGN** | **None.** No dependency is a design surface. |
| **FIX** | **None.** |

## Notes

- **`gradle/libs.versions.toml` and `settings.gradle.kts` live at the repo root**,
  not in `ZalithLauncher/`. A search run from the module root for a version
  catalog will return nothing — that is a path trap, not a missing file.
- **The dependency coordinates are alias-referenced, not inline.** Extracting
  `group:artifact:version` from `ZalithLauncher/build.gradle.kts` with a regex
  returns empty, because each `implementation(...)` line is a `libs.x.y` alias
  resolved in the TOML. This is a Gradle version-catalog convention, not a
  missing declaration.
- **`material3 1.5.0-alpha20` must not be downgraded.** The expressive APIs the
  theme depends on are alpha-only in that line.
- `minSdk 26` means `LocalConfiguration.screenWidthDp` needs no version guard,
  which is what makes `RESPONSIVE.md`'s zero-new-dependency width class correct.
- `Krox/` at the repo root is **not a build input.** Reading it for inspiration is
  fine; referencing it from a Gradle file is not.
- All conclusions here are unverified by a compiler until R-01 clears — see the
  verification-status section in `docs/REGRESSIONS.md`.
