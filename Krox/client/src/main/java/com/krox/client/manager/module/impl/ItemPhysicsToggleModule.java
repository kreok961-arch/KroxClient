package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.KeybindSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class ItemPhysicsToggleModule extends Module {
   private final KeybindSetting keybind;

   public ItemPhysicsToggleModule() {
      super("item_physics_toggle", "Item Physics Toggle", "Independent on/off for item physics from any other system.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      keybind = this.register(new KeybindSetting(this, "keybind", "Keybind", 0, "Keybind"));
   }
   private boolean wasDown = false;

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.options == null || mc.getWindow() == null || this.keybind.get() == 0) {
         return;
      }

      boolean down = net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), this.keybind.get());
      if (down && !this.wasDown) {
         com.krox.client.manager.module.Module target = com.krox.client.KroxClient.get()
            .getClientManager().modules().byId("item_physics");

         if (target != null) {
            target.toggle();
         }
      }

      this.wasDown = down;
   }
}
