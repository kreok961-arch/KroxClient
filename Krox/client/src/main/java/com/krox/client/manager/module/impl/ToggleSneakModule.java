package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.KeybindSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class ToggleSneakModule extends Module {
   private final BooleanSetting autoDisableOnJump;
   private final KeybindSetting keybind;
   private final BooleanSetting showHUDIndicator;

   public ToggleSneakModule() {
      super("toggle_sneak", "Toggle Sneak", "Toggle sneak with a keybind.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      keybind = this.register(new KeybindSetting(this, "keybind", "Keybind", 0, "Keybind"));
      showHUDIndicator = this.register(new BooleanSetting(this, "showHUDIndicator", "Show HUD Indicator", true, "Show HUD Indicator"));
      autoDisableOnJump = this.register(new BooleanSetting(this, "autoDisableOnJump", "Auto Disable On Jump", true, "Auto Disable On Jump"));
   }
   private boolean toggled;

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      net.minecraft.client.network.ClientPlayerEntity p = mc.player;
      if (p == null) {
         return;
      }

      if (this.keybind.get() > 0 && net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), this.keybind.get())) {
         this.toggled = !this.toggled;
      }

      if (this.toggled) {
         net.minecraft.util.PlayerInput in = p.getLastPlayerInput();
         if (this.autoDisableOnJump.get() && in != null && in.jump()) {
            this.toggled = false;
         } else {
            p.setSneaking(true);
         }
      }
   }

   @Override
   public String renderText() {
      return this.showHUDIndicator.get() && this.toggled ? "Sneak" : null;
   }
}
