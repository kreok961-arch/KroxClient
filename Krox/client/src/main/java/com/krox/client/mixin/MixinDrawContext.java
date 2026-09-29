package com.krox.client.mixin;

import com.krox.client.KroxClient;
import com.krox.client.util.BlurService;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * D-10: {@code GuiRenderState.applyBlur} throws "Can only blur once per frame" on the
 * second call. Vanilla's Screen, Krox, and the HUD all reach {@code DrawContext.applyBlur}
 * in one frame, so the gate sits on the method they share rather than on any one caller.
 */
@Mixin(DrawContext.class)
public abstract class MixinDrawContext {
   @Inject(
      method = "applyBlur",
      at = {@At("HEAD")},
      cancellable = true
   )
   private void krox_blurOncePerFrame(CallbackInfo ci) {
      try {
         if (!BlurService.claim((DrawContext)(Object)this)) {
            ci.cancel();
         }
      } catch (Throwable var1) {
         KroxClient.LOGGER.error("[Krox] Blur gate threw - vanilla blur continues.", var1);
      }
   }
}
