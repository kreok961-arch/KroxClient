package com.krox.client.core.event;

/**
 * Base for every event on the Krox bus. Separate from Fabric's callbacks on
 * purpose: we need priorities and cancellation, and Fabric's bus has neither.
 * See docs/adr/0002-event-bus.md.
 */
public abstract class KroxEvent {
   private final long timestamp = System.currentTimeMillis();
   private boolean cancelled;

   public long getTimestamp() {
      return this.timestamp;
   }

   /** Non-cancellable by default: a listener vetoing someone is opt-in, not the norm. */
   public boolean isCancellable() {
      return false;
   }

   public void cancel() {
      if (this.isCancellable()) {
         this.cancelled = true;
      }
   }

   public boolean isCancelled() {
      return this.cancelled;
   }
}