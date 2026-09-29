package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class ClearGlassModule extends Module {
   public ClearGlassModule() {
      super("clear_glass", "Clear Glass / Water", "Reduces glass/water visual obstruction.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "clearGlass", "Clear Glass", false, "Clear Glass"));
      this.register(new BooleanSetting(this, "clearWater", "Clear Water", false, "Clear Water"));
      this.register(new BooleanSetting(this, "reduceFog", "Reduce Fog", false, "Reduce Fog"));
      this.register(new NumberSetting(this, "fogReduction", "Fog Reduction", 0.5, 0, 1, 0.01, "Fog Reduction"));
   }
   // TODO(D-26): making glass/water see-through is a block render-layer swap in
   // BlockRenderManager; fog reduction is a fog-distance override in
   // GameRenderer.
}
