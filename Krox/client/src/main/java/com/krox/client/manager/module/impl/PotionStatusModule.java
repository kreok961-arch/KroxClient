package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import java.util.List;

public final class PotionStatusModule extends HudElement {
   public enum LayoutMode {
      HORIZONTAL_,
      VERTICAL_
   }

   private final EnumSetting<LayoutMode> layout;
   private final NumberSetting lowTimeThreshold;
   private final BooleanSetting lowTimeWarn;
   private final BooleanSetting showAmplifier;
   private final BooleanSetting showNames;
   private final BooleanSetting showTime;

   public PotionStatusModule() {
      super("potion_status", "Potion Status", "Active potion effects with timers.", ModuleCategory.HUD);
      this.register(new BooleanSetting(this, "showIcons", "Show Icons", true, "Show Icons"));
      showNames = this.register(new BooleanSetting(this, "showNames", "Show Names", true, "Show Names"));
      this.register(new BooleanSetting(this, "showLevel", "Show Level", true, "Show Level"));
      showTime = this.register(new BooleanSetting(this, "showTime", "Show Time", true, "Show Time"));
      showAmplifier = this.register(new BooleanSetting(this, "showAmplifier", "Show Amplifier", true, "Show Amplifier"));
      lowTimeWarn = this.register(new BooleanSetting(this, "lowTimeWarn", "Low Time Warn", true, "Low Time Warn"));
      lowTimeThreshold = this.register(new NumberSetting(this, "lowTimeThreshold", "Low Time Threshold", 10, 0, 100, 1, "Low Time Threshold"));
      layout = this.register(new EnumSetting<LayoutMode>(this, "layout", "Layout", LayoutMode.VERTICAL_, "Layout"));
   }
   @Override
   public boolean isMultiLine() {
      return this.layout.get() == LayoutMode.VERTICAL_;
   }

   @Override
   public String renderText() {
      return this.isMultiLine() ? null : String.join("  ", effectLines());
   }

   @Override
   public java.util.List<String> renderLines() {
      return this.isMultiLine() ? effectLines() : List.of();
   }

   private java.util.List<String> effectLines() {
      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      if (p == null) {
         return List.of();
      }

      java.util.List<String> out = new java.util.ArrayList<>();
      for (net.minecraft.entity.effect.StatusEffectInstance eff : p.getStatusEffects()) {
         StringBuilder sb = new StringBuilder();
         if (this.showNames.get()) {
            sb.append(net.minecraft.text.Text.translatable(eff.getTranslationKey()).getString());
         }

         if (this.showAmplifier.get() && eff.getAmplifier() > 0) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            sb.append('I').append(eff.getAmplifier() + 1);
         }

         if (this.showTime.get() && eff.getDuration() > 0) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            int secs = eff.getDuration() / 20;
            sb.append(String.format("%d:%02d", secs / 60, secs % 60));
            if (this.lowTimeWarn.get() && secs <= this.lowTimeThreshold.get()) {
               sb.append('!');
            }
         }

         out.add(sb.toString());
      }

      return out;
   }
}
