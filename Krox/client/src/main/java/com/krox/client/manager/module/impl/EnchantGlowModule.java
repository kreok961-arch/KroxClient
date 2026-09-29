package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;
import java.lang.Math;

public final class EnchantGlowModule extends Module {
   public enum ModeMode {
      RGB_,
      CHROMA_,
      SOLID_,
      PULSE_
   }

   private final BooleanSetting applyToArmor;
   private final ColorSetting color;
   private final EnumSetting<ModeMode> mode;
   private final NumberSetting speed;

   public EnchantGlowModule() {
      super("enchant_glow", "Enchant Dynamic Glow", "Animated glow for enchanted items.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      mode = this.register(new EnumSetting<ModeMode>(this, "mode", "Mode", ModeMode.RGB_, "Mode"));
      color = this.register(new ColorSetting(this, "color", "Color", KroxTheme.TEXT_PRIMARY, "Color"));
      speed = this.register(new NumberSetting(this, "speed", "Speed", 0, 0, 1, 0.01, "Speed"));
      applyToArmor = this.register(new BooleanSetting(this, "applyToArmor", "Apply To Armor", true, "Apply To Armor"));
   }
   @Override
   public String renderText() {
      return null;
   }

   /** Packed colour the enchant overlay mixin tints enchanted items with. */
   public int glowColor() {
      double period = Math.max(0.05D, this.speed.get() / 10.0D);
      double t = (System.currentTimeMillis() % (long)(period * 1000.0D)) / (period * 1000.0D);
      return switch (this.mode.get()) {
         case ModeMode.CHROMA_ -> com.krox.client.util.KroxTheme.rgb((int)(255.0D * t), (int)(255.0D * (1.0D - t)), 128);
         case ModeMode.SOLID_ -> this.color.get();
         case ModeMode.PULSE_ -> com.krox.client.util.KroxTheme.dim(this.color.get(), (float)Math.abs(Math.sin(t * Math.PI)));
         default -> com.krox.client.util.KroxTheme.rgb((int)(255.0D * t), 0, (int)(255.0D * (1.0D - t)));
      };
   }

   public boolean tintArmor() {
      return this.applyToArmor.get();
   }
}
