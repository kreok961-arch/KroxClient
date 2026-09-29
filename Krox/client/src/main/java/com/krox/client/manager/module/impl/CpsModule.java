package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;
import java.lang.Math;
import java.util.List;

public final class CpsModule extends HudElement {
   public enum LayoutMode {
      HORIZONTAL_,
      VERTICAL_
   }

   private final BooleanSetting dynamicColor;
   private final EnumSetting<LayoutMode> layout;
   private final BooleanSetting showLabel;
   private final BooleanSetting trackLeft;
   private final BooleanSetting trackRight;
   private final NumberSetting windowMs;

   public CpsModule() {
      super("cps", "CPS Display", "Clicks per second (LMB/RMB).", ModuleCategory.HUD);
      trackLeft = this.register(new BooleanSetting(this, "trackLeft", "Track Left", true, "Track Left"));
      trackRight = this.register(new BooleanSetting(this, "trackRight", "Track Right", true, "Track Right"));
      windowMs = this.register(new NumberSetting(this, "windowMs", "Window Ms", 1000, 500, 5000, 1, "counting window"));
      showLabel = this.register(new BooleanSetting(this, "showLabel", "Show Label", true, "Show Label"));
      layout = this.register(new EnumSetting<LayoutMode>(this, "layout", "Layout", LayoutMode.HORIZONTAL_, "Layout"));
      dynamicColor = this.register(new BooleanSetting(this, "dynamicColor", "Dynamic Color", false, "color shifts with CPS"));
   }
   private final java.util.ArrayList<Long> left = new java.util.ArrayList<>();
   private final java.util.ArrayList<Long> right = new java.util.ArrayList<>();

   /** Called by the mouse mixin so CPS counts real button presses, not polls. */
   public void record(boolean isLeft) {
      long now = System.currentTimeMillis();
      (isLeft ? this.left : this.right).add(now);
   }

   @Override
   protected void onTick() {
      long cutoff = System.currentTimeMillis() - (long)this.windowMs.get();
      this.left.removeIf(t -> t < cutoff);
      this.right.removeIf(t -> t < cutoff);
   }

   @Override
   public String renderText() {
      if (this.layout.get() == LayoutMode.VERTICAL_) {
         return null;
      }

      return text();
   }

   @Override
   public boolean isMultiLine() {
      return this.layout.get() == LayoutMode.VERTICAL_;
   }

   @Override
   public java.util.List<String> renderLines() {
      if (this.layout.get() != LayoutMode.VERTICAL_) {
         return List.of();
      }

      return List.of(
         this.trackLeft.get() ? "LMB " + this.left.size() : "",
         this.trackRight.get() ? "RMB " + this.right.size() : ""
      );
   }

   private String text() {
      StringBuilder sb = new StringBuilder();
      if (this.showLabel.get()) {
         sb.append("CPS: ");
      }

      if (this.trackLeft.get()) {
         sb.append(this.left.size());
      }

      if (this.trackLeft.get() && this.trackRight.get()) {
         sb.append(" / ");
      }

      if (this.trackRight.get()) {
         sb.append(this.right.size());
      }

      if (this.dynamicColor.get()) {
         int worst = Math.max(this.left.size(), this.right.size());
         this.textColor.set(worst >= 15 ? com.krox.client.util.KroxTheme.DANGER : worst >= 10 ? com.krox.client.util.KroxTheme.WARNING : com.krox.client.util.KroxTheme.SUCCESS);
      }

      return sb.toString();
   }
}
