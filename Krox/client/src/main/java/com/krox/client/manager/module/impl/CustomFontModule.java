package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.manager.module.StringSetting;

public final class CustomFontModule extends Module {
   public enum FontMode {
      DEFAULT_,
      SANS_,
      SERIF_,
      MONO_,
      CUSTOM_
   }

   public enum WeightMode {
      LIGHT_,
      REGULAR_,
      MEDIUM_,
      BOLD_
   }

   public CustomFontModule() {
      super("custom_font", "Custom Font Renderer", "Custom font for KROX UI.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new EnumSetting<FontMode>(this, "font", "Font", FontMode.DEFAULT_, "Font"));
      this.register(new StringSetting(this, "customFontPath", "Custom Font Path", "", "Custom Font Path"));
      this.register(new NumberSetting(this, "fontSize", "Font Size", 13, 10, 24, 1, "Font Size"));
      this.register(new EnumSetting<WeightMode>(this, "weight", "Weight", WeightMode.LIGHT_, "Weight"));
      this.register(new BooleanSetting(this, "antialiasing", "Antialiasing", true, "Antialiasing"));
      this.register(new BooleanSetting(this, "shadow", "Shadow", true, "Shadow"));
   }
   // TODO(D-11): replacing the font needs a FontManager mixin (D-11 also
   // covers the missing rendertype_text on GLES), so none of these settings
   // are reachable from Module.
}
