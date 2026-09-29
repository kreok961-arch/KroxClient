package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.KeybindSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class FreelookModule extends Module {
   public enum ModeMode {
      HOLD_,
      TOGGLE_
   }

   public FreelookModule() {
      super("freelook", "Freelook 360", "Orbit camera independent of player movement.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new KeybindSetting(this, "keybind", "Keybind", 342, "Keybind"));
      this.register(new EnumSetting<ModeMode>(this, "mode", "Mode", ModeMode.HOLD_, "Mode"));
      this.register(new NumberSetting(this, "sensitivity", "Sensitivity", 1, 0.1, 2, 0.1, "Sensitivity"));
      this.register(new BooleanSetting(this, "invertY", "Invert Y", false, "Invert Y"));
      this.register(new BooleanSetting(this, "smoothReturn", "Smooth Return", true, "Smooth Return"));
   }
   // TODO(D-38): freelook detaches the camera from the player rotation.
   // In 1.21.11 Camera.setRotation(..) is protected and the per-frame mouse
   // delta lives in Mouse's private cursorDeltaX/Y, so neither is reachable
   // from a Module tick. It needs a mixin on Mouse.onMouseButton/onCursor
   // that stops feeding the player rotation while the key is held. Nothing
   // here can move the camera without fabricating an API.
}
