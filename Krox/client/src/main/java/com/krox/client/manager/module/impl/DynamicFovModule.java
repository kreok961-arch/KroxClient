package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import java.lang.Math;

public final class DynamicFovModule extends Module {
   private final NumberSetting bowFov;
   private final BooleanSetting smoothTransitions;
   private final NumberSetting speedFov;
   private final NumberSetting sprintFov;
   private final NumberSetting transitionMs;

   public DynamicFovModule() {
      super("dynamic_fov", "FOV", "FOV changes based on sprinting, speed, bow, zoom.", ModuleCategory.COMBAT);
      sprintFov = this.register(new NumberSetting(this, "sprintFov", "Sprint Fov", 10, 0, 30, 1, "Sprint Fov"));
      speedFov = this.register(new NumberSetting(this, "speedFov", "Speed Fov", 5, 0, 30, 1, "Speed Fov"));
      bowFov = this.register(new NumberSetting(this, "bowFov", "Bow Fov", 15, 0, 30, 1, "Bow Fov"));
      smoothTransitions = this.register(new BooleanSetting(this, "smoothTransitions", "Smooth Transitions", true, "Smooth Transitions"));
      transitionMs = this.register(new NumberSetting(this, "transitionMs", "Transition Ms", 300, 100, 1000, 1, "Transition Ms"));
   }
   private int baseFov = 70;
   private int targetFov = 70;
   private double savedFov = 70.0D;

   @Override
   protected void onEnable() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.options != null) {
         this.baseFov = mc.options.getFov().getValue();
         this.targetFov = this.baseFov;
         this.savedFov = this.baseFov;
      }
   }

   @Override
   protected void onDisable() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.options != null) {
         mc.options.getFov().setValue((int)Math.round(this.savedFov));
      }
   }

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.options == null || mc.player == null) {
         return;
      }

      this.baseFov = mc.options.getFov().getValue();
      int want = this.baseFov;

      if (mc.player.isSprinting()) {
         want += (int)this.sprintFov.get();
      }

      if (usingBow(mc)) {
         want += (int)this.bowFov.get();
      }

      want += (int)(this.speedFov.get() * speedRatio(mc));
      want = (int)net.minecraft.util.math.MathHelper.clamp(want, this.baseFov, 110);
      this.targetFov = want;

      if (this.smoothTransitions.get()) {
         double perTick = 70.0D / Math.max(1, this.transitionMs.get()) * 50.0D;
         this.savedFov = net.minecraft.util.math.MathHelper.stepTowards(
            (float)this.savedFov, (float)this.targetFov, (float)perTick);
      } else {
         this.savedFov = this.targetFov;
      }

      mc.options.getFov().setValue((int)Math.round(this.savedFov));
   }

   private static boolean usingBow(net.minecraft.client.MinecraftClient mc) {
      return mc.player.isUsingItem()
         && mc.player.getMainHandStack().getItem() instanceof net.minecraft.item.BowItem;
   }

   private static double speedRatio(net.minecraft.client.MinecraftClient mc) {
      net.minecraft.util.math.Vec3d v = mc.player.getVelocity();
      double blocksPerTick = Math.sqrt(v.x * v.x + v.z * v.z);
      return net.minecraft.util.math.MathHelper.clamp(blocksPerTick / 0.4D, 0.0D, 1.0D);
   }
}
