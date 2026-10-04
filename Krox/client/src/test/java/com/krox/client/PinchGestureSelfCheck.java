package com.krox.client;

import com.krox.client.gesture.GestureMath;
import com.krox.client.gesture.GestureState;
import com.krox.client.gesture.PinchGesture;
import com.krox.client.gesture.PinchGestureRecognizer;
import com.krox.client.gesture.PinchThreshold;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Vec2d;

/**
 * Z1 gate: the pinch recognizer must work with no platform input at all. Every
 * pointer event here is synthetic, which is exactly how it is meant to be driven
 * while GLFW has no touch backend (docs/adr/0007-pinch-touch-source.md).
 */
public final class PinchGestureSelfCheck {
   private PinchGestureSelfCheck() {
   }

   public static void main(String[] args) {
      detectsPinch();
      thresholdHoldsSmallJitter();
      scaleFactorTracksSpread();
      thirdFingerCancels();
      releaseSettlesThenIdles();
      resetClearsEverything();
      mathIsSane();
      System.out.println("[PinchGestureSelfCheck] all checks passed");
   }

   private static PinchGestureRecognizer rig(List<PinchGesture> out) {
      return new PinchGestureRecognizer(PinchThreshold.defaults(), out::add);
   }

   private static void down(PinchGestureRecognizer r, long id, double x, double y) {
      r.onDown(id, x, y, 0L);
   }

   private static void move(PinchGestureRecognizer r, long id, double x, double y) {
      r.onMove(id, x, y, 0L);
   }

   private static void up(PinchGestureRecognizer r, long id) {
      r.onUp(id, 0L);
   }

   private static void eq(double actual, double expected, String what) {
      if (Math.abs(actual - expected) > 1.0E-6D) {
         throw new AssertionError(what + ": expected " + expected + " got " + actual);
      }
   }

   private static void eq(boolean actual, boolean expected, String what) {
      if (actual != expected) {
         throw new AssertionError(what + ": expected " + expected + " got " + actual);
      }
   }

   private static void eq(Object actual, Object expected, String what) {
      if (!actual.equals(expected)) {
         throw new AssertionError(what + ": expected " + expected + " got " + actual);
      }
   }

   private static void detectsPinch() {
      List<PinchGesture> out = new ArrayList<>();
      PinchGestureRecognizer r = rig(out);

      down(r, 0L, 100.0D, 100.0D);
      eq(r.state(), GestureState.DETECTING, "one finger should be DETECTING");

      down(r, 1L, 200.0D, 100.0D);
      eq(r.state(), GestureState.DETECTING, "two fingers down should be DETECTING");

      move(r, 0L, 50.0D, 100.0D);
      move(r, 1L, 250.0D, 100.0D);
      eq(r.state(), GestureState.ACTIVE, "spread past threshold should be ACTIVE");
      eq(r.lastScale(), 2.0D, "distance doubled -> scale 2.0");
   }

   private static void thresholdHoldsSmallJitter() {
      List<PinchGesture> out = new ArrayList<>();
      PinchGestureRecognizer r = rig(out);

      down(r, 0L, 100.0D, 100.0D);
      down(r, 1L, 200.0D, 100.0D);
      // 4dp of travel: under the 16dp threshold and under 2% scale.
      move(r, 0L, 98.0D, 100.0D);
      move(r, 1L, 202.0D, 100.0D);
      eq(r.state(), GestureState.DETECTING, "jitter must not promote to ACTIVE");
      eq(out.isEmpty(), true, "jitter must not emit");
   }

   private static void scaleFactorTracksSpread() {
      PinchGestureRecognizer r = rig(new ArrayList<>());

      down(r, 0L, 0.0D, 0.0D);
      down(r, 1L, 200.0D, 0.0D);
      move(r, 0L, -50.0D, 0.0D);
      move(r, 1L, 250.0D, 0.0D);
      eq(r.lastScale(), 1.5D, "200 -> 300 distance is scale 1.5");

      move(r, 0L, 0.0D, 0.0D);
      move(r, 1L, 200.0D, 0.0D);
      eq(r.lastScale(), 1.0D, "back to start distance is scale 1.0");
   }

   private static void thirdFingerCancels() {
      List<PinchGesture> out = new ArrayList<>();
      PinchGestureRecognizer r = rig(out);

      down(r, 0L, 0.0D, 0.0D);
      down(r, 1L, 200.0D, 0.0D);
      move(r, 0L, -50.0D, 0.0D);
      move(r, 1L, 250.0D, 0.0D);
      eq(r.state(), GestureState.ACTIVE, "precondition: ACTIVE");

      down(r, 2L, 400.0D, 400.0D);
      eq(r.state(), GestureState.CANCELLED, "a third finger must cancel the pinch");
      eq(out.get(out.size() - 1).state(), GestureState.CANCELLED, "cancel must reach listeners");
   }

   private static void releaseSettlesThenIdles() {
      List<PinchGesture> out = new ArrayList<>();
      PinchGestureRecognizer r = rig(out);

      down(r, 0L, 0.0D, 0.0D);
      down(r, 1L, 200.0D, 0.0D);
      move(r, 0L, -50.0D, 0.0D);
      move(r, 1L, 250.0D, 0.0D);
      eq(r.state(), GestureState.ACTIVE, "precondition: ACTIVE");

      up(r, 0L);
      eq(r.state(), GestureState.SETTLING, "lifting a finger from ACTIVE must SETTLE");
      PinchGesture last = out.get(out.size() - 1);
      eq(last.state(), GestureState.SETTLING, "settle must reach listeners");
      eq(last.scaleFactor(), 1.0D, "settle reports 1.0 so zoom eases home");

      up(r, 1L);
      eq(r.state(), GestureState.IDLE, "no fingers left must be IDLE");
   }

   private static void resetClearsEverything() {
      PinchGestureRecognizer r = rig(new ArrayList<>());

      down(r, 0L, 0.0D, 0.0D);
      down(r, 1L, 200.0D, 0.0D);
      move(r, 0L, -50.0D, 0.0D);
      move(r, 1L, 250.0D, 0.0D);
      r.reset();
      eq(r.state(), GestureState.IDLE, "reset must return to IDLE");
      eq(r.tracker().count(), 0, "reset must forget every contact");
      eq(r.lastScale(), 1.0D, "reset must clear scale");
   }

   private static void mathIsSane() {
      eq(GestureMath.distance(new Vec2d(0.0D, 0.0D), new Vec2d(3.0D, 4.0D)), 5.0D, "3-4-5 triangle");
      eq(GestureMath.scaleFactor(0.0D, 0.0D), 1.0D, "zero start distance must not divide by zero");
      eq(GestureMath.clamp(5.0D, 1.0D, 3.0D), 3.0D, "clamp high");
      eq(GestureMath.clamp(-5.0D, 1.0D, 3.0D), 1.0D, "clamp low");
      eq(GestureMath.lerp(0.0D, 10.0D, 0.5D), 5.0D, "lerp midpoint");
      eq(GestureMath.smoothStep(0.0D), 0.0D, "smoothStep(0) is 0");
      eq(GestureMath.smoothStep(0.5D), 0.5D, "smoothStep is symmetric at 0.5");
      eq(GestureMath.smoothStep(1.0D), 1.0D, "smoothStep(1) is 1");
   }
}