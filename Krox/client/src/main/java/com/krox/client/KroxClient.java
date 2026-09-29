package com.krox.client;

import com.krox.client.manager.ClientManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class KroxClient implements ClientModInitializer {
   public static final String MOD_ID = "krox";
   public static final String MOD_NAME = "Krox Client";
   public static final String MOD_VERSION = "1.0.0";
   public static final Logger LOGGER = LoggerFactory.getLogger("Krox Client");
   private static KroxClient instance;
   private final ClientManager clientManager = ClientManager.get();

   public static KroxClient get() {
      return instance;
   }

   public void onInitializeClient() {
      instance = this;
      LOGGER.info("[Krox] Booting Krox Client {} for Minecraft 1.21.11", "1.0.0");

      try {
         this.clientManager.initialize();
         LOGGER.info("[Krox] Krox Client initialized successfully.");
      } catch (Throwable var2) {
         LOGGER.error("[Krox] Critical initialization failure - running in safe fallback mode.", var2);
      }

      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         try {
            ClientManager.get().modules().tickAll();
            ClientManager.get().keybinds().onTick();
         } catch (Throwable var2x) {
            LOGGER.error("[Krox] Client tick handler threw.", var2x);
         }
      });
      HudRenderCallback.EVENT.register((HudRenderCallback)(ctx, tickCounter) -> {
         try {
            ClientManager.get().hud().onHudRender(ctx, tickCounter);
         } catch (Throwable var3) {
            LOGGER.error("[Krox] HUD render handler threw.", var3);
         }
      });
   }

   public ClientManager getClientManager() {
      return this.clientManager;
   }
}
