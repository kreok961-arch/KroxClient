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

   /**
    * The version is read from the loader rather than hardcoded. It said "KROX v1.0.0"
    * while mod_version was 16.0.0, so the HUD would have lied on every release.
    * The lookup itself lives on KroxClient.version() -- this used to carry its own
    * copy of it, which is the same second source of truth D-13 is about.
    */
   @Override
   public String renderText() {
      return "KROX v" + KroxClient.version();
   }
}
