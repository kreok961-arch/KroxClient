package com.krox.client.gui.screen;

import com.krox.client.KroxClient;
import com.krox.client.manager.cape.CapeProfile;
import com.krox.client.util.KroxTheme;
import java.util.List;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public final class KroxCapeScreen extends net.minecraft.client.gui.screen.Screen {
   private static final int PREVIEW_W = 64;
   private static final int PREVIEW_H = 32;
   private static final int GRID_PADDING = 8;
   private static final int COLS = 6;

   public KroxCapeScreen() {
      super(net.minecraft.text.Text.literal("Krox - Capes"));
   }

   public void render(net.minecraft.client.gui.DrawContext ctx, int mouseX, int mouseY, float delta) {
      this.renderBackground(ctx, mouseX, mouseY, delta);
      ctx.fill(20, 20, this.width - 20, this.height - 20, KroxTheme.BACKGROUND);
      ctx.drawText(this.textRenderer, "CAPES", 32, 32, KroxTheme.ACCENT, true);
      ctx.drawText(this.textRenderer, "Click a cape to set it active. R to reload.", 32, 44, KroxTheme.TEXT_SECONDARY, false);
      List<CapeProfile> profiles = KroxClient.get().getClientManager().capes().list();
      String activeId = KroxClient.get().getClientManager().capes().getActiveId();
      int startX = 32;
      int startY = 64;
      int i = 0;

      for (CapeProfile p : profiles) {
         int col = i % 6;
         int row = i / 6;
         int x = startX + col * 72;
         int y = startY + row * 60;
         int bg = p.getId().equals(activeId) ? KroxTheme.ACCENT : KroxTheme.ELEVATED;
         ctx.fill(x, y, x + 64, y + 32, bg);
         ctx.fill(x + 4, y + 4, x + 64 - 4, y + 32 - 4, KroxTheme.SURFACE);
         ctx.drawText(this.textRenderer, p.getDisplayName(), x, y + 32 + 2, p.getId().equals(activeId) ? KroxTheme.ACCENT : KroxTheme.TEXT_PRIMARY, false);
         i++;
      }

      super.render(ctx, mouseX, mouseY, delta);
   }

   public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubleClick) {
      double mouseX = click.x();
      double mouseY = click.y();
      List<CapeProfile> profiles = KroxClient.get().getClientManager().capes().list();
      int startX = 32;
      int startY = 64;

      for (int i = 0; i < profiles.size(); i++) {
         int col = i % 6;
         int row = i / 6;
         int x = startX + col * 72;
         int y = startY + row * 60;
         if (mouseX >= (double)x && mouseX < (double)(x + 64) && mouseY >= (double)y && mouseY < (double)(y + 32)) {
            KroxClient.get().getClientManager().capes().setActive(profiles.get(i).getId());
            return true;
         }
      }

      return super.mouseClicked(click, doubleClick);
   }

   public boolean keyPressed(net.minecraft.client.input.KeyInput keyInput) {
      int keyCode = keyInput.key();
      if (keyCode == 82) {
         KroxClient.get().getClientManager().capes().reload();
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
