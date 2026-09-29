package com.krox.client.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;

public final class KroxTheme {
   public static int BACKGROUND = -16250870;
   public static int SURFACE = -15592938;
   public static int ELEVATED = -14408660;
   public static int ACCENT = -2349530;
   public static int ACCENT_HOVER = -1096636;
   public static int TEXT_PRIMARY = -1710619;
   public static int TEXT_SECONDARY = -7697782;
   public static int TEXT_MUTED = -10855846;
   public static int BORDER = -13421766;
   public static int SUCCESS = -14498466;
   public static int WARNING = -680437;
   public static int DANGER = -2349530;

   private KroxTheme() {
   }

   public static void init() {
   }

   public static int rgb(int r, int g, int b) {
      return net.minecraft.util.math.ColorHelper.getArgb(255, r, g, b);
   }

   public static int dim(int color, float ratio) {
      int a = color >> 24 & 0xFF;
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      r = (int)((float)r * (1.0F - ratio));
      g = (int)((float)g * (1.0F - ratio));
      b = (int)((float)b * (1.0F - ratio));
      return a << 24 | r << 16 | g << 8 | b;
   }

   public static int lighten(int color, float ratio) {
      int a = color >> 24 & 0xFF;
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      r = (int)((float)r + (float)(255 - r) * ratio);
      g = (int)((float)g + (float)(255 - g) * ratio);
      b = (int)((float)b + (float)(255 - b) * ratio);
      return a << 24 | r << 16 | g << 8 | b;
   }

   public static void rect(net.minecraft.client.gui.DrawContext ctx, int x, int y, int w, int h, int color) {
      ctx.fill(x, y, x + w, y + h, color);
   }

   public static void border(net.minecraft.client.gui.DrawContext ctx, int x, int y, int w, int h, int color) {
      ctx.fill(x, y, x + w, y + 1, color);
      ctx.fill(x, y + h - 1, x + w, y + h, color);
      ctx.fill(x, y, x + 1, y + h, color);
      ctx.fill(x + w - 1, y, x + w, y + h, color);
   }
}
