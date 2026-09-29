package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class TextureCacheModule extends Module {
   public enum EvictionPolicyMode {
      LRU_,
      LFU_,
      FIFO_
   }

   public TextureCacheModule() {
      super("texture_cache", "Texture Streaming Cache", "Memory management and eviction for textures.", ModuleCategory.PERFORMANCE);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new NumberSetting(this, "maxCacheMB", "Max Cache MB", 512, 128, 4096, 1, "Max Cache MB"));
      this.register(new EnumSetting<EvictionPolicyMode>(this, "evictionPolicy", "Eviction Policy", EvictionPolicyMode.LRU_, "Eviction Policy"));
      this.register(new BooleanSetting(this, "preloadAdjacent", "Preload Adjacent", true, "Preload Adjacent"));
   }
   // TODO(D-43): a real LRU/LFU/FIFO cache has to own
   // NativeImageBackedTexture lifetimes inside the texture manager; a module
   // tick runs after uploads and cannot evict live GL textures.
}
