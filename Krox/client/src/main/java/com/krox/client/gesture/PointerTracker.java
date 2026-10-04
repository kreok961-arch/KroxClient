package com.krox.client.gesture;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Down/move/up bookkeeping for every finger on screen. Platform-agnostic: it is
 * fed by whatever the touch source reports and knows nothing about GLFW.
 */
public final class PointerTracker {
   private final Map<Long, PointerData> pointers = new LinkedHashMap<>();

   public void down(long id, double x, double y, long nowMs) {
      this.pointers.put(id, new PointerData(id, x, y, nowMs));
   }

   /** Unknown id is ignored: a MOVE with no matching DOWN is not ours to crash on. */
   public void move(long id, double x, double y) {
      PointerData old = this.pointers.get(id);

      if (old != null) {
         this.pointers.put(id, new PointerData(id, x, y, old.downTimeMs()));
      }
   }

   public boolean up(long id) {
      return this.pointers.remove(id) != null;
   }

   public void clear() {
      this.pointers.clear();
   }

   public int count() {
      return this.pointers.size();
   }

   /** Live pointers in the order they went down. */
   public List<PointerData> all() {
      return new ArrayList<>(this.pointers.values());
   }

   /** The two oldest contacts, or null when fewer than two are down. */
   public PointerData[] firstTwo() {
      if (this.pointers.size() < 2) {
         return null;
      }

      List<PointerData> all = this.all();
      return new PointerData[]{all.get(0), all.get(1)};
   }
}