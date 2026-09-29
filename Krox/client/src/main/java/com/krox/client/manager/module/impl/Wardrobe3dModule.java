package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class Wardrobe3dModule extends Module {
   public enum BackgroundMode {
      SOLID_,
      GRADIENT_,
      TRANSPARENT_
   }

   public Wardrobe3dModule() {
      super("wardrobe_3d", "3D Wardrobe", "Interactive 3D player preview in the cosmetics screen.", ModuleCategory.ACCOUNT);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new BooleanSetting(this, "rotation", "Rotation", true, "Rotation"));
      this.register(new BooleanSetting(this, "zoom", "Zoom", true, "Zoom"));
      this.register(new BooleanSetting(this, "idleAnimation", "Idle Animation", true, "Idle Animation"));
      this.register(new EnumSetting<BackgroundMode>(this, "background", "Background", BackgroundMode.SOLID_, "Background"));
   }
   // TODO(D-49): the 3D preview is a screen with its own render pass;
   // Module has neither a screen hook nor a DrawContext outside the HUD
   // pass, so rotation/zoom/idleAnimation/background have nothing to drive.
}
