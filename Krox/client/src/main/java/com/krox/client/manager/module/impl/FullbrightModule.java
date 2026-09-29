package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class FullbrightModule extends Module {
   public enum ModeMode {
      SIMPLE_,
      GAMMA_,
      CUSTOM_
   }

   private final NumberSetting customGamma;
   private final EnumSetting<ModeMode> mode;
   private final BooleanSetting onlyInCaves;

   public FullbrightModule() {
      super("fullbright", "Fullbright", "Client-side maximum brightness.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      mode = this.register(new EnumSetting<ModeMode>(this, "mode", "Mode", ModeMode.SIMPLE_, "Mode"));
      customGamma = this.register(new NumberSetting(this, "customGamma", "Custom Gamma", 10, 0, 20, 0.1, "Custom Gamma"));
      onlyInCaves = this.register(new BooleanSetting(this, "onlyInCaves", "Only In Caves", false, "Only In Caves"));
   }
   private double restoreGamma = -1.0D;

   @Override
   protected void onEnable() {
      apply();
   }

   @Override
   protected void onDisable() {
      restore();
   }

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.player == null || mc.world == null) {
         return;
      }

      // "Only in caves" means the player cannot see the sky, so the
      // gamma override is lifted while outdoors.
      if (this.onlyInCaves.get()
         && mc.world.getDimension().hasSkyLight()
         && mc.world.isSkyVisibleAllowingSea(mc.player.getBlockPos())) {
         restore();
         return;
      }

      apply();
   }

   private void apply() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.options == null) {
         return;
      }

      if (this.restoreGamma < 0.0D) {
         this.restoreGamma = mc.options.getGamma().getValue();
      }

      double want = switch (this.mode.get()) {
         case ModeMode.SIMPLE_ -> 16.0D;
         case ModeMode.GAMMA_ -> 16.0D;
         default -> this.customGamma.get();
      };

      // GameOptions clamps gamma to 0..1, so the spec's 0-20 dial is mapped
      // onto that range before it is written.
      mc.options.getGamma().setValue(net.minecraft.util.math.MathHelper.clamp(want / 16.0D, 0.0D, 1.0D));
   }

   private void restore() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (this.restoreGamma >= 0.0D && mc.options != null) {
         mc.options.getGamma().setValue(this.restoreGamma);
      }

      this.restoreGamma = -1.0D;
   }
}
