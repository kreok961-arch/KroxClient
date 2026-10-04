# QA — out-of-game self-checks

Some logic in this mod can only be observed by staring at a running game: does the CPS
counter decay, does the limiter actually refuse a press, does the HUD redraw mid-drag.
That is a bad way to find out you are wrong. Each check below is a plain `main()` that
exercises the same code paths the game does, without the game.

No JUnit, no fixtures. The mod has never had a test source set, and one check does not
justify introducing a framework. When a check does need the classpath, it is spelled out
below rather than hidden behind a runner.

## Running a check

The checks live in `src/test/java`, not on the normal classpath. They need:

- `build/classes/java/main` and `build/classes/java/test` (compile first with
  `gradlew compileTestJava`)
- the Yarn-mapped Minecraft jar
- the loader/util jars, because `ConfigManager` and `Setting` touch `FabricLoader` and
  Gson even when the check never leaves in-memory state

```sh
cd Krox/client
bash gradlew compileTestJava

MCJAR=$(find ~/.gradle/caches/fabric-loom/minecraftMaven -name 'minecraft-merged-*.jar' \
        ! -name '*intermediary*' | head -1)
CP="build/classes/java/main:build/classes/java/test:build/resources/main:$MCJAR"
for p in slf4j log4j gson api fabric-loader fastutil datafixerupper joml \
         commons-lang authlib brigadier asm; do
  for j in $(find ~/.gradle/caches/modules-2/files-2.1 -name '*.jar' \
             | grep -i "/$p" | grep -v sources | grep -v javadoc); do
    CP="$CP:$j"
  done
done

java -cp "$CP" com.krox.client.CpsLimiterSelfCheck
java -cp "$CP" com.krox.client.HudDragSelfCheck
```

`SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder"` on the way out is
expected and harmless — the checks do not need a logger, and Minecraft's logging
implementation is not on this classpath.

## What is checked

### CpsLimiterSelfCheck

`com.krox.client.CpsLimiterSelfCheck` — the sliding rate window and the block gate.

It covers the four things that read as plausible right up until you watch them in game
and they are wrong:

1. **The window slides.** `rate()` prunes on read, not on record. A module that stopped
   receiving clicks has to decay to 0 rather than report its last count forever. This is
   a real regression the check was written against — pruning used to live in `record()`.
2. **Only clicks inside the last second count.** Two clicks, a pause, one click — all
   three are inside the window; after another pause only the third survives.
3. **The gate is off three ways.** `shouldBlock()` must not fire when the module is
   disabled, must not fire in warn-only mode, and must not fire under the ceiling. All
   three failure modes are indistinguishable in game, so all three are asserted.
4. **The HUD text tracks the mode.** Disabled renders nothing; warn-only over the
   ceiling renders unprefixed; blocking mode over the ceiling prefixes `! `.

Note the ordering rule these checks follow: assert a value **before** changing the
setting that would change it. Both of the two defects this check caught were a test
asserting against a default it had already overridden.

### HudDragSelfCheck

`com.krox.client.HudDragSelfCheck` — the HUD drag math, from press to release.

Reaching it at all required a change: `HudManager.onMouseDrag` read the window
through `MinecraftClient.getInstance()`, and it now takes the screen size as
parameters, as does `beginDrag` for the widget's top-left. The mixin already holds a
client, so nothing was lost — but with the static gone, the real drag path is
reachable from a plain `main()`.

It covers five things:

1. **A drag exists only between a press and a release.** Cursor movement with nothing
   pressed leaves the widget where it was.
2. **The press-time offset is what makes it track rather than snap.** The check grabs
   a widget 5px right and above its origin, then drags twice. A drag that dropped the
   offset would put the origin on the cursor; 5px over 639 is 0.0078, so it misses by
   three whole setting steps.
3. **A release ends the drag,** and a later cursor move must not resurrect it.
4. **The normalized result follows the size it was handed,** not a cached one — same
   cursor, half-size window, result roughly doubles.
5. **A value past 1.0 clamps rather than storing,** so a drag cannot fling a widget
   off screen.

It does not execute the mixin. Which method the hook injects into is settled by
`javap`, recorded in `docs/DEFECTS.md` — a hook on the wrong method would pass every
assertion here.

**Comparing a dragged position.** `NumberSetting.set()` rounds to the setting's step
before storing, so a value read back is a multiple of 0.001 and never the exact
quotient the drag math produced. The check puts its *expected* value through the same
rounding rather than widening the tolerance, so a real error still fails by whole
steps instead of hiding inside a fatter epsilon.

**The anti-vacuity rule.** A check here is not finished when it passes — it is
finished when it has been shown to *fail* against a deliberately broken
implementation. Five mutations of the drag math were run: dropping the x offset, dropping
the y offset, making `onMouseRelease` a no-op, caching a stale screen size, and dividing
by `screenW` instead of `screenW - 1`. Each was caught by a different assertion and all
five were reverted. This is the D-04 lesson: a check that is green for the wrong reason
is worse than no check at all.

The five kill lines are recorded in `docs/DEFECTS.md`. The lesson is not the count — it
is that each one dies on a *different* assertion. A check that only ever turns red for a
single reason knows one bug, not its subject.

## Adding one

Keep it to the smallest thing that fails if the logic breaks. If a check needs a mock
framework to run, the design is telling you something — reach for `register()`-style
seams in the module itself rather than growing the harness. If reaching the code
means deleting a static singleton from it, that is the fix, not the mock.
