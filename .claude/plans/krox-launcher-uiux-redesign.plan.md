# Plan: KROX Launcher — UI/UX Redesign

**Source PRD**: inline "KROX LAUNCHER — ULTIMATE MASTER PROMPT" directive
**Target**: `ZalithLauncher/` (Android app, Kotlin, Jetpack Compose + Material3 Expressive 1.5.0-alpha20, Navigation3)
**Complexity**: Large (8 phases, ~40 screens, 0 functional changes)
**Hard rule**: never claim a build passes without running the build.

---

## Summary

Visual-only redesign of the existing KROX Launcher. No features added, none removed, no launch-engine
touch. The leverage point is already in place: `kroxDark`/`kroxLight` in `ui/theme/Theme.kt:582-656`
already implement the mandated palette (`#08080A` bg, `#121216` surface, `#24242C` variant, `#DC2626`
primary). The real work is the **rest of the design system** — typography is currently
`Typography()` (empty), shapes/spacing/motion tokens do not exist as a token layer, and the sidebar
and Home were already partially hand-tuned away from the palette.

**But the project does not currently compile, and cannot be compiled in this environment.** Those
two facts gate everything and are addressed as Task 0 below.

---

## Discovery — the three facts that change the plan

### 1. The source does not compile: `RightMenu` is undefined

`LauncherScreen.kt:133` calls `RightMenu(...)`. Grep across all of `src/` finds exactly one hit —
the call site. There is no `fun RightMenu` anywhere. `ContentMenu` is defined at
`LauncherScreen.kt:150`; `RightMenu` is not. **The Home screen is missing its entire right-hand
pane (the 3/7-weight column holding launch, account, and version actions).**

The prior audit doc `docs/audit/SCREENS.md` was correct about this. I previously doubted it; I was
wrong. This is a **FIX** under the spec's own classification, and it must be resolved before any
redesign work, because the redesigned Home cannot be laid out around a missing pane.

Its call signature (known, from the call site) tells us exactly what it rendered:

```
RightMenu(
  isVisible, modifier, onLaunchGame: (Version?) -> Unit,
  toAccountManageScreen, toVersionManageScreen, toVersionSettingsScreen
)
```

Note this also resolves the "is there a Play button on Home" question: **yes** — `onLaunchGame` is
Home's launch callback, wired in `MainScreen.kt:475-479` to `EventViewModel.Event.Launch.Game(version)`.

### 2. No Android SDK — the build gate moves to GitHub CI

```
$ ./gradlew :ZalithLauncher:compileDebugKotlin
BUILD FAILED
> SDK location not found. Define a valid SDK location with an ANDROID_HOME environment
  variable or by setting the sdk.dir path in your project's local.properties file
  at '/workspaces/KroxClient/local.properties'.
```

- `ANDROID_HOME` and `ANDROID_SDK_ROOT` are both empty
- no `local.properties`
- no SDK at `~/Android/Sdk`, `/opt/android*`, `/usr/lib/android-sdk`
- no `sdkmanager` / `adb` on PATH
- no `build/` directories anywhere — **the project has never been built in this environment**
- only JDK is **Microsoft OpenJDK 25.0.4.1**; AGP 9.2.0 / Gradle 9.5.0 want JDK 17 or 21

**Decision (user): use GitHub CI to build the APK.** The repo has a remote
(`github.com/kreok961-arch/KroxClient`) and `gh` is authenticated as `kreok961-arch`, so CI can
serve as the compile gate for every phase. See Task 0b.

The existing workflows cannot serve that role as written:

| Workflow | Problem |
|---|---|
| `build_debug.yml` | **Right shape** (5-arch matrix, `ZalithLauncher:assembleDebug`) but pins `actions/checkout@v7`, `setup-java@v5`, `upload-artifact@v7` — none of these tags exist. Also runs `./client/gradlew :client:remapJar`; `client/` is not in this repo. |
| `build.yml` | Valid action versions, but runs `./gradlew assembleDebug` (root, no module) and the same dead `client/` step. |
| `push_ci.yml`, `release_ci.yml`, `last.yml` | All call `build.yml` with `with: variant: Release` + 4 secrets, but `build.yml` declares **no `workflow_call` trigger and no inputs**. All three are dead. `release_ci.yml` and `last.yml` also pin `actions/download-artifact@v8`, which does not exist. |

