package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import java.lang.Math;

public final class ComboModule extends HudElement {
   private final NumberSetting maxScale;
   private final NumberSetting resetTimeout;
   private final BooleanSetting scaleOnCombo;
   private final BooleanSetting showLabel;

   public ComboModule() {
      super("combo", "Combo Counter", "Consecutive hits on a target.", ModuleCategory.HUD);
      resetTimeout = this.register(new NumberSetting(this, "resetTimeout", "Reset Timeout", 2000, 0, 20000, 1, "Reset Timeout"));
      showLabel = this.register(new BooleanSetting(this, "showLabel", "Show Label", true, "Show Label"));
      scaleOnCombo = this.register(new BooleanSetting(this, "scaleOnCombo", "Scale On Combo", true, "Scale On Combo"));
      maxScale = this.register(new NumberSetting(this, "maxScale", "Max Scale", 1.5, 1, 2, 0.01, "Max Scale"));
   }
   private int hits;
   private long lastHit;

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.player != null && mc.crosshairTarget != null && mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY) {
         this.hits++;
         this.lastHit = System.currentTimeMillis();
      } else if (System.currentTimeMillis() - this.lastHit > this.resetTimeout.get()) {
         this.hits = 0;
      }
   }

   @Override
   public String renderText() {
      if (this.hits <= 1) {
         return null;
      }

      if (this.scaleOnCombo.get()) {
         this.scale.setValue(Math.min(this.maxScale.get(), 1.0D + this.hits * 0.05D), this.scale.getMin(), this.scale.getMax(), this.scale.getStep());
      }

      return (this.showLabel.get() ? "Combo " : "") + this.hits + "x";
   }
}
