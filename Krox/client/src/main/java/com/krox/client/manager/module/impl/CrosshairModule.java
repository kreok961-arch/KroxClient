package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;
import java.lang.Math;

public final class CrosshairModule extends Module {
   public enum StyleMode {
      CROSS_,
      DOT_,
      CIRCLE_,
      ARROW_,
      T_SHAPE_
   }

   private final ColorSetting color;
   private final NumberSetting dotSize;
   private final BooleanSetting dynamicGap;
   private final NumberSetting gap;
   private final BooleanSetting hitmarker;
   private final ColorSetting hitmarkerColor;
   private final NumberSetting lineLength;
   private final NumberSetting lineThickness;
   private final BooleanSetting showDot;
   private final EnumSetting<StyleMode> style;

   public CrosshairModule() {
      super("crosshair", "Crosshair", "Customizable crosshair with dynamic gap.", ModuleCategory.HUD);
      style = this.register(new EnumSetting<StyleMode>(this, "style", "Style", StyleMode.CROSS_, "Style"));
      gap = this.register(new NumberSetting(this, "gap", "Gap", 4, 0, 20, 1, "Gap"));
      lineThickness = this.register(new NumberSetting(this, "lineThickness", "Line Thickness", 1, 1, 4, 1, "Line Thickness"));
      lineLength = this.register(new NumberSetting(this, "lineLength", "Line Length", 5, 2, 12, 1, "Line Length"));
      dotSize = this.register(new NumberSetting(this, "dotSize", "Dot Size", 1, 1, 4, 1, "Dot Size"));
      showDot = this.register(new BooleanSetting(this, "showDot", "Show Dot", false, "Show Dot"));
      dynamicGap = this.register(new BooleanSetting(this, "dynamicGap", "Dynamic Gap", false, "gap grows when attacking"));
      color = this.register(new ColorSetting(this, "color", "Color", KroxTheme.TEXT_PRIMARY, "Color"));
      hitmarker = this.register(new BooleanSetting(this, "hitmarker", "Hitmarker", false, "Hitmarker"));
      hitmarkerColor = this.register(new ColorSetting(this, "hitmarkerColor", "Hitmarker Color", KroxTheme.DANGER, "Hitmarker Color"));
   }
   private long hitAt;

   @Override
   public String renderText() {
      return null;
   }

   @Override
   public boolean isCustomRender() {
      return true;
   }

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY
         && net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), 0)) {
         this.hitAt = System.currentTimeMillis();
      }
   }

   @Override
   public void renderCustom(net.minecraft.client.gui.DrawContext ctx, int screenWidth, int screenHeight) {
      int cx = screenWidth / 2;
      int cy = screenHeight / 2;
      int color = this.color.get();
      int th = Math.max(1, (int)this.lineThickness.get());
      int len = (int)this.lineLength.get();
      int gap = (int)this.gap.get() + (this.dynamicGap.get() && attacking() ? 2 : 0);
      switch (this.style.get()) {
         case StyleMode.DOT_ -> ctx.fill(cx - th, cy - th, cx + th, cy + th, color);
         case StyleMode.ARROW_ -> {
            ctx.fill(cx - th, cy - gap - len, cx + th, cy - gap, color);
            ctx.fill(cx - gap - len, cy - th, cx - gap, cy + th, color);
            ctx.fill(cx + gap, cy - th, cx + gap + len, cy + th, color);
         }
         case StyleMode.T_SHAPE_ -> {
            ctx.fill(cx - th, cy - gap - len, cx + th, cy - gap, color);
            ctx.fill(cx - th, cy - gap, cx + th, cy + gap, color);
         }
         case StyleMode.CIRCLE_ -> {
            for (int i = 0; i < 360; i += 3) {
               double a = Math.toRadians(i);
               int px = cx + (int)Math.round(Math.cos(a) * len);
               int py = cy + (int)Math.round(Math.sin(a) * len);
               ctx.fill(px - th, py - th, px + th, py + th, color);
            }
         }
         default -> {
            ctx.fill(cx - gap - len, cy - th, cx - gap, cy + th, color);
            ctx.fill(cx + gap, cy - th, cx + gap + len, cy + th, color);
            ctx.fill(cx - th, cy - gap - len, cx + th, cy - gap, color);
            ctx.fill(cx - th, cy + gap, cx + th, cy + gap + len, color);
         }
      }

      if (this.showDot.get()) {
         int d = (int)this.dotSize.get();
         ctx.fill(cx - d, cy - d, cx + d, cy + d, color);
      }

      if (this.hitmarker.get() && System.currentTimeMillis() - this.hitAt < 200L) {
         int c = this.hitmarkerColor.get();
         ctx.fill(cx - 6, cy - 6, cx - 3, cy - 3, c);
         ctx.fill(cx + 3, cy - 6, cx + 6, cy - 3, c);
         ctx.fill(cx - 6, cy + 3, cx - 3, cy + 6, c);
         ctx.fill(cx + 3, cy + 3, cx + 6, cy + 6, c);
      }
   }

   private static boolean attacking() {
      return net.minecraft.client.util.InputUtil.isKeyPressed(net.minecraft.client.MinecraftClient.getInstance().getWindow(), 0);
   }
}