Fixing all six is out of scope for a UI redesign. Task 0b writes **one** working workflow for the
launcher and uses it as the gate.

### 3. The spec's information architecture does not match this launcher

Spec Part 5 lists 8 destinations: Home, Statistics, Changelog, Logs, File Manager, Accounts, Settings, About.

This launcher's actual sidebar (`MainScreen.kt:331-371`) has **6**, in this order:

| # | Label | String res | Nav target | Icon |
|---|-------|-----------|-----------|------|
| 1 | Main Menu | `generic_main_menu` | `NormalNavKey.LauncherMain` | `ic_home_filled` |
| 2 | Account | `account` | `NormalNavKey.AccountManager()` | `ic_person_outlined` |
| 3 | File Manager | `page_title_file_manager` | `NormalNavKey.BuiltInFileManager()` | `ic_folder_filled` |
| 4 | Download | `generic_download` | `NestedNavKey.Download` | `ic_download_2_filled` |
| 5 | Settings | `generic_setting` | `NestedNavKey.Settings` | `ic_settings_filled` |
| 6 | About | `settings_tab_info_about` | `NormalNavKey.Settings.AboutInfo` | `ic_info_filled` |
| — | Task Menu | `main_task_menu` | slide-in `TaskMenu` (bottom) | `ic_assignment_filled` |

**Changelog does not exist** — no nav key, no screen, no string. Statistics, Logs, Versions,
Multiplayer, Cape Gallery, Recordings all exist as screens but are reached from *inside* other
screens, not the sidebar.

Per the spec's own Part 1 one-line rule (*"If the current launcher does not already have it, the
redesign must not add it"*), **the 6 real destinations are frozen as-is.** No Changelog is added.
This is recorded in `docs/audit/RISKS.md` as a documented spec/reality divergence, not silently
ignored.

Also frozen: `generic_main_menu` ("Main Menu") is used for **both** the logo button
(`MainScreen.kt:319`) and nav item 1 (`:334`) — so visible labels cannot be naively added without a
collision.

---

## Patterns to Mirror

| Category | Source | Pattern |
|---|---|---|
| Theme preset | `ui/theme/Theme.kt:48-578` | `private val <name>Dark = darkColorScheme(...)`, wired in the `when` at `Theme.kt:702-733` |
| Theme entry | `ui/theme/Theme.kt:680-767` | `ZalithLauncherTheme` reads `AllSettings`, cross-fades schemes via `activeMaskView` |
| Color semantics | `ui/theme/Palette.kt:86` | `buttonColor(isPrimary)` — semantic accessor, not raw `Color(0x…)` at call sites |
| Card surface | `ui/components/BackgroundCard.kt` | single container primitive used by every screen — retune once, all screens follow |
| Nav transitions | `ui/screens/_Navigation.kt:120-137` | `rememberTransitionSpec()` reads user setting + speed → one place to retime |
| Rail item | `ui/components/_SimpleRail.kt:69-150` | `TextRailItem` — the one existing animated rail primitive |
| Test style | `src/test/.../FileTest.kt` | plain JUnit `assert*`, no framework, no fixtures |

---

## Design-system reality check

