package com.krox.client.gui.screen;

import com.krox.client.KroxClient;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ColorSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.KeybindSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.MultiEnumSetting;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.manager.module.Setting;
import com.krox.client.manager.module.StringSetting;
import com.krox.client.util.KroxTheme;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.Click;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.Screen;

public final class KroxClickGuiScreen extends net.minecraft.client.gui.screen.Screen {
   private static final int PADDING = 6;
   private static final int CATEGORY_BAR_WIDTH = 90;
   private static final int MODULE_LIST_WIDTH = 200;
   private static final int SETTINGS_WIDTH = 240;
   private static final int ROW_HEIGHT = 18;
   private static final int HEADER_HEIGHT = 28;
   private ModuleCategory selectedCategory = null;
   private Module selectedModule = null;
   private boolean settingsExpanded = false;
   private String searchQuery = "";
   private String editingKeybindModuleId = null;
   private StringSetting editingString = null;
   // ponytail: one shared cursor for every multi-select row; a per-setting map
   // only matters once a screen shows two multi-selects at once.
   private int multiCursor = 0;

   public KroxClickGuiScreen() {
      super(net.minecraft.text.Text.literal("Krox Client"));
   }

   protected void init() {
      List<ModuleCategory> cats = KroxClient.get().getClientManager().modules().activeCategories();
      if (!cats.isEmpty() && this.selectedCategory == null) {
         this.selectedCategory = cats.get(0);
      }
   }

