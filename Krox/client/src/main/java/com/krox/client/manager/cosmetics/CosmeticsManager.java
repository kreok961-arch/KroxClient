package com.krox.client.manager.cosmetics;

import com.krox.client.KroxClient;
import com.krox.client.manager.cape.KroxCapeManager;
import com.krox.client.manager.skin.KroxSkinManager;

public final class CosmeticsManager {
   private boolean cosmeticsEnabled = true;

   public void initialize() {
      KroxClient.LOGGER.info("[Krox] CosmeticsManager initialized.");
   }

   public boolean isEnabled() {
      return this.cosmeticsEnabled;
   }

   public void setEnabled(boolean v) {
      this.cosmeticsEnabled = v;
      KroxClient.get().getClientManager().config().set("cosmetics.enabled", v);
      KroxClient.get().getClientManager().config().save();
   }

   public KroxSkinManager skins() {
      return KroxClient.get().getClientManager().skins();
   }

   public KroxCapeManager capes() {
      return KroxClient.get().getClientManager().capes();
   }
}
