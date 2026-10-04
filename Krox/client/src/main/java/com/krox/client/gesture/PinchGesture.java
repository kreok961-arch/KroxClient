package com.krox.client.gesture;

import net.minecraft.util.math.Vec2d;

/**
 * One pinch observation, spec §4.4.2. Immutable; the recognizer emits a new one
 * every move rather than mutating, so a listener can hold onto it safely.
 *
 * @param state recognizer state at emit time
 * @param scaleFactor currentDistance / startDistance; &gt;1 spread, &lt;1 closed
 * @param center midpoint of the two fingers, dp
 * @param rotationDegrees line angle in degrees, so the HUD indicator can rotate
 * @param startDistance finger separation when the pinch began, dp
 * @param currentDistance finger separation now, dp
 * @param pointerCount contacts on screen, so listeners can ignore 3+ finger cases
 * @param elapsedMs time since the pinch began
 */
public record PinchGesture(
      GestureState state,
      double scaleFactor,
      Vec2d center,
      double rotationDegrees,
      double startDistance,
      double currentDistance,
      int pointerCount,
      long elapsedMs) {
   /** Signed zoom delta from the gesture start; &gt;0 means spread. */
   public double scaleDelta() {
      return this.scaleFactor - 1.0D;
   }
}