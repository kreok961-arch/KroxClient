package com.krox.client.manager.hud;

import java.util.List;
import net.minecraft.client.gui.DrawContext;

public interface HudWidget {
   String renderText();

   default boolean isMultiLine() {
      return false;
   }

   default List<String> renderLines() {
      return List.of();
   }

   default boolean isCustomRender() {
      return false;
   }

   default void renderCustom(net.minecraft.client.gui.DrawContext ctx, int screenWidth, int screenHeight) {
   }
}
