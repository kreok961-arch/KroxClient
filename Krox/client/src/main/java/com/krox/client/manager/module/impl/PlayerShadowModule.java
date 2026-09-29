package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;

public final class PlayerShadowModule extends Module {
   public PlayerShadowModule() {
      super("player_shadow", "Player Shadow", "Ground shadow under players.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new NumberSetting(this, "opacity", "Opacity", 0.4, 0, 1, 0.01, "Opacity"));
      this.register(new NumberSetting(this, "size", "Size", 1, 0.5, 2, 0.1, "Size"));
      this.register(new ColorSetting(this, "tint", "Tint", KroxTheme.BACKGROUND, "Tint"));
      this.register(new BooleanSetting(this, "onlyPlayers", "Only Players", true, "Only Players"));
   }
   // TODO(D-29): a custom ground shadow under players is a world-space
   // render pass; Module has no WorldRenderContext.
}
