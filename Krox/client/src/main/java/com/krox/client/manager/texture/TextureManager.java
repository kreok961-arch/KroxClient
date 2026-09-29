package com.krox.client.manager.texture;

import com.krox.client.KroxClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;

public final class TextureManager {
   private final Map<String, net.minecraft.util.Identifier> cache = new HashMap<>();
   private final Map<String, net.minecraft.client.texture.NativeImageBackedTexture> backing = new HashMap<>();
   private static final int MAX_CACHE = 64;

   public void initialize() {
      KroxClient.LOGGER.info("[Krox] TextureManager initialized (cache cap = {})", 64);
   }

   public net.minecraft.util.Identifier loadTexture(String namespace, String path, Path file) {
      String key = namespace + ":" + path;
      synchronized (this.cache) {
         net.minecraft.util.Identifier cached = this.cache.get(key);
         if (cached != null) {
            return cached;
         } else {
            net.minecraft.client.texture.NativeImage image;
            try {
               image = net.minecraft.client.texture.NativeImage.read(Files.newInputStream(file));
            } catch (IOException var12) {
               KroxClient.LOGGER.error("[Krox] Texture load failed for {}: {}", key, var12.getMessage());
               return null;
            }

            if (image.getWidth() > 0 && image.getHeight() > 0 && image.getWidth() <= 8192 && image.getHeight() <= 8192) {
               while (this.cache.size() >= 64) {
                  String oldest = this.cache.keySet().stream().findFirst().orElse(null);
                  if (oldest == null) {
                     break;
                  }

                  net.minecraft.util.Identifier id = this.cache.remove(oldest);
                  net.minecraft.client.texture.NativeImageBackedTexture back = this.backing.remove(oldest);
                  if (back != null) {
                     back.close();
                  }

                  if (id != null) {
                     net.minecraft.client.MinecraftClient.getInstance().getTextureManager().destroyTexture(id);
                  }
               }

               net.minecraft.util.Identifier idx = net.minecraft.util.Identifier.of(namespace, path);
               net.minecraft.client.texture.NativeImageBackedTexture dynamic = new net.minecraft.client.texture.NativeImageBackedTexture(() -> key, image);
               net.minecraft.client.MinecraftClient.getInstance().getTextureManager().registerTexture(idx, dynamic);
               this.cache.put(key, idx);
               this.backing.put(key, dynamic);
               return idx;
            } else {
               KroxClient.LOGGER
                  .error("[Krox] Texture {} has invalid dimensions {}x{} - rejected.", new Object[]{key, image.getWidth(), image.getHeight()});
               image.close();
               return null;
            }
         }
      }
   }

   public void release(String namespace, String path) {
      String key = namespace + ":" + path;
      synchronized (this.cache) {
         net.minecraft.util.Identifier id = this.cache.remove(key);
         net.minecraft.client.texture.NativeImageBackedTexture back = this.backing.remove(key);
         if (back != null) {
            back.close();
         }

         if (id != null) {
            net.minecraft.client.MinecraftClient.getInstance().getTextureManager().destroyTexture(id);
         }
      }
   }

   public void releaseAll() {
      synchronized (this.cache) {
         for (String key : new ArrayList<>(this.cache.keySet())) {
            net.minecraft.util.Identifier id = this.cache.remove(key);
            net.minecraft.client.texture.NativeImageBackedTexture back = this.backing.remove(key);
            if (back != null) {
               back.close();
            }

            if (id != null) {
               net.minecraft.client.MinecraftClient.getInstance().getTextureManager().destroyTexture(id);
            }
         }
      }
   }
}
