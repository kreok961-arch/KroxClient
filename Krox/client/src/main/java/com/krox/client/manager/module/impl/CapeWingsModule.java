package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class CapeWingsModule extends Module {
   public CapeWingsModule() {
      super("cape_wings", "Custom Cape / Wings", "Cosmetic cape and wings surfaced via the supplied cosmetics jar.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "showCape", "Show Cape", true, "Show Cape"));
      this.register(new BooleanSetting(this, "showWings", "Show Wings", false, "Show Wings"));
      this.register(new BooleanSetting(this, "cape", "Cape", false, "Cape"));
      this.register(new BooleanSetting(this, "wings", "Wings", false, "Wings"));
      this.register(new NumberSetting(this, "animationSpeed", "Animation Speed", 0, 0, 1, 0.01, "Animation Speed"));
      this.register(new BooleanSetting(this, "hiddenInFirstPerson", "Hidden In First Person", true, "Hidden In First Person"));
   }
   // TODO(D-24): cape/wing render layers are chosen by the player renderer;
   // Module cannot add a render layer. The cosmetic pickers the spec lists
   // (cape, wings) are untyped and are not emitted as settings.
}
