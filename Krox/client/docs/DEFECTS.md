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
| `BlurService:103,107` via `num()`→`find()` at `:147` | `screen_blur.strength` and `glass_ui.blurStrength` read as `0.0`, so `wants()` returned 0 and Krox left every frame to vanilla |

The fix routes the three fields through `register(...)`, which does
`settings.add(setting); setting.load();` — the single choke point. An audit of all
90 module impls now finds **zero** bare `= new *Setting(this, ...)` assignments
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

**Status: Verified.** All 14 `cfg()` call sites across the 7 `Setting` subclasses did
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
at class-load. `javap -c` on `FabricLoaderImpl` (fabric-loader 0.19.5, a *separate*
artifact — it is not in the Minecraft jar) shows `getConfigDir()` at offset 0
dereferences `configDir` with no null guard, passes it to `Files.exists`, and
`createDirectories` on the false branch, so
touching `ConfigManager` from anywhere before the loader was live threw
`ExceptionInInitializerError` — and because it is a *static initializer*, the class
stays permanently broken for the life of the JVM, not just for the first call.

Paths are now resolved lazily by `dir()`/`file()`/`tmp()` on first use. In-game the
results are identical, since `getConfigDir()` is long since resolved by then.

---

### Wiring

| # | Symptom | Status |
|---|---------|--------|
| D-02a | HUD widgets could not be dragged. | **Verified.** `mixin/MixinMouse.java`. The drag hooks `onCursorPos` at `TAIL`, not `onMouseButton` — see D-02c. The drag math itself is covered out of game by `HudDragSelfCheck`. |
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

#### Remap evidence — all five registered mixins

The `@Inject(method = "...")` string literal must be remapped too, not just the
descriptors, or the mixin fails at class-load. A wrong `cancellable` on an
`@Inject` is the same class of bug. Verified in the built jar with `javap -v`:
every registered mixin's `RuntimeVisibleAnnotations` read as below. This now
covers **all five**, not just the three the entries above discuss.

| Mixin | `method=[...]` | `@At` | cancellable | declared on |
|--------|---------------|-------|-------------|-------------|
| `MixinDrawContext` | `method_71278` | HEAD | yes | `class_332` `DrawContext` (`gir`) |
| `MixinScreenBlur` | `method_57734` | HEAD | yes | `class_332` `DrawContext` |
| `MixinScreenBlur` | `method_25420` | TAIL | no | `class_332` `DrawContext` |
| `MixinMouse` | `method_1601` | HEAD | yes | `class_312` `Mouse` (`gfk`) |
| `MixinMouse` | `method_1600` | TAIL | no | `class_312` `Mouse` |
| `MixinTitleScreen` | `method_25426` | HEAD | yes | `class_437` `Screen` — **inherited** |
| `MixinPlayerEntityRenderer` | `method_62604` | TAIL | no | `class_1007` `PlayerEntityRenderer` |

`MixinMouse`'s two injections resolve to `onMouseButton` `(JLgzd;I)V` and
`onCursorPos` `(JDD)V`; against the **intermediary** jar, `class_312` (`Mouse`)
declares both:

```
private void method_1601(long, net.minecraft.class_11910, int);
private void method_1600(long, double, double);
```

`class_11910` (`MouseInput`) is a
`Record`; its two int components are `comp_4801` and `comp_4797`.

> `Mouse` is `class_312` (obf `gfk`), **not** `class_636`. Guessing the class id
> is how mixin work goes wrong; read `mappings.tiny` instead.

**`MixinTitleScreen` injects an inherited method.** `method_25426` is
`Screen.init` on `class_437`; `TitleScreen` is `class_442` and declares no `init`
of its own, so a HEAD injection with `cancellable=true` genuinely suppresses
vanilla initialisation rather than a duplicate that never existed. Injecting an
inherited method is valid — the check that matters is that the target does *not*
redeclare it.

**`MixinPlayerEntityRenderer` has the most surface, because it also touches
fields and record components.** An unremapped field reference is the same crash
class as an unremapped method name, and the descriptor has to match to the
parameter:

