package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;
import java.lang.Math;

public final class HitmarkersModule extends Module {
   public enum StyleMode {
      CROSS_,
      X_,
      DOT_,
      RING_
   }

   private final BooleanSetting criticalVariant;
   private final NumberSetting duration;
   private final NumberSetting size;
   private final EnumSetting<StyleMode> style;

   public HitmarkersModule() {
      super("hitmarkers", "Hitmarkers", "Visual/audio feedback on hit.", ModuleCategory.COMBAT);
      style = this.register(new EnumSetting<StyleMode>(this, "style", "Style", StyleMode.CROSS_, "Style"));
      this.register(new ColorSetting(this, "color", "Color", KroxTheme.TEXT_PRIMARY, "Color"));
      size = this.register(new NumberSetting(this, "size", "Size", 16, 4, 32, 1, "Size"));
      duration = this.register(new NumberSetting(this, "duration", "Duration", 300, 0, 3000, 1, "Duration"));
      this.register(new BooleanSetting(this, "sound", "Sound", true, "Sound"));
      this.register(new NumberSetting(this, "soundVolume", "Sound Volume", 0.5, 0, 1, 0.01, "Sound Volume"));
      criticalVariant = this.register(new BooleanSetting(this, "criticalVariant", "Critical Variant", true, "Critical Variant"));
   }
   private long hitAt;

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY
         && net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), 0)) {
         this.hitAt = System.currentTimeMillis();
      }
   }

   @Override
   public String renderText() {
      long age = System.currentTimeMillis() - this.hitAt;
      if (age > this.duration.get()) {
         return null;
      }

      int arms = Math.max(1, (int)(this.size.get() / 8.0D));
      String shape = switch (this.style.get()) {
         case StyleMode.CROSS_ -> "+";
         case StyleMode.X_ -> "x";
         case StyleMode.DOT_ -> ".";
         default -> "o";
      };
      return (this.criticalVariant.get() && age < this.duration.get() / 2 ? "!" : "") + shape.repeat(arms);
   }
}
