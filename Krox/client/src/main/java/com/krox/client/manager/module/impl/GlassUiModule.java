package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class GlassUiModule extends Module {
   public GlassUiModule() {
      super("glass_ui", "Glassmorphic UI Engine", "Glass-style panels across the KROX UI.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new NumberSetting(this, "blurStrength", "Blur Strength", 8, 0, 20, 0.1, "Blur Strength"));
      this.register(new NumberSetting(this, "transparency", "Transparency", 0.5, 0, 0.9, 0.01, "Transparency"));
      this.register(new NumberSetting(this, "borderStrength", "Border Strength", 1, 0, 10, 0.1, "Border Strength"));
      this.register(new BooleanSetting(this, "disableOnMobile", "Disable On Mobile", true, "Disable On Mobile"));
   }
   // Blur is owned by com.krox.client.util.BlurService, which MixinScreenBlur
   // calls at the tail of Screen.renderBackground -- never from here, since a
   // second blur in one frame is D-10. blurStrength feeds that service;
   // transparency/borderStrength are panel drawing, done by the Krox screens.
}
