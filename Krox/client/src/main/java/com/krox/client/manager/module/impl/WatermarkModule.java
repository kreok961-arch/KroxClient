package com.krox.client.manager.module.impl;

import com.krox.client.KroxClient;
import com.krox.client.manager.hud.HudWidget;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class WatermarkModule extends Module implements HudWidget {
   public WatermarkModule() {
      super("watermark", "Watermark", "Show the Krox Client brand on the HUD.", ModuleCategory.HUD);
   }

   @Override
   protected void onEnable() {
      KroxClient.get().getClientManager().hud().register(this);
   }

   @Override
   protected void onDisable() {
      KroxClient.get().getClientManager().hud().unregister(this);
   }

   @Override
   public String renderText() {
      return "KROX v1.0.0";
   }
}
