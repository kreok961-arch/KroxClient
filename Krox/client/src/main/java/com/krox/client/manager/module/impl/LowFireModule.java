package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class LowFireModule extends Module {
   public LowFireModule() {
      super("low_fire", "Low Fire", "Reduces the fire overlay height when on fire.", ModuleCategory.COMBAT);
      this.register(new NumberSetting(this, "height", "Height", 0.3, 0, 1, 0.01, "Height"));
      this.register(new BooleanSetting(this, "hideFully", "Hide Fully", false, "Hide Fully"));
   }
   // TODO(D-19): keeping fire low in the HUD needs a mixin into
   // InGameHud.drawStatusBarEffects / the fire overlay draw.
}
