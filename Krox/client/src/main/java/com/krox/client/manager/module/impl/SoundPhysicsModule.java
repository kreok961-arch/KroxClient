package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class SoundPhysicsModule extends Module {
   public SoundPhysicsModule() {
      super("sound_physics", "Sound Physics", "Environmental sound occlusion and reverb.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new BooleanSetting(this, "occlusion", "Occlusion", true, "Occlusion"));
      this.register(new NumberSetting(this, "occlusionStrength", "Occlusion Strength", 0.5, 0, 1, 0.01, "Occlusion Strength"));
      this.register(new BooleanSetting(this, "reverb", "Reverb", true, "Reverb"));
      this.register(new NumberSetting(this, "reverbStrength", "Reverb Strength", 0.3, 0, 1, 0.01, "Reverb Strength"));
      this.register(new BooleanSetting(this, "absorption", "Absorption", true, "Absorption"));
   }
   // TODO(D-33): occlusion/reverb/absorption are per-sound parameters applied
   // by the sound engine at play time; there is no client hook to rewrite
   // them.
}
