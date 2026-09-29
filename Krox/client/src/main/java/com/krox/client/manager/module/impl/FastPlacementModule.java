package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class FastPlacementModule extends Module {
   public FastPlacementModule() {
      super("fast_placement", "Fast Placement Helper", "Improves placement consistency and responsiveness. Not a placement cheat.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new NumberSetting(this, "placementDelayMs", "Placement Delay Ms", 0, 0, 500, 1, "Placement Delay Ms"));
      this.register(new BooleanSetting(this, "onlyWhenHolding", "Only When Holding", true, "Only When Holding"));
   }
   // TODO(D-35): lowering the use-item cooldown is a vanilla item-use mixin;
   // Module can only read placement state, never rewrite the cooldown.
}
