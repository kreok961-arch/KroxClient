package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;

public final class ScoreboardModule extends Module {
   public enum FontMode {
      _default_,
      mono_,
      small_
   }

   public enum PositionMode {
      RIGHT_,
      LEFT_
   }

   public ScoreboardModule() {
      super("scoreboard", "Scoreboard Customizer", "Customize vanilla scoreboard.", ModuleCategory.HUD);
      this.register(new BooleanSetting(this, "visible", "Visible", true, "Visible"));
      this.register(new EnumSetting<PositionMode>(this, "position", "Position", PositionMode.RIGHT_, "Position"));
      this.register(new EnumSetting<FontMode>(this, "font", "Font", FontMode._default_, "Font"));
      this.register(new NumberSetting(this, "fontSize", "Font Size", 12, 8, 20, 1, "Font Size"));
      this.register(new NumberSetting(this, "opacity", "Opacity", 1, 0, 1, 0.01, "Opacity"));
      this.register(new ColorSetting(this, "textColor", "Text Color", KroxTheme.TEXT_PRIMARY, "Text Color"));
      this.register(new ColorSetting(this, "backgroundColor", "Background Color", KroxTheme.TEXT_PRIMARY, "Background Color"));
      this.register(new NumberSetting(this, "backgroundOpacity", "Background Opacity", 0, 0, 1, 0.01, "Background Opacity"));
      this.register(new BooleanSetting(this, "hideNumbers", "Hide Numbers", false, "Hide Numbers"));
      this.register(new BooleanSetting(this, "hideBackground", "Hide Background", false, "Hide Background"));
   }
   // TODO(D-17): restyling the scoreboard needs a mixin into
   // ScoreboardHud.render(..); Module has no DrawContext and no world render
   // context, so position/font/size/opacity cannot be applied from here.
}
