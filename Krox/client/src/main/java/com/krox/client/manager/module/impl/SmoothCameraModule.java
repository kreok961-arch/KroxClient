package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class SmoothCameraModule extends Module {
   public SmoothCameraModule() {
      super("smooth_camera", "Smooth Camera", "Smooths camera jitter and micro-movements.", ModuleCategory.COMBAT);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new NumberSetting(this, "strength", "Strength", 0.3, 0, 1, 0.01, "Strength"));
      this.register(new BooleanSetting(this, "preserveQuickMovements", "Preserve Quick Movements", true, "Preserve Quick Movements"));
      this.register(new BooleanSetting(this, "onlyWhenIdle", "Only When Idle", false, "Only When Idle"));
   }
   // TODO(D-19): camera smoothing has to run between the mouse input and
   // the player rotation, i.e. inside the mouse mixin. Module only sees the
   // already-applied rotation.
}
