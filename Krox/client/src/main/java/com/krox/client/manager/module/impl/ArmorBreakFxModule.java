package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class ArmorBreakFxModule extends Module {
   public ArmorBreakFxModule() {
      super("armor_break_fx", "Armor Break FX", "Visual/audio effect when armor breaks.", ModuleCategory.COMBAT);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new BooleanSetting(this, "particleEffect", "Particle Effect", true, "Particle Effect"));
      this.register(new BooleanSetting(this, "soundEffect", "Sound Effect", true, "Sound Effect"));
      this.register(new NumberSetting(this, "soundVolume", "Sound Volume", 0, 0, 1, 0.01, "Sound Volume"));
      this.register(new BooleanSetting(this, "screenFlash", "Screen Flash", false, "Screen Flash"));
   }
   // TODO(D-20): the armor-break signal lives in the damage handler; there
   // is no client-side hook that sees an armor piece break.
}
