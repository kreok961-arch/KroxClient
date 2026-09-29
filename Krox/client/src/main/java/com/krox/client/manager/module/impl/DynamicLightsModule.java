package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class DynamicLightsModule extends Module {
   public DynamicLightsModule() {
      super("dynamic_lights", "Lights", "Light sources for held/dropped items (torches, glowstone).", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new NumberSetting(this, "updateRate", "Update Rate", 2, 1, 10, 1, "Update Rate"));
      this.register(new BooleanSetting(this, "onlyHeld", "Only Held", false, "Only Held"));
      this.register(new NumberSetting(this, "intensity", "Intensity", 0.8, 0, 1, 0.01, "Intensity"));
   }
   // TODO(D-25): dynamic light entities are spawned into the world and lit by
   // the light engine; a client-side emitter needs a WorldRenderer mixin.
}
