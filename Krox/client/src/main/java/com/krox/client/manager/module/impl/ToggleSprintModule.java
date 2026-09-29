package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.KeybindSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class ToggleSprintModule extends Module {
   public enum HudIndicatorStyleMode {
      ICON_,
      TEXT_,
      BAR_
   }

   private final EnumSetting<HudIndicatorStyleMode> hudIndicatorStyle;
   private final KeybindSetting keybind;
   private final BooleanSetting showHUDIndicator;

   public ToggleSprintModule() {
      super("toggle_sprint", "Toggle Sprint", "Toggle sprint on/off with a keybind.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      keybind = this.register(new KeybindSetting(this, "keybind", "Keybind", 82, "Keybind"));
      showHUDIndicator = this.register(new BooleanSetting(this, "showHUDIndicator", "Show HUD Indicator", true, "Show HUD Indicator"));
      hudIndicatorStyle = this.register(new EnumSetting<HudIndicatorStyleMode>(this, "hudIndicatorStyle", "Hud Indicator Style", HudIndicatorStyleMode.ICON_, "Hud Indicator Style"));
   }
   private boolean toggled;

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.player == null) {
         return;
      }

      if (this.keybind.get() > 0 && net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), this.keybind.get())) {
         this.toggled = !this.toggled;
      }

      mc.options.getSprintToggled().setValue(this.toggled);
   }

   @Override
   public String renderText() {
      if (!this.showHUDIndicator.get() || !this.toggled) {
         return null;
      }

      return switch (this.hudIndicatorStyle.get()) {
         case HudIndicatorStyleMode.ICON_ -> "S";
         case HudIndicatorStyleMode.BAR_ -> "[###]";
         default -> "Sprint";
      };
   }
}
