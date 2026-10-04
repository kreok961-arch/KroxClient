package com.krox.client.core.event;

import com.krox.client.KroxClient;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Priority-ordered, cancellable dispatch. Three rules, each from a real failure
 * mode:
 *
 * <ul>
 *   <li>Every listener call is try/caught. One module throwing must not abort a
 *       HUD render or a client tick.
 *   <li>The sort runs on a copy. A module disabling itself from its own handler
 *       mutates the registry mid-dispatch; that has to be safe.
 *   <li>The sort is stable, so same-priority listeners keep registration order.
 * </ul>
 */
public final class EventBus {
   private static final Comparator<Registration<?>> BY_PRIORITY =
      Comparator.comparingInt(r -> r.priority.getSlot());

   private final Map<Class<? extends KroxEvent>, List<Registration<?>>> listeners = new ConcurrentHashMap<>();

   public <E extends KroxEvent> void register(Class<E> type, EventListener<E> listener, EventPriority priority) {
      this.listeners
         .computeIfAbsent(type, t -> new CopyOnWriteArrayList<>())
         .add(new Registration<>(listener, priority));
   }

   public <E extends KroxEvent> void register(Class<E> type, EventListener<E> listener) {
      this.register(type, listener, EventPriority.NORMAL);
   }

   @SuppressWarnings("unchecked")
   public <E extends KroxEvent> E post(E event) {
      List<Registration<?>> list = this.listeners.get(event.getClass());
      if (list == null || list.isEmpty()) {
         return event;
      }

      // Snapshot: a listener may register or unregister while we dispatch.
      List<Registration<?>> ordered = new ArrayList<>(list);
      ordered.sort(BY_PRIORITY);

      for (Registration<?> r : ordered) {
         try {
            ((EventListener<E>)r.listener).onEvent(event);
         } catch (Throwable var3) {
            KroxClient.LOGGER.error(
               "[Krox] Listener for {} threw - skipping it, bus keeps going.", event.getClass().getSimpleName(), var3);
         }

         if (event.isCancelled()) {
            break;
         }
      }

      return event;
   }

   public <E extends KroxEvent> void unregister(Class<E> type, EventListener<E> listener) {
      List<Registration<?>> list = this.listeners.get(type);
      if (list == null) {
         return;
      }

      for (Registration<?> r : list) {
         if (r.listener == listener) {
            list.remove(r);
            return;
         }
      }
   }

   public void clear() {
      this.listeners.clear();
   }

   private static final class Registration<E extends KroxEvent> {
      final EventListener<E> listener;
      final EventPriority priority;

      Registration(EventListener<E> listener, EventPriority priority) {
         this.listener = listener;
         this.priority = priority;
      }
   }
}