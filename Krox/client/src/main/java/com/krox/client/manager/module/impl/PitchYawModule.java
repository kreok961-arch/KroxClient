package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class PitchYawModule extends HudElement {
   private final NumberSetting decimalPlaces;
   private final BooleanSetting showPitch;
   private final BooleanSetting showYaw;

   public PitchYawModule() {
      super("pitch_yaw", "Pitch / Yaw", "Camera rotation angles.", ModuleCategory.HUD);
      showPitch = this.register(new BooleanSetting(this, "showPitch", "Show Pitch", true, "Show Pitch"));
      showYaw = this.register(new BooleanSetting(this, "showYaw", "Show Yaw", true, "Show Yaw"));
      decimalPlaces = this.register(new NumberSetting(this, "decimalPlaces", "Decimal Places", 1, 0, 2, 1, "Decimal Places"));
   }
   @Override
   public String renderText() {
      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      if (p == null) {
         return null;
      }

      String f = "%." + (int)this.decimalPlaces.get() + "f";
      StringBuilder sb = new StringBuilder();
      if (this.showPitch.get()) {
         sb.append("P ").append(String.format(f, p.getPitch()));
      }

      if (this.showYaw.get()) {
         if (sb.length() > 0) {
            sb.append(' ');
         }

         sb.append("Y ").append(String.format(f, p.getYaw()));
      }

      return sb.toString();
   }
}
