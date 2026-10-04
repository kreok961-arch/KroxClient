package com.krox.client.core.event;

/** Dispatch order. Lower ordinal runs first; ties keep registration order. */
public enum EventPriority {
   HIGHEST(0),
   HIGH(1),
   NORMAL(2),
   LOW(3),
   LOWEST(4);

   private final int slot;

   EventPriority(int slot) {
      this.slot = slot;
   }

   public int getSlot() {
      return this.slot;
   }
}