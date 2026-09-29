package com.krox.client.util;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Presence check for the companion mods named in fabric.mod.json's "suggests".
 *
 * <p>None of them exposes an API a third mod can call, so Krox cannot link them --
 * they are installed into gameDir/mods alongside it. This only answers "is it
 * there", which is what the settings screen needs to tell the user.
 */
public final class ModPresence {
   private ModPresence() {
   }

   public static boolean isLoaded(String modId) {
      return FabricLoader.getInstance().getModContainer(modId).isPresent();
   }
}
