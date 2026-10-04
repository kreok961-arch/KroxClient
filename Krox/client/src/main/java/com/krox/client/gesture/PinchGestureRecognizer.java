package com.krox.client.gesture;

import net.minecraft.util.math.Vec2d;
import java.util.List;
import java.util.function.Consumer;

/**
 * The pinch state machine, spec §5.5. Consumes pointer down/move/up and emits
 * {@link PinchGesture} on every observation. Nothing platform-specific lives here,
 * which is the point: the self-check drives it synthetically, the touch source
 * drives it in game.
 *
 * <p>ponytail: O(1) per event over a map of contacts. Fine at 10 fingers; a device
 * reporting hundreds of contacts would need a different structure, and it will not.
 */
public final class PinchGestureRecognizer {
   private final PointerTracker tracker = new PointerTracker();
   private final PinchThreshold threshold;
   private final Consumer<PinchGesture> sink;

   private GestureState state = GestureState.IDLE;
   private double startDistance;
   private Vec2d startCenter = Vec2d.ZERO;
   private Vec2d center = Vec2d.ZERO;
   private long startedAtMs;
   private double lastEmittedScale = 1.0D;

   public PinchGestureRecognizer(PinchThreshold threshold, Consumer<PinchGesture> sink) {
      this.threshold = threshold;
      this.sink = sink;
   }

   public GestureState state() {
      return this.state;
   }

   public PointerTracker tracker() {
      return this.tracker;
   }

   /** Live contacts, exposed for the HUD finger-count badge. */
   public List<PointerData> pointers() {
      return this.tracker.all();
   }

   /** Last scale pushed to listeners; 1.0 when idle. */
   public double lastScale() {
      return this.lastEmittedScale;
   }

   public void onDown(long id, double x, double y, long nowMs) {
      this.tracker.down(id, x, y, nowMs);
      this.reconcile(nowMs);
   }

   public void onMove(long id, double x, double y, long nowMs) {
      this.tracker.move(id, x, y);
      this.reconcile(nowMs);
   }

   public void onUp(long id, long nowMs) {
      this.tracker.up(id);
      this.reconcile(nowMs);
   }

   /** World change: the gesture is meaningless once we leave the world. */
   public void reset() {
      this.tracker.clear();
      this.state = GestureState.IDLE;
      this.startDistance = 0.0D;
      this.lastEmittedScale = 1.0D;
      this.startedAtMs = 0L;
   }

   /**
    * One place decides the state, so down/move/up/extra-finger cannot drift apart.
    */
   private void reconcile(long nowMs) {
      int count = this.tracker.count();

      if (count == 0) {
         if (this.state == GestureState.ACTIVE) {
            this.transition(GestureState.SETTLING);
            this.emit(nowMs, 1.0D);
         } else if (this.state != GestureState.IDLE) {
            this.transition(GestureState.IDLE);
         }

         return;
      }

      if (count > 2) {
         // A third finger is not a pinch. Cancel rather than guess.
         this.transition(GestureState.CANCELLED);
         this.emit(nowMs, 1.0D);
         return;
      }

      if (count == 1) {
         if (this.state == GestureState.ACTIVE) {
            this.transition(GestureState.SETTLING);
            this.emit(nowMs, 1.0D);
         } else if (this.state.isLive()) {
            this.transition(GestureState.IDLE);
         }

         return;
      }

      PointerData[] two = this.tracker.firstTwo();

      if (two == null) {
         return;
      }

      Vec2d a = two[0].pos();
      Vec2d b = two[1].pos();
      double distance = GestureMath.distance(a, b);
      this.center = GestureMath.center(a, b);

      if (!this.state.isLive()) {
         this.startDistance = distance;
         this.startCenter = this.center;
         this.startedAtMs = nowMs;
         this.lastEmittedScale = 1.0D;
         this.transition(GestureState.DETECTING);
         return;
      }

      if (this.state == GestureState.DETECTING && !this.threshold.isExceeded(this.startDistance, distance)) {
         return;
      }

      if (this.state == GestureState.DETECTING) {
         this.transition(GestureState.ACTIVE);
      }

      this.emit(nowMs, GestureMath.scaleFactor(distance, this.startDistance));
   }

   private void transition(GestureState next) {
      this.state = next;
   }

   private void emit(long nowMs, double scale) {
      this.lastEmittedScale = scale;
      double rotation = Math.toDegrees(
         Math.atan2(this.center.y - this.startCenter.y, this.center.x - this.startCenter.x));
      this.sink.accept(new PinchGesture(
         this.state,
         scale,
         this.center,
         rotation,
         this.startDistance,
         this.startDistance * scale,
         this.tracker.count(),
         Math.max(0L, nowMs - this.startedAtMs)));
   }
}