| Reference in the mixin | intermediary | resolved |
|---|---|---|
| injection descriptor | `(class_11890, class_10055, float, CallbackInfo)` | ≡ `updateRenderState` `(Lcgc;Lick;F)V` |
| `state.skinTextures` | `field_53520` | on `class_10055` |
| `state.capeVisible` | `field_53532` | on `class_10055` |
| `SkinTextures` components | `comp_1626..comp_1630` | `body`, `cape`, `elytra`, `model`, `secure` |

`class_11890` is `PlayerLikeEntity`, which is what the parameter actually is —
not `PlayerEntity`. The mixin's whole body sits inside a `try/catch (Throwable)`
that logs and falls back to vanilla, so even a null skin container degrades
rather than crashes.

### The four hand-written modules — now registered

**Status: Fixed.** Four files in `manager/module/impl/` sat outside the generated
86-module set and were never passed to `ModuleManager.register`, so they never
instantiated. The owner asked for them to be added; all four are now registered and the
set is complete.

| File | Module id | Note |
|------|-----------|------|
| `ModuleListModule.java` | `modulelist` | hand-written; right-aligned multi-line HUD widget |
| `RotationLockModule.java` | `rotationlock` | hand-written; already D-04-compliant, its setting goes through `register(...)` |
| `SprintModule.java` | `autosprint` | hand-written; see the overlap note below |
| `WatermarkModule.java` | `watermark` | hand-written; version hardcode removed, see D-13 |

Re-verified by set-difference after the change: **90 impl files, 90 registered, 0
unregistered, 0 registered names with no impl file, 0 duplicate ids.** The
registered-without-an-impl direction is the one that would be a class-load crash.

**`autosprint` and `toggle_sprint` are not duplicates.** `SprintModule` sprints while
`options.forwardKey` is held and food > 6; `ToggleSprintModule` flips sprinting on a
keypress. Different ids, different triggers, both safe to register.

### D-13 — the watermark hardcoded a version that was wrong

**Status: Verified.** `WatermarkModule.renderText()` returned the literal
`"KROX v1.0.0"` while `gradle.properties:10` sets `mod_version=16.0.0`. Harmless while
the module was unreachable; the moment it was registered the HUD would have lied on
every release, and the lie would have survived every future bump since the string was
not derived from anything.

`renderText()` now reads the version from the loader, using the same API `ModPresence`
already calls: `FabricLoader.getInstance().getModContainer("krox").getMetadata()
.getVersion().getFriendlyString()`. Verified by `javap` on fabric-loader 0.19.5 —
`getModContainer(String)` returns `Optional<ModContainer>`, and the friendly string
lives on `net.fabricmc.loader.api.Version` (**not** `...api.metadata.ModVersion`,
which does not exist). Falls back to `?` if the container is absent.

The general rule: a version string belongs in `gradle.properties` and is stamped into
`fabric.mod.json` at build time. A second copy of it in Java is a second source of
truth, and it will drift.

### Lost tooling

The 86 generated module files survive in `manager/module/impl/`, and with the four
hand-written ones now registered (see above) the set is complete: **90 impl files, 90
registered, 0 orphans in either direction.** So nothing is missing. But the
**generator that produced them does not**: the spec parser,
the generator, and the 40-part mod spec lived in `/tmp` and went with the container.
Regenerating the module set is therefore not possible until that spec is recovered and
committed. `docs/spec/master-spec.md` is the **launcher's** spec (PART 0–8, Zalith
V31) and does not contain the module set. Re-checked this pass: the only other spec
candidates in the tree are `Krox/KROX_MASTER_PLAN.md` and `docs/spec/master-spec.md`,
both launcher documents. There is no mod spec in the tree under any name.

The tree is now committed, so that half of the problem is closed: `git ls-files Krox/`
returns 171 files, against 0 at `eedbe13`. An earlier revision of this section argued
the point with `git status --porcelain` showing the whole `Krox/` tree as `??`. That was
true when written and stopped being true at `4704ef0`; re-checked this pass, the only
untracked entry is `src/test/java/com/krox/client/HudDragSelfCheck.java`.

The lesson that survives is narrower, and it is about the tooling rather than the tree:
the spec parser and the generator lived in `/tmp` and were never committed *anywhere*,
so a container that dropped `/tmp` dropped them irrecoverably. The `Krox/` tree is now
safe from that specific fate. Nothing is safe from a container unless it is in git, and
nothing outside the working directory is in git unless it is committed.

