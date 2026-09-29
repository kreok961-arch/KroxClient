package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;

public final class BossbarModule extends Module {
   public enum PositionMode {
      TOP_,
      CENTER_,
      BOTTOM_
   }

   public BossbarModule() {
      super("bossbar", "Bossbar Customizer", "Customize boss bar visibility and position.", ModuleCategory.HUD);
      this.register(new BooleanSetting(this, "visible", "Visible", true, "Visible"));
      this.register(new EnumSetting<PositionMode>(this, "position", "Position", PositionMode.TOP_, "Position"));
      this.register(new NumberSetting(this, "offsetY", "Offset Y", 0, -200, 200, 1, "Offset Y"));
      this.register(new NumberSetting(this, "scale", "Scale", 1, 0.5, 2, 0.1, "Scale"));
      this.register(new NumberSetting(this, "opacity", "Opacity", 1, 0, 1, 0.01, "Opacity"));
      this.register(new ColorSetting(this, "backgroundColor", "Background Color", KroxTheme.TEXT_PRIMARY, "Background Color"));
      this.register(new ColorSetting(this, "textColor", "Text Color", KroxTheme.TEXT_PRIMARY, "Text Color"));
   }
   // TODO(D-17): repositioning/rescaling the boss bar needs a mixin into
   // BossBarHud.render(..). No Module hook reaches the HUD render pass, so
   // none of these settings can be honoured yet.
}
