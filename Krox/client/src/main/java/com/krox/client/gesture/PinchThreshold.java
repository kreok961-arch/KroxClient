package com.krox.client.gesture;

/**
 * Movement needed before a pinch is real. Below this, finger jitter from a palm
 * resting on the glass would swing the FOV.
 *
 * <p>Two knobs, from spec §5.4: absolute finger travel in dp, and relative scale
 * change. Whichever trips first promotes DETECTING to ACTIVE.
 */
public final class PinchThreshold {
   public static final double DEFAULT_MIN_DISTANCE_DP = 16.0D;
   public static final double DEFAULT_MIN_SCALE_DELTA = 0.02D;

   private final double minDistanceDp;
   private final double minScaleDelta;

   public PinchThreshold(double minDistanceDp, double minScaleDelta) {
      this.minDistanceDp = minDistanceDp;
      this.minScaleDelta = minScaleDelta;
   }

   public static PinchThreshold defaults() {
      return new PinchThreshold(DEFAULT_MIN_DISTANCE_DP, DEFAULT_MIN_SCALE_DELTA);
   }

   public double minDistanceDp() {
      return this.minDistanceDp;
   }

   public double minScaleDelta() {
      return this.minScaleDelta;
   }

   /** True once either absolute travel or relative scale has moved enough. */
   public boolean isExceeded(double startDistanceDp, double currentDistanceDp) {
      double travel = Math.abs(currentDistanceDp - startDistanceDp);
      if (travel >= this.minDistanceDp) {
         return true;
      }

      double scale = GestureMath.scaleFactor(currentDistanceDp, startDistanceDp);
      return Math.abs(scale - 1.0D) >= this.minScaleDelta;
   }
}