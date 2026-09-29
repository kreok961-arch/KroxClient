package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;
import java.lang.Math;

public final class ReachCircleModule extends Module {
   private final ColorSetting color;
   private final BooleanSetting pulseOnAttack;
   private final NumberSetting radius;
   private final BooleanSetting showOnlyInCombat;
   private final NumberSetting thickness;

   public ReachCircleModule() {
      super("reach_circle", "Reach Display Circle", "Visual ring showing melee reach.", ModuleCategory.COMBAT);
      radius = this.register(new NumberSetting(this, "radius", "Radius", 3, 3, 6, 0.1, "Radius"));
      color = this.register(new ColorSetting(this, "color", "Color", KroxTheme.ACCENT, "Color"));
      thickness = this.register(new NumberSetting(this, "thickness", "Thickness", 1, 0.5, 3, 0.1, "Thickness"));
      pulseOnAttack = this.register(new BooleanSetting(this, "pulseOnAttack", "Pulse On Attack", true, "Pulse On Attack"));
      showOnlyInCombat = this.register(new BooleanSetting(this, "showOnlyInCombat", "Show Only In Combat", true, "Show Only In Combat"));
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
      return true;
   }

   @Override
   public void renderCustom(net.minecraft.client.gui.DrawContext ctx, int screenWidth, int screenHeight) {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      net.minecraft.client.network.ClientPlayerEntity p = mc.player;
      if (p == null || (this.showOnlyInCombat.get() && p.getAttacker() == null)) {
         return;
      }

      double r = this.radius.get();
      if (this.pulseOnAttack.get() && net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), 0)) {
         r *= 1.05D;
      }

      double ox = p.getX();
      double oy = p.getY() + 0.1D;
      double oz = p.getZ();
      int rgb = this.color.get() & 0xFFFFFF;
      int th = Math.max(1, (int)this.thickness.get());
      float[] a = new float[3];
      float[] b = new float[3];
      boolean have = com.krox.client.util.WorldProjection.project(ctx, ox + r, oy, oz, a);

      for (int i = 3; i <= 360; i += 3) {
         double t = Math.toRadians(i);
         double x = ox + Math.cos(t) * r;
         double z = oz + Math.sin(t) * r;
         if (!com.krox.client.util.WorldProjection.project(ctx, x, oy, z, b)) {
            have = false;
            continue;
         }

         if (have) {
            for (int n = 0; n < th; n++) {
               com.krox.client.util.WorldProjection.line(ctx, a[0], a[1] + n, b[0], b[1] + n,
                  com.krox.client.util.WorldProjection.fade(rgb, a[2]));
            }
         }

         a[0] = b[0];
         a[1] = b[1];
         a[2] = b[2];
         have = true;
      }
   }
}
