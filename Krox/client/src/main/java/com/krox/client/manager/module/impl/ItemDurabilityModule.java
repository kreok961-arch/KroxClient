package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;
import java.lang.Math;
import java.util.List;

public final class ItemDurabilityModule extends HudElement {
   private final BooleanSetting hideUnbreakable;
   private final BooleanSetting showHeldItem;
   private final BooleanSetting showOffhand;
   private final BooleanSetting showPercentage;
   private final BooleanSetting showValue;
   private final NumberSetting warnThreshold;

   public ItemDurabilityModule() {
      super("item_durability", "Item Durability", "Held item and offhand durability.", ModuleCategory.HUD);
      showHeldItem = this.register(new BooleanSetting(this, "showHeldItem", "Show Held Item", true, "Show Held Item"));
      showOffhand = this.register(new BooleanSetting(this, "showOffhand", "Show Offhand", true, "Show Offhand"));
      showPercentage = this.register(new BooleanSetting(this, "showPercentage", "Show Percentage", true, "Show Percentage"));
      showValue = this.register(new BooleanSetting(this, "showValue", "Show Value", false, "Show Value"));
      hideUnbreakable = this.register(new BooleanSetting(this, "hideUnbreakable", "Hide Unbreakable", true, "Hide Unbreakable"));
      warnThreshold = this.register(new NumberSetting(this, "warnThreshold", "Warn Threshold", 10, 0, 100, 1, "Warn Threshold"));
   }
   @Override
   public boolean isMultiLine() {
      return this.showHeldItem.get() && this.showOffhand.get();
   }

   @Override
   public String renderText() {
      if (this.isMultiLine()) {
         return null;
      }

      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      if (p == null) {
         return null;
      }

      String s = this.showHeldItem.get() ? durability(p.getInventory().getSelectedStack()) : null;
      if (s == null && this.showOffhand.get()) {
         s = durability(p.getOffHandStack());
      }

      return s;
   }

   @Override
   public java.util.List<String> renderLines() {
      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      if (p == null) {
         return List.of();
      }

      java.util.List<String> out = new java.util.ArrayList<>();
      if (this.showHeldItem.get()) {
         String s = durability(p.getInventory().getSelectedStack());
         if (s != null) {
            out.add(s);
         }
      }

      if (this.showOffhand.get()) {
         String s = durability(p.getOffHandStack());
         if (s != null) {
            out.add(s);
         }
      }

      return out;
   }

   private String durability(net.minecraft.item.ItemStack stack) {
      if (stack.isEmpty()) {
         return null;
      }

      if (stack.getMaxDamage() <= 0) {
         return this.hideUnbreakable.get() ? null : stack.getName().getString();
      }

      int left = stack.getMaxDamage() - stack.getDamage();
      StringBuilder sb = new StringBuilder(stack.getName().getString()).append(' ');
      if (this.showPercentage.get()) {
         sb.append(Math.round(100.0F * left / stack.getMaxDamage())).append('%');
      }

      if (this.showValue.get()) {
         if (this.showPercentage.get()) {
            sb.append(' ');
         }

         sb.append(left).append('/').append(stack.getMaxDamage());
      }

      if (left <= (int)this.warnThreshold.get()) {
         this.textColor.set(com.krox.client.util.KroxTheme.DANGER);
      }

      return sb.toString();
   }
}
