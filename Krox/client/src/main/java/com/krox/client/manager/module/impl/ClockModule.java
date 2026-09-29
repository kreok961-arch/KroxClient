package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.ModuleCategory;

public final class ClockModule extends HudElement {
   public enum ModeMode {
      LOCAL_12H_,
      LOCAL_24H_,
      MC_TIME_,
      BOTH_
   }

   private final EnumSetting<ModeMode> mode;
   private final BooleanSetting showDate;
   private final BooleanSetting showSeconds;
   private final BooleanSetting showTimezone;

   public ClockModule() {
      super("clock", "Clock", "Real and in-game time.", ModuleCategory.HUD);
      mode = this.register(new EnumSetting<ModeMode>(this, "mode", "Mode", ModeMode.LOCAL_24H_, "Mode"));
      showSeconds = this.register(new BooleanSetting(this, "showSeconds", "Show Seconds", false, "Show Seconds"));
      showDate = this.register(new BooleanSetting(this, "showDate", "Show Date", false, "Show Date"));
      showTimezone = this.register(new BooleanSetting(this, "showTimezone", "Show Timezone", false, "Show Timezone"));
   }
   @Override
   public String renderText() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      StringBuilder sb = new StringBuilder();

      if (this.showDate.get()) {
         sb.append(local().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append(" ");
      }

      switch (this.mode.get()) {
         case ModeMode.LOCAL_12H_:
            sb.append(local().format(java.time.format.DateTimeFormatter.ofPattern(
               seconds() ? "hh:mm:ss a" : "hh:mm a")));
            break;
         case ModeMode.LOCAL_24H_:
            sb.append(local().format(java.time.format.DateTimeFormatter.ofPattern(
               seconds() ? "HH:mm:ss" : "HH:mm")));
            break;
         case ModeMode.MC_TIME_:
            sb.append(mcTime());
            break;
         default:
            sb.append(local().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
            sb.append(" / ").append(mcTime());
      }

      if (this.showTimezone.get()) {
         sb.append(" ").append(java.time.ZoneId.systemDefault().getId());
      }

      return sb.toString();
   }

   private boolean seconds() {
      return this.showSeconds.get();
   }

   private static java.time.LocalDateTime local() {
      return java.time.LocalDateTime.now();
   }

   private static String mcTime() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.world == null) {
         return "--:--";
      }

      long ticks = mc.world.getTimeOfDay() % 24000L;
      int hours = (int)((ticks / 1000L + 6L) % 24L);
      int minutes = (int)(ticks / 1000L % 60L) * 60 / 100;
      return String.format("%02d:%02d", hours, minutes);
   }
}
