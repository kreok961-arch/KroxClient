package com.krox.client.mixin;

import com.krox.client.KroxClient;
import com.krox.client.util.BlurService;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Krox's single blur call site.
 *
 * <p>{@code Screen.renderBackground} runs once per screen per frame from
 * {@code Screen.renderWithTooltip}, so a tail inject puts Krox's blur on the layer holding
 * the background it is blurring -- the same slot vanilla's own blur takes. Both hooks are
 * needed because that slot is one mark per frame: the head inject hands the frame to Krox,
 * and the tail inject spends it. When Krox does not want blur, both stay out of the way and
 * vanilla blurs as before.
 */
@Mixin(Screen.class)
public abstract class MixinScreenBlur {
   @Inject(
      method = "applyBlur",
      at = {@At("HEAD")},
      cancellable = true
   )
   private void krox_deferToPanelBlur(DrawContext ctx, CallbackInfo ci) {
      try {
         if (BlurService.wants((Screen)(Object)this) > 0) {
            ci.cancel();
         }
      } catch (Throwable var1) {
         KroxClient.LOGGER.error("[Krox] Blur hand-off threw - vanilla blur continues.", var1);
      }
   }

   @Inject(
      method = "renderBackground",
      at = {@At("TAIL")}
   )
   private void krox_applyPanelBlur(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      try {
         BlurService.apply(ctx, (Screen)(Object)this);
      } catch (Throwable var2) {
         KroxClient.LOGGER.error("[Krox] Panel blur threw - vanilla background kept.", var2);
      }
   }
}
