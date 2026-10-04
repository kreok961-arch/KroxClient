package com.krox.client.core.event;

/**
 * The one bus instance. Mixins call {@link #post}; everything downstream of a
 * mixin goes through here.
 */
public final class KroxEvents {
   private static final EventBus BUS = new EventBus();

   private KroxEvents() {
   }

   public static EventBus bus() {
      return BUS;
   }

   public static <E extends KroxEvent> E post(E event) {
      return BUS.post(event);
   }

   public static <E extends KroxEvent> void register(Class<E> type, EventListener<E> listener) {
      BUS.register(type, listener);
   }

   public static <E extends KroxEvent> void register(Class<E> type, EventListener<E> listener, EventPriority priority) {
      BUS.register(type, listener, priority);
   }

   public static <E extends KroxEvent> void unregister(Class<E> type, EventListener<E> listener) {
      BUS.unregister(type, listener);
   }
}