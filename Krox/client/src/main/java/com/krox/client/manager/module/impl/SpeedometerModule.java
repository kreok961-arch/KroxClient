package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import java.lang.Math;

public final class SpeedometerModule extends HudElement {
   private final NumberSetting decimalPlaces;
   private final BooleanSetting showHorizontal;
   private final BooleanSetting showUnit;
   private final BooleanSetting showVertical;

   public SpeedometerModule() {
      super("speedometer", "Speedometer", "Player movement speed in blocks per second.", ModuleCategory.HUD);
      showHorizontal = this.register(new BooleanSetting(this, "showHorizontal", "Show Horizontal", true, "Show Horizontal"));
      showVertical = this.register(new BooleanSetting(this, "showVertical", "Show Vertical", false, "Show Vertical"));
      showUnit = this.register(new BooleanSetting(this, "showUnit", "Show Unit", true, "Show Unit"));
      decimalPlaces = this.register(new NumberSetting(this, "decimalPlaces", "Decimal Places", 2, 1, 2, 1, "Decimal Places"));
   }
   private final java.util.ArrayDeque<net.minecraft.util.math.Vec3d> trail = new java.util.ArrayDeque<>();

   @Override
   protected void onTick() {
      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      if (p == null) {
         return;
      }

      this.trail.addLast(p.getEntityPos());
      while (this.trail.size() > 20) {
         this.trail.removeFirst();
      }
   }

   @Override
   public String renderText() {
      if (this.trail.size() < 2) {
         return null;
      }

      net.minecraft.util.math.Vec3d a = this.trail.peekFirst();
      net.minecraft.util.math.Vec3d b = this.trail.peekLast();
      String f = "%." + (int)this.decimalPlaces.get() + "f";
      StringBuilder sb = new StringBuilder();
      if (this.showHorizontal.get()) {
         sb.append(String.format(f, Math.hypot(b.x - a.x, b.z - a.z)));
      }

      if (this.showVertical.get()) {
         if (sb.length() > 0) {
            sb.append('/');
         }

         sb.append(String.format(f, Math.abs(b.y - a.y)));
      }

      if (this.showUnit.get()) {
         sb.append(" b/s");
      }

      return sb.toString();
   }
}
