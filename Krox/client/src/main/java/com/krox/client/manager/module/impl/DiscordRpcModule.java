package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class DiscordRpcModule extends Module {
   private final BooleanSetting privacyMode;
   private final BooleanSetting showDimension;
   private final BooleanSetting showServer;
   private final BooleanSetting showTime;

   public DiscordRpcModule() {
      super("discord_rpc", "Discord Rich Presence", "Discord rich presence showing client state.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      showServer = this.register(new BooleanSetting(this, "showServer", "Show Server", true, "Show Server"));
      showDimension = this.register(new BooleanSetting(this, "showDimension", "Show Dimension", true, "Show Dimension"));
      showTime = this.register(new BooleanSetting(this, "showTime", "Show Time", true, "Show Time"));
      privacyMode = this.register(new BooleanSetting(this, "privacyMode", "Privacy Mode", false, "Privacy Mode"));
   }
   private String lastDetail = "";
   private long lastPush = 0L;

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.player == null || mc.getNetworkHandler() == null) {
         return;
      }

      long now = System.currentTimeMillis();
      if (now - this.lastPush < 5000L) {
         return;
      }

      this.lastPush = now;
      StringBuilder sb = new StringBuilder("Minecraft");

      if (this.showServer.get()) {
         net.minecraft.client.network.ServerInfo info = mc.getNetworkHandler().getServerInfo();

         if (info != null) {
            // Privacy mode swaps the server name for a constant, matching what
            // every other client hides behind its privacy toggle.
            sb.append(" - ").append(this.privacyMode.get() ? "server" : info.name);
         }
      }

      if (this.showDimension.get() && mc.world != null) {
         sb.append(" [").append(mc.world.getRegistryKey().getValue().getPath()).append("]");
      }

      if (this.showTime.get() && mc.world != null) {
         long ticks = mc.world.getTimeOfDay() / 1000L;
         sb.append(String.format(" %02d:%02d", (int)((ticks / 60L + 6L) % 24L), (int)(ticks % 60L)));
      }

      this.lastDetail = sb.toString();
   }

   @Override
   public String renderText() {
      // TODO(D-30): actually handing this to Discord needs the Discord RPC
      // dependency, which the spec forbids adding. The presence line is
      // built and shown in the HUD so the formatting is ready for the
      // transport when it lands.
      return this.lastDetail.isEmpty() ? null : this.lastDetail;
   }
}
