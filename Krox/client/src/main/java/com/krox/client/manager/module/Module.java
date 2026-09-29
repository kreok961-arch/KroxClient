package com.krox.client.manager.module;

import com.krox.client.KroxClient;
import com.krox.client.manager.hud.HudWidget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Module implements HudWidget {
   private final String id;
   private final String name;
   private final String description;
   private final ModuleCategory category;
   private boolean enabled = false;
   private final List<Setting> settings = new ArrayList<>();

   protected Module(String id, String name, String description, ModuleCategory category) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.category = category;
   }

   protected <T extends Setting> T register(T setting) {
      this.settings.add(setting);
      setting.load();
      return setting;
   }

   protected void onEnable() {
   }

   protected void onDisable() {
   }

   protected void onTick() {
   }

   public void onSettingChanged(Setting setting) {
   }

   /**
    * HudWidget's only abstract method. A plain Module is not a HUD widget by
    * itself, so the default is no text; widgets that draw override this or
    * return true from isCustomRender() and paint in renderCustom(..).
    */
   @Override
   public String renderText() {
      return null;
   }

   public final String getId() {
      return this.id;
   }

   public final String getName() {
      return this.name;
   }

   public final String getDescription() {
      return this.description;
   }

   public final ModuleCategory getCategory() {
      return this.category;
   }

   public final boolean isEnabled() {
      return this.enabled;
   }

   public final List<Setting> getSettings() {
      return Collections.unmodifiableList(this.settings);
   }

   public final void setEnabled(boolean v) {
      if (v != this.enabled) {
         this.enabled = v;

         try {
            if (this.enabled) {
               this.onEnable();
            } else {
               this.onDisable();
            }
         } catch (Throwable var3) {
            KroxClient.LOGGER.error("[Krox] Module {} threw during {}: {}", new Object[]{this.id, this.enabled ? "onEnable" : "onDisable", var3});
         }

         KroxClient.get().getClientManager().config().set("modules." + this.id + ".enabled", this.enabled);
         KroxClient.get().getClientManager().config().save();
         KroxClient.get().getClientManager().notifications().post("Krox", (this.enabled ? "Enabled " : "Disabled ") + this.name);
      }
   }

   public final void toggle() {
      this.setEnabled(!this.enabled);
   }

   public final void tickIfEnabled() {
      if (this.enabled) {
         try {
            this.onTick();
         } catch (Throwable var2) {
            KroxClient.LOGGER.error("[Krox] Module {} threw during onTick", this.id, var2);
         }
      }
   }
}
