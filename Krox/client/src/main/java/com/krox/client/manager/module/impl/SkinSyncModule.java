package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class SkinSyncModule extends Module {
   private final BooleanSetting syncCape;
   private final BooleanSetting syncSkin;
   private final BooleanSetting syncWings;

   public SkinSyncModule() {
      super("skin_sync", "Capes / Skins Sync", "Sync supported cosmetic sources via the cosmetics jar.", ModuleCategory.ACCOUNT);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      syncCape = this.register(new BooleanSetting(this, "syncCape", "Sync Cape", true, "Sync Cape"));
      syncSkin = this.register(new BooleanSetting(this, "syncSkin", "Sync Skin", true, "Sync Skin"));
      syncWings = this.register(new BooleanSetting(this, "syncWings", "Sync Wings", false, "Sync Wings"));
   }
   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.player == null) {
         return;
      }

      // There is no network sync to run. The one honest thing this can do
      // is mirror the operator's choices onto the already-local cosmetic
      // sets and report which of them are allowed to draw.
      com.krox.client.KroxClient.get().getClientManager().cosmetics().setEnabled(this.syncSkin.get());

      StringBuilder sb = new StringBuilder();

      if (this.syncCape.get()) {
         sb.append("cape ").append(com.krox.client.KroxClient.get().getClientManager()
            .capes().getActiveId());
      }

      if (this.syncWings.get()) {
         if (sb.length() > 0) {
            sb.append(" ");
         }

         sb.append("wings");
      }

      this.lastState = sb.toString();
   }

   private String lastState = "";

   @Override
   public String renderText() {
      return this.lastState.isEmpty() ? null : this.lastState.trim();
   }
}