   public void render(net.minecraft.client.gui.DrawContext ctx, int mouseX, int mouseY, float delta) {
      ctx.fill(0, 0, this.width, this.height, -1879048192);
      int totalWidth = 290 + (this.settingsExpanded ? 240 : 0);
      int totalHeight = 196;
      int baseX = (this.width - totalWidth) / 2;
      int baseY = (this.height - totalHeight) / 2;
      ctx.fill(baseX, baseY, baseX + totalWidth, baseY + totalHeight, KroxTheme.BACKGROUND);
      ctx.fill(baseX, baseY, baseX + totalWidth, baseY + 1, KroxTheme.ACCENT);
      ctx.fill(baseX, baseY + totalHeight - 1, baseX + totalWidth, baseY + totalHeight, KroxTheme.BORDER);
      ctx.drawText(this.textRenderer, "KROX CLIENT", baseX + 6, baseY + 8, KroxTheme.ACCENT, true);
      ctx.drawText(this.textRenderer, "v" + KroxClient.version(), baseX + 6 + 110, baseY + 8, KroxTheme.TEXT_MUTED, true);
      ctx.drawText(this.textRenderer, "ESC to close", baseX + totalWidth - 80, baseY + 8, KroxTheme.TEXT_SECONDARY, true);
      int catX = baseX + 6;
      int catY = baseY + 28;
      int listX = catX + 90 + 6;
      ctx.fill(catX - 2, catY - 2, catX + 90 - 2, catY + 108 + 2, KroxTheme.SURFACE);
      int catRowY = catY;

      for (ModuleCategory cat : KroxClient.get().getClientManager().modules().activeCategories()) {
         boolean selected = cat == this.selectedCategory;
         int bg = selected ? KroxTheme.ACCENT : (this.mouseIn(mouseX, mouseY, catX - 2, catRowY, 90, 18) ? KroxTheme.ELEVATED : 0);
         if (bg != 0) {
            ctx.fill(catX - 2, catRowY, catX + 90 - 2, catRowY + 18, bg);
         }

         ctx.drawText(this.textRenderer, cat.getDisplayName(), catX + 4, catRowY + 5, selected ? -1 : KroxTheme.TEXT_PRIMARY, false);
         catRowY += 18;
      }

      List<Module> modules = this.selectedCategory == null ? List.of() : KroxClient.get().getClientManager().modules().byCategory(this.selectedCategory);
      if (!this.searchQuery.isEmpty()) {
         String q = this.searchQuery.toLowerCase();
         List<Module> var32 = new ArrayList<>(modules);
         modules = var32.stream().filter(m -> m.getName().toLowerCase().contains(q)).toList();
      }

      int rowY = catY;

      for (Module m : modules) {
         boolean sel = m == this.selectedModule;
         int bg = sel ? KroxTheme.ELEVATED : (this.mouseIn(mouseX, mouseY, listX, rowY, 200, 18) ? KroxTheme.SURFACE : 0);
         if (bg != 0) {
            ctx.fill(listX, rowY, listX + 200, rowY + 18, bg);
         }

         int toggleX = listX + 200 - 22;
         int toggleY = rowY + 4;
         int toggleColor = m.isEnabled() ? KroxTheme.ACCENT : KroxTheme.BORDER;
         ctx.fill(toggleX, toggleY, toggleX + 16, toggleY + 10, toggleColor);
         int knobX = m.isEnabled() ? toggleX + 8 : toggleX + 1;
         ctx.fill(knobX, toggleY + 1, knobX + 7, toggleY + 9, -1);
         String label = m.getName();
         ctx.drawText(this.textRenderer, label, listX + 4, rowY + 5, m.isEnabled() ? KroxTheme.ACCENT_HOVER : KroxTheme.TEXT_PRIMARY, false);
         rowY += 18;
      }

      if (this.settingsExpanded && this.selectedModule != null) {
         int setX = listX + 200 + 6;
         ctx.fill(setX - 2, catY - 2, setX + 240 - 2, catY + 108 + 2, KroxTheme.SURFACE);
         ctx.drawText(this.textRenderer, this.selectedModule.getName().toUpperCase(), setX + 4, catY + 4, KroxTheme.ACCENT, false);
         ctx.drawText(this.textRenderer, this.selectedModule.getDescription(), setX + 4, catY + 16, KroxTheme.TEXT_SECONDARY, false);
         int sy = catY + 30;

         for (Setting s : this.selectedModule.getSettings()) {
            ctx.drawText(this.textRenderer, s.getName(), setX + 6, sy + 4, KroxTheme.TEXT_PRIMARY, false);
            int widgetX = setX + 240 - 90;
            int widgetY = sy + 2;
            int widgetW = 80;
            int widgetH = 12;
            if (s instanceof BooleanSetting bs) {
               int bg2 = bs.get() ? KroxTheme.ACCENT : KroxTheme.BORDER;
               ctx.fill(widgetX, widgetY, widgetX + widgetW, widgetY + widgetH, bg2);
               int kx = bs.get() ? widgetX + widgetW - 7 : widgetX + 1;
               ctx.fill(kx, widgetY + 1, kx + 6, widgetY + widgetH - 1, -1);
            } else if (s instanceof NumberSetting ns) {
               double frac = (ns.get() - ns.getMin()) / Math.max(1.0E-9, ns.getMax() - ns.getMin());
               int fillW = (int)((double)widgetW * frac);
               ctx.fill(widgetX, widgetY, widgetX + widgetW, widgetY + widgetH, KroxTheme.ELEVATED);
               ctx.fill(widgetX, widgetY, widgetX + fillW, widgetY + widgetH, KroxTheme.ACCENT);
               ctx.drawText(this.textRenderer, String.format("%.1f", ns.get()), widgetX + widgetW + 4, widgetY + 1, KroxTheme.TEXT_SECONDARY, false);
            } else if (s instanceof EnumSetting<?> es) {
               ctx.fill(widgetX, widgetY, widgetX + widgetW, widgetY + widgetH, KroxTheme.ELEVATED);
               ctx.fill(widgetX, widgetY, widgetX + 2, widgetY + widgetH, KroxTheme.ACCENT);
               ctx.drawText(this.textRenderer, es.get().name(), widgetX + 6, widgetY + 1, KroxTheme.TEXT_PRIMARY, false);
            } else if (s instanceof KeybindSetting ks) {
               ctx.fill(widgetX, widgetY, widgetX + widgetW, widgetY + widgetH, KroxTheme.ELEVATED);
               boolean editing = this.editingKeybindModuleId != null && this.editingKeybindModuleId.equals(this.selectedModule.getId());
               String txt = editing ? "..." : net.minecraft.client.util.InputUtil.fromKeyCode(new net.minecraft.client.input.KeyInput(ks.get(), 0, 0)).getLocalizedText().getString();
               ctx.drawText(this.textRenderer, txt, widgetX + 6, widgetY + 1, KroxTheme.TEXT_PRIMARY, false);
            } else if (s instanceof ColorSetting cs) {
               ctx.fill(widgetX, widgetY, widgetX + widgetW, widgetY + widgetH, KroxTheme.ELEVATED);
               ctx.fill(widgetX + 2, widgetY + 2, widgetX + 12, widgetY + widgetH - 2, cs.get() | 0xFF000000);
               ctx.drawText(this.textRenderer, cs.toHex(), widgetX + 16, widgetY + 1, KroxTheme.TEXT_PRIMARY, false);
            } else if (s instanceof StringSetting ss) {
               ctx.fill(widgetX, widgetY, widgetX + widgetW, widgetY + widgetH, KroxTheme.ELEVATED);
               String txt = ss.get() + (ss == this.editingString ? "_" : "");
               // Long prefixes overflow the 80px column, so trim from the left and
               // keep the caret end visible.
               if (this.textRenderer.getWidth(txt) > widgetW - 8) {
                  txt = this.textRenderer.trimToWidth(txt, widgetW - 8);
               }

               ctx.drawText(this.textRenderer, txt, widgetX + 4, widgetY + 1, KroxTheme.TEXT_PRIMARY, false);
            } else if (s instanceof MultiEnumSetting<?> mes) {
               ctx.fill(widgetX, widgetY, widgetX + widgetW, widgetY + widgetH, KroxTheme.ELEVATED);
               ctx.fill(widgetX, widgetY, widgetX + 2, widgetY + widgetH, KroxTheme.ACCENT);
               String txt = this.multiLabel(mes);
               if (this.textRenderer.getWidth(txt) > widgetW - 8) {
                  txt = this.textRenderer.trimToWidth(txt, widgetW - 8);
               }

               ctx.drawText(this.textRenderer, txt, widgetX + 6, widgetY + 1, KroxTheme.TEXT_PRIMARY, false);
            }

            sy += 18;
         }
      }

      int searchY = baseY + totalHeight - 24;
      ctx.fill(baseX + 6, searchY, baseX + totalWidth - 6, searchY + 16, KroxTheme.SURFACE);
      ctx.drawText(this.textRenderer, "Search: " + this.searchQuery + "_", baseX + 6 + 4, searchY + 4, KroxTheme.TEXT_PRIMARY, false);
   }

