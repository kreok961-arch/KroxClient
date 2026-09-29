package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;
import java.lang.Math;

public final class VirtualJoystickModule extends HudElement {
   private final NumberSetting opacity;
   private final BooleanSetting showBorder;
   private final NumberSetting size;

   public VirtualJoystickModule() {
      super("virtual_joystick", "Virtual Joystick", "On-screen joystick for movement and camera.", ModuleCategory.MOBILE);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      size = this.register(new NumberSetting(this, "size", "Size", 1, 0.5, 2, 0.1, "Size"));
      opacity = this.register(new NumberSetting(this, "opacity", "Opacity", 0.6, 0, 1, 0.01, "Opacity"));
      showBorder = this.register(new BooleanSetting(this, "showBorder", "Show Border", true, "Show Border"));
      this.register(new NumberSetting(this, "deadzone", "Deadzone", 0.05, 0, 0.3, 0.01, "Deadzone"));
   }
   @Override
   protected void onEnable() {
      com.krox.client.KroxClient.get().getClientManager().hud().register(this);
   }

   @Override
   protected void onDisable() {
      com.krox.client.KroxClient.get().getClientManager().hud().unregister(this);
   }

   @Override
   public boolean isCustomRender() {
      return true;
   }

   @Override
   public String renderText() {
      return null;
   }

   @Override
   public void renderCustom(net.minecraft.client.gui.DrawContext ctx, int screenWidth, int screenHeight) {
      int r = (int)(20.0D * this.size.get());
      int cx = (int)(this.posX.get() * (screenWidth - 1));
      int cy = (int)(this.posY.get() * (screenHeight - 1));
      int c = com.krox.client.util.KroxTheme.ACCENT & 0xFFFFFF | (int)Math.round(255.0F * this.opacity.get()) << 24;
      int knob = (int)Math.max(r / 4, 1.0D);

      if (this.showBorder.get()) {
         for (int i = 0; i < 360; i += 3) {
            double a = Math.toRadians(i);
            int px = cx + (int)Math.round(Math.cos(a) * r);
            int py = cy + (int)Math.round(Math.sin(a) * r);
            ctx.fill(px, py, px + 1, py + 1, c);
         }
      }

      ctx.fill(cx - knob, cy - knob, cx + knob, cy + knob, c);
   }
}
