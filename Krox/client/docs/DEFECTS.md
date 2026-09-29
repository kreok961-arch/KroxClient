# Defect Register — KROXCLIENT v16.0 (the Fabric mod)

Scope: `Krox/client` only. The Android launcher under `ZalithLauncher/` keeps its own
log at `docs/REGRESSIONS.md`.

> **The two projects' defect numbers do not overlap.** `D-10` here is the `Can only blur
> once per frame` crash. `D-10` in `docs/REGRESSIONS.md` is an off-table corner radius in
> the launcher's Compose theme. Both are real; they are unrelated. Cite the file, not
> the number.

This file was reconstructed in-tree after the original register was lost with the
`/tmp` working directory that held the spec parser and module generator. Every entry
below is backed by a file in `Krox/client/src` or by a `javap` result recorded in the
entry. Nothing here is asserted from memory of the lost file.

---

## Resolved

| # | Symptom | Root cause | Fix | Evidence |
|---|---------|-----------|-----|----------|
| D-10 | `IllegalStateException: Can only blur once per frame` crashes the game whenever a screen with a blurred background is open. | `GuiRenderState.applyBlur` throws unless `blurLayer == Integer.MAX_VALUE`; only `GuiRenderState.clear` restores that sentinel. Vanilla `Screen.applyBlur` spends the frame's one mark, and Krox's own blur call then found it spent. | `util/BlurService` owns the mark. The gate sits on `DrawContext.applyBlur` — the single method vanilla, Krox, and any HUD widget all funnel through — not on individual callers. `claim()` keys off the `DrawContext` instance identity, which *is* frame identity: `GameRenderer.render` calls `GuiRenderState.clear` and then constructs exactly one `DrawContext`. | `mixin/MixinDrawContext.java` (HEAD, `cancellable`); `mixin/MixinScreenBlur.java` (cancels vanilla at HEAD of `Screen.applyBlur`, spends it at TAIL of `renderBackground`); `util/BlurService.java`. Built jar: `MixinDrawContext` → `method_71278`, `MixinScreenBlur` → `method_57734` + `method_25420`. |

### D-10 ordering, settled by decompile

`renderBackground` marks the blur *before* `renderDarkening`, so the darkening scrim
stays crisp. Krox marks it at TAIL instead. This is deliberate, not an oversight:
`net.minecraft.client.gui.render.GuiRenderer` renders `draws[0..blurLayer)` with
`LayerFilter.BEFORE_BLUR`, then `GameRenderer.renderBlur()`, then
`draws[blurLayer..end)` with `LayerFilter.AFTER_BLUR`. `renderDarkening` is a uniform
translucent fill, so blurring it is a no-op — the two placements are visually identical
and TAIL is the simpler one.

---

## Open

### D-04 — settings were declared but never registered

**Status: Verified.** Four defects, one root cause: `Module.register(T)` is the only
thing that puts a setting on `getSettings()`, and the modules were assigning their
setting fields directly.

Before the fix, `CpsLimiterModule` read:

```java
enabled = new BooleanSetting(this, "enabled", "Enabled", false, "Enabled");
```

so `getSettings()` was empty. Every consumer that walks the list saw nothing:

| Consumer | What it lost |
|---|---|
| `KroxClickGuiScreen` (3 sites) | no sliders, toggles or colours rendered — a module opened to a blank panel |
| `BlurService:152` | the blur strength setting, silently the default |

The fix routes the three fields through `register(...)`, which does
`settings.add(setting); setting.load();` — the single choke point. An audit of all
88 module impls now finds **zero** bare `= new *Setting(this, ...)` assignments
outside `this.register(...)`, so this class of defect is gone rather than fixed once.

**The part that mattered more than the missing settings:** the bug made
`CpsLimiterSelfCheck` pass **vacuously**. The check's `set()` helper looks a setting up
by id, and with an empty `getSettings()` it silently missed — so every assertion ran
against untouched defaults. It went green while the code path it claims to cover was
unreachable. When `register()` was fixed and the helper made strict, the check
immediately failed twice on two genuinely wrong assertions of its own (see CPS-d). A
green test that was green for the wrong reason is worse than no test; the strictness
is the fix, not a detail.

### D-11 — `Setting.cfg()` NPE outside a running game

**Status: Verified.** All 14 `cfg()` call sites across the 8 `Setting` subclasses did
`KroxClient.get().getClientManager().config()`. `KroxClient.get()` is a static
singleton that is null until the game constructs it, so any construction of a `Setting`
outside a live client — a self-check, a tool, an IDE eval — NPE'd out of every
`load()`/`save()`.

`Setting.cfg()` now returns a bare `new ConfigManager()` when the singleton is null.
Safe because `ConfigManager` is pure in-memory state once reached (see D-12). The
alternative, an NPE out of every load path, is worse. The same guard now exists in
`BlurService` and `MixinMouse`, the two other places that reach for the singleton
before the game is up.

### D-12 — `ConfigManager` resolved its paths in a static initializer

**Status: Verified.** `configDir`/`configFile` were `static final` fields initialised
at class-load. `javap` on the Yarn-mapped `FabricLoaderImpl` shows `getConfigDir()`
dereferences a field with no null guard and calls `Files.exists(configDir)` on it, so
touching `ConfigManager` from anywhere before the loader was live threw
`ExceptionInInitializerError` — and because it is a *static initializer*, the class
stays permanently broken for the life of the JVM, not just for the first call.

Paths are now resolved lazily by `dir()`/`file()`/`tmp()` on first use. In-game the
results are identical, since `getConfigDir()` is long since resolved by then.

---

### Wiring

| # | Symptom | Status |
|---|---------|--------|
| D-02a | HUD widgets could not be dragged. | **Verified.** `mixin/MixinMouse.java`. The drag hooks `onCursorPos` at `TAIL`, not `onMouseButton` — see D-02c. |
| D-02b | A click on a widget both dragged it *and* attacked. | **Verified.** `HudManager.onMouseClick` returns `boolean` documented as "returns true if the click was consumed", but the mixin ignored the return and never cancelled. Now `ci.cancel()` fires on a consumed press. |
| D-02c | The drag was originally hooked on `onMouseButton`. | **Wrong hook, fixed.** A drag is many cursor moves with no button state change, so the button callback can never drive it. `onCursorPos` is the only mid-drag path; `Mouse.getX/getY` give its scaled coordinates. |
| CPS-a | `CpsModule.record()` and `CpsLimiterModule.record()` had no caller, so CPS read 0 forever. | **Verified.** `MixinMouse.krox_recordCps` fires at the `HEAD` of `onMouseButton`, before the click reaches the game. GLFW action `1` is the press edge, so a held button counts once. |
| CPS-b | `CpsLimiterModule.shouldBlock()` had no caller anywhere, so the limiter could never block. | **Verified.** `MixinMouse.krox_limiterBlocks` now gates the press, and cancels the vanilla handler when it blocks. |
| CPS-c | `blockWhenExceeded` was a dead setting, and the click deque was only pruned inside `record()`. | **Verified.** The setting is gone, replaced by the `warnOnly` boolean that `shouldBlock()` actually reads. Pruning moved to `rate()`, so a stopped click stream decays to 0 instead of reading a stale non-zero. |
| CPS-d | The self-check asserted against settings it had already overridden, and asserted the blocking `! ` prefix while `warnOnly` was still `true`. | **Verified.** Both were defects in the check, not the module: `renderText()` and `shouldBlock()` were right and are unchanged. The default-ceiling assertion now runs *before* the `maxCps` override, and the unprefixed warn-only form is asserted before `warnOnly` is cleared. Only surfaced once D-04 made the helper strict. |
| — | Known behaviour, not a bug: clicks made while any `Screen` is open are **not** counted. The HEAD inject returns early on `currentScreen != null`, and the vanilla HUD does not render while a screen is open, so a CPS readout would be invisible anyway. Confirm this is wanted. | Needs owner confirmation. |

#### Why `onMouseButton` at `HEAD` is the right choke point

From `javap` on the Yarn-mapped `Mouse`: there is no `onAttack` / `doAttack` /
`rightClick` method. Attack and use are driven entirely by
`KeyBinding.setKeyPressed` at offsets 591–595, with `KeyBinding.onKeyPressed` at
603–605 when the press flag is set. Cancelling at `HEAD` is therefore both
necessary and sufficient — cancelling anywhere later would leave the key binding
latched.

#### Remap evidence (D-10, D-02, CPS)

The `@Inject(method = "...")` string literal must be remapped too, not just the
descriptors, or the mixin fails at class-load. Verified in the built jar with
`javap -v`: the `RuntimeVisibleAnnotations` read `method=["method_1601"]` with
`@At(value="HEAD")` and `cancellable=true`, and `method=["method_1600"]` with
`@At(value="TAIL")`. Against the **intermediary** jar, `class_312` (`Mouse`)
declares both:

```
private void method_1601(long, net.minecraft.class_11910, int);
private void method_1600(long, double, double);
```

from `mappings.tiny`: `method_1601` → `onMouseButton` `(JLgzd;I)V` and
`method_1600` → `onCursorPos` `(JDD)V`. `class_11910` (`MouseInput`) is a
`Record`; its two int components are `comp_4801` and `comp_4797`.

> `Mouse` is `class_312` (obf `gfk`), **not** `class_636`. Guessing the class id
> is how mixin work goes wrong; read `mappings.tiny` instead.

### Not in the module set

Four files in `manager/module/impl/` are not in the 86-module set and are not
registered in `ModuleManager`, so they never instantiate:

| File | Module id | Note |
|------|-----------|------|
| `ModuleListModule.java` | `modulelist` | hand-written, superseded by the generated set |
| `RotationLockModule.java` | `rotationlock` | hand-written |
| `SprintModule.java` | `autosprint` | hand-written; `ToggleSprintModule` (`togglesprint`) **is** registered and covers the same behaviour |
| `WatermarkModule.java` | `watermark` | hand-written; hardcodes `KROX v1.0.0` against `mod_version=16.0.0` |

Deleting dead code is the obvious call, but they are the owner's files, not mine, and
`autosprint` may be a wanted feature that simply missed the set. **Awaiting owner
decision** — nothing depends on them either way.

### Lost tooling

The 86 generated module files survive in `manager/module/impl/` and all 86 are
registered in `ModuleManager`. Re-verified by set-difference this pass: 90 impl files,
86 registered, 4 unregistered (the ones above), and **0 registered names with no impl
file** — the direction that would be a class-load crash. So nothing is missing. But the
**generator that produced them does not**: the spec parser,
the generator, and the 40-part mod spec lived in `/tmp` and went with the container.
Regenerating the module set is therefore not possible until that spec is recovered and
committed. `docs/spec/master-spec.md` is the **launcher's** spec (PART 0–8, Zalith
V31) and does not contain the module set. Re-checked this pass: the only other spec
candidates in the tree are `Krox/KROX_MASTER_PLAN.md` and `docs/spec/master-spec.md`,
both launcher documents. There is no mod spec in the tree under any name.

The commit is the fix, and it is bigger than it looks: `git status --porcelain` shows
the whole `Krox/` tree as `??` untracked, which is the same single point of failure that
lost the tooling in the first place.

### R-01 — signing key exposure

Owner action. Not a code change.

---

## Regression risk

Every fix in this file is a mixin, and mixins fail at class-load time: a wrong method
name, a wrong descriptor, or an unremapped reference is a crash before the game
window opens, not a runtime feature failure. The mapping to check is Yarn →
intermediary in the built jar, verified for the D-10 mixins above. Any new mixin needs
the same check before it is trusted.

The CPS limiter and the HUD drag are additionally covered by an out-of-game check,
`CpsLimiterSelfCheck` — how to run it is in `docs/qa/README.md`. It is the only
executable evidence in this file that is not a `javap` dump, and it is what caught
CPS-d. D-02a/b/c remain verified by build and remap inspection only: nothing here
executes a `DrawContext`, so a drag that wires up correctly but tracks the wrong
coordinates would still pass.

**Build evidence for every "Verified" above:** `gradlew build --rerun-tasks`,
9/9 tasks executed, `BUILD SUCCESSFUL`, plus
`CpsLimiterSelfCheck` → `all checks passed`. Both re-run on demand; neither is a
cached result.
