package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;
import java.lang.Math;
import java.util.List;

public final class ArmorHudModule extends HudElement {
   public enum OrientationMode {
      HORIZONTAL_,
      VERTICAL_
   }

   private final EnumSetting<OrientationMode> orientation;
   private final BooleanSetting showBoots;
   private final BooleanSetting showChestplate;
   private final BooleanSetting showDurability;
   private final BooleanSetting showHelmet;
   private final BooleanSetting showLeggings;
   private final BooleanSetting showPercentage;
   private final BooleanSetting warnLowDurability;
   private final NumberSetting warnThreshold;

   public ArmorHudModule() {
      super("armor_hud", "Armor HUD", "Displays equipped armor with durability.", ModuleCategory.HUD);
      orientation = this.register(new EnumSetting<OrientationMode>(this, "orientation", "Orientation", OrientationMode.VERTICAL_, "Orientation"));
      showHelmet = this.register(new BooleanSetting(this, "showHelmet", "Show Helmet", true, "Show Helmet"));
      showChestplate = this.register(new BooleanSetting(this, "showChestplate", "Show Chestplate", true, "Show Chestplate"));
      showLeggings = this.register(new BooleanSetting(this, "showLeggings", "Show Leggings", true, "Show Leggings"));
      showBoots = this.register(new BooleanSetting(this, "showBoots", "Show Boots", true, "Show Boots"));
      showDurability = this.register(new BooleanSetting(this, "showDurability", "Show Durability", true, "Show Durability"));
      showPercentage = this.register(new BooleanSetting(this, "showPercentage", "Show Percentage", false, "Show Percentage"));
      warnLowDurability = this.register(new BooleanSetting(this, "warnLowDurability", "Warn Low Durability", true, "Warn Low Durability"));
      warnThreshold = this.register(new NumberSetting(this, "warnThreshold", "Warn Threshold", 10, 1, 50, 1, "Warn Threshold"));
   }
   @Override
   public String renderText() {
      return this.orientation.get() == OrientationMode.VERTICAL_ ? null : row();
   }

   @Override
   public boolean isMultiLine() {
      return this.orientation.get() == OrientationMode.VERTICAL_;
   }

   @Override
   public java.util.List<String> renderLines() {
      if (this.orientation.get() != OrientationMode.VERTICAL_) {
         return List.of();
      }

      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      if (p == null) {
         return List.of("No armor");
      }

      java.util.List<String> out = new java.util.ArrayList<>();
      if (this.showHelmet.get()) {
         out.add(entry(p, net.minecraft.entity.EquipmentSlot.HEAD, "Helm"));
      }

      if (this.showChestplate.get()) {
         out.add(entry(p, net.minecraft.entity.EquipmentSlot.CHEST, "Chst"));
      }

      if (this.showLeggings.get()) {
         out.add(entry(p, net.minecraft.entity.EquipmentSlot.LEGS, "Legs"));
      }

      if (this.showBoots.get()) {
         out.add(entry(p, net.minecraft.entity.EquipmentSlot.FEET, "Boots"));
      }

      return out;
   }

   private String row() {
      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      if (p == null) {
         return "No armor";
      }

      StringBuilder sb = new StringBuilder();
      for (net.minecraft.entity.EquipmentSlot slot : new net.minecraft.entity.EquipmentSlot[]{net.minecraft.entity.EquipmentSlot.HEAD, net.minecraft.entity.EquipmentSlot.CHEST, net.minecraft.entity.EquipmentSlot.LEGS, net.minecraft.entity.EquipmentSlot.FEET}) {
         net.minecraft.item.ItemStack s = p.getEquippedStack(slot);
         if (s.isEmpty()) {
            continue;
         }

         if (sb.length() > 0) {
            sb.append("  ");
         }

         sb.append(label(slot)).append(this.showDurability.get() ? durability(s) : "");
      }

      return sb.length() == 0 ? "No armor" : sb.toString();
   }

   private String entry(net.minecraft.client.network.ClientPlayerEntity p, net.minecraft.entity.EquipmentSlot slot, String name) {
      net.minecraft.item.ItemStack s = p.getEquippedStack(slot);
      if (s.isEmpty()) {
         return name + " -";
      }

      return name + " " + (this.showDurability.get() ? durability(s) : "");
   }

   private static String label(net.minecraft.entity.EquipmentSlot slot) {
      return switch (slot) {
         case HEAD -> "Helm";
         case CHEST -> "Chst";
         case LEGS -> "Legs";
         case FEET -> "Boots";
         default -> "";
      };
   }

   private String durability(net.minecraft.item.ItemStack stack) {
      if (stack.getMaxDamage() <= 0) {
         return "";
      }

      int left = stack.getMaxDamage() - stack.getDamage();
      String txt = this.showPercentage.get()
         ? Math.round(100.0F * left / stack.getMaxDamage()) + "%"
         : String.valueOf(left);
      if (this.warnLowDurability.get() && left <= (int)this.warnThreshold.get()) {
         this.textColor.set(com.krox.client.util.KroxTheme.DANGER);
      }

      return txt;
   }
}
