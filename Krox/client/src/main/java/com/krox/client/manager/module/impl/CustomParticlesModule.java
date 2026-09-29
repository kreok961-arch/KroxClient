package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class CustomParticlesModule extends Module {
   public enum PresetMode {
      DEFAULT_,
      MINIMAL_,
      VIVID_,
      CINEMATIC_
   }

   public CustomParticlesModule() {
      super("custom_particles", "Custom Particle FX", "Custom particle effect customization.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new EnumSetting<PresetMode>(this, "preset", "Preset", PresetMode.DEFAULT_, "Preset"));
      this.register(new NumberSetting(this, "density", "Density", 1, 0.1, 3, 0.1, "Density"));
      this.register(new NumberSetting(this, "sizeMultiplier", "Size Multiplier", 1, 0.5, 2, 0.1, "Size Multiplier"));
      this.register(new NumberSetting(this, "lifeMultiplier", "Life Multiplier", 1, 0.5, 2, 0.1, "Life Multiplier"));
   }
   // TODO(D-18): particle density/size/life are decided at spawn time by the
   // particle engines; Module has no particle hook.
}
