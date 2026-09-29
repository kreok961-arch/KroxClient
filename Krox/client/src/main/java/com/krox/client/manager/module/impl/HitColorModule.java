package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;

public final class HitColorModule extends Module {
   public HitColorModule() {
      super("hit_color", "Custom Hit Color", "Color of the damage tint on hit entities.", ModuleCategory.COMBAT);
      this.register(new ColorSetting(this, "color", "Color", KroxTheme.DANGER, "Color"));
      this.register(new NumberSetting(this, "opacity", "Opacity", 0.4, 0, 1, 0.01, "Opacity"));
      this.register(new BooleanSetting(this, "onlyPlayers", "Only Players", false, "Only Players"));
      this.register(new BooleanSetting(this, "onlyCrits", "Only Crits", false, "Only Crits"));
   }
   // TODO(D-18): tinting the crosshair on a hit needs a mixin into
   // InGameHud.drawCrosshair(..) or the crosshair widget's tint supplier;
   // Module exposes no render hook.
}
