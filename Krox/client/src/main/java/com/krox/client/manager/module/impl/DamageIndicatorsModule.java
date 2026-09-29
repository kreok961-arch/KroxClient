package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;

public final class DamageIndicatorsModule extends Module {
   public enum StyleMode {
      CLASSIC_,
      MINIMAL_,
      CRIT_
   }

   private final NumberSetting fadeTime;
   private final BooleanSetting showForOthers;
   private final BooleanSetting showForSelf;

   public DamageIndicatorsModule() {
      super("damage_indicators", "Damage Indicators", "Floating damage numbers.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new EnumSetting<StyleMode>(this, "style", "Style", StyleMode.CLASSIC_, "Style"));
      this.register(new ColorSetting(this, "color", "Color", KroxTheme.TEXT_PRIMARY, "Color"));
      this.register(new ColorSetting(this, "critColor", "Crit Color", KroxTheme.WARNING, "Crit Color"));
      this.register(new NumberSetting(this, "riseSpeed", "Rise Speed", 0, 0, 1, 0.01, "Rise Speed"));
      fadeTime = this.register(new NumberSetting(this, "fadeTime", "Fade Time", 0, 0, 10, 1, "Fade Time"));
      showForSelf = this.register(new BooleanSetting(this, "showForSelf", "Show For Self", false, "Show For Self"));
      showForOthers = this.register(new BooleanSetting(this, "showForOthers", "Show For Others", true, "Show For Others"));
   }
   private final java.util.ArrayDeque<float[]> marks = new java.util.ArrayDeque<>();

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.player == null) {
         return;
      }

      if (this.showForOthers.get() && mc.crosshairTarget != null && mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY
         && net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), 0)) {
         this.marks.addLast(new float[]{(float)((net.minecraft.util.hit.EntityHitResult)mc.crosshairTarget).getEntity().getX(), (float)((net.minecraft.util.hit.EntityHitResult)mc.crosshairTarget).getEntity().getY() + 2.0F, (float)((net.minecraft.util.hit.EntityHitResult)mc.crosshairTarget).getEntity().getZ(), System.currentTimeMillis()});
      }

      if (this.showForSelf.get() && net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), 0)) {
         this.marks.addLast(new float[]{(float)mc.player.getX(), (float)mc.player.getY() + 2.0F, (float)mc.player.getZ(), System.currentTimeMillis()});
      }

      this.marks.removeIf(m -> System.currentTimeMillis() - m[3] > this.fadeTime.get());
   }

   @Override
   public boolean isCustomRender() {
      return !this.marks.isEmpty();
   }

   @Override
   public String renderText() {
      return null;
   }
}
