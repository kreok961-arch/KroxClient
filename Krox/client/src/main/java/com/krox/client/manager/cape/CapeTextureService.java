package com.krox.client.manager.cape;

import com.krox.client.KroxClient;
import net.minecraft.util.Identifier;

public final class CapeTextureService {
   private CapeTextureService() {
   }

   public static net.minecraft.util.Identifier getActiveTextureId() {
      KroxCapeManager mgr = KroxClient.get().getClientManager().capes();
      CapeProfile p = mgr.getActive();
      if (p == null) {
         return null;
      } else {
         String ns = "krox";
         String path = "capes/" + p.getId();
         return KroxClient.get().getClientManager().textures().loadTexture(ns, path, p.getPath());
      }
   }
}
