package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class TimeChangerModule extends Module {
   public enum PresetMode {
      DAY_,
      SUNSET_,
      NIGHT_,
      SUNRISE_,
      CUSTOM_
   }

   private final NumberSetting customTicks;
   private final EnumSetting<PresetMode> preset;

   public TimeChangerModule() {
      super("time_changer", "Time Changer", "Client-side world time override.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      preset = this.register(new EnumSetting<PresetMode>(this, "preset", "Preset", PresetMode.DAY_, "Preset"));
      customTicks = this.register(new NumberSetting(this, "customTicks", "Custom Ticks", 6000, 0, 24000, 1, "Custom Ticks"));
      this.register(new BooleanSetting(this, "affectsWorld", "Affects World", false, "client-side only by default"));
   }
   @Override
   public String renderText() {
      if (net.minecraft.client.MinecraftClient.getInstance().world == null) {
         return null;
      }

      long ticks = switch (this.preset.get()) {
         case PresetMode.DAY_ -> 1000L;
         case PresetMode.SUNSET_ -> 12000L;
         case PresetMode.NIGHT_ -> 15000L;
         case PresetMode.SUNRISE_ -> 23000L;
         default -> (long)this.customTicks.get();
      };
      return "Time " + ticks / 1000L + "k";
   }
}
