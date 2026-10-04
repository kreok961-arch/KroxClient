package com.krox.client;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.hud.HudManager;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

/**
 * Self-check for the HUD drag (defect D-02a). Plain main() with throws rather than
 * JUnit, matching CpsLimiterSelfCheck -- the mod has no test source set.
 *
 * <p>This covers the part of a drag that reads as correct while being wrong: the offset
 * captured at press time. A drag that forgets it snaps the widget's origin to the
 * cursor, which looks fine for the first few pixels and then throws the widget across
 * the screen. Nothing in the source makes that visible.
 *
 * <p>The screen size and the widget's top-left are passed in rather than read from the
 * MinecraftClient singleton, which is what makes the path reachable from here at all --
 * see HudManager.onMouseDrag and HudManager.beginDrag.
 *
 * <p>It does not execute the mixin. What it covers is the part the hook feeds: that a
 * drag exists only between a press and a release, that the origin tracks the cursor
 * with the press-time offset held constant, and that the normalized result follows the
 * window size it is handed. Which method the mixin injects into is settled by javap, in
 * docs/DEFECTS.md.
 *
 * <p>Run it: see docs/qa/README.md.
 */
public final class HudDragSelfCheck {
   /**
    * A HudElement subclass that never reaches a TextRenderer or a window, so it can be
    * dragged without a game. posX/posY are inherited and real, so moveTo writes the
    * same settings a live widget does -- asserted via reflection because the check is
    * not in the module's package.
    */
   private static final class DragWidget extends HudElement {
      DragWidget() {
         super("dragcheck", "Drag Check", "Self-check fixture.", ModuleCategory.HUD);
      }

      @Override
      public String renderText() {
         return "KROX";
      }
   }

   private static final int W = 640;
   private static final int H = 480;

   public static void main(String[] args) throws Exception {
      noDragWithoutAPress();
      widgetTracksTheCursorNotTheOrigin();
      releaseStopsTheDrag();
      survivesAResize();
      cannotBeDraggedOffTheEdge();
      System.out.println("HudDragSelfCheck: all checks passed");
   }

   /** A drag only exists between a press and a release. */
   private static void noDragWithoutAPress() throws Exception {
      HudManager hud = new HudManager();
      DragWidget w = new DragWidget();
      setPos(w, 0.2, 0.3);

      for (int i = 0; i < 5; i++) {
         hud.onMouseDrag(100.0 + i, 50.0, W, H);
      }

      check(closeX(nx(w), 0.2, w) && closeY(ny(w), 0.3, w), "cursor moves with nothing pressed leave the widget put");
   }

   /**
    * The offset captured at press time is what keeps the widget under the cursor instead
    * of snapping its origin to it.
    */
   private static void widgetTracksTheCursorNotTheOrigin() throws Exception {
      HudManager hud = new HudManager();
      DragWidget w = new DragWidget();
      setPos(w, 0.2, 0.3);

      // Widget origin in screen space: 0.2*639 and 0.3*479. Press 5px right and 5px
      // above it, so every later origin must land at cursor-5 on both axes. A 5px
      // offset over 639px is 0.0078 -- three steps -- so a drag that dropped the
      // offset would miss these by far more than the tolerance, which is the point.
      double ox = 0.2 * (W - 1);
      double oy = 0.3 * (H - 1);
      check(hud.beginDrag(w, ox + 5.0, oy + 5.0, ox, oy), "beginDrag reports the press as consumed");

      hud.onMouseDrag(ox + 105.0, oy + 55.0, W, H);
      check(closeX(nx(w), 0.2 + 100.0 / (W - 1), w), "x keeps the press-time offset, got " + nx(w));
      check(closeY(ny(w), 0.3 + 50.0 / (H - 1), w), "y keeps the press-time offset, got " + ny(w));

      // Further out, same 5px offset: the widget tracks rather than drifts.
      hud.onMouseDrag(ox + 205.0, oy + 105.0, W, H);
      check(closeX(nx(w), 0.2 + 200.0 / (W - 1), w), "x offset stays constant over a longer drag, got " + nx(w));
      check(closeY(ny(w), 0.3 + 100.0 / (H - 1), w), "y offset stays constant over a longer drag, got " + ny(w));
   }

   /** The button going up ends the drag; a later cursor move must not resurrect it. */
   private static void releaseStopsTheDrag() throws Exception {
      HudManager hud = new HudManager();
      DragWidget w = new DragWidget();
      setPos(w, 0.2, 0.3);
      hud.beginDrag(w, 0.2 * (W - 1) + 5.0, 0.3 * (H - 1) + 5.0, 0.2 * (W - 1), 0.3 * (H - 1));
      hud.onMouseDrag(ox0() + 50.0, oy0() + 50.0, W, H);
      check(!closeX(nx(w), 0.2, w), "setup: the drag moved the widget before release");

      hud.onMouseRelease();
      double held = nx(w);
      hud.onMouseDrag(400.0, 300.0, W, H);
      check(closeX(nx(w), held, w), "cursor moves after release do not move the widget");
   }

