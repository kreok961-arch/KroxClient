package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class TouchScalingModule extends Module {
   public TouchScalingModule() {
      super("touch_scaling", "Touch Target Scaling", "Enlarges touch hit areas beyond visual bounds.", ModuleCategory.MOBILE);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new NumberSetting(this, "minimumSize", "Minimum Size", 48, 32, 96, 1, "Minimum Size"));
      this.register(new NumberSetting(this, "extraPadding", "Extra Padding", 8, 0, 24, 1, "Extra Padding"));
   }
   // TODO(D-45): a minimum widget size is a layout pass over every
   // Clickable Widget in Screen.render(..); Module cannot resize them.
}
