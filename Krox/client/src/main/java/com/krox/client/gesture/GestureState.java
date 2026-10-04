package com.krox.client.gesture;

/**
 * Recognizer lifecycle, per the Pinch-to-Zoom spec §4.3.
 *
 * <pre>
 *   IDLE --(2 pointers down)--&gt; DETECTING --(moved past threshold)--&gt; ACTIVE
 *   ACTIVE --(finger lifted)--&gt; SETTLING --(animation done)--&gt; IDLE
 *   DETECTING|ACTIVE|SETTLING --(pointers below minimum)--&gt; CANCELLED --&gt; IDLE
 * </pre>
 */
public enum GestureState {
   /** No pointers, or all released. */
   IDLE,
   /** Exactly two pointers down, movement below threshold. */
   DETECTING,
   /** Recognized pinch. Scale factor is meaningful. */
   ACTIVE,
   /** Pinch released, zoom is easing back to 1.0. */
   SETTLING,
   /** Aborted mid-gesture -- extra finger, or pointers vanished. */
   CANCELLED;

   public boolean isLive() {
      return this == DETECTING || this == ACTIVE;
   }
}