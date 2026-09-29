package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;

public final class PackDisplayModule extends HudElement {
   private final BooleanSetting showCount;
   private final BooleanSetting showName;

   public PackDisplayModule() {
      super("pack_display", "Pack Display", "Current resource pack name.", ModuleCategory.HUD);
      this.register(new BooleanSetting(this, "showIcon", "Show Icon", true, "Show Icon"));
      showName = this.register(new BooleanSetting(this, "showName", "Show Name", true, "Show Name"));
      showCount = this.register(new BooleanSetting(this, "showCount", "Show Count", false, "if multiple"));
   }
   @Override
   public String renderText() {
      java.util.Collection<String> ids = net.minecraft.client.MinecraftClient.getInstance().getResourcePackManager().getEnabledIds();
      if (ids.isEmpty()) {
         return null;
      }

      StringBuilder sb = new StringBuilder();
      if (this.showName.get()) {
         sb.append(ids.iterator().next());
      }

      if (this.showCount.get() && ids.size() > 1) {
         if (sb.length() > 0) {
            sb.append(' ');
         }

         sb.append('(').append(ids.size()).append(')');
      }

      return sb.length() == 0 ? null : sb.toString();
   }
}
