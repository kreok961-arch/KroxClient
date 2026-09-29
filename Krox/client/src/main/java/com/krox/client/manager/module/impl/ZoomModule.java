package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.KeybindSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import java.lang.Math;

public final class ZoomModule extends Module {
   private final KeybindSetting keybind;
   private final BooleanSetting showOverlay;
   private final BooleanSetting smooth;
   private final NumberSetting zoomLevel;

   public ZoomModule() {
      super("zoom", "Smooth Zoom", "OptiFine-style smooth zoom.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      keybind = this.register(new KeybindSetting(this, "keybind", "Keybind", 86, "Keybind"));
      zoomLevel = this.register(new NumberSetting(this, "zoomLevel", "Zoom Level", 4, 1, 10, 0.1, "Zoom Level"));
      smooth = this.register(new BooleanSetting(this, "smooth", "Smooth", true, "Smooth"));
      this.register(new NumberSetting(this, "transitionMs", "Transition Ms", 200, 100, 500, 1, "Transition Ms"));
      this.register(new BooleanSetting(this, "reduceSensitivity", "Reduce Sensitivity", true, "Reduce Sensitivity"));
      showOverlay = this.register(new BooleanSetting(this, "showOverlay", "Show Overlay", true, "Show Overlay"));
   }
   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (this.keybind.get() <= 0) {
         return;
      }

      boolean down = net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), this.keybind.get());
      double target = down ? this.zoomLevel.get() : 1.0D;
      int current = mc.options.getFov().getValue();
      int next = this.smooth.get() ? (int)Math.round(current + (target - current) * 0.3D) : (int)Math.round(target);
      mc.options.getFov().setValue(next);
   }

   @Override
   public String renderText() {
      return this.showOverlay.get() && net.minecraft.client.util.InputUtil.isKeyPressed(net.minecraft.client.MinecraftClient.getInstance().getWindow(), this.keybind.get())
         ? String.format("%.0fx", this.zoomLevel.get())
         : null;
   }
}
