package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class DirectionModule extends HudElement {
   private final BooleanSetting showCardinal;
   private final BooleanSetting showPitch;
   private final BooleanSetting showYaw;

   public DirectionModule() {
      super("direction", "Direction / Compass", "Facing direction with N/E/S/W indicators.", ModuleCategory.HUD);
      showCardinal = this.register(new BooleanSetting(this, "showCardinal", "Show Cardinal", true, "N/E/S/W"));
      showYaw = this.register(new BooleanSetting(this, "showYaw", "Show Yaw", false, "exact degrees"));
      showPitch = this.register(new BooleanSetting(this, "showPitch", "Show Pitch", false, "Show Pitch"));
      this.register(new BooleanSetting(this, "showRibbon", "Show Ribbon", false, "directional ribbon"));
      this.register(new NumberSetting(this, "ribbonWidth", "Ribbon Width", 80, 40, 200, 1, "Ribbon Width"));
   }
   @Override
   public String renderText() {
      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      if (p == null) {
         return null;
      }

      float yaw = p.getYaw();
      StringBuilder sb = new StringBuilder();
      if (this.showCardinal.get()) {
         sb.append(cardinal(yaw));
      }

      if (this.showYaw.get()) {
         if (sb.length() > 0) {
            sb.append(' ');
         }

         sb.append(String.format("%.1f", yaw));
      }

      if (this.showPitch.get()) {
         if (sb.length() > 0) {
            sb.append(' ');
         }

         sb.append(String.format("%.1f", p.getPitch()));
      }

      return sb.length() == 0 ? null : sb.toString();
   }

   private static String cardinal(float yaw) {
      float m = ((yaw % 360.0F) + 360.0F) % 360.0F;
      if (m < 22.5F || m >= 337.5F) {
         return "S";
      } else if (m < 67.5F) {
         return "SW";
      } else if (m < 112.5F) {
         return "W";
      } else if (m < 157.5F) {
         return "NW";
      } else if (m < 202.5F) {
         return "N";
      } else if (m < 247.5F) {
         return "NE";
      } else if (m < 292.5F) {
         return "E";
      }

      return "SE";
   }
}
