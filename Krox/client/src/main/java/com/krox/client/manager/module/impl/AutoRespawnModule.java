package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class AutoRespawnModule extends Module {
   private final NumberSetting delayMs;
   private final BooleanSetting enabled;
   private final BooleanSetting notifyOnRespawn;
   private final BooleanSetting onlyOnServers;

   public AutoRespawnModule() {
      super("auto_respawn", "Auto Respawn", "Automated respawn on supported death screens.", ModuleCategory.COMBAT);
      enabled = this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      delayMs = this.register(new NumberSetting(this, "delayMs", "Delay Ms", 500, 0, 5000, 1, "Delay Ms"));
      onlyOnServers = this.register(new BooleanSetting(this, "onlyOnServers", "Only On Servers", true, "Only On Servers"));
      notifyOnRespawn = this.register(new BooleanSetting(this, "notifyOnRespawn", "Notify On Respawn", true, "Notify On Respawn"));
   }
   private long deadAt = -1L;

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (!this.enabled.get()) {
         return;
      }

      if (mc.player == null || !mc.player.isDead() || !(mc.currentScreen instanceof net.minecraft.client.gui.screen.DeathScreen)) {
         this.deadAt = -1L;
         return;
      }

      if (this.onlyOnServers.get() && mc.getNetworkHandler() == null) {
         return;
      }

      long now = System.currentTimeMillis();

      if (this.deadAt < 0L) {
         this.deadAt = now;
         return;
      }

      if (now - this.deadAt < (long)this.delayMs.get()) {
         return;
      }

      // The vanilla Respawn button calls exactly this, so the server still
      // decides whether the respawn is accepted.
      mc.player.requestRespawn();
      this.deadAt = -1L;

      if (this.notifyOnRespawn.get()) {
         com.krox.client.KroxClient.get().getClientManager().notifications()
            .post("Krox", "Respawned");
      }
   }
}