| Spec requirement | Current state | Phase-1 work |
|---|---|---|
| Palette (§3.1) | **Done** — `kroxDark`/`kroxLight` at `Theme.kt:582-656` match the tokens | none; verify contrast only |
| Typography (§3.5, 10 styles) | **Missing** — `ui/theme/Type.kt:23` is `val AppTypography = Typography()` | build the 10-style scale |
| Spacing scale (§3.3) | **Missing** — screens hardcode `6/8/12/16.dp` inline | add scale + migrate high-traffic sites |
| Radius scale (§3.4) | Partial — code uses `shapes.extraLarge` / `.large` ad hoc | map to xs4/sm6/md8/lg12/xl16 |
| Motion (§3.6, ≤240ms) | **Non-conformant** — `TextRailItem` uses 300ms `FastOutSlowInEasing`; `MotionScheme.expressive()` drives most transitions | clamp durations + easings app-wide |
| Elevation (§3.7, 2 levels) | `CardDefaults.elevatedCardElevation(6.dp)` in places; `tonalElevation = 3.dp` on sidebar | collapse to 2 levels |
| Icons (§7.8, one family) | **Done** — 129 `ic_*.xml` vectors, no icon library | none |
| Glass (§3.4) | Blur exists in `CardTitleLayout(blur = …)` | clamp to overlay-only |

Hardcoded colors: **166** `Color(0x…)` occurrences under `ui/`, but 140 of them are the theme
definitions themselves (`Theme.kt` 70, `Color.kt` 70). Real leakage is ~26 across
`log_parser/`, `AccountElements.kt`, `_FakeShadow.kt`, `BuiltInFileManager.kt`, `Effects.kt`.

---

## Sidebar: spec-vs-code divergence, resolved

Spec Part 6.2 asks for ~220dp expanded / ~72dp collapsed, *"if the current launcher supports collapse
— if not, do not add collapse."* It does not collapse, and collapse is a **feature addition**. So:

- Keep the **72dp rail** as the only form.
- Do **not** implement 220dp/collapse.
- Spec's 220dp item height / 12dp padding / 8dp radius apply to the rail's own metrics, scaled
  sensibly to a 72dp rail: 48dp buttons → 48dp hit area, radius 12dp → mapped to `radius-md` 8dp.
- Add the 2dp accent left bar on Selected (spec detail, not present today — `SidebarNavButton`
  currently only tints bg + icon).
- Selected bg is `accent.copy(alpha = 0.15f)` today; spec says `rgba(220,38,38,0.10)` → retune to
  0.10f.

Note: the sidebar does **not** use `TextRailItem` — it uses a private `SidebarNavButton`
(`MainScreen.kt:393-422`). `MainScreen.kt:89` imports `TextRailItem` and never uses it (dead
import; harmless, remove). `TextRailItem`'s only live caller is
`VersionsManageElements.kt:330`.

---

## Files to Change

| File | Action | Why |
|---|---|---|
| `ui/screens/content/LauncherScreen.kt` | **FIX** then UPDATE | define/restore `RightMenu`, then redesign Home |
| `ui/theme/Type.kt` | UPDATE | the 10-style KROX type scale (currently empty) |
| `ui/theme/Theme.kt` | UPDATE | wire `AppShapes`/spacing; verify `kroxDark` contrast; `MotionScheme` retiming |
| `ui/theme/Palette.kt` | UPDATE | align semantic accessors to spec tokens |
| `ui/components/BackgroundCard.kt` | UPDATE | radius + 2-level elevation + hairline border |
| `ui/components/Buttons.kt` | UPDATE | primary/secondary/danger/ghost variants, 120ms states |
| `ui/components/_SimpleRail.kt` | UPDATE | 300ms→120ms, `extraLarge`→8dp, 16/8→12dp |
| `ui/screens/main/MainScreen.kt` | UPDATE | sidebar Selected left-bar, spec metrics, 6 items frozen |
| `ui/screens/_Navigation.kt` | UPDATE | clamp transition spec to ≤240ms |
| ~30 `ui/screens/content/**` + `settings/**` screens | UPDATE | per-screen layout/polish, no behavior change |
| `res/values/themes.xml` | UPDATE | `Theme.Material3.Light.NoActionBar` → `.Dark` (spec mandates dark identity) |
| `docs/audit/*.md` (18 files) | CREATE/UPDATE | Phase 0 audit, corrected |
| `docs/spec/tokens.md`, `components.md`, `icons.md` | CREATE | Phase 1 token docs |
| `.github/workflows/launcher-build.yml` | CREATE | the compile gate (Task 0b) |
| `docs/REGRESSIONS.md` | CREATE | per spec's rollback log |

