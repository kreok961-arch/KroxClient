package com.krox.client.gui.screen;

import com.krox.client.KroxClient;
import com.krox.client.util.KroxTheme;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;

public final class KroxTitleScreen extends net.minecraft.client.gui.screen.Screen {
   public KroxTitleScreen() {
      super(net.minecraft.text.Text.literal("Krox Client - Main Menu"));
   }

   protected void init() {
      int btnW = 220;
      int btnH = 18;
      int x = (this.width - btnW) / 2;
      int y = this.height / 3 + 10;
      this.addDrawableChild(
         net.minecraft.client.gui.widget.ButtonWidget.builder(net.minecraft.text.Text.literal("Singleplayer"), b -> this.client.setScreen(new net.minecraft.client.gui.screen.world.SelectWorldScreen(this)))
            .dimensions(x, y, btnW, btnH)
            .build()
      );
      y += btnH + 4;
      this.addDrawableChild(
         net.minecraft.client.gui.widget.ButtonWidget.builder(net.minecraft.text.Text.literal("Multiplayer"), b -> this.client.setScreen(new net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen(this)))
            .dimensions(x, y, btnW, btnH)
            .build()
      );
      y += btnH + 4;
      this.addDrawableChild(
         net.minecraft.client.gui.widget.ButtonWidget.builder(net.minecraft.text.Text.literal("Options"), b -> this.client.setScreen(new net.minecraft.client.gui.screen.option.OptionsScreen(this, this.client.options)))
            .dimensions(x, y, btnW, btnH)
            .build()
      );
      y += btnH + 4;
      this.addDrawableChild(
         net.minecraft.client.gui.widget.ButtonWidget.builder(net.minecraft.text.Text.literal("Krox Client"), b -> KroxClient.get().getClientManager().gui().openClickGui())
            .dimensions(x, y, btnW, btnH)
            .build()
      );
      y += btnH + 4;
      this.addDrawableChild(
         net.minecraft.client.gui.widget.ButtonWidget.builder(net.minecraft.text.Text.literal("Cosmetics"), b -> KroxClient.get().getClientManager().gui().openSkins())
            .dimensions(x, y, btnW, btnH)
            .build()
      );
      y += btnH + 4;
      this.addDrawableChild(
         net.minecraft.client.gui.widget.ButtonWidget.builder(net.minecraft.text.Text.literal("Exit Game"), b -> this.client.scheduleStop()).dimensions(x, y, btnW, btnH).build()
      );
   }

   public void render(net.minecraft.client.gui.DrawContext ctx, int mouseX, int mouseY, float delta) {
      ctx.fill(0, 0, this.width, this.height, KroxTheme.BACKGROUND);
      int cx = this.width / 2;
      ctx.drawText(this.textRenderer, "KROX", cx - 20, 30, KroxTheme.ACCENT, true);
      ctx.drawText(this.textRenderer, "v1.0.0 - Minecraft 1.21.11", cx - 80, 44, KroxTheme.TEXT_MUTED, false);
      ctx.fill(cx - 50, 56, cx + 50, 58, KroxTheme.ACCENT);
      super.render(ctx, mouseX, mouseY, delta);
      ctx.drawText(this.textRenderer, "Built from scratch - Fabric Loader 0.19.5", 8, this.height - 14, KroxTheme.TEXT_MUTED, false);
   }

   public boolean shouldPause() {
      return false;
   }
}
