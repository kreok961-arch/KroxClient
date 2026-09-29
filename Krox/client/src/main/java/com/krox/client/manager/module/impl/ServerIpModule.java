package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;

public final class ServerIpModule extends HudElement {
   private final BooleanSetting privacyHide;
   private final BooleanSetting showClientVersion;
   private final BooleanSetting showPing;
   private final BooleanSetting showServer;
   private final BooleanSetting showVersion;

   public ServerIpModule() {
      super("server_ip", "Server IP", "Current server info.", ModuleCategory.HUD);
      showServer = this.register(new BooleanSetting(this, "showServer", "Show Server", true, "Show Server"));
      showPing = this.register(new BooleanSetting(this, "showPing", "Show Ping", true, "Show Ping"));
      showVersion = this.register(new BooleanSetting(this, "showVersion", "Show Version", true, "Show Version"));
      showClientVersion = this.register(new BooleanSetting(this, "showClientVersion", "Show Client Version", false, "Show Client Version"));
      privacyHide = this.register(new BooleanSetting(this, "privacyHide", "Privacy Hide", false, "replaces IP with ***"));
   }
   @Override
   public String renderText() {
      net.minecraft.client.network.ServerInfo info = net.minecraft.client.MinecraftClient.getInstance().getCurrentServerEntry();
      if (info == null) {
         return this.showServer.get() ? "Singleplayer" : null;
      }

      StringBuilder sb = new StringBuilder();
      if (this.showServer.get()) {
         sb.append(this.privacyHide.get() ? maskAddress(info.address) : info.address);
      }

      if (this.showPing.get()) {
         if (sb.length() > 0) {
            sb.append(' ');
         }

         sb.append(info.ping).append("ms");
      }

      if (this.showVersion.get()) {
         if (sb.length() > 0) {
            sb.append(' ');
         }

         sb.append(info.version);
      }

      if (this.showClientVersion.get()) {
         if (sb.length() > 0) {
            sb.append(' ');
         }

         sb.append(net.minecraft.SharedConstants.getGameVersion());
      }

      return sb.length() == 0 ? null : sb.toString();
   }

   private static String maskAddress(String address) {
      int i = address.indexOf(':');
      return i < 0 ? "***" : address.substring(0, i) + ":***";
   }
}
