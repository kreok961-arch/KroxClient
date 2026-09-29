package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class HurtCamModule extends Module {
   public HurtCamModule() {
      super("hurt_cam", "Hurt Cam", "Controls the camera tilt when hurt.", ModuleCategory.COMBAT);
      this.register(new NumberSetting(this, "intensity", "Intensity", 0.5, 0, 1, 0.01, "Intensity"));
      this.register(new BooleanSetting(this, "disableOnLowHealth", "Disable On Low Health", false, "Disable On Low Health"));
   }
   // TODO(D-19): scaling the vanilla hurt tilt needs a mixin into
   // GameRenderer.getViewProjectionMatrix / the hurt-tick camera shake.
}
