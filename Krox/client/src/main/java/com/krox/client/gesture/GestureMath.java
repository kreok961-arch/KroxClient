package com.krox.client.gesture;

import net.minecraft.util.math.Vec2d;

/** Pure math for two-pointer pinch. No state, no side effects. */
public final class GestureMath {
   private GestureMath() {
   }

   /** Distance between two pointers, in dp. */
   public static double distance(Vec2d a, Vec2d b) {
      double dx = a.x - b.x;
      double dy = a.y - b.y;
      return Math.sqrt(dx * dx + dy * dy);
   }

   /** Midpoint of the two pointers. */
   public static Vec2d center(Vec2d a, Vec2d b) {
      return new Vec2d((a.x + b.x) * 0.5D, (a.y + b.y) * 0.5D);
   }

   /** current / start. &gt;1 means fingers spread, &lt;1 means they closed. */
   public static double scaleFactor(double currentDistance, double startDistance) {
      if (startDistance <= 1.0E-6D) {
         return 1.0D;
      }

      return currentDistance / startDistance;
   }

   public static double clamp(double value, double min, double max) {
      return value < min ? min : Math.min(value, max);
   }

   public static double lerp(double from, double to, double t) {
      return from + (to - from) * clamp(t, 0.0D, 1.0D);
   }

   /** 3t^2 - 2t^3. Zoom easing, so the first pixels of finger travel are not a jerk. */
   public static double smoothStep(double t) {
      double c = clamp(t, 0.0D, 1.0D);
      return c * c * (3.0D - 2.0D * c);
   }
}