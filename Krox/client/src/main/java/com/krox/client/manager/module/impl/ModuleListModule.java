package com.krox.client.manager.module.impl;

import com.krox.client.KroxClient;
import com.krox.client.manager.hud.HudWidget;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import java.util.List;

public final class ModuleListModule extends Module implements HudWidget {
   public ModuleListModule() {
      super("modulelist", "Module List", "Show currently enabled modules on the HUD.", ModuleCategory.HUD);
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
      return null;
   }

   @Override
   public boolean isMultiLine() {
      return true;
   }

   @Override
   public List<String> renderLines() {
      return KroxClient.get().getClientManager().modules().all().stream().filter(Module::isEnabled).map(Module::getName).toList();
   }
}
