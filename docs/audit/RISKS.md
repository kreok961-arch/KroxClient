# Phase 0 Audit — Risks

## R-01 — Signing key and passwords published on a public branch (CRITICAL, open)

**Status:** OPEN. Owner decision required. Not fixable by the redesign.

**Finding.** `kreok961-arch/KroxClient` is a public repository. The remote branch
`origin/codespace-special-halibut-r776q4jgqvr6fpvx4` (tip `3d9ab76`, "Pending changes
exported from your codespace") carries, in its tree:

- `ZalithLauncher/zalith_launcher_debug.jks` — the file **both** signing configs use
  (`ZalithLauncher/build.gradle.kts:49` `releaseBuild`, `:55` `debugBuild`)
- `ZalithLauncher/zalith_launcher.jks`
- `ZalithLauncher/gradle.properties` with plaintext `default_store_password` and
  `default_key_password`

Verified with `git ls-tree` / `git cat-file -p` against the fetched remote ref. The key
alias is `movtery_zalith_debug`. Anyone can read all four items with a plain clone.

**Why this outranks a routine secret leak.** The leaked key is the app-signing key for
the release build, not a build-internal credential. Rotation of the *password* does not
help — the private key itself must be replaced. Until it is, any party can produce an
APK that Android accepts as this application.

**Mitigation applied so far (repo-local, reversible):**

- `.gitignore` now covers `ZalithLauncher/*.jks` and `ZalithLauncher/gradle.properties`,
  so the Phase 0 baseline commit cannot re-publish them. Confirmed via
  `git check-ignore -v` and `git ls-files --error-unmatch` (not tracked).
- `.github/workflows/launcher-build.yml` supplies the two passwords to the build as
  `ORG_GRADLE_PROJECT_default_store_password` / `..._default_key_password` from Actions
  secrets, so CI no longer depends on the properties file existing. This uses Gradle's
  built-in `ORG_GRADLE_PROJECT_*` convention; **no** change to `build.gradle.kts` was
  required.

**Remaining, requires the owner:**

1. Generate a replacement key; re-sign release and debug with it. Treat the current key
   as compromised even if no misuse is known.
2. `git push origin --delete codespace-special-halibut-r776q4jgqvr6fpvx4`
3. Decide on history. Deleting the branch does not remove the blobs: existing clones keep
   them, and GitHub may retain unreachable objects. On a public repo a history rewrite is
   the only complete cleanup. **Not performed — destructive, unasked.**

**Open blocker for CI.** The local `gh` token returns
`HTTP 403: Resource not accessible by integration` for `gh secret set`, so
`DEFAULT_STORE_PASSWORD` and `DEFAULT_KEY_PASSWORD` cannot be created from this
environment. They must be added in the repo web UI, or `gh` must be re-authenticated with
`repo` scope. Until then the workflow will fail at `build.gradle.kts:26-27`.

## R-02 — Changelog and log shortcuts absent from Home (accepted divergence)

`LauncherScreen` originally exposed `onNavigateToStats`, `onNavigateToPlayTimeStats`, and
`onNavigateToLog`, reachable from an in-Home `SideBar` rail and a 2x2 `StatsGrid`
(`WeeklyPlayTimeChart`, `DailyPlayTimeCard`, `LastLogCard`, `ChangelogCard`). Those
elements were removed in an earlier session, before the redesign began, together with the
`CHANGELOGS_URL` / `CHANGELOGS_UPDATE_TR` constants.

The restore of `RightMenu` + `VersionManagerLayout` from `3d9ab76` deliberately did **not**
reintroduce them: `MainScreen.kt` currently calls `LauncherScreen` without those three
lambdas, and the spec's one-line rule is *"if the current launcher does not already have
it, the redesign must not add it."* Restoring them would be a functional addition.

**Consequence:** the underlying destinations still exist in `NormalNavKey`
(`GameStats`, `PlayTimeStats`, `LogView`), but nothing in the UI navigates to them from
Home. If they are found to be genuinely unreachable, that is a **pre-existing** defect
from before the redesign and should be raised separately, not fixed under this spec.

## R-03 — `RightMenu` reconstruction is no longer guesswork (RESOLVED, plan correction)

The approved plan carried this as its top risk: *"`RightMenu` reconstruction is guesswork
— original is gone | **High**"*. That is **falsified**. `git reflog` surfaced codespace
export commit `3d9ab76`, which contains the intact 1029-line `LauncherScreen.kt`.
`RightMenu` (:762), `RightMenuContent` (:616), and `VersionManagerLayout` (:799) were
restored verbatim rather than re-derived.

The restore was **selective**, not a whole-file checkout. `git checkout 3d9ab76 --` was
considered and rejected: diffing `MainScreen.kt` showed the original had *two* rails and a
Changelog/stats grid that had been intentionally removed (see R-02). A full checkout would
have silently re-added them.

## R-04 — `SideBar.kt` is dead code violating the motion spec (open, cosmetic)

`ui/screens/content/elements/SideBar.kt` is now 0-reference after the selective restore.
It is also a spec §3.6 violation: it animates with
`spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)`
— spec permits no bounce/elastic/spring.

Left in place. Deletion is a scope decision, not a compile gate, and it is unrelated to
the visual redesign. Candidate for removal during Phase 6 if confirmed still unreferenced.

## R-05 — Spec §3.6 contradicts two shipped user settings (accepted, owner to confirm)

Spec §3.6 says *"nothing above 240ms, and no bounce / elastic / spring."* Two things in the
launcher already violate that, and both are **user-facing settings that shipped**:

1. **Bounce / jelly-bounce page transitions.** `utils/animation/AnimationUtils.kt:102-152`
   exposes `getAnimateTweenBounce` / `getAnimateTweenJellyBounce` /
   `getSwapAnimateTween(swapIn = …)`, reachable through the
   `AllSettings.launcherSwapAnimateType` setting with `TransitionAnimationType.BOUNCE`
   and `.JELLY_BOUNCE`.
2. **The animation-speed slider's scale.** `getAnimateSpeed()` (:64-68) is
   `calculateAnimationTime(AllSettings.launcherAnimateSpeed.state, 1500, 0.1f)`. At the
   default setting the derived duration is well past 240ms.

Both are preserved deliberately. The one-line rule is *"if the launcher has it, the redesign
must not remove it."* Deleting the bounce option removes a shipped setting; re-clamping
`calculateAnimationTime` would silently change how every existing animation feels, which
is behaviour, not appearance. Both were left byte-identical.

**Enforcement is therefore directional, not absolute:**

- The redesign **never writes a new** bounce / spring / elastic / >240ms transition.
- Existing user-selectable options stay, and stay in QA scope as a known divergence.

Related and *not* accepted: the default-`animationSpec` fallbacks inside Compose
(`animateDpAsState` / `animateColorAsState` default to `spring()`) were real violations
in redesigned code and have been pinned to the KROX scale — see `tokens.md` §6.2.

**Owner action:** accept or reject this divergence explicitly. Rejection means the shipped
setting is removed, which is a behaviour change and needs its own ADR.
