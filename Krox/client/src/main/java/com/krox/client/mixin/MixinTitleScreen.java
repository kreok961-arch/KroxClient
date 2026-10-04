package com.krox.client.mixin;

import com.krox.client.KroxClient;
import com.krox.client.util.KroxTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pillar 1: the vanilla TitleScreen is kept. Branding is painted over it at TAIL only,
 * so every vanilla button -- Realms included -- and the splash text stay live and clickable.
 * The previous version swapped the screen from an init HEAD inject with ci.cancel(),
 * which is exactly what the pillar forbids.
 *
 * <p>Bottom-left corner on purpose: the logo and buttons own the centre, the splash text
 * owns the bottom right.
 */
@Mixin({net.minecraft.client.gui.screen.TitleScreen.class})
public abstract class MixinTitleScreen {
   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void krox_branding(DrawContext ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      try {
         if (KroxClient.get() == null) {
            return;
         }

         MinecraftClient mc = (MinecraftClient)(Object)this;
         int x = 8;
         int y = mc.getWindow().getScaledHeight() - 26;

         // Emblem.
         ctx.fill(x, y, x + 12, y + 12, KroxTheme.ACCENT);
         ctx.fill(x + 3, y + 3, x + 9, y + 9, KroxTheme.BACKGROUND);
         // Panel, wordmark, build tag.
         ctx.fill(x + 14, y, x + 130, y + 12, 0x66000000);
         ctx.drawText(mc.textRenderer, "KROX", x + 18, y + 2, KroxTheme.ACCENT, true);
         ctx.drawText(mc.textRenderer, "v" + KroxClient.version(), x + 56, y + 2, KroxTheme.TEXT_MUTED, false);
      } catch (Throwable var2) {
         KroxClient.LOGGER.error("[Krox] Title screen branding threw - vanilla screen untouched.", var2);
      }
   }
}
