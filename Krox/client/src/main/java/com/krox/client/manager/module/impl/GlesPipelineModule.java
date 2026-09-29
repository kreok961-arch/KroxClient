package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class GlesPipelineModule extends Module {
   public enum TextureCompressionMode {
      NONE_,
      ETC2_,
      ASTC_
   }

   public GlesPipelineModule() {
      super("gles_pipeline", "ARM64 GLES 3.2 Pipeline", "Mobile-optimized rendering pipeline for ARM64 + GLES 3.2.", ModuleCategory.PERFORMANCE);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new NumberSetting(this, "batchSize", "Batch Size", 64, 1, 256, 1, "Batch Size"));
      this.register(new EnumSetting<TextureCompressionMode>(this, "textureCompression", "Texture Compression", TextureCompressionMode.NONE_, "Texture Compression"));
      this.register(new BooleanSetting(this, "useVBOs", "Use VB Os", true, "Use VB Os"));
   }
   // TODO(D-40): the GLES pipeline choice is made once when the render
   // pipeline is built at startup; batchSize/textureCompression/useVBOs
   // cannot be switched from a module tick and the pipeline set is fixed.
}
