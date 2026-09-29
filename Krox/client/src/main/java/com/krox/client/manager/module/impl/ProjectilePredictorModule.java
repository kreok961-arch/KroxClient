package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.MultiEnumSetting;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;
import java.lang.Math;

public final class ProjectilePredictorModule extends Module {
   public enum TypeOptions {
      ARROWS_,
      SNOWBALLS_,
      EGGS_,
      PEARLS_,
      SPLASH_POTIONS_
   }

   private final ColorSetting color;
   private final BooleanSetting dashed;
   private final BooleanSetting enabled;
   private final BooleanSetting onlyWhenAiming;
   private final NumberSetting width;

   public ProjectilePredictorModule() {
      super("projectile_predictor", "Projectile Predictor", "Shows trajectory for arrows, snowballs, eggs, ender pearls.", ModuleCategory.COMBAT);
      enabled = this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new MultiEnumSetting(this, "type", "Type", TypeOptions.class, new TypeOptions[]{TypeOptions.ARROWS_}, "Type"));
      color = this.register(new ColorSetting(this, "color", "Color", KroxTheme.TEXT_PRIMARY, "Color"));
      width = this.register(new NumberSetting(this, "width", "Width", 0, 0, 1, 0.01, "Width"));
      dashed = this.register(new BooleanSetting(this, "dashed", "Dashed", true, "Dashed"));
      onlyWhenAiming = this.register(new BooleanSetting(this, "onlyWhenAiming", "Only When Aiming", true, "Only When Aiming"));
   }
   @Override
   protected void onEnable() {
      com.krox.client.KroxClient.get().getClientManager().hud().register(this);
   }

   @Override
   protected void onDisable() {
      com.krox.client.KroxClient.get().getClientManager().hud().unregister(this);
   }

   @Override
   public boolean isCustomRender() {
      return this.enabled.get();
   }

   @Override
   public void renderCustom(net.minecraft.client.gui.DrawContext ctx, int screenWidth, int screenHeight) {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.player == null) {
         return;
      }

      if (this.onlyWhenAiming.get() && !net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), 0)) {
         return;
      }

      net.minecraft.util.math.Vec3d pos = mc.player.getEntityPos();
      net.minecraft.util.math.Vec3d vel = mc.player.getVelocity();
      int rgb = this.color.get() & 0xFFFFFF;
      int w = Math.max(1, (int)this.width.get());
      float[] a = new float[3];
      float[] b = new float[3];
      boolean have = false;

      for (int i = 0; i <= 60; i++) {
         double t = i / 10.0D;
         double x = pos.x + vel.x * t;
         double y = pos.y + 1.62D + vel.y * t - 0.05D * t * t;
         double z = pos.z + vel.z * t;
         if (!com.krox.client.util.WorldProjection.project(ctx, x, y, z, b)) {
            have = false;
            continue;
         }

         if (have && (!this.dashed.get() || i % 2 == 0)) {
            int col = com.krox.client.util.WorldProjection.fade(rgb, a[2]);
            for (int n = 0; n < w; n++) {
               com.krox.client.util.WorldProjection.line(ctx, a[0], a[1] + n, b[0], b[1] + n, col);
            }
         }

         a[0] = b[0];
         a[1] = b[1];
         a[2] = b[2];
         have = true;
      }
   }
}
