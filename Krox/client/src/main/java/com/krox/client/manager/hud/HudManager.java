package com.krox.client.manager.hud;

import com.krox.client.KroxClient;
import com.krox.client.manager.notification.NotificationManager;
import com.krox.client.util.KroxTheme;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class HudManager {
   private final List<HudWidget> widgets = new CopyOnWriteArrayList<>();
   private HudElement dragging;
   private double dragOffsetX;
   private double dragOffsetY;

   public void initialize() {
      KroxClient.LOGGER.info("[Krox] HudManager initialized.");
   }

   public void register(HudWidget w) {
      if (!this.widgets.contains(w)) {
         this.widgets.add(w);
      }
   }

   public void unregister(HudWidget w) {
      this.widgets.remove(w);
   }

   public void onHudRender(net.minecraft.client.gui.DrawContext ctx, net.minecraft.client.render.RenderTickCounter tickCounter) {
      KroxClient.get().getClientManager().render().tick();
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      net.minecraft.client.font.TextRenderer tr = mc.textRenderer;
      int x = 4;
      int y = 4;

      for (HudWidget w : this.widgets) {
         if (w.isCustomRender()) {
            w.renderCustom(ctx, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
         } else if (w instanceof HudElement e) {
            e.draw(ctx);
         } else if (!w.isMultiLine()) {
            String text = w.renderText();
            if (text != null && !text.isEmpty()) {
               int w_ = tr.getWidth(text);
               ctx.fill(x - 1, y - 1, x + w_ + 2, y + 9 + 1, KroxTheme.SURFACE);
               ctx.drawText(tr, text, x + 1, y, KroxTheme.TEXT_PRIMARY, true);
               y += 9 + 2;
            }
         }
      }

      int sw = mc.getWindow().getScaledWidth();
      int ry = 4;

      for (HudWidget wx : this.widgets) {
         if (wx.isMultiLine()) {
            List<String> lines = wx.renderLines();
            if (lines != null && !lines.isEmpty()) {
               for (String line : lines) {
                  int lw = tr.getWidth(line);
                  int lx = sw - lw - 8;
                  ctx.fill(lx - 2, ry - 1, lx + lw + 2, ry + 9 + 1, KroxTheme.SURFACE);
                  ctx.drawText(tr, line, lx, ry, KroxTheme.ACCENT, true);
                  ry += 9 + 2;
               }
            }
         }
      }

      List<NotificationManager.Notification> notifs = KroxClient.get().getClientManager().notifications().snapshot();
      int ny = mc.getWindow().getScaledHeight() - 4;

      for (int i = notifs.size() - 1; i >= 0; i--) {
         NotificationManager.Notification n = notifs.get(i);
         String title = n.title;
         String body = n.message;
         int tw = tr.getWidth(title);
         int bw = tr.getWidth(body);
         int wxx = Math.max(tw, bw) + 8;
         int h = 9 * 2 + 6;
         ny -= h + 2;
         int nx = sw - wxx - 4;
         ctx.fill(nx, ny, nx + wxx, ny + h, KroxTheme.ELEVATED);
         ctx.fill(nx, ny, nx + 2, ny + h, KroxTheme.ACCENT);
         ctx.drawText(tr, title, nx + 6, ny + 3, KroxTheme.ACCENT_HOVER, true);
         ctx.drawText(tr, body, nx + 6, ny + 3 + 9 + 2, KroxTheme.TEXT_PRIMARY, true);
      }
   }

   /**
    * Starts a drag when the click lands on a positioned widget's panel. Returns true if
    * the click was consumed, so it does not also reach the game.
    */
   public boolean onMouseClick(double mouseX, double mouseY) {
      for (HudWidget w : this.widgets) {
         if (w instanceof HudElement e && e.hitTest(mouseX, mouseY)) {
            this.dragging = e;
            this.dragOffsetX = mouseX - e.scaledX();
            this.dragOffsetY = mouseY - e.scaledY();
            return true;
         }
      }

      return false;
   }

   public void onMouseRelease() {
      this.dragging = null;
   }

   public void onMouseDrag(double mouseX, double mouseY) {
      if (this.dragging == null) {
         return;
      }

      int sw = net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledWidth();
      int sh = net.minecraft.client.MinecraftClient.getInstance().getWindow().getScaledHeight();
      this.dragging.moveTo((mouseX - this.dragOffsetX) / (double)Math.max(1, sw - 1), (mouseY - this.dragOffsetY) / (double)Math.max(1, sh - 1));
   }
}
