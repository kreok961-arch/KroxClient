package com.krox.client.manager.ui;

import com.krox.client.KroxClient;
import com.krox.client.gui.screen.KroxCapeScreen;
import com.krox.client.gui.screen.KroxClickGuiScreen;
import com.krox.client.gui.screen.KroxSettingsScreen;
import com.krox.client.gui.screen.KroxSkinScreen;
import net.minecraft.client.MinecraftClient;

public final class GuiManager {
   public void initialize() {
      KroxClient.LOGGER.info("[Krox] GuiManager initialized.");
   }

   public void openClickGui() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc != null) {
         mc.setScreen(new KroxClickGuiScreen());
      }
   }

   public void openSkins() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc != null) {
         try {
            mc.setScreen(new KroxSkinScreen());
         } catch (Throwable var3) {
            KroxClient.LOGGER.error("[Krox] Failed to open skin screen.", var3);
         }
      }
   }

   public void openCapes() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc != null) {
         try {
            mc.setScreen(new KroxCapeScreen());
         } catch (Throwable var3) {
            KroxClient.LOGGER.error("[Krox] Failed to open cape screen.", var3);
         }
      }
   }

   public void openSettings() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc != null) {
         try {
            mc.setScreen(new KroxSettingsScreen());
         } catch (Throwable var3) {
            KroxClient.LOGGER.error("[Krox] Failed to open settings screen.", var3);
         }
      }
   }
}
