package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class ReplayModule extends Module {
   private final BooleanSetting autoStartRecording;
   private final BooleanSetting notificationOnStart;
   private final BooleanSetting notificationOnStop;
   private final BooleanSetting recordingIndicator;

   public ReplayModule() {
      super("replay", "Replay / Recording", "Replay recording, playback, cinematic camera via the supplied replay jar.", ModuleCategory.WORLD);
      autoStartRecording = this.register(new BooleanSetting(this, "autoStartRecording", "Auto Start Recording", false, "Auto Start Recording"));
      recordingIndicator = this.register(new BooleanSetting(this, "recordingIndicator", "Recording Indicator", true, "Recording Indicator"));
      notificationOnStart = this.register(new BooleanSetting(this, "notificationOnStart", "Notification On Start", true, "Notification On Start"));
      notificationOnStop = this.register(new BooleanSetting(this, "notificationOnStop", "Notification On Stop", true, "Notification On Stop"));
      this.register(new BooleanSetting(this, "proxyCommands", "Proxy Commands", true, "`/kroxreplay pause` etc."));
   }
   @Override
   protected void onEnable() {
      com.krox.client.KroxClient.get().getClientManager().hud().register(this);
      if (this.autoStartRecording.get() && this.notificationOnStart.get()) {
         com.krox.client.KroxClient.get().getClientManager().notifications().post("Krox", "Replay recording started");
      }
   }

   @Override
   protected void onDisable() {
      com.krox.client.KroxClient.get().getClientManager().hud().unregister(this);
      if (this.notificationOnStop.get()) {
         com.krox.client.KroxClient.get().getClientManager().notifications().post("Krox", "Replay recording stopped");
      }
   }

   @Override
   public String renderText() {
      if (!this.recordingIndicator.get()) {
         return null;
      }

      return com.krox.client.util.ModPresence.isLoaded("flashback") ? "REC" : "Replay jar absent";
   }
}