   /**
    * Positions are normalized, which is the point: the drag must divide by the screen
    * size it was handed, not a cached one. Same cursor position, smaller window. The
    * cursor stays well inside both windows, because a normalized value past 1.0 is
    * clamped rather than stored -- that is {@link #cannotBeDraggedOffTheEdge()}.
    */
   private static void survivesAResize() throws Exception {
      HudManager hud = new HudManager();
      DragWidget w = new DragWidget();
      setPos(w, 0.0, 0.0);

      // Grabbed the origin exactly, so the normalized result is the cursor itself.
      hud.beginDrag(w, 0.0, 0.0, 0.0, 0.0);
      hud.onMouseDrag(100.0, 50.0, W, H);
      check(closeX(nx(w), 100.0 / (W - 1), w), "wide window, got " + nx(w));
      check(closeY(ny(w), 50.0 / (H - 1), w), "wide window, got " + ny(w));

      // Same cursor, half-size window: the normalized result must roughly double.
      hud.onMouseDrag(100.0, 50.0, W / 2, H / 2);
      check(closeX(nx(w), 100.0 / (W / 2 - 1), w), "narrower window divides by the size it was given, got " + nx(w));
      check(closeY(ny(w), 50.0 / (H / 2 - 1), w), "shorter window divides by the size it was given, got " + ny(w));
   }

   /**
    * A normalized position past 1.0 cannot be stored, so a drag cannot fling a widget
    * off the screen. Found the hard way: the first version of the resize check used a
    * cursor on the right edge of the small window, and read 1.0 rather than the
    * quotient -- correct, but not what the check was trying to say.
    */
   private static void cannotBeDraggedOffTheEdge() throws Exception {
      HudManager hud = new HudManager();
      DragWidget w = new DragWidget();
      setPos(w, 0.0, 0.0);

      hud.beginDrag(w, 0.0, 0.0, 0.0, 0.0);
      hud.onMouseDrag(10_000.0, 10_000.0, W, H);
      check(closeX(nx(w), 1.0, w), "past the right edge clamps to 1.0, got " + nx(w));
      check(closeY(ny(w), 1.0, w), "past the bottom edge clamps to 1.0, got " + ny(w));

      hud.onMouseDrag(-10_000.0, -10_000.0, W, H);
      check(closeX(nx(w), 0.0, w), "left of the origin clamps to 0.0, got " + nx(w));
      check(closeY(ny(w), 0.0, w), "above the origin clamps to 0.0, got " + ny(w));
   }

   private static double ox0() {
      return 0.2 * (W - 1);
   }

   private static double oy0() {
      return 0.3 * (H - 1);
   }

   private static void setPos(DragWidget w, double x, double y) throws Exception {
      posX(w).set(x);
      posY(w).set(y);
   }

   private static double nx(DragWidget w) throws Exception {
      return posX(w).get();
   }

   private static double ny(DragWidget w) throws Exception {
      return posY(w).get();
   }

   /** The fields are protected in another package; reflection keeps the check honest about what it reads. */
   private static NumberSetting posX(DragWidget w) throws Exception {
      return (NumberSetting)field("posX").get(w);
   }

   private static NumberSetting posY(DragWidget w) throws Exception {
      return (NumberSetting)field("posY").get(w);
   }

   private static java.lang.reflect.Field field(String name) throws Exception {
      java.lang.reflect.Field f = HudElement.class.getDeclaredField(name);
      f.setAccessible(true);
      return f;
   }

   /**
    * A dragged position is stored through {@link NumberSetting#set}, which rounds to
    * the setting's step before storing, so the value read back is a multiple of 0.001
    * and never the exact quotient the drag math produced. The expected value is put
    * through the same rounding rather than merely given a fatter tolerance, so the
    * assertion still fails on a real error -- a drag that dropped the offset lands
    * whole steps away, not half a step.
    */
   private static boolean closeX(double actual, double expected, DragWidget w) throws Exception {
      return close(actual, expected, posX(w).getStep());
   }

   private static boolean closeY(double actual, double expected, DragWidget w) throws Exception {
      return close(actual, expected, posY(w).getStep());
   }

   private static boolean close(double actual, double expected, double step) {
      return Math.abs(actual - (double)Math.round(expected / step) * step) < 1e-9;
   }

   private static void check(boolean ok, String what) {
      if (!ok) {
         throw new AssertionError("HUD drag self-check failed: " + what);
      }
   }

   private HudDragSelfCheck() {
   }
}
