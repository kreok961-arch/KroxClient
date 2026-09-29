package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class TargetHudModule extends HudElement {
   private final NumberSetting fadeTimeout;
   private final BooleanSetting showArmor;
   private final BooleanSetting showDistance;
   private final BooleanSetting showHealth;
   private final BooleanSetting showName;

   public TargetHudModule() {
      super("target_hud", "Target HUD", "Shows the currently targeted player/entity.", ModuleCategory.HUD);
      this.register(new BooleanSetting(this, "showAvatar", "Show Avatar", true, "Show Avatar"));
      showHealth = this.register(new BooleanSetting(this, "showHealth", "Show Health", true, "Show Health"));
      this.register(new BooleanSetting(this, "showHealthBar", "Show Health Bar", true, "Show Health Bar"));
      showName = this.register(new BooleanSetting(this, "showName", "Show Name", true, "Show Name"));
      showDistance = this.register(new BooleanSetting(this, "showDistance", "Show Distance", true, "Show Distance"));
      showArmor = this.register(new BooleanSetting(this, "showArmor", "Show Armor", false, "Show Armor"));
      fadeTimeout = this.register(new NumberSetting(this, "fadeTimeout", "Fade Timeout", 2000, 500, 5000, 1, "Fade Timeout"));
   }
   private net.minecraft.entity.Entity target;
   private long lastSeen;

   @Override
   public String renderText() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      net.minecraft.entity.Entity t = mc.crosshairTarget != null && mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.ENTITY ? ((net.minecraft.util.hit.EntityHitResult)mc.crosshairTarget).getEntity() : null;
      if (t != null) {
         this.target = t;
         this.lastSeen = System.currentTimeMillis();
      } else if (System.currentTimeMillis() - this.lastSeen > this.fadeTimeout.get()) {
         this.target = null;
      }

      if (this.target == null) {
         return null;
      }

      StringBuilder sb = new StringBuilder();
      if (this.showName.get()) {
         sb.append(this.target.getName().getString());
      }

      if (this.target instanceof net.minecraft.entity.LivingEntity le) {
         if (this.showHealth.get()) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            sb.append(String.format("%.1f/%.1f", le.getHealth(), le.getMaxHealth()));
         }

         if (this.showArmor.get()) {
            sb.append(" [").append(le.getArmor()).append(']');
         }
      }

      if (this.showDistance.get() && mc.player != null) {
         if (sb.length() > 0) {
            sb.append(' ');
         }

         sb.append(String.format("%.1fm", mc.player.distanceTo(this.target)));
      }

      return sb.toString();
   }
}
