package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.ModuleCategory;
import java.lang.Math;

public final class MemoryModule extends HudElement {
   public enum FormatMode {
      MB_,
      GB_
   }

   private final EnumSetting<FormatMode> format;
   private final BooleanSetting showAllocated;
   private final BooleanSetting showMax;
   private final BooleanSetting showPercentage;
   private final BooleanSetting showUsed;

   public MemoryModule() {
      super("memory", "Memory / RAM", "JVM heap usage.", ModuleCategory.HUD);
      showAllocated = this.register(new BooleanSetting(this, "showAllocated", "Show Allocated", true, "Show Allocated"));
      showUsed = this.register(new BooleanSetting(this, "showUsed", "Show Used", true, "Show Used"));
      showMax = this.register(new BooleanSetting(this, "showMax", "Show Max", true, "Show Max"));
      showPercentage = this.register(new BooleanSetting(this, "showPercentage", "Show Percentage", true, "Show Percentage"));
      format = this.register(new EnumSetting<FormatMode>(this, "format", "Format", FormatMode.MB_, "Format"));
   }
   @Override
   public String renderText() {
      java.lang.Runtime rt = java.lang.Runtime.getRuntime();
      boolean gb = this.format.get() == FormatMode.GB_;
      double scale = gb ? 1073741824.0D : 1048576.0D;
      String unit = gb ? "GB" : "MB";
      double used = (double)(rt.totalMemory() - rt.freeMemory()) / scale;
      double alloc = (double)rt.totalMemory() / scale;
      double max = (double)rt.maxMemory() / scale;
      StringBuilder sb = new StringBuilder();
      if (this.showUsed.get()) {
         sb.append(String.format("%.0f", used));
      }

      if (this.showAllocated.get()) {
         if (sb.length() > 0) {
            sb.append('/');
         }

         sb.append(String.format("%.0f", alloc));
      }

      if (this.showMax.get()) {
         if (sb.length() > 0) {
            sb.append('/');
         }

         sb.append(String.format("%.0f", max));
      }

      if (sb.length() > 0) {
         sb.append(unit);
      }

      if (this.showPercentage.get()) {
         sb.append(' ').append(Math.round(100.0D * used / Math.max(1.0D, max))).append('%');
      }

      return sb.length() == 0 ? null : sb.toString();
   }
}
