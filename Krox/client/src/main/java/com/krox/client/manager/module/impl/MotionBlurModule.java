package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class MotionBlurModule extends Module {
   public MotionBlurModule() {
      super("motion_blur", "Motion Blur", "Screen motion blur.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new NumberSetting(this, "intensity", "Intensity", 0.5, 0, 1, 0.01, "Intensity"));
      this.register(new NumberSetting(this, "samples", "Samples", 4, 2, 16, 1, "Samples"));
      this.register(new BooleanSetting(this, "disableOnMobile", "Disable On Mobile", true, "Disable On Mobile"));
   }
   // TODO(D-21): temporal accumulation needs a framebuffer ping-pong in the
   // render mixin; no Module hook can access the previous frame.
}
