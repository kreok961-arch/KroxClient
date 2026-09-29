package com.krox.client.mixin;

import com.krox.client.KroxClient;
import com.krox.client.gui.screen.KroxTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({net.minecraft.client.gui.screen.TitleScreen.class})
public abstract class MixinTitleScreen {
   private static boolean krox_swapped = false;

   @Inject(
      method = {"init"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void krox_swapToKroxMenu(CallbackInfo ci) {
      if (!krox_swapped) {
         krox_swapped = true;

         try {
            net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
            mc.setScreen(new KroxTitleScreen());
         } catch (Throwable var3) {
            KroxClient.LOGGER.error("[Krox] Failed to swap title screen to Krox menu - vanilla fallback.", var3);
         }

         ci.cancel();
      }
   }
}
