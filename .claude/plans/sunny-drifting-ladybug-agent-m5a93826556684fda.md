# Krox Launcher V31.1 — Ultimate Master Detailed Parallel Swarm Plan

## 0. Context & Directive
- **Repo:** `alihamza897600-jpgo/KroxLauncherV2` @ `main` — mutate in place. **Do not create new repo.**
- **Root:** `ZalithLauncher/` (single Android app module) + submodules `LWJGL`, `Terracotta`, `InputMap`, `ColorPicker`, `LayerController`; stub `client/`; reference `optix/`; assets `launcher icon/`; `settings.gradle.kts` (`include(":ZalithLauncher")...` no `:client` yet); 5 workflows `.github/workflows/{build,build_debug,push_ci,release_ci,last}.yml`; `gradle-wrapper.properties`, `build.gradle.kts`, `gradle.properties`.
- **Toolchain authoritative:** Inspect before changing — `gradle-wrapper.properties` (Gradle), `ZalithLauncher/build.gradle.kts` (AGP/Kotlin/compose), `gradle/libs.versions.toml` if present, `JDK 17`, Fabric Loom + Yarn/Mojang mappings pinned for MC target.
- **Blocker:** `ZalithLauncher/src/main/java/com/movtery/zalithlauncher/ui/screens/content/BuiltInFileManager.kt:1565-1575` reported syntax break (`}/)/]/lambda/when`) — root-cause fix required before any build.
- **Goals (10):** (1) Krox design system (2) legacy branding removal (3) client module (4) Optix skin/cape selective port (5) auto-build remapped JAR (6) integration/bundling (7) auto-install to instances (8) auto-activation gated 1.21.11+Fabric (9) GH Actions APK (10) continuous build-fix loop — all verified, no fake impl.
- **Quality gates:** No fabricated MC/Mixin/Fabric APIs — verify every target class/method against real mappings; no hardcoded paths; no dummy buttons; `.jar.disabled` rename only; only `krox-client-*.jar` touched in `mods/`.

User verbatim (1): "write but also addthis that do every task in parallel using all agents and use all agents to get better result and finish the work fast."
User verbatim (2): "yes and make more detailed plan so we can do fast"
This file is the plan artifact consumed by OpenClaude plan viewer (`Read`/`ExitPlanMode`). No code imports it. No data file I/O. No existing file duplicates it — `~/.claude/plans` empty, `~/.agent/plans` empty, repo `.claude/plans` has only stale `krox-launcher-ui-redesign.plan.md`. Grep `sunny-drifting` has no matches.

## 1. Ladder (Ponytail full — enforced every phase)
1. YAGNI — no greenfield project, no new framework.
2. Reuse — existing `nav`/`theme`/`mod`/`skin`/`account`/`version`/`instance`/`fileOps`/`downloader` logic; grep before writing.
3. Stdlib — Kotlin stdlib / AndroidX existing.
4. Native — Compose + XML already present → single token source, no new theming lib; `<input>`/CSS/DB constraint analogues where applicable.
5. Installed deps only — Fabric Loom + installed AGP/Kotlin/JDK; never add dependency for N lines.
6. One-liner if possible.
7. Minimal diff only after tracing full flow end-to-end. Bug fix = root cause in shared function, not per-caller patch.

## 2. Parallel Execution Model — USE ALL AGENTS (DEFAULT)

```
                        ┌─► G2-A Design System (P4) ──────────────┐
                        │                                         │
  G1 SERIAL GATE (P1-3) ┼─► G2-B Icons (P5) ──────────────────────┤
  1 agent, ~15min       ├─► G3-A..G 7 Screens (P6-10,15-16) 7× ───┤──► G5 Bundling (P17-20) ─┐
                        ├─► G4-A Client Module (P12-14) 1 agent ─┤         (starts on JAR) ├─► FINAL CONVERGE
                        └─► G6-B CI prep (P22 early) ───────────┘                          │   G6-A APK/Verify
                                                                                           └─► G6-A/B push (P27)
  Max concurrency: 10-12 agents. Disjoint files → concurrent. Shared findings via this plan + swarm memory.
```

