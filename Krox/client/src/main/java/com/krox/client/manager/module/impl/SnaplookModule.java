package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.KeybindSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class SnaplookModule extends Module {
   public enum ModeMode {
      HOLD_,
      TOGGLE_
   }

   public SnaplookModule() {
      super("snaplook", "SnapLook", "Instantly look backward.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new KeybindSetting(this, "keybind", "Keybind", 0, "Keybind"));
      this.register(new NumberSetting(this, "angle", "Angle", 180, 90, 180, 1, "Angle"));
      this.register(new EnumSetting<ModeMode>(this, "mode", "Mode", ModeMode.HOLD_, "Mode"));
   }
   // TODO(D-38): snaplook scales the mouse delta by a fixed step. The delta
   // is Mouse's private cursorDeltaX/Y and Camera.setRotation(..) is
   // protected, so a Module tick cannot apply it. Needs the same Mouse mixin
   // as freelook.
}
