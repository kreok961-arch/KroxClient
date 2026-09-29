package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;

public final class CritFxModule extends Module {
   public enum StyleMode {
      STAR_,
      SPARK_,
      RING_,
      CROSS_
   }

   public CritFxModule() {
      super("crit_fx", "Critical Particle FX", "Custom visual effect for critical hits.", ModuleCategory.COMBAT);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new EnumSetting<StyleMode>(this, "style", "Style", StyleMode.STAR_, "Style"));
      this.register(new ColorSetting(this, "color", "Color", KroxTheme.WARNING, "Color"));
      this.register(new NumberSetting(this, "size", "Size", 0, 0, 1, 0.01, "Size"));
      this.register(new NumberSetting(this, "duration", "Duration", 0, 0, 10, 1, "Duration"));
      this.register(new BooleanSetting(this, "sound", "Sound", false, "Sound"));
   }
   // TODO(D-18): spawning a crit effect on a confirmed crit hit needs the
   // attack mixin that reports the crit flag; Module has no attack hook.
}
