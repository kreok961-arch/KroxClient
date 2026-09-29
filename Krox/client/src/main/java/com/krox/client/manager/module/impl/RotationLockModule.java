package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public final class RotationLockModule extends Module {
   private final BooleanSetting alsoLockPitch = this.register(new BooleanSetting(this, "lockPitch", "Also Lock Pitch", true, "Lock pitch in addition to yaw."));
   private float lockedYaw = 0.0F;
   private float lockedPitch = 0.0F;

   public RotationLockModule() {
      super("rotationlock", "Rotation Lock", "Freeze your yaw/pitch at the current value.", ModuleCategory.PLAYER);
   }

   @Override
   protected void onEnable() {
      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      if (p != null) {
         this.lockedYaw = p.getYaw();
         this.lockedPitch = p.getPitch();
      }
   }

   @Override
   protected void onTick() {
      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      if (p != null) {
         p.setYaw(this.lockedYaw);
         if (this.alsoLockPitch.get()) {
            p.setPitch(this.lockedPitch);
         }
      }
   }
}
