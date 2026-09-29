package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.util.KroxTheme;

public final class KeystrokesModule extends HudElement {
   public enum LayoutMode {
      COMPACT_,
      EXPANDED_
   }

   private final EnumSetting<LayoutMode> layout;
   private final BooleanSetting showCPS;
   private final BooleanSetting showLMB;
   private final BooleanSetting showRMB;
   private final BooleanSetting showShift;
   private final BooleanSetting showSpace;
   private final BooleanSetting showWASD;

   public KeystrokesModule() {
      super("keystrokes", "Keystrokes", "Displays pressed movement and action keys.", ModuleCategory.HUD);
      showWASD = this.register(new BooleanSetting(this, "showWASD", "Show WASD", true, "Show WASD"));
      showSpace = this.register(new BooleanSetting(this, "showSpace", "Show Space", true, "Show Space"));
      showShift = this.register(new BooleanSetting(this, "showShift", "Show Shift", true, "Show Shift"));
      showLMB = this.register(new BooleanSetting(this, "showLMB", "Show LMB", true, "Show LMB"));
      showRMB = this.register(new BooleanSetting(this, "showRMB", "Show RMB", true, "Show RMB"));
      showCPS = this.register(new BooleanSetting(this, "showCPS", "Show CPS", true, "Show CPS"));
      layout = this.register(new EnumSetting<LayoutMode>(this, "layout", "Layout", LayoutMode.COMPACT_, "Layout"));
      this.register(new BooleanSetting(this, "pressLighting", "Press Lighting", true, "Press Lighting"));
      this.register(new BooleanSetting(this, "rippleEffect", "Ripple Effect", false, "Ripple Effect"));
      this.register(new ColorSetting(this, "keyColor", "Key Color", KroxTheme.TEXT_PRIMARY, "Key Color"));
      this.register(new ColorSetting(this, "pressedColor", "Pressed Color", KroxTheme.ACCENT, "Pressed Color"));
   }
   @Override
   public boolean isMultiLine() {
      return this.layout.get() == LayoutMode.COMPACT_ || this.showCPS.get();
   }

   @Override
   public String renderText() {
      return null;
   }

   @Override
   public java.util.List<String> renderLines() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      net.minecraft.client.network.ClientPlayerEntity p = mc.player;
      java.util.List<String> rows = new java.util.ArrayList<>();
      net.minecraft.util.PlayerInput in = p == null ? null : p.getLastPlayerInput();
      if (this.showWASD.get() && in != null) {
         rows.add(key("W", in.forward())
            + key("A", in.left())
            + key("S", in.backward())
            + key("D", in.right()));
      }

      StringBuilder bot = new StringBuilder();
      if (this.showSpace.get() && in != null) {
         bot.append(key("SPC", in.jump()));
      }

      if (this.showShift.get() && in != null) {
         bot.append(key("SHF", in.sneak()));
      }

      if (this.showLMB.get()) {
         bot.append(key("LMB", net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), 0)));
      }

      if (this.showRMB.get()) {
         bot.append(key("RMB", net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), 1)));
      }

      if (bot.length() > 0) {
         rows.add(bot.toString());
      }

      if (this.showCPS.get()) {
         rows.add("CPS");
      }

      return rows;
   }

   private String key(String label, boolean isDown) {
      return "[" + label + (isDown ? "*" : "") + "]";
   }
}
