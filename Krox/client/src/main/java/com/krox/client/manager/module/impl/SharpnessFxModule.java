package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;

public final class SharpnessFxModule extends Module {
   public enum ParticleTypeMode {
      Flame_,
      Soul_,
      SculkSoul_,
      ElectricSpark_,
      Sweep_,
      Crit_,
      EndRod_
   }

   public SharpnessFxModule() {
      super("sharpness_fx", "Sharpness FX", "Custom particle for sharpness enchanted hits.", ModuleCategory.COMBAT);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new ColorSetting(this, "color", "Color", KroxTheme.TEXT_PRIMARY, "Color"));
      this.register(new EnumSetting<ParticleTypeMode>(this, "particleType", "Particle Type", ParticleTypeMode.Flame_, "Particle Type"));
      this.register(new NumberSetting(this, "count", "Count", 1, 1, 20, 1, "Count"));
      this.register(new BooleanSetting(this, "perLevel", "Per Level", true, "more particles for higher level"));
   }
   // TODO(D-18): a sharpness hit effect needs the attack mixin that reports
   // the enchantment level of the striking item.
}
