package com.krox.client.manager.skin;

import com.krox.client.KroxClient;
import java.util.List;

public final class KroxSkinManager {
   private final SkinStorage storage = new SkinStorage();
   private String activeId = null;

   public void initialize() {
      this.storage.loadAll();
      this.activeId = KroxClient.get().getClientManager().config().getString("skin.active", null);
      if (this.activeId != null && this.storage.byId(this.activeId) == null) {
         KroxClient.LOGGER.warn("[Krox] Saved active skin '{}' not found - disabling.", this.activeId);
         this.activeId = null;
      }

      KroxClient.LOGGER.info("[Krox] KroxSkinManager initialized (active = {}).", this.activeId == null ? "vanilla" : this.activeId);
   }

   public void reload() {
      this.storage.loadAll();
      if (this.activeId != null && this.storage.byId(this.activeId) == null) {
         this.activeId = null;
      }

      KroxClient.get().getClientManager().notifications().post("Krox", "Reloaded " + this.storage.list().size() + " skins.");
   }

   public List<SkinProfile> list() {
      return this.storage.list();
   }

   public SkinProfile getActive() {
      return this.activeId == null ? null : this.storage.byId(this.activeId);
   }

   public String getActiveId() {
      return this.activeId;
   }

   public void setActive(String id) {
      if (id == null) {
         this.activeId = null;
      } else {
         SkinProfile p = this.storage.byId(id);
         if (p == null) {
            KroxClient.get().getClientManager().notifications().post("Krox", "Unknown skin - falling back to vanilla.");
            this.activeId = null;
         } else {
            this.activeId = id;
            KroxClient.get().getClientManager().notifications().post("Krox", "Skin set: " + p.getDisplayName());
         }
      }

      KroxClient.get().getClientManager().config().set("skin.active", this.activeId == null ? "" : this.activeId);
      KroxClient.get().getClientManager().config().save();
   }

   public boolean isEnabled() {
      return this.activeId != null;
   }
}
