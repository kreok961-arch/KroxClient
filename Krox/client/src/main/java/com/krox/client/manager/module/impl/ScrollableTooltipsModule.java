package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class ScrollableTooltipsModule extends Module {
   public ScrollableTooltipsModule() {
      super("scrollable_tooltips", "Scrollable Tooltips", "Scroll long item tooltips.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new NumberSetting(this, "scrollSpeed", "Scroll Speed", 1, 0.5, 3, 0.1, "Scroll Speed"));
      this.register(new BooleanSetting(this, "invertScroll", "Invert Scroll", false, "Invert Scroll"));
   }
   // TODO(D-36): scrolling a tooltip needs a scroll handler on
   // ItemTooltipScreen; Module has no screen-mouse hook.
}
