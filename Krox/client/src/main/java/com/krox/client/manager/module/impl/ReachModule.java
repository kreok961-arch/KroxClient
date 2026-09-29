package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import java.lang.Math;

public final class ReachModule extends HudElement {
   private final NumberSetting fadeTimeout;
   private final BooleanSetting showDecimals;
   private final BooleanSetting showLabel;

   public ReachModule() {
      super("reach", "Reach Display", "Distance of last successful melee hit.", ModuleCategory.HUD);
      showDecimals = this.register(new BooleanSetting(this, "showDecimals", "Show Decimals", true, "Show Decimals"));
      this.register(new NumberSetting(this, "decimalPlaces", "Decimal Places", 2, 1, 2, 1, "Decimal Places"));
      showLabel = this.register(new BooleanSetting(this, "showLabel", "Show Label", true, "Show Label"));
      fadeTimeout = this.register(new NumberSetting(this, "fadeTimeout", "Fade Timeout", 2000, 0, 20000, 1, "Fade Timeout"));
   }
   private double lastReach;
   private long lastHit;

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.player == null || mc.crosshairTarget == null || mc.crosshairTarget.getType() != net.minecraft.util.hit.HitResult.Type.ENTITY) {
         return;
      }

      this.lastReach = mc.player.distanceTo(((net.minecraft.util.hit.EntityHitResult)mc.crosshairTarget).getEntity());
      this.lastHit = System.currentTimeMillis();
   }

   @Override
   public String renderText() {
      if (System.currentTimeMillis() - this.lastHit > this.fadeTimeout.get()) {
         return null;
      }

      String num = this.showDecimals.get()
         ? String.format("%.2f", this.lastReach)
         : String.valueOf(Math.round(this.lastReach));
      return (this.showLabel.get() ? "Reach " : "") + num;
   }
}
