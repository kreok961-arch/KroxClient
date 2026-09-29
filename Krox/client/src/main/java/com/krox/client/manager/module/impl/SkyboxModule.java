package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.manager.module.StringSetting;

public final class SkyboxModule extends Module {
   public enum TypeMode {
      PANORAMA_,
      STARFIELD_,
      SOLID_,
      GRADIENT_
   }

   public SkyboxModule() {
      super("skybox", "Custom Skybox", "Custom skybox with panorama or starfield.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new EnumSetting<TypeMode>(this, "type", "Type", TypeMode.PANORAMA_, "Type"));
      this.register(new StringSetting(this, "panoramaPath", "Panorama Path", "", "Panorama Path"));
      this.register(new NumberSetting(this, "starCount", "Star Count", 100, 100, 2000, 1, "Star Count"));
      this.register(new NumberSetting(this, "transitionSpeed", "Transition Speed", 0, 0, 1, 0.01, "Transition Speed"));
      this.register(new BooleanSetting(this, "showSun", "Show Sun", true, "Show Sun"));
      this.register(new BooleanSetting(this, "showMoon", "Show Moon", true, "Show Moon"));
      this.register(new BooleanSetting(this, "showStars", "Show Stars", true, "Show Stars"));
   }
   // TODO(D-23): the skybox is built by SkyRenderer; swapping the panorama
   // /starfield needs a mixin on that renderer.
}
