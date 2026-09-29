package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.manager.module.StringSetting;
import com.krox.client.util.KroxTheme;

public final class PingModule extends HudElement {
   private final BooleanSetting colorThresholds;
   private final NumberSetting greenMs;
   private final StringSetting label;
   private final BooleanSetting showLabel;
   private final BooleanSetting showUnit;
   private final NumberSetting yellowMs;

   public PingModule() {
      super("ping", "Ping Display", "Real server ping in ms.", ModuleCategory.HUD);
      showLabel = this.register(new BooleanSetting(this, "showLabel", "Show Label", true, "Show Label"));
      label = this.register(new StringSetting(this, "label", "Label", "Ping: ", "Label"));
      showUnit = this.register(new BooleanSetting(this, "showUnit", "Show Unit", true, "Show Unit"));
      colorThresholds = this.register(new BooleanSetting(this, "colorThresholds", "Color Thresholds", true, "green <50, yellow <150, red >150"));
      greenMs = this.register(new NumberSetting(this, "greenMs", "Green Ms", 50, 0, 500, 1, "Green Ms"));
      yellowMs = this.register(new NumberSetting(this, "yellowMs", "Yellow Ms", 150, 0, 1500, 1, "Yellow Ms"));
   }
   @Override
   public String renderText() {
      net.minecraft.client.network.ClientPlayerEntity p = net.minecraft.client.MinecraftClient.getInstance().player;
      net.minecraft.client.network.PlayerListEntry entry = p == null || p.networkHandler == null ? null : p.networkHandler.getPlayerListEntry(p.getUuid());
      if (entry == null) {
         return (this.showLabel.get() ? this.label.get() : "") + "--";
      }

      int ms = entry.getLatency();
      if (this.colorThresholds.get()) {
         this.textColor.set(ms > this.yellowMs.get() ? com.krox.client.util.KroxTheme.DANGER : ms > this.greenMs.get() ? com.krox.client.util.KroxTheme.WARNING : com.krox.client.util.KroxTheme.SUCCESS);
      }

      return (this.showLabel.get() ? this.label.get() : "") + ms + (this.showUnit.get() ? "ms" : "");
   }
}
