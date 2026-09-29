package com.krox.client.util;

import com.krox.client.KroxClient;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.manager.module.Setting;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.SimpleOption;

/**
 * The single owner of Krox's screen blur, and the fix for D-10
 * ("Can only blur once per frame").
 *
 * <p>{@code GuiRenderState.applyBlur} throws unless {@code blurLayer == Integer.MAX_VALUE},
 * and only {@code GuiRenderState.clear} restores that sentinel. {@code GameRenderer.render}
 * calls {@code clear} and then builds exactly one {@code DrawContext} per frame, so the
 * DrawContext identity is the frame identity -- {@link #claim} keys off that rather than a
 * frame counter. Vanilla's {@code Screen.applyBlur}, Krox's call below, and any HUD widget
 * can all want blur in one frame; the claim lets the first through and no-ops the rest.
 *
 * <p>{@link #wants} is the same decision {@link #apply} makes, read one step earlier so the
 * screen mixin can cancel vanilla's own blur and hand the frame's single mark to Krox.
 */
public final class BlurService {
   private static DrawContext blurredFrame;
   private static Integer vanillaBlurriness;
   private static boolean overridden;

   private BlurService() {
   }

   /**
    * Called from the head of {@code DrawContext.applyBlur}.
    *
    * @return true for the first caller of the frame, whose blur goes through; false for
    *         every later one, which the mixin then cancels.
    */
   public static boolean claim(DrawContext ctx) {
      if (ctx == blurredFrame) {
         return false;
      }

      blurredFrame = ctx;
      return true;
   }

   /**
    * Krox's one call site, run at the tail of {@code Screen.renderBackground} so the
    * panorama and darkening are already in the layer being blurred.
    */
   public static void apply(DrawContext ctx, Screen screen) {
      MinecraftClient mc = MinecraftClient.getInstance();

      if (mc == null || mc.options == null) {
         return;
      }

      SimpleOption<Integer> option = mc.options.getMenuBackgroundBlurriness();
      int strength = wants(screen);

      if (strength <= 0) {
         if (overridden && option.getValue() != vanillaBlurriness) {
            option.setValue(vanillaBlurriness);
         }

         overridden = false;
         return;
      }

      if (!overridden) {
         // Snapshot on the first frame we take over, so a later change in the vanilla
         // options screen is what gets restored.
         vanillaBlurriness = option.getValue();
         overridden = true;
      }

      if (option.getValue() != strength) {
         option.setValue(strength);
      }

      ctx.applyBlur();
   }

   /**
    * @return the blurriness Krox wants on this screen, or 0 to leave the frame to vanilla.
    */
   public static int wants(Screen screen) {
      if (KroxClient.get() == null || MinecraftClient.getInstance() == null) {
         return 0;
      }

      Module screenBlur = KroxClient.get().getClientManager().modules().byId("screen_blur");
      Module glassUi = KroxClient.get().getClientManager().modules().byId("glass_ui");
      int strength = 0;

      if (on(screenBlur, "enabled")) {
         strength = Math.max(strength, (int)Math.round(num(screenBlur, "strength")));
      }

      if (on(glassUi, "enabled")) {
         strength = Math.max(strength, (int)Math.round(num(glassUi, "blurStrength")));
      }

      if (strength <= 0) {
         return 0;
      }

      if (on(screenBlur, "onlyPanels") && !isKroxPanel(screen)) {
         return 0;
      }

      // ponytail: this Fabric loader's EnvType is CLIENT/SERVER only, so "mobile" can only
      // be guessed from the GLES backend string. Swap in a real platform check if one ships.
      boolean mobileOptOut = (on(screenBlur, "disableOnMobile") || on(glassUi, "disableOnMobile")) && isGles();
      return mobileOptOut ? 0 : strength;
   }

   private static boolean isKroxPanel(Screen screen) {
      return screen != null && screen.getClass().getName().startsWith("com.krox.client.gui.screen.");
   }

   private static boolean isGles() {
      try {
         String backend = RenderSystem.getBackendDescription().toLowerCase(Locale.ROOT);
         return backend.contains("opengl es") || backend.contains("gles");
      } catch (Throwable var1) {
         return false;
      }
   }

   private static boolean on(Module module, String settingId) {
      Setting setting = find(module, settingId);
      return setting instanceof BooleanSetting bool && bool.get();
   }

   private static double num(Module module, String settingId) {
      Setting setting = find(module, settingId);
      return setting instanceof NumberSetting number ? number.get() : 0.0;
   }

   private static Setting find(Module module, String settingId) {
      if (module == null) {
         return null;
      }

      List<Setting> settings = module.getSettings();

      for (Setting setting : settings) {
         if (setting.getId().equals(settingId)) {
            return setting;
         }
      }

      return null;
   }
}
