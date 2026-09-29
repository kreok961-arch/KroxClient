package com.krox.client.manager.skin;

import com.krox.client.KroxClient;
import net.minecraft.util.Identifier;

public final class SkinTextureService {
   private SkinTextureService() {
   }

   public static net.minecraft.util.Identifier getActiveTextureId() {
      KroxSkinManager mgr = KroxClient.get().getClientManager().skins();
      SkinProfile p = mgr.getActive();
      if (p == null) {
         return null;
      } else {
         String ns = "krox";
         String path = "skins/" + p.getId();
         return KroxClient.get().getClientManager().textures().loadTexture(ns, path, p.getPath());
      }
   }
}