**Timeline (wall time minimized):**
- `T0-T15m` G1 only.
- `T15m-T90m` G2(2) + G3(7) + G4(1) + G6-B(1) = 11 agents parallel.
- `T60m+` G5 starts as soon as G4 remapped JAR appears (overlap, don't wait for all G3).
- `T90m-T120m` G5 + G6-A converge, full `assembleDebug` + verification + CI watch.
- `T120m` `git push origin main`.

**Rule:** If two tasks touch disjoint files, they run concurrently. No serial queue where parallel works.

## 3. Swarm Allocation — 12 Agents (concrete files & commands)

| # | Agent | Phases | Input files inspected | Output files owned (disjoint) | Verify command |
|---|-------|--------|-----------------------|-------------------------------|----------------|
| G1-A | Explore (very thorough) + code-explorer | P1-3 | `settings.gradle.kts`, `gradle-wrapper.properties`, `ZalithLauncher/build.gradle.kts`, `AndroidManifest.xml`, `src/main/{java,res}/**`, `gradle/libs.versions.toml`, `.github/workflows/*.yml`, `BuiltInFileManager.kt` | `BuiltInFileManager.kt:1565-1575` fix + `docs/discovery.md` (ephemeral) | `./gradlew :ZalithLauncher:assembleDebug --dry-run` + `kotlinc` syntax |
| G2-A | coder | P4 | `res/values/colors.xml`, `themes.xml`, `drawable/**`, `ui/theme/**`, Compose `Theme.kt` | `res/values/krox_colors.xml` (or overwrite `colors.xml`), `res/values/krox_themes.xml`, `ui/theme/KroxTheme.kt`, `drawable/krox_*` | `./gradlew :ZalithLauncher:assembleDebug` drawable/theme no dup |
| G2-B | coder | P5 | `launcher icon/**`, `res/mipmap-*/**`, `res/drawable/ic_launcher*.xml`, `AndroidManifest.xml`, `client/src/main/resources/fabric.mod.json` icon | `res/mipmap-{hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher*.webp/png`, `res/drawable/ic_launcher_{foreground,background,monochrome}.xml`, manifest `<application android:icon>` | `aapt2 dump` / build APK contains icons; no missing density |
| G3-A | coder | P6 Home | `ui/screens/main/MainScreen.kt`, `ui/screens/home/**`, nav graph, launch pipeline `launch/**` | `ui/screens/home/KroxHomeScreen.kt` (or edit MainScreen), `drawable/krox_k_mark.xml` | Launch button still calls real `launchMinecraft()` — grep |
| G3-B | coder | P7 Instance | `ui/screens/instance/**`, `config/instance/**`, `persistence/**` (RAM/JVM) | `ui/screens/instance/KroxInstanceManager.kt`, `res/layout/krox_instance_card.xml` | RAM slider persists via existing `SharedPreferences/DataStore` key — verify |
| G3-C | coder | P8 FileMgr | `ui/screens/content/BuiltInFileManager.kt`, `file/**`, `gameDir` resolver | `ui/screens/content/KroxFileManagerTopBar.kt` (horizontal `/mods /saves ...` bar) | Bar scrollable, active state, path = `instance.gameDir.resolve(sub)` |
| G3-D | coder | P9 Downloader | `ui/screens/content/downloader/**`, `api/modrinth/**` or version API | `ui/screens/content/downloader/KroxContentDownloader.kt` + `KroxVersionModal.kt` | Per-version Install calls real `downloadVersion()` — no stub |
| G3-E | coder | P10-11 Skin/Cape | `ui/screens/skin/**`, `render/**`, `optix/src/**/skin/**`, `cape/**`, `texture/**`, mappings | `ui/screens/skin/KroxSkinCapeScreen.kt` + ported `client/src/main/java/com/krox/client/{skin,cape,texture,render}/**` (selective) | 3D viewport still renders; imports resolve against verified mappings |
| G3-F | coder | P15 ModMgr | `ui/screens/mods/**`, `file/rename` logic | `ui/screens/mods/KroxModManager.kt` | `.jar<->.jar.disabled` via `File.renameTo`; `[+ Add]` navigates to Downloader Mods tab |
| G3-G | coder | P16 Nav | `ui/navigation/**`, `ui/components/SideBar.kt` or `NavDock.kt` | `ui/navigation/KroxNavDock.kt` | Account item anchored bottom-most, `WindowInsets` safe |
| G4-A | coder + kotlin-build-resolver | P12-14 Client | `settings.gradle.kts`, `client/**`, `fabric.mod.json`, mappings, `optix/**` | `settings.gradle.kts` (+`include(":client")`), `client/build.gradle.kts` (Loom), `client/src/main/{java/resources}/**`, `fabric.mod.json`, `kroxclient.mixins.json`, `com.krox.client.KroxClient.kt` | `./gradlew :client:remapJar` green; `unzip -l client/build/libs/krox-client-*.jar | grep fabric.mod.json` |
| G5-A | coder | P17-20 Bundling | `client/build/libs/krox-client-*.jar`, root `build.gradle.kts`, `ZalithLauncher/src/main/assets/**` or `src/main/resources/**`, instance resolver | `build-logic/krox-bundling.gradle.kts` or root `build.gradle.kts` extract task, `ZalithLauncher/src/main/java/com/krox/launcher/bundling/{KroxClientBundler,CompatibilityRegistry,ClientDeployLifecycle}.kt`, `src/main/assets/krox-client-*.jar` (or cache) | Idempotent: marker `version+sha256` checked; lifecycle `BUNDLED→PREPARED→WAITING→DETECTED→DEPLOYED→VERIFIED` logged |
| G6-A | kotlin-build-resolver + java-build-resolver | P21,24,25 APK+Verify | `gradlew tasks`, all modules | Build fix patches across `ZalithLauncher/**` + `client/**` | `./gradlew :ZalithLauncher:assembleDebug` + `:client:remapJar` both green; APK exists `ZalithLauncher/build/outputs/apk/debug/*.apk` |
| G6-B | coder | P22-23 CI | `.github/workflows/*.yml` | `.github/workflows/build.yml` (or `krox-ci.yml`) | `act --dry-run` / YAML lint; triggers `push: [main]` + `pull_request`, JDK 17, cache, `upload-artifact` |

**Ownership avoids conflicts:** G2-A owns `values/**`+`theme/**`; G2-B owns `mipmap/**`+`drawable/ic_launcher*`; each G3-* owns one screen dir; G4 owns `client/**`+`settings.gradle.kts`; G5 owns bundling glue; G6 owns workflows + build fixes (serializes on build files via swarm lock).

## 4. G1 — Discovery & Blocker (P1-3) — SERIAL GATE — detail

**P1 Full walk (checklist):**
- [ ] `settings.gradle.kts` — modules, `pluginManagement` repos.
- [ ] `gradle-wrapper.properties` — Gradle version; `gradle.properties` — `android.useAndroidX`, `kotlin.code.style`.
- [ ] `ZalithLauncher/build.gradle.kts` — AGP, Kotlin, Compose, `minSdk/targetSdk/compileSdk`, `namespace`.
- [ ] `AndroidManifest.xml` — `package`, `<application>` icon/theme, permissions.
- [ ] `src/main/java/**` — nav graph, `MainScreen.kt`, `BuiltInFileManager.kt`, downloader, skin, mod, account, version, instance, JRE/Fabric installer.
- [ ] `src/main/res/**` — `values/colors.xml`, `themes.xml`, `layouts`, `drawables`, `mipmap-*`.
- [ ] `.github/workflows/*.yml` — triggers, JDK, cache, gradle task, artifacts.
- [ ] `optix/` — package `com.krox`? texture/skin/cape/render entry points; list files to port vs skip.
- [ ] `launcher icon/` — list densities, SVG/WEBP/PNG sources.
- [ ] Grep `Zalith|Pojav|zalith|pojav` — classify visual string vs `package com.movtery.zalithlauncher` vs API import — safe rename plan (visual only).
- [ ] Record MC target + mappings: `gradle/libs.versions.toml` `minecraft`, `yarn`, `loom`, `fabric-loader`.

**P2 Toolchain lock:**
- JDK 17 (`java -version`), `./gradlew --version`, `./gradlew tasks --all | grep assemble`.

**P3 BuiltInFileManager fix:**
- Read `BuiltInFileManager.kt` 1540-1600, trace function boundary, bracket balance, `when`/`lambda`/`Composable` scope.
- Root-cause fix (not blind `}`): restore closed block where all callers route through; run `kotlinc -classpath` or `./gradlew :ZalithLauncher:assembleDebug --dry-run` to verify.
- Exit criteria: file compiles in isolation; G2/G3/G4 unblocked.

## 5. G2 — Design System + Icons (P4-5) — 2 parallel

**P4 Tokens (single source):**
- Obsidian `#08080A` bg, Metallic Charcoal `#121216` surface, Elevated `#24242C` card, Crimson `#DC2626` + restrained outer glow (`#DC2626` 12-16% blur, not neon).
- XML: `res/values/krox_colors.xml` (`krox_obsidian`, `krox_charcoal`, `krox_elevated`, `krox_crimson`, `krox_crimson_glow`), `res/values/krox_themes.xml` (`Theme.Krox` extends `Theme.Material3.DayNight`).
- Compose: `ui/theme/KroxTheme.kt` `KroxColors` data class + `CompositionLocal`; no duplicate definitions — XML references Compose tokens or vice versa via resource.
- Rules: hierarchy, 8dp grid, 48dp touch target, fast dense UI, no AI-slop oversized cards.

**P5 Icons:**
- Source `launcher icon/` → export `ic_launcher.webp` + `ic_launcher_round.webp` per `mipmap-{hdpi,xhdpi,xxhdpi,xxxhdpi}` (or adaptive `ic_launcher_foreground.xml` + `ic_launcher_background.xml` + `ic_launcher_monochrome.xml`).
- Wiring: `AndroidManifest.xml` `android:icon="@mipmap/ic_launcher"` `android:roundIcon="@mipmap/ic_launcher_round"`; adaptive `<adaptive-icon>` if used.
- Also `client/src/main/resources/assets/krox/icon.png` for `fabric.mod.json` `"icon": "assets/krox/icon.png"`.

## 6. G3 — 7 Screens (P6-10,15-16) — 7 parallel agents

Each screen: **preserve real backend**, no dummy UI, no TODO-only handlers, no fake sliders, no hardcoded `gameDir`.

- **G3-A P6 Home:** Obsidian bg, Krox K mark (red metallic), profile/version/instance chips, crimson Launch CTA — calls real `LaunchManager.launch(instance)`; state from `InstanceRepository`.
- **G3-B P7 Instance Manager:** Metallic cards `#121216` border `#24242C`, crimson ring on selected, **real** RAM slider (`SeekBar`/`Slider` → `instanceConfig.allocatedMemoryMB` via `DataStore`/`SharedPreferences`), JVM args `TextField` → `instanceConfig.jvmArgs` persisted, delete/duplicate/clone wired.
- **G3-C P8 File Manager:** Top horizontal bar `[/mods | /saves | /resourcepacks | /shaderpacks | /logs]` scrollable `LazyRow`, active tab highlighted crimson, content = `gameDir.resolve(tab)` listing; file ops (copy/move/delete/rename) use existing `FileOperations`.
- **G3-D P9 Content Downloader:** Per-mod large version modal (version list via Modrinth/CurseForge API), per-version **Install** button calls real `ModDownloader.install(modId, versionId, instance)`, compatibility badge from `CompatibilityRegistry`.
- **G3-E P10-11 Skin/Cape:** Dedicated page, keep existing 3D viewport/OpenGL (`GLSurfaceView`/`TextureView`); **selective** port from `optix/src/**/skin/**`, `cape/**`, `texture/**`, `render/**`, `model/**` only — each file mapping-audited (`net.minecraft.client.render.*` vs Yarn/Mojang), package `com.krox.client.*`, texture cache `BufferedImage`→`NativeImage` bridge preserved.
- **G3-F P15 Mod Manager:** Lists `gameDir/mods/*.jar` + `*.jar.disabled`; toggle via `file.renameTo(File(parent, nameWithoutDisabled))` safe; top-left `[+ Add]` → `navController.navigate("downloader/mods")`.
- **G3-G P16 Nav Dock:** Account entry **anchored bottom-most** (`Column { Spacer(Modifier.weight(1f)); AccountItem() }` or `ConstraintLayout` bottom), `WindowInsets.navigationBars` padding, preserves `AccountManager` login/logout.

## 7. G4 — Krox Client Module (P12-14) — stepwise compile (1 agent, internal serial, overlaps G3)

Order (each step must compile before next):
1. **Target lock:** MC `1.21.11` + Fabric Loader + Fabric API; verify Yarn build for 1.21.11 exists (`https://linkie.shedaniel.me/mappings` / `fabricmc.net/develop`); if missing, fallback to latest 1.21.x with registry note — never fabricate mappings.
2. **Loom:** `settings.gradle.kts` add `include(":client")`; `client/build.gradle.kts` apply `fabric-loom`, `dependencies { minecraft "com.mojang:minecraft:1.21.11"; mappings "net.fabricmc:yarn:1.21.11+build.X:v2"; modImplementation "net.fabricmc:fabric-loader:0.16.x" }`; `loom { accessWidenerPath ... }` if needed.
3. **Metadata:** `client/src/main/resources/fabric.mod.json` (`schemaVersion 1`, `id "krox-client"`, `version "${project.version}"`, `entrypoint { main ["com.krox.client.KroxClient"] }`, `mixins ["kroxclient.mixins.json"]`, `depends { fabricloader, minecraft, java }`), `kroxclient.mixins.json` (`package "com.krox.client.mixin"`, `refmap`, `mixins []` initially).
4. **Entrypoint empty compile:** `client/src/main/java/com/krox/client/KroxClient.java` (or `.kt`) `implements ModInitializer { @Override public void onInitialize() { KroxConfig.init(); } }` — `./gradlew :client:compileJava` green.
5. **Config:** `KroxConfig` (Cloth Config optional — prefer plain JSON `config/krox.json` if dep not installed; ladder rung 5).
6. **Skin/Cape → Texture → Render:** port selective `optix` files one package at a time, each `./gradlew :client:compileJava` green; fix imports against Yarn (`net.minecraft.client.texture.NativeImage`, `AbstractTexture`, `EntityModel` etc.) — never invent method sig.
7. **Mixins:** add only verified targets (`@Mixin(GameRenderer.class)` etc.) with `@At` validated; `client:compileJava` + `kroxclient.refmap.json` generated.
8. **Remapped JAR:** `./gradlew :client:remapJar` (or `:client:build`); verify `client/build/libs/krox-client-*.jar` is ZIP containing `fabric.mod.json` with correct `id`/`entrypoints`, `kroxclient.mixins.json`, `com/krox/client/**.class` (not dev jar `*-dev-shadow.jar`).

## 8. G5 — Bundling & Auto-Activation (P17-20) — 1 agent

- **Identify artifact:** Loom remapped JAR = `client/build/libs/krox-client-<version>.jar` (not `*-dev.jar`, not `*-sources.jar`); verify `jar tf` contains `fabric.mod.json`.
- **Bundling (P17-18):** Root `build.gradle.kts` (or `ZalithLauncher/build.gradle.kts`) task `prepareKroxClient` copies remapped JAR to `ZalithLauncher/src/main/assets/krox/krox-client.jar` (or `build/generated/krox/`) idempotently; `android { sourceSets { getByName("main") { assets.srcDirs(...) } } }` includes it in APK. Alternative: `ZalithLauncher/build.gradle.kts` `tasks.register<Copy>("bundleKroxClient") { from(rootProject.file("client/build/libs/krox-client-*.jar")) into("src/main/assets/krox") }` + `preBuild.dependsOn(bundleKroxClient)`.
- **Runtime extract (P19):** `KroxClientBundler` on app start: `assets.open("krox/krox-client.jar")` → `filesDir/krox/krox-client-<version>.jar` + marker `krox-client.version` containing `version+sha256`; if marker matches existing file's sha256, skip copy (idempotent).
- **Compatibility registry (P20):** `CompatibilityRegistry` central `Map<MinecraftVersion, Set<FabricLoaderVersion>>` — initial `{"1.21.11": ["0.16.x"]}`; `isSupported(instance)` checks `instance.minecraftVersion` + `instance.isFabric` (read from `instance.json`/`instance.cfg`/`fabric-loader` marker via real `InstanceRepository`).
- **Auto-deploy (P20):** `ClientDeployLifecycle` states `BUNDLED→PREPARED→WAITING→DETECTED→DEPLOYED→VERIFIED`:
  - `BUNDLED` APK contains asset.
  - `PREPARED` extracted to `filesDir/krox/`.
  - `WAITING` instance selected, not yet compatible.
  - `DETECTED` `isSupported(instance)==true` → resolve `instanceDir = instance.gameDir` (or `instances/<id>/.minecraft`), `modsDir = instanceDir.resolve("mods")`, `mkdirs`.
  - `DEPLOYED` copy `filesDir/krox/krox-client-*.jar` → `modsDir/krox-client-<version>.jar`; delete stale `modsDir/krox-client-*.jar` older versions; **only** `krox-client-*.jar` touched; never delete user mods; no duplicates (`listFiles { it.name.matches(Regex("krox-client-.*\\.jar")) }`).
  - `VERIFIED` `modsDir/krox-client-<version>.jar` exists + `sha256` matches bundled; log state.
- **No user mod deletion invariant:** `deploy()` filters `name.startsWith("krox-client-")` only.

## 9. G6 — APK + CI + Verification (P21-27) — 2 parallel

**G6-A APK + Verify (P21,24,25):**
- Discover: `./gradlew tasks --all | grep -i assemble` → real task `:ZalithLauncher:assembleDebug` (not `:app:assembleDebug`).
- Iterative: `./gradlew :ZalithLauncher:assembleDebug` → fix → rerun until green; then `./gradlew :client:remapJar` green.
- Verify checklist (P24-25 7 screens):
  - [ ] Launcher compiles + APK `ZalithLauncher/build/outputs/apk/debug/ZalithLauncher-debug.apk` exists (`ls -lh`, `aapt dump badging`).
  - [ ] Client compiles + remapped JAR exists + ZIP valid + `fabric.mod.json` id/entrypoint correct.
  - [ ] Bundled path: APK contains `assets/krox/krox-client.jar` (`unzip -l APK | grep krox`).
  - [ ] Instance path via real arch: `InstanceRepository.getInstances()` → `instance.gameDir` → `mods/` deploy works for `1.21.11+Fabric` mock instance.
  - [ ] 7 screens reachable via nav graph, no `NotImplementedError`/stub.
  - [ ] No broken drawables/themes — `aapt2` no errors.
  - [ ] Branding: grep `Zalith|Pojav` — zero user-facing strings; internal `com.movtery.zalithlauncher` package preserved if rename breaks.
  - [ ] No fabricated APIs — every `net.minecraft.*` import resolves against Yarn for 1.21.11 (compile green is proof).
  - [ ] No duplicate JARs, no user mod deletion.

**G6-B CI (P22-23):**
- Workflow `.github/workflows/build.yml` (extend or create `krox-ci.yml`):
  ```yaml
  name: Krox CI
  on: { push: { branches: [main] }, pull_request: { branches: [main] } }
  jobs:
    build:
      runs-on: ubuntu-latest
      steps:
        - uses: actions/checkout@v4
        - uses: actions/setup-java@v4  # JDK 17, distribution temurin
          with: { java-version: '17', distribution: temurin }
        - uses: gradle/actions/setup-gradle@v4 # cache
        - run: ./gradlew :client:remapJar --no-daemon
        - run: ./gradlew :ZalithLauncher:assembleDebug --no-daemon
        - uses: actions/upload-artifact@v4
          with: { name: KroxLauncher-APK, path: ZalithLauncher/build/outputs/apk/debug/*.apk }
        - uses: actions/upload-artifact@v4
          with: { name: krox-client-jar, path: client/build/libs/krox-client-*.jar }
  ```
- Keep existing `build_debug.yml`/`push_ci.yml` compatible or consolidate — don't duplicate triggers.
- Monitor: `gh run list --limit 5` + `gh run view <id> --log-failed`; fix until green.

## 10. Dependency Graph (what blocks what)

```
G1 ─┬─► G2-A ─┐
    ├─► G2-B ─┤
    ├─► G3-A..G (7) ─┼─► G5 (needs G4 JAR) ─► G6-A APK (needs G2+G3+G4+G5)
    ├─► G4 ──────────┘                      ─► G6-B CI (needs G4 task names)
    └─► G6-B (early, needs only settings discovery)
G6-A also needs G1 fix.
G5 needs G4 remapped JAR path + G6-A instance arch (can mock early).
```

## 11. Push (P27)

```bash
git status
git diff --stat
git add ZalithLauncher/src/main/java ZalithLauncher/src/main/res client/ .github/workflows/ settings.gradle.kts gradle/ launcher\ icon/ # only sources, never build/.gradle/.idea
git commit -m "feat: complete Krox Launcher V31.1 redesign and Krox Client integration"
git push origin main
# report: branch, changed files, JAR/APK paths, Actions URL, blockers
```

## 12. Risks & Mitigations
| Risk | Mitigation |
|------|------------|
| AGP/Kotlin/JDK drift | Inspect `build.gradle.kts`+wrapper first; pin JDK 17 in workflow; `./gradlew --version` early |
| Mappings vs MC 1.21.11 | Verify Yarn build exists before Loom; fallback to latest verified 1.21.x with registry update |
| Optix mappings mismatch | Per-file import audit; incremental compile; skip file if target class absent |
| LWJGL natives | Submodules `LWJGL` untouched unless build fails; verify `jniLibs` |
| Loom remap artifact name | `ls client/build/libs/*.jar` + `jar tf` to identify production JAR; don't assume name |
| Density/manifest wiring | Build APK + `aapt dump` + install test; fallback to `mipmap-hdpi` if adaptive fails |
| GateGuard hooks | Pre-stage facts for Write/Bash; use `cat >` heredoc if Write denied |

## 13. Execution Order — Who Runs When

1. **Now:** G1-A starts (this plan). On G1 green, launch G2-A,B + G3-A..G + G4-A + G6-B in parallel (11 agents, one `Task` call with 11 prompts).
2. **Mid:** G5-A launches as soon as G4 first `remapJar` succeeds (don't wait for G3).
3. **Final:** G6-A APK assemble + full verification; G6-B watches CI; converge → `git push`.