### R-01 — signing key exposure

Owner action. Not a code change.

---

## Regression risk

Every fix in this file is a mixin, and mixins fail at class-load time: a wrong method
name, a wrong descriptor, or an unremapped reference is a crash before the game
window opens, not a runtime feature failure. The mapping to check is Yarn →
intermediary in the built jar, verified for all five registered mixins in the table
above. Any new mixin needs the same check before it is trusted.

The CPS limiter and the HUD drag are covered by out-of-game checks,
`CpsLimiterSelfCheck` and `HudDragSelfCheck` — how to run either is in
`docs/qa/README.md`. They are the only executable evidence in this file that is
not a `javap` dump. `CpsLimiterSelfCheck` is what caught CPS-d, and it caught it
only once D-04 forced its `set()` helper to be strict; `HudDragSelfCheck` was
written afterwards, with that lesson applied.

**What `HudDragSelfCheck` does and does not settle.** Making it possible was a
2-line signature change: `onMouseDrag` now takes the screen size as parameters, and
`beginDrag` takes the widget's top-left, both already held by the mixin. That
removes `MinecraftClient` from the whole drag path, which was the only thing
blocking it. Built jar confirms the new descriptor `onMouseDrag:(DDII)V`.

Covered, against the real `HudManager`: a drag exists only between a press and a
release; the origin tracks the cursor with the press-time offset held constant; the
normalized result follows the size it is handed rather than a cached one; and a
value past 1.0 clamps instead of storing, so a drag cannot fling a widget off screen.

**Not covered, and not claimed:** the mixin is never executed. The check
instantiates no `Mouse`, so *which* method the hook fires into — the actual D-02c
defect — still rests on the `javap` remap evidence above and nothing else. A hook
pointed at the wrong method would pass every assertion here.

Five mutations were run to confirm the check is not vacuous, the failure D-04 records.
Dropping the x offset, dropping the y offset, making `onMouseRelease` a no-op, caching
a stale screen size, and dividing by `screenW` rather than `screenW - 1` — each was
caught by a *different* assertion, and all five were reverted:

```
offset dropped      -> KILLED  "x keeps the press-time offset, got 0.364"
release no-op       -> KILLED  "cursor moves after release do not move the widget"
stale 480 cached    -> KILLED  "shorter window divides by the size it was given"
divide by screenW   -> KILLED  "x offset stays constant over a longer drag"
y offset dropped    -> KILLED  "y keeps the press-time offset"
```

The first two were run when the check was written; the other three came from a
verification pass, which is also where the `onMouseDrag` descriptor and the D-02c hook
repointing below were checked. That the five fail on five *different* lines is the
point — a check that only ever turns red for one reason has not been shown to be
load-bearing, it has been shown to know one bug.

**Build evidence for every "Verified" above:** `gradlew build --rerun-tasks`,
9/9 tasks executed, `BUILD SUCCESSFUL`, producing
`build/libs/krox-client-16.0.0.jar` (253,668 bytes, 201 entries, zip integrity
verified, `fabric.mod.json` stamped `id: krox` / `version: 16.0.0`), plus
`CpsLimiterSelfCheck` → `all checks passed` and `HudDragSelfCheck` →
`all checks passed`. All three re-run on demand; none is a cached result.

**The build does not run with the committed `org.gradle.jvmargs=-Xmx2G` on this
machine.** Total RAM is ~7.9 GB with no swap, and the box already holds ~5.8 GB of
other processes. The first attempt died with *Gradle build daemon disappeared
unexpectedly* — and worse, `remapJar` died **mid-write**, leaving a 22-byte
`krox-client-16.0.0.jar` that looked like a successful build's output. Compiled
classes and the sources jar were both intact, so exit-code-blindness here would have
shipped a corrupt artifact. The working invocation is:

```sh
bash gradlew build --rerun-tasks --no-parallel --no-daemon \
  -Dorg.gradle.jvmargs="-Xmx1200m -XX:MaxMetaspaceSize=384m"
```

`gradle.properties` is left alone deliberately — it is a committed file, and the
constraint is this machine's memory, not the project's. If the daemon is ever raised,
check the jar's size, not the exit code: a 22-byte jar with a successful-looking
log is the failure mode here.
