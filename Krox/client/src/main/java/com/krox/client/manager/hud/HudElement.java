package com.krox.client.manager.hud;

import com.krox.client.KroxClient;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.util.KroxTheme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * Base for HUD modules that share the spec's common presentation settings
 * (background / backgroundRounded / backgroundOpacity / textColor / scale) and need
 * a draggable position.
 *
 * <p>Position is normalized (0.0,0.0)-(1.0,1.0) per the spec's single coordinate fix,
 * so a widget keeps its place across window resizes and GUI scale changes.
 */
public abstract class HudElement extends Module implements HudWidget {
   protected final BooleanSetting background = this.register(new BooleanSetting(this, "background", "Background", true, "Draw a panel behind the text."));
   protected final BooleanSetting backgroundRounded = this.register(new BooleanSetting(this, "backgroundRounded", "Rounded", true, "Round the background corners."));
   protected final NumberSetting backgroundOpacity = this.register(new NumberSetting(this, "backgroundOpacity", "Opacity", 0.5, 0.0, 1.0, 0.05, "Background transparency."));
   protected final ColorSetting textColor = this.register(new ColorSetting(this, "textColor", "Text Color", KroxTheme.TEXT_PRIMARY, "Widget text colour."));
   protected final BooleanSetting textShadow = this.register(new BooleanSetting(this, "textShadow", "Text Shadow", true, "Draw a drop shadow behind the text."));
   protected final NumberSetting scale = this.register(new NumberSetting(this, "scale", "Scale", 1.0, 0.5, 2.0, 0.1, "Widget scale."));
   protected final NumberSetting posX = this.register(new NumberSetting(this, "x", "X", 0.02, 0.0, 1.0, 0.001, "Normalized horizontal position."));
   protected final NumberSetting posY = this.register(new NumberSetting(this, "y", "Y", 0.02, 0.0, 1.0, 0.001, "Normalized vertical position."));

   protected static final int LINE_HEIGHT = 9;
   protected static final int LINE_SPACING = 2;

   protected HudElement(String id, String name, String description, ModuleCategory category) {
      super(id, name, description, category);
   }

   @Override
   protected void onEnable() {
      KroxClient.get().getClientManager().hud().register(this);
   }

   @Override
   protected void onDisable() {
      KroxClient.get().getClientManager().hud().unregister(this);
   }

   protected int scaledLineHeight() {
      return (int)Math.round(LINE_HEIGHT * this.scale.get());
   }

   protected int scaledWidth(String text, TextRenderer tr) {
      return (int)Math.round(tr.getWidth(text) * this.scale.get());
   }

   protected int scaledHeight(int lines) {
      return (int)Math.round((LINE_HEIGHT * lines + LINE_SPACING * (lines - 1)) * this.scale.get());
   }

   protected int scaledX() {
      return (int)Math.round(this.posX.get() * (MinecraftClient.getInstance().getWindow().getScaledWidth() - 1));
   }

   protected int scaledY() {
      return (int)Math.round(this.posY.get() * (MinecraftClient.getInstance().getWindow().getScaledHeight() - 1));
   }

   /** The lines this widget draws, used for sizing and hit testing. */
   protected List<String> lines() {
      List<String> out = new ArrayList<>();

      if (!this.isMultiLine()) {
         String single = this.renderText();

         if (single != null && !single.isEmpty()) {
            out.add(single);
         }
      }

      out.addAll(this.renderLines());
      return out;
   }

   /** Widget bounds in screen space, used for click/drag hit testing. */
   public int hitWidth() {
      TextRenderer tr = MinecraftClient.getInstance().textRenderer;
      int w = 0;

      for (String line : this.lines()) {
         w = Math.max(w, tr.getWidth(line));
      }

      return (int)Math.round((w + 4) * this.scale.get());
   }

   public int hitHeight() {
      return this.scaledHeight(Math.max(1, this.lines().size())) + 2;
   }

   /** True when the point lands inside this widget's panel. */
   public boolean hitTest(double mouseX, double mouseY) {
      int x = this.scaledX();
      int y = this.scaledY();
      return mouseX >= x && mouseX < x + this.hitWidth() && mouseY >= y && mouseY < y + this.hitHeight();
   }

   /** Drag handler. Takes normalized coords so the widget survives a resize. */
   public void moveTo(double nx, double ny) {
      this.posX.set(nx);
      this.posY.set(ny);
   }

   /** Draws the widget at its normalized position. Called by the HUD manager. */
   public void draw(DrawContext ctx) {
      List<String> lines = this.lines();

      if (lines.isEmpty()) {
         return;
      }

      TextRenderer tr = MinecraftClient.getInstance().textRenderer;
      int x = this.scaledX();
      int y = this.scaledY();
      this.drawPanel(ctx, x, y, this.hitWidth(), this.hitHeight());

      int step = this.scaledLineHeight() + LINE_SPACING;
      int ly = y + 1;

      for (String line : lines) {
         this.drawLine(ctx, tr, line, x + 2, ly);
         ly += step;
      }
   }

   /** Paints the optional background panel. Callers pass already-scaled bounds. */
   protected void drawPanel(DrawContext ctx, int x, int y, int w, int h) {
      if (!this.background.get()) {
         return;
      }

      int color = this.textColor.withAlpha((float)this.backgroundOpacity.get());
      if (this.backgroundRounded.get()) {
         int inset = 1;
         ctx.fill(x + inset, y, x + w - inset, y + h, color);
         ctx.fill(x, y + inset, x + w, y + h - inset, color);
         ctx.fill(x + 2, y, x + w - 2, y + 1, color);
         ctx.fill(x + 2, y + h - 1, x + w - 2, y + h, color);
      } else {
         ctx.fill(x, y, x + w, y + h, color);
      }
   }

   /** Draws one line honouring textColor. */
   protected void drawLine(DrawContext ctx, TextRenderer tr, String text, int x, int y) {
      if (text == null || text.isEmpty()) {
         return;
      }

      ctx.drawText(tr, text, x, y, this.textColor.get(), this.textShadow.get());
   }
}
