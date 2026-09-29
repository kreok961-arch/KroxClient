package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;

public final class SprintModule extends Module {
   public SprintModule() {
      super("autosprint", "Auto Sprint", "Automatically sprint when moving forward.", ModuleCategory.PLAYER);
   }

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.player != null && mc.options != null) {
         net.minecraft.entity.player.PlayerEntity p = mc.player;
         net.minecraft.client.option.KeyBinding forward = mc.options.forwardKey;
         boolean pressingForward = forward.isPressed();
         if (pressingForward && !p.isSprinting() && p.getHungerManager().getFoodLevel() > 6) {
            p.setSprinting(true);
         }
      }
   }
}