**Never touched** (spec §2.3): `game/` (330 files / 38,602 LOC — launch engine), `path/`,
`library/`, `provider/`, `bridge/`, `database/`, `terracotta/`, `crashlogs/`, `utils/`,
`setting/` (except default values, if any), and every `entry<…>` wiring block in `MainScreen.kt`.

---

## Tasks

### Task 0 — Unblock (must complete first)

**0a. Fix `RightMenu`.** The prior session's edits left `LauncherScreen.kt` calling an undefined
composable. The call site fixes its contract exactly: `isVisible: Boolean`, `onLaunchGame: (Version?) -> Unit`,
three nav lambdas, `weight(3f)` right-aligned column. Reconstruct it against existing primitives
(`BackgroundCard`, `CardTitleLayout`, `TextRailItem`, `VersionsManager.currentVersion`, the existing
`ic_*` set) — no new functionality, just the pane that was lost. The launch event target
(`EventViewModel.Event.Launch.Game`) already exists and is unchanged.

**0b. GitHub CI becomes the compile gate.** Write `.github/workflows/launcher-build.yml`:
- `on: workflow_dispatch` + `push`/`pull_request` scoped to `ZalithLauncher/**`, `gradle/**`,
  `gradle.properties`, `settings.gradle.kts`, `build.gradle.kts`, `LWJGL/**`, `Terracotta/**`,
  `LayerController/**`, `ColorPicker/**`, `InputMap/**` (so docs-only pushes don't burn CI minutes).
- `actions/checkout@v4` with `submodules: recursive`; `actions/setup-java@v4` temurin **17**;
  `gradle/actions/setup-gradle@v4`.
- `./gradlew :ZalithLauncher:assembleDebug -Darch=${{ matrix.arch }}` over the existing 5-arch matrix
  (`all`, `arm`, `arm64`, `x86`, `x86_64`) — `arch` is read at `ZalithLauncher/build.gradle.kts:30`
  and drives the ABI splits.
- `actions/upload-artifact@v4` on the APK, `if: always()`.
- **No `client/` step** — that directory doesn't exist in this repo.
- Leave `build.yml` / `push_ci.yml` / `release_ci.yml` / `last.yml` alone; they are pre-existing and
  already broken, and touching them is not this project's job.

**Gate protocol:** after each phase, `gh workflow run launcher-build.yml` (or push), then
`gh run watch`. Phase N does not start until phase N-1's run is green. A red run *is* the regression
signal the spec asks for — identify the offending file, revert it, re-run, log in
`docs/REGRESSIONS.md`.

**0c. Baseline commit.** `git ls-files ZalithLauncher` returns **0** — the entire app is untracked
against the single `eedbe13 Initial commit`. The spec's regression strategy ("identify the culprit
commit → revert") is impossible without a baseline, and with CI as the gate a remote baseline is
mandatory. Commit the tree as-is (after 0a compiles) so every later phase has a revert target.

**Validate:** `./gradlew :ZalithLauncher:compileDebugKotlin` on CI, or the plan is explicitly
re-scoped as unverified.

---

### Task 1 — Design system (Phase 1)

- Write `docs/spec/tokens.md` — the frozen token table from §3, verbatim, as the single reference.
- `Type.kt`: replace `Typography()` with the 10-style scale (display/h1/h2/h3/body/bodyStrong/
  caption/meta/overline/mono) at the specified sp/weight/lineHeight.
- **Font family (decided):** `res/font/` does not exist, so no bundled typeface is available. Per the
  user's call, build the 10-style scale on the **platform default family**. No binary added to the
  repo; the Inter/Manrope deviation is recorded in `docs/spec/tokens.md`.
- Add spacing + radius scales; expose via the existing `Palette.kt` semantic-accessor idiom rather
  than a new abstraction.
- Collapse elevation to the 2 spec levels; wire the 2dp accent left-bar on rail Selected.
- Retime every animation to ≤240ms and to linear / ease-out / `cubic-bezier(0.2,0,0,1)` only.
- **Validate:** compiles; no `durationMillis = ` above 240 anywhere under `ui/`
  (`grep -rn "durationMillis = [3-9][0-9][0-9]" ui/` → empty).

### Task 2 — Navigation shell (Phase 2)

- `MainScreen.kt` sidebar: 6 items in frozen order, spec metrics, selected left-bar, 120ms
  transitions. Preserve every `onClick` body verbatim — they carry the `clearBeforeNavKeys` /
  `removeAndNavigateTo` semantics that keep back-stack behavior intact.
- Remove the dead `TextRailItem` import.
- **Validate:** all 6 destinations still resolve; back button still unwinds; the sidebar's callbacks
  are byte-identical to baseline.

### Task 3 — Home (Phase 3)

- Redesign `ContentMenu` (7/7 weight) and the restored `RightMenu` (3/7 weight) to spec.
- **Freeze:** the DEBUG warning card, `PlayTimeStatsCard` (title / value / subtitle — value keeps
  `MaterialTheme.colorScheme.primary`), and the three `HomePageState` branches (Blank / Loading /
  None). Custom home page is user content — restyle its container, never its content.
- **Validate:** `onLaunchGame` still fires `Event.Launch.Game`; all 3 nav lambdas still work;
  custom home page still renders.

### Task 4 — Remaining screens (Phase 4)

Per-screen layout/polish across Accounts, Versions, Settings + 9 subs, File Manager, Download + 13
subs, Logs, Statistics ×2, About, License, Multiplayer, Cape Gallery, Recordings, Web, File Editor,
Home Page Editor, File Selector, and the 11 dialogs. **Layout only.** Every existing `stringResource`,
every `onClick`, every `ViewModel` call, every nav key stays.

**Validate:** per screen — open it, confirm every interactive element reaches the same destination
as baseline. Compile each batch.

### Task 5 — Responsive (Phase 5)

Rearrange existing content only. `MainScreen.kt:261-271` already shows a 30%-width slide-in task
panel and a 7/7↔3/7 Home split — both need small-screen fallbacks. No new destinations.

### Task 6 — Polish (Phase 6)

States (hover/selected/focus/disabled/loading/empty/error) — **presentation only**; `DisabledAlpha`
is already `0.38f` (`CommonElements.kt:113`) and stays. Micro-interactions, spacing rhythm,
consistency sweep across all screens.

**DONE.** Hover/pressed: `KroxHoverOverlay` + `Color.shift()` in `Palette.kt`, wired into
`ScalingActionButton`, `ClickableBackgroundCard`, `_SimpleRail` — R-40, closes D-02.
Empty/error/loading: new `StateLabel` in `Layouts.kt` replaces `ScalingLabel` at all 24
sites across 14 files; 3 latent `Box` fill bugs fixed — R-41. `grep "ScalingLabel"` → 0
external callers (dead, kept — D-12). `grep "durationMillis = [3-9][0-9][0-9]"` → 0.

### Task 7 — QA (Phase 7)

Run the 30 QA items. Compilable subset in this environment; the rest need a device/emulator.

---

## Validation

Compile gate is CI. Local commands, for the pure-logic checks that don't need a toolchain:

```bash
# the compile gate (Task 0b)
gh workflow run launcher-build.yml && gh run watch

# spec conformance greps — these run anywhere
grep -rn "durationMillis = [3-9][0-9][0-9]" ZalithLauncher/src/main/java/  # → empty
grep -c "NormalNavKey\." ZalithLauncher/src/main/java/com/movtery/zalithlauncher/ui/screens/NormalNavKey.kt   # → unchanged from baseline
git diff --stat <baseline-sha> -- ZalithLauncher/src/main/java/com/movtery/zalithlauncher/game/  # → empty
```

Everything else needs the CI runner: `:ZalithLauncher:assembleDebug` (5 archs) and
`:ZalithLauncher:testDebugUnitTest` (7 existing tests, all non-UI).

---

## Risks

| Risk | Severity | Mitigation |
|---|---|---|
| ~~`RightMenu` reconstruction is guesswork — original is gone~~ **RESOLVED** | ~~**High**~~ → **None** | **Falsified.** `git reflog` surfaced codespace export `3d9ab76` with the intact 1029-line `LauncherScreen.kt`. `RightMenu`/`RightMenuContent`/`VersionManagerLayout` restored **verbatim** (Task 0a done). Restore was **selective**, not `git checkout` — the original also had a second rail and a Changelog/stats grid that a prior session intentionally removed. See `docs/audit/RISKS.md` R-03. |
| No local Android SDK — compile depends on CI | **High** | Task 0b **done**: `.github/workflows/launcher-build.yml`, temurin 17, `ndk-actions/setup-ndk@v1` pinned to 25.2.9519653 (the build uses `ndkBuild` + `src/main/jni/Android.mk`), 5-arch matrix, passwords via `ORG_GRADLE_PROJECT_*` from Actions secrets. **Not yet run** — blocked on R-01 secrets. Slower feedback than local, but the gate is honest. Pushing to a shared remote is a user-owned action — confirm before each push. |
| `ZalithLauncher/` untracked → no revert target | **High** | Task 0c baseline commit before any change. **Blocked:** `ZalithLauncher/gradle.properties` + `*.jks` were already published on public branch `codespace-special-halibut-r776q4jgqvr6fpvx4`; now gitignored, but key rotation and branch deletion are owner actions. See `docs/audit/RISKS.md` R-01. |
| Signing key + passwords already public (R-01) | **Critical** | Not a design risk — an active exposure. Password rotation is insufficient; the key itself must be replaced. Owner decision required before any push. |
| Spec IA (8 dests) ≠ real IA (6) | Medium | Frozen to real; Changelog not added; documented in `RISKS.md` |
| `Theme.Material3.Light.NoActionBar` parent vs. dark identity | Medium | Switch to `.Dark`; Compose `ColorScheme` overrides it anyway, so risk is splash/nav-bar only |
| No UI test infrastructure | Medium | Screens are Compose — no JVM test possible without adding Robolectric/compose-test. Not adding it (out of scope); QA-27 satisfied by manual checklist |
| Six pre-existing workflows are broken (invalid action tags, dead `client/` steps, `variant:` inputs on a `workflow_call`-less `build.yml`) | Low | Out of scope. Task 0b writes one working `launcher-build.yml` and leaves the rest untouched. |
| 79k LOC of UI across ~40 screens | Medium | 8 phases, compile-verify each, one phase per commit |

---

## Acceptance

- [ ] `RightMenu` defined; `launcher-build.yml` green
- [ ] Baseline commit pushed (revert target for the whole redesign)
- [ ] `kroxDark`/`kroxLight` match spec §3.1 exactly
- [ ] 10-style type scale shipped; spacing/radius/elevation/motion tokens at spec values
- [ ] No animation >240ms; only linear / ease-out / `cubic-bezier(0.2,0,0,1)`
- [ ] Sidebar = 6 destinations, original order, original callbacks, original nav keys
- [ ] Nav-key inventory in `NormalNavKey.kt` unchanged
- [ ] Launch path untouched: `Event.Launch.Game` → `tryLaunch` → engine
- [ ] `game/`, `path/`, `library/`, `setting/`, `database/` diffs = 0
- [ ] No new screens, no new nav keys, no Changelog
- [ ] 7 existing unit tests pass
- [ ] `docs/audit/` 18 files accurate; `RISKS.md` records the Changelog divergence
- [ ] Every QA item from the spec either verified with a command, or explicitly marked unverifiable here
