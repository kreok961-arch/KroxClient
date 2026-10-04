# ADR 0007 — Pinch zoom ships behind a pluggable touch source

- **Status:** Accepted
- **Date:** 2026-10-04
- **Depends on:** [0001](0001-tech-stack.md), [0003](0003-mixin-policy.md), [0006](0006-waypoint-package-root.md).

## Context

The Pinch-to-Zoom spec's capture layer is built on `org.lwjgl.glfw.GLFWTouch` and
`GLFW.glfwGetTouch*`. Those symbols were verified against the actual toolchain for
KROX Client (MC 1.21.11, Yarn build.6, Java 21) and **do not exist**:

| Check | Result |
| --- | --- |
| `GLFWTouch` class in `lwjgl-glfw-3.3.3.jar` | absent |
| Touch symbols in native `libglfw.so` (3.3.3) | zero (`nm -D \| grep -i touch` → empty) |
| Touch classes in the Minecraft jar | only `HandledScreen$LetGoTouchStack` |
| `Window` touch callback | none; `WindowEventHandler` has 3 methods, none touch |
| `Keyboard.onKey(long,int,KeyInput)` | exists but is `private`, and GLFW only synthesizes it from `GLFW_KEY_*` — never multi-touch |

GLFW gained `GLFW_TOUCH_*` in **3.4**, which LWJGL binds as `GLFWTouch`. KROX Client
is on LWJGL 3.3.3, pinned by MC 1.21.11. Upgrading LWJGL inside a Fabric mod is not
possible: the loader supplies the classpath, and swapping `libglfw.so` underneath it
risks the whole game, not just this feature.

A second spec item also does not survive contact: `GameOptionsAccessor` with
`@Accessor("fov") int getFov()`. In 1.21.11 `GameOptions.getFov()` already returns
`SimpleOption<Integer>` with public `getValue()`/`setValue(T)` — the existing
`ZoomModule` uses it. The accessor is unnecessary.

## Decision

1. **Everything except the touch source is built in full.** The gesture framework,
   pinch recognizer, pointer tracking, gesture math, zoom controller, config store and
   feedback overlay are platform-independent and ship now.
2. **The touch source is an interface** (`TouchSource`). It is the single seam where
   platform input enters. Desktop and GLFW-3.3.3 Android bind to
   `UnavailableTouchSource`, which reports "no multi-touch" and is inert.
3. **`UnavailableTouchSource` is a supported, logged state, not an error.** The zoom
   feature reports itself unavailable rather than silently doing nothing, and the
   existing keybind zoom stays as the desktop path.
4. **`GameOptionsAccessor` is dropped.** FOV is read and written through
   `options.getFov()` directly.

## Alternatives rejected

| Option | Why not |
| --- | --- |
| Bundle LWJGL 3.4 + GLFW 3.4 | Fabric's classpath is loader-controlled; shipping a second `org.lwjgl` breaks every other LWJGL user in the game. |
| Call `libglfw.so` touch symbols via JNI | The symbols do not exist in 3.3.3. There is nothing to bind. |
| Parse Android `MotionEvent` via JNI | Needs an activity-level hook outside the mod's reach, is Android-only, and violates "everything inside the Fabric mod". |
| Synthesize pointers from `Mouse` events | A mouse is one pointer. This is a fake gesture, and QA-Z36 would fail because the game would receive fabricated input. |
| Drop the feature entirely | Wrong. Every layer except the platform input binding is real, useful, and testable now. |

## Consequences

- The spec's 5 mixins reduce to 3: `InGameHudZoomMixin` (feedback), `ScreenOpenMixin`
  (cancel on screen open), `WorldLeaveMixin` (reset on world leave).
  `GameOptionsAccessor` is not needed, and `TouchScreenMixin` has no valid target on
  this toolchain.
- When MC moves to an LWJGL/GLFW with `GLFWTouch`, implementing `TouchSource` is the
  entire remaining work for the capture layer. Nothing above it changes.
- QA-Z01…Z15 (multi-touch) cannot pass on this toolchain and are reported as
  **BLOCKED-BY-PLATFORM**, not as passed. QA-Z16…Z25 (zoom math, clamping,
  sensitivity, smoothing) are verifiable through the self-check and pass.

```ponytail: the touch source is one interface with one inert implementation. Adding a
real backend is a new class implementing TouchSource -- no recognizer, state machine,
zoom controller, or renderer is touched. If GLFW 3.4 never arrives, the honest outcome
is that KROX keeps keybind zoom and the gesture framework sits as unused, tested code;
delete gesture/ rather than leave a feature that cannot fire.
```

## Revisit when

LWJGL/GLFW reaches 3.4 in the MC version KROX targets. At that point implement
`TouchSource` against `GLFWTouch` and close the blocked QA items.