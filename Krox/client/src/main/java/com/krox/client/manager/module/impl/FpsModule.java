package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.manager.module.StringSetting;
import java.lang.Integer;
import java.lang.Math;

public final class FpsModule extends HudElement {
   private final NumberSetting graphHistory;
   private final StringSetting label;
   private final BooleanSetting showFps;
   private final BooleanSetting showFrameTime;
   private final BooleanSetting showGraph;
   private final BooleanSetting showLabel;

   public FpsModule() {
      super("fps", "FPS Display", "Real-time frames-per-second counter.", ModuleCategory.HUD);
      showLabel = this.register(new BooleanSetting(this, "showLabel", "Show Label", true, "prefix text"));
      label = this.register(new StringSetting(this, "label", "Label", "FPS: ", "prefix"));
      showFps = this.register(new BooleanSetting(this, "showFps", "Show Fps", true, "numeric value"));
      showFrameTime = this.register(new BooleanSetting(this, "showFrameTime", "Show Frame Time", false, "ms per frame"));
      showGraph = this.register(new BooleanSetting(this, "showGraph", "Show Graph", false, "sparkline"));
      graphHistory = this.register(new NumberSetting(this, "graphHistory", "Graph History", 60, 10, 200, 1, "samples"));
   }
   private final java.util.ArrayDeque<Integer> history = new java.util.ArrayDeque<>();

   @Override
   public String renderText() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      StringBuilder sb = new StringBuilder();
      if (this.showLabel.get()) {
         sb.append(this.label.get());
      }

      if (this.showFps.get()) {
         sb.append(mc.getCurrentFps());
      }

      if (this.showFrameTime.get()) {
         if (sb.length() > 0) {
            sb.append(' ');
         }

         sb.append(String.format("%.2fms", 1000.0D / Math.max(1, mc.getCurrentFps())));
      }

      if (this.showGraph.get()) {
         if (sb.length() > 0) {
            sb.append(' ');
         }

         sb.append(graph());
      }

      return sb.toString();
   }

   @Override
   protected void onTick() {
      if (this.showGraph.get()) {
         this.history.addLast(net.minecraft.client.MinecraftClient.getInstance().getCurrentFps());
         int cap = (int)this.graphHistory.get();
         while (this.history.size() > cap) {
            this.history.removeFirst();
         }
      }
   }

   private String graph() {
      int lo = Integer.MAX_VALUE;
      int hi = Integer.MIN_VALUE;
      for (int v : this.history) {
         lo = Math.min(lo, v);
         hi = Math.max(hi, v);
      }

      if (lo == Integer.MAX_VALUE) {
         return "";
      }

      int span = Math.max(1, hi - lo);
      StringBuilder sb = new StringBuilder();
      for (int v : this.history) {
         int idx = (int)Math.round((double)(v - lo) / span * 7.0D);
         sb.append("_".charAt(Math.max(0, Math.min(7, idx))));
      }

      return sb.toString();
   }
}
