package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.KeybindSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class PerspectiveKeyModule extends Module {
   public enum OrderMode {
      FIRST_THIRD_BACK_THIRD_FRONT_,
      FIRST_THIRD_FRONT_THIRD_BACK_
   }

   private final KeybindSetting keybind;
   private final EnumSetting<OrderMode> order;
   private final BooleanSetting skipFirst;

   public PerspectiveKeyModule() {
      super("perspective_key", "Perspective Key", "Cycle through camera perspectives.", ModuleCategory.WORLD);
      keybind = this.register(new KeybindSetting(this, "keybind", "Keybind", 294, "Keybind"));
      order = this.register(new EnumSetting<OrderMode>(this, "order", "Order", OrderMode.FIRST_THIRD_BACK_THIRD_FRONT_, "Order"));
      skipFirst = this.register(new BooleanSetting(this, "skipFirst", "Skip First", false, "Skip First"));
   }
   private boolean wasDown = false;

   @Override
   protected void onTick() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.options == null || mc.getWindow() == null || this.keybind.get() == 0) {
         return;
      }

      boolean down = net.minecraft.client.util.InputUtil.isKeyPressed(mc.getWindow(), this.keybind.get());
      if (down && !this.wasDown) {
         cycle(mc);
      }

      this.wasDown = down;
   }

   private void cycle(net.minecraft.client.MinecraftClient mc) {
      net.minecraft.client.option.Perspective[] order = this.order.get() == OrderMode.FIRST_THIRD_FRONT_THIRD_BACK_
         ? new net.minecraft.client.option.Perspective[]{net.minecraft.client.option.Perspective.FIRST_PERSON,
            net.minecraft.client.option.Perspective.THIRD_PERSON_FRONT,
            net.minecraft.client.option.Perspective.THIRD_PERSON_BACK}
         : new net.minecraft.client.option.Perspective[]{net.minecraft.client.option.Perspective.FIRST_PERSON,
            net.minecraft.client.option.Perspective.THIRD_PERSON_BACK,
            net.minecraft.client.option.Perspective.THIRD_PERSON_FRONT};

      net.minecraft.client.option.Perspective current = mc.options.getPerspective();

      for (int i = 0; i < order.length; i++) {
         if (order[i] != current) {
            continue;
         }

         int next = (i + 1) % order.length;

         if (this.skipFirst.get() && order[next] == net.minecraft.client.option.Perspective.FIRST_PERSON) {
            next = (next + 1) % order.length;
         }

         mc.options.setPerspective(order[next]);
         return;
      }

      mc.options.setPerspective(order[0]);
   }
}
