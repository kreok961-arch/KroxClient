package com.krox.client.gui.screen;

import com.krox.client.KroxClient;
import com.krox.client.util.KroxTheme;
import com.krox.client.util.ModPresence;
import java.util.List;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public final class KroxSettingsScreen extends net.minecraft.client.gui.screen.Screen {
   private static final int ROW_HEIGHT = 18;
   private static final List<String> CATEGORIES = List.of("GENERAL", "APPEARANCE", "MODULES", "HUD", "COSMETICS", "PERFORMANCE", "ABOUT");
   private int selectedCategory = 0;

   public KroxSettingsScreen() {
      super(net.minecraft.text.Text.literal("Krox - Settings"));
   }

   public void render(net.minecraft.client.gui.DrawContext ctx, int mouseX, int mouseY, float delta) {
      this.renderBackground(ctx, mouseX, mouseY, delta);
      ctx.fill(20, 20, this.width - 20, this.height - 20, KroxTheme.BACKGROUND);
      ctx.drawText(this.textRenderer, "KROX SETTINGS", 32, 32, KroxTheme.ACCENT, true);
      int catX = 32;
      int catY = 64;
      int catW = 120;
      int panelX = catX + catW + 12;
      ctx.fill(catX - 2, catY - 2, catX + catW, catY + CATEGORIES.size() * 18 + 2, KroxTheme.SURFACE);
      int rowY = catY;

      for (int i = 0; i < CATEGORIES.size(); i++) {
         boolean sel = i == this.selectedCategory;
         if (sel) {
            ctx.fill(catX - 2, rowY, catX + catW, rowY + 18, KroxTheme.ACCENT);
         }

         ctx.drawText(this.textRenderer, CATEGORIES.get(i), catX + 6, rowY + 5, sel ? -1 : KroxTheme.TEXT_PRIMARY, false);
         rowY += 18;
      }

      int panelW = this.width - panelX - 40;
      ctx.fill(panelX - 2, catY - 2, panelX + panelW, catY + CATEGORIES.size() * 18 + 2, KroxTheme.ELEVATED);
      String var14 = CATEGORIES.get(this.selectedCategory);
      switch (var14) {
         case "GENERAL":
            this.drawGeneral(ctx, panelX, catY, panelW);
            break;
         case "APPEARANCE":
            this.drawAppearance(ctx, panelX, catY, panelW);
            break;
         case "HUD":
            this.drawHud(ctx, panelX, catY, panelW);
            break;
         case "COSMETICS":
            this.drawCosmetics(ctx, panelX, catY, panelW);
            break;
         case "PERFORMANCE":
            this.drawPerformance(ctx, panelX, catY, panelW);
            break;
         case "ABOUT":
            this.drawAbout(ctx, panelX, catY, panelW);
            break;
         case "MODULES":
            this.drawModulesHint(ctx, panelX, catY, panelW);
      }

      super.render(ctx, mouseX, mouseY, delta);
   }

   private void drawGeneral(net.minecraft.client.gui.DrawContext ctx, int x, int y, int w) {
      ctx.drawText(this.textRenderer, "Open Krox GUI: RIGHT SHIFT", x + 6, y + 6, KroxTheme.TEXT_PRIMARY, false);
      ctx.drawText(this.textRenderer, "Press RIGHT SHIFT in-game to open the Krox Click GUI.", x + 6, y + 22, KroxTheme.TEXT_SECONDARY, false);
   }

   private void drawAppearance(net.minecraft.client.gui.DrawContext ctx, int x, int y, int w) {
      ctx.drawText(this.textRenderer, "Background:  #08080A", x + 6, y + 6, KroxTheme.BACKGROUND, false);
      ctx.fill(x + 6 + 100, y + 6, x + 6 + 110, y + 18, KroxTheme.BACKGROUND);
      ctx.drawText(this.textRenderer, "Surface:     #121216", x + 6, y + 22, KroxTheme.SURFACE, false);
      ctx.fill(x + 6 + 100, y + 22, x + 6 + 110, y + 34, KroxTheme.SURFACE);
      ctx.drawText(this.textRenderer, "Elevated:    #24242C", x + 6, y + 38, KroxTheme.ELEVATED, false);
      ctx.fill(x + 6 + 100, y + 38, x + 6 + 110, y + 50, KroxTheme.ELEVATED);
      ctx.drawText(this.textRenderer, "Accent:      #DC2626", x + 6, y + 54, KroxTheme.ACCENT, false);
      ctx.fill(x + 6 + 100, y + 54, x + 6 + 110, y + 66, KroxTheme.ACCENT);
   }

   private void drawHud(net.minecraft.client.gui.DrawContext ctx, int x, int y, int w) {
      ctx.drawText(this.textRenderer, "HUD widgets are toggled from the Click GUI.", x + 6, y + 6, KroxTheme.TEXT_PRIMARY, false);
      ctx.drawText(this.textRenderer, "Categories: HUD.", x + 6, y + 22, KroxTheme.TEXT_SECONDARY, false);
   }

   private void drawCosmetics(net.minecraft.client.gui.DrawContext ctx, int x, int y, int w) {
      ctx.drawText(this.textRenderer, "Manage skins and capes from the corresponding screens.", x + 6, y + 6, KroxTheme.TEXT_PRIMARY, false);
   }

   private void drawPerformance(net.minecraft.client.gui.DrawContext ctx, int x, int y, int w) {
      ctx.drawText(this.textRenderer, "Low CPU HUD rendering: on", x + 6, y + 6, KroxTheme.SUCCESS, false);
      ctx.drawText(this.textRenderer, "Animations: lightweight", x + 6, y + 22, KroxTheme.TEXT_PRIMARY, false);
      ctx.drawText(this.textRenderer, "Texture cache: 64 slots", x + 6, y + 38, KroxTheme.TEXT_PRIMARY, false);
   }

   private void drawAbout(net.minecraft.client.gui.DrawContext ctx, int x, int y, int w) {
      ctx.drawText(this.textRenderer, "Krox Client v" + KroxClient.version(), x + 6, y + 6, KroxTheme.ACCENT, true);
      ctx.drawText(this.textRenderer, "Minecraft 1.21.11 / Fabric Loader 0.19.5", x + 6, y + 22, KroxTheme.TEXT_PRIMARY, false);
      ctx.drawText(this.textRenderer, "Fabric API 0.141.6+1.21.11 / Yarn 1.21.11+build.6", x + 6, y + 34, KroxTheme.TEXT_PRIMARY, false);
      ctx.drawText(this.textRenderer, "Built from scratch - no third-party client dependency.", x + 6, y + 50, KroxTheme.TEXT_SECONDARY, false);
      ctx.drawText(this.textRenderer, "Companion mods (install into your mods folder, not bundled):", x + 6, y + 70, KroxTheme.TEXT_SECONDARY, false);
      int cY = y + 82;

      for (String id : List.of("axolotlclient", "flashback", "optixclient")) {
         boolean present = ModPresence.isLoaded(id);
         ctx.drawText(this.textRenderer, id + ": " + (present ? "detected" : "not installed"), x + 6, cY, present ? KroxTheme.SUCCESS : KroxTheme.TEXT_MUTED, false);
         cY += 12;
      }
   }

   private void drawModulesHint(net.minecraft.client.gui.DrawContext ctx, int x, int y, int w) {
      ctx.drawText(this.textRenderer, "Use the Click GUI (RIGHT SHIFT) to toggle modules.", x + 6, y + 6, KroxTheme.TEXT_PRIMARY, false);
   }

   public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubleClick) {
      double mouseX = click.x();
      double mouseY = click.y();
      int catX = 32;
      int catY = 64;
      int catW = 120;
      int rowY = catY;

      for (int i = 0; i < CATEGORIES.size(); i++) {
         if (mouseX >= (double)(catX - 2) && mouseX < (double)(catX + catW) && mouseY >= (double)rowY && mouseY < (double)(rowY + 18)) {
            this.selectedCategory = i;
            return true;
         }

         rowY += 18;
      }

      return super.mouseClicked(click, doubleClick);
   }

   public boolean keyPressed(net.minecraft.client.input.KeyInput keyInput) {
      if (keyInput.key() == 256) {
         this.close();
         return true;
      } else {
         return super.keyPressed(keyInput);
      }
   }

   public boolean shouldPause() {
      return false;
   }
}
