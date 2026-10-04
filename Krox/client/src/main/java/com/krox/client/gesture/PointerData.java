package com.krox.client.gesture;

import net.minecraft.util.math.Vec2d;

/**
 * One live finger, in dp-scaled screen space.
 *
 * @param id platform pointer id; stable for the life of the contact
 * @param x x in dp
 * @param y y in dp
 * @param downTimeMs timestamp of the DOWN event, for tap/long-press later
 */
public record PointerData(long id, double x, double y, long downTimeMs) {
   public Vec2d pos() {
      return new Vec2d(this.x, this.y);
   }
}