package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class ParticleMultiplierModule extends Module {
   public ParticleMultiplierModule() {
      super("particle_multiplier", "Particle Multiplier", "Multiplies attack particle count.", ModuleCategory.COMBAT);
      this.register(new NumberSetting(this, "multiplier", "Multiplier", 2, 1, 10, 1, "Multiplier"));
      this.register(new BooleanSetting(this, "onlyCrits", "Only Crits", false, "Only Crits"));
      this.register(new BooleanSetting(this, "onlySweep", "Only Sweep", false, "Only Sweep"));
      this.register(new BooleanSetting(this, "onlySharpness", "Only Sharpness", false, "Only Sharpness"));
   }
   // TODO(D-18): amplifying vanilla attack particles needs a mixin into the
   // attack-packet handler that spawns CRIT/SWEEP effects.
}
