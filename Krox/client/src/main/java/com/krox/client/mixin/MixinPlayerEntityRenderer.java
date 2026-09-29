package com.krox.client.mixin;

import com.krox.client.KroxClient;
import com.krox.client.manager.cape.CapeTextureService;
import com.krox.client.manager.cosmetics.CosmeticsManager;
import com.krox.client.manager.skin.SkinTextureService;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo.TextureAssetInfo;
import net.minecraft.util.AssetInfo.TextureAsset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({net.minecraft.client.render.entity.PlayerEntityRenderer.class})
public abstract class MixinPlayerEntityRenderer {
   @Inject(
      method = {"updateRenderState"},
      at = {@At("TAIL")}
   )
   private void krox_swapSkinTextures(net.minecraft.entity.PlayerLikeEntity entity, net.minecraft.client.render.entity.state.PlayerEntityRenderState state, float tickDelta, CallbackInfo ci) {
      try {
         net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
         if (mc == null || mc.player == null) {
            return;
         }

         if (entity != mc.player) {
            return;
         }

         CosmeticsManager cosmetics = KroxClient.get().getClientManager().cosmetics();
         if (!cosmetics.isEnabled()) {
            return;
         }

         net.minecraft.entity.player.SkinTextures original = state.skinTextures;
         if (original == null) {
            return;
         }

         TextureAsset body = original.body();
         TextureAsset cape = original.cape();
         TextureAsset elytra = original.elytra();
         net.minecraft.entity.player.PlayerSkinType model = original.model();
         boolean secure = original.secure();
         net.minecraft.util.Identifier kroxBody = SkinTextureService.getActiveTextureId();
         if (kroxBody != null) {
            body = new TextureAssetInfo(kroxBody);
         }

         net.minecraft.util.Identifier kroxCape = CapeTextureService.getActiveTextureId();
         if (kroxCape != null) {
            cape = new TextureAssetInfo(kroxCape);
            elytra = new TextureAssetInfo(kroxCape);
            state.capeVisible = true;
         }

         state.skinTextures = new net.minecraft.entity.player.SkinTextures(body, cape, elytra, model, secure);
      } catch (Throwable var15) {
         KroxClient.LOGGER.error("[Krox] PlayerEntityRenderer swap failed - falling back to vanilla.", var15);
      }
   }
}