   private boolean mouseIn(int mx, int my, int x, int y, int w, int h) {
      return mx >= x && mx < x + w && my >= y && my < y + h;
   }

   public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubleClick) {
      int mx = (int)click.x();
      int my = (int)click.y();
      int totalWidth = 290 + (this.settingsExpanded ? SETTINGS_WIDTH : 0);
      int totalHeight = 196;
      int baseX = (this.width - totalWidth) / 2;
      int baseY = (this.height - totalHeight) / 2;
      int catX = baseX + PADDING;
      int catY = baseY + HEADER_HEIGHT;
      int listX = catX + CATEGORY_BAR_WIDTH + PADDING;

      int catRowY = catY;

      for (ModuleCategory cat : KroxClient.get().getClientManager().modules().activeCategories()) {
         if (this.mouseIn(mx, my, catX - PADDING, catRowY, CATEGORY_BAR_WIDTH, ROW_HEIGHT)) {
            this.selectedCategory = cat;
            return true;
         }

         catRowY += ROW_HEIGHT;
      }

      List<Module> modules = this.selectedCategory == null
         ? List.of()
         : KroxClient.get().getClientManager().modules().byCategory(this.selectedCategory);
      int rowY = catY;

      for (Module m : modules) {
         if (this.mouseIn(mx, my, listX, rowY, MODULE_LIST_WIDTH, ROW_HEIGHT)) {
            int toggleX = listX + MODULE_LIST_WIDTH - 22;
            int toggleY = rowY + 4;

            if (this.mouseIn(mx, my, toggleX, toggleY, 16, 10)) {
               m.toggle();
            } else {
               this.selectedModule = m;
               this.settingsExpanded = true;
            }

            return true;
         }

         rowY += ROW_HEIGHT;
      }

      if (this.settingsExpanded && this.selectedModule != null) {
         int setX = listX + MODULE_LIST_WIDTH + PADDING;
         int sy = catY + 30;

         for (Setting s : this.selectedModule.getSettings()) {
            int widgetX = setX + SETTINGS_WIDTH - 90;
            int widgetY = sy + 2;
            int widgetW = 80;
            int widgetH = 12;

            if (this.mouseIn(mx, my, widgetX, widgetY, widgetW, widgetH)) {
               if (s instanceof BooleanSetting bs) {
                  bs.set(!bs.get());
               } else if (s instanceof NumberSetting ns) {
                  double frac = (double)(mx - widgetX) / (double)widgetW;
                  ns.set(ns.getMin() + frac * (ns.getMax() - ns.getMin()));
               } else if (s instanceof EnumSetting<?> es) {
                  this.cycleEnum(es);
               } else if (s instanceof KeybindSetting) {
                  this.editingKeybindModuleId = this.selectedModule.getId();
               } else if (s instanceof ColorSetting cs) {
                  this.cycleColor(cs);
               } else if (s instanceof StringSetting ss) {
                  this.editingString = ss == this.editingString ? null : ss;
               } else if (s instanceof MultiEnumSetting<?> mes) {
                  this.toggleMulti(mes);
               }

               return true;
            }

            sy += ROW_HEIGHT;
         }
      }

      int searchY = baseY + totalHeight - 24;

      if (this.mouseIn(mx, my, baseX + PADDING, searchY, totalWidth - PADDING * 2, 16)) {
         return true;
      }

      return super.mouseClicked(click, doubleClick);
   }

   // The original bytecode made the erased EnumSetting.set(Enum) call straight from
   // getValues(), which Java's generic capture check rejects. Cycle through a raw
   // handle so the widening stays explicit rather than smuggled through a wildcard.
   @SuppressWarnings({"rawtypes", "unchecked"})
   private void cycleEnum(EnumSetting es) {
      Enum[] values = es.getValues();
      int idx = 0;

      for (int i = 0; i < values.length; ++i) {
         if (values[i].equals(es.get())) {
            idx = i;
            break;
         }
      }

      es.set(values[(idx + 1) % values.length]);
   }

   // A full picker is a separate screen; a click walks r -> g -> b -> r so the
   // row stays editable inside the 240px column.
   private void cycleColor(ColorSetting cs) {
      int c = cs.get() | 0xFF000000;
      int[] channels = {c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF};
      int idx = 0;

      for (int i = 0; i < 3; ++i) {
         if (channels[i] != 0) {
            idx = i;
            break;
         }
      }

      channels[idx] = channels[idx] == 255 ? 0 : 255;
      cs.set(0xFF000000 | channels[0] << 16 | channels[1] << 8 | channels[2]);
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   private void toggleMulti(MultiEnumSetting mes) {
      Enum[] values = mes.getValues();
      if (values.length == 0) {
         return;
      }

      mes.toggle(values[this.multiCursor % values.length]);
      this.multiCursor = (this.multiCursor + 1) % values.length;
   }

   private String multiLabel(MultiEnumSetting<?> mes) {
      Set<?> sel = mes.get();
      if (sel.isEmpty()) {
         return "none";
      }

      int total = mes.getValues().length;
      if (sel.size() == total) {
         return "all";
      }

      StringBuilder sb = new StringBuilder();
      for (Object o : sel) {
         if (sb.length() > 0) {
            sb.append(',');
         }

         sb.append(((Enum<?>)o).name());
      }

      return sb.toString();
   }

   public boolean keyPressed(net.minecraft.client.input.KeyInput keyInput) {
      int keyCode = keyInput.key();
      if (this.editingString != null) {
         // A string field swallows every key except Escape, which ends the edit
         // instead of the screen.
         if (keyCode == 256) {
            this.editingString = null;
         }

         return true;
      }

      if (this.editingKeybindModuleId != null && this.selectedModule != null) {
         for (Setting s : this.selectedModule.getSettings()) {
            if (s instanceof KeybindSetting ks) {
               ks.set(keyCode);
               this.editingKeybindModuleId = null;
               return true;
            }
         }

         this.editingKeybindModuleId = null;
         return true;
      } else if (keyCode == 256) {
         this.close();
         return true;
      } else {
         return super.keyPressed(keyInput);
      }
   }

   public boolean charTyped(net.minecraft.client.input.CharInput charInput) {
      String s = charInput.asString();
      if (s == null || s.isEmpty()) {
         return super.charTyped(charInput);
      }

      char chr = s.charAt(0);
      if (chr == 8 || chr == 127) {
         if (this.editingString != null) {
            String cur = this.editingString.get();
            this.editingString.set(cur.isEmpty() ? "" : cur.substring(0, cur.length() - 1));
         } else {
            this.searchQuery = this.searchQuery.substring(0, Math.max(0, this.searchQuery.length() - 1));
         }

         return true;
      }

      if (this.editingString != null) {
         // Field text keeps the user's casing; the search box is lowercased so
         // matching stays case-insensitive.
         this.editingString.set(this.editingString.get() + chr);
         return true;
      }

      if (Character.isLetterOrDigit(chr)) {
         this.searchQuery = this.searchQuery + Character.toLowerCase(chr);
         return true;
      }

      return super.charTyped(charInput);
   }

   public boolean shouldPause() {
      return false;
   }
}
