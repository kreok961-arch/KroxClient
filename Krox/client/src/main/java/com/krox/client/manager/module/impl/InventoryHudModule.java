package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.ModuleCategory;
import java.lang.Math;
import java.util.List;

public final class InventoryHudModule extends HudElement {
   public enum OrientationMode {
      GRID_,
      ROW_
   }

   private final EnumSetting<OrientationMode> orientation;
   private final BooleanSetting showCounts;
   private final BooleanSetting showHotbar;
   private final BooleanSetting showOnlyMain;

   public InventoryHudModule() {
      super("inventory_hud", "Player Inventory HUD", "Compact view of the player's 27-slot inventory.", ModuleCategory.HUD);
      showOnlyMain = this.register(new BooleanSetting(this, "showOnlyMain", "Show Only Main", true, "hide hotbar"));
      showHotbar = this.register(new BooleanSetting(this, "showHotbar", "Show Hotbar", true, "Show Hotbar"));
      this.register(new BooleanSetting(this, "showIcons", "Show Icons", true, "Show Icons"));
      this.register(new BooleanSetting(this, "showDurability", "Show Durability", true, "Show Durability"));
      showCounts = this.register(new BooleanSetting(this, "showCounts", "Show Counts", true, "Show Counts"));
      orientation = this.register(new EnumSetting<OrientationMode>(this, "orientation", "Orientation", OrientationMode.GRID_, "Orientation"));
   }
   @Override
   public boolean isMultiLine() {
      return this.orientation.get() == OrientationMode.GRID_ || this.showHotbar.get();
   }

   @Override
   public String renderText() {
      return this.isMultiLine() ? null : grid(0, 27);
   }

   @Override
   public java.util.List<String> renderLines() {
      int from = this.showOnlyMain.get() ? 9 : 0;
      int to = this.showHotbar.get() ? 36 : 27;
      if (this.orientation.get() == OrientationMode.ROW_) {
         return List.of(grid(from, to));
      }

      java.util.List<String> out = new java.util.ArrayList<>();
      for (int i = from; i < to; i += 9) {
         out.add(grid(i, Math.min(to, i + 9)));
      }

      return out;
   }

   private String grid(int from, int to) {
      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      if (p == null) {
         return "";
      }

      net.minecraft.util.collection.DefaultedList<net.minecraft.item.ItemStack> stacks = p.getInventory().getMainStacks();
      StringBuilder sb = new StringBuilder();
      for (int i = from; i < to; i++) {
         net.minecraft.item.ItemStack s = stacks.get(i);
         String cell = s.isEmpty()
            ? "--"
            : this.showCounts.get() && s.getCount() > 1 ? String.valueOf(s.getCount()) : s.getName().getString().substring(0, 1);
         sb.append('[').append(String.format("%-2s", cell)).append(']');
      }

      return sb.toString();
   }
}
