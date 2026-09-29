package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class ScreenBlurModule extends Module {
   public ScreenBlurModule() {
      super("screen_blur", "Screen Blur", "Blur behind KROX UI panels.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new NumberSetting(this, "strength", "Strength", 8, 0, 20, 0.1, "Strength"));
      this.register(new BooleanSetting(this, "onlyPanels", "Only Panels", true, "Only Panels"));
      this.register(new BooleanSetting(this, "disableOnMobile", "Disable On Mobile", true, "Disable On Mobile"));
   }
   // D-10 is closed: com.krox.client.util.BlurService owns the blur and
   // MixinScreenBlur calls it once per frame at the tail of
   // Screen.renderBackground, cancelling vanilla's own applyBlur on the way.
   // Nothing here may call ctx.applyBlur -- a second call in one frame throws.
}
