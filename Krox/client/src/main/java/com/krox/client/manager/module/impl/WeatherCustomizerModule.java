package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class WeatherCustomizerModule extends Module {
   public enum ModeMode {
      CLEAR_,
      RAIN_,
      SNOW_,
      THUNDER_,
      NATURAL_
   }

   public WeatherCustomizerModule() {
      super("weather_customizer", "Weather Customizer", "Client-side weather override.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new EnumSetting<ModeMode>(this, "mode", "Mode", ModeMode.CLEAR_, "Mode"));
      this.register(new BooleanSetting(this, "soundEnabled", "Sound Enabled", true, "Sound Enabled"));
      this.register(new NumberSetting(this, "particleDensity", "Particle Density", 1, 0, 2, 0.1, "Particle Density"));
      this.register(new BooleanSetting(this, "onlyWhenRaining", "Only When Raining", false, "Only When Raining"));
   }
   // TODO(D-28): rain/snow/thunder particles and sounds are spawned by the
   // client world tick; overriding the mode needs a mixin there.
}
