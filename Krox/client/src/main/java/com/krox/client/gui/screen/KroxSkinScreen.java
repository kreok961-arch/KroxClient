package com.krox.client.gui.screen;

import com.krox.client.KroxClient;
import com.krox.client.manager.skin.SkinProfile;
import com.krox.client.util.KroxTheme;
import java.util.List;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public final class KroxSkinScreen extends net.minecraft.client.gui.screen.Screen {
   private static final int PREVIEW_SIZE = 64;
   private static final int GRID_PADDING = 8;
   private static final int COLS = 6;

   public KroxSkinScreen() {
      super(net.minecraft.text.Text.literal("Krox - Skins"));
   }

   public void render(net.minecraft.client.gui.DrawContext ctx, int mouseX, int mouseY, float delta) {
      this.renderBackground(ctx, mouseX, mouseY, delta);
      ctx.fill(20, 20, this.width - 20, this.height - 20, KroxTheme.BACKGROUND);
      ctx.drawText(this.textRenderer, "SKINS", 32, 32, KroxTheme.ACCENT, true);
      ctx.drawText(this.textRenderer, "Click a skin to set it active. R to reload.", 32, 44, KroxTheme.TEXT_SECONDARY, false);
      List<SkinProfile> profiles = KroxClient.get().getClientManager().skins().list();
      String activeId = KroxClient.get().getClientManager().skins().getActiveId();
      int startX = 32;
      int startY = 64;
      int i = 0;

      for (SkinProfile p : profiles) {
         int col = i % 6;
         int row = i / 6;
         int x = startX + col * 72;
         int y = startY + row * 92;
         int bg = p.getId().equals(activeId) ? KroxTheme.ACCENT : KroxTheme.ELEVATED;
         ctx.fill(x, y, x + 64, y + 64, bg);
         ctx.fill(x + 8, y + 8, x + 64 - 8, y + 64 - 8, KroxTheme.SURFACE);
         ctx.drawText(this.textRenderer, p.getDisplayName(), x, y + 64 + 2, p.getId().equals(activeId) ? KroxTheme.ACCENT : KroxTheme.TEXT_PRIMARY, false);
         i++;
      }

      super.render(ctx, mouseX, mouseY, delta);
   }

   public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubleClick) {
      double mouseX = click.x();
      double mouseY = click.y();
      List<SkinProfile> profiles = KroxClient.get().getClientManager().skins().list();
      int startX = 32;
      int startY = 64;

      for (int i = 0; i < profiles.size(); i++) {
         int col = i % 6;
         int row = i / 6;
         int x = startX + col * 72;
         int y = startY + row * 92;
         if (mouseX >= (double)x && mouseX < (double)(x + 64) && mouseY >= (double)y && mouseY < (double)(y + 64)) {
            KroxClient.get().getClientManager().skins().setActive(profiles.get(i).getId());
            return true;
         }
      }

      return super.mouseClicked(click, doubleClick);
   }

   public boolean keyPressed(net.minecraft.client.input.KeyInput keyInput) {
      int keyCode = keyInput.key();
      if (keyCode == 82) {
         KroxClient.get().getClientManager().skins().reload();
         return true;
      } else if (keyCode == 256) {
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
