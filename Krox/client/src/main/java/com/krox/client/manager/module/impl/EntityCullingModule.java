package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class EntityCullingModule extends Module {
   public EntityCullingModule() {
      super("entity_culling", "Entity Culling", "Frustum/occlusion/distance culling for entities.", ModuleCategory.PERFORMANCE);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new BooleanSetting(this, "frustumCull", "Frustum Cull", true, "Frustum Cull"));
      this.register(new BooleanSetting(this, "occlusionCull", "Occlusion Cull", true, "Occlusion Cull"));
      this.register(new BooleanSetting(this, "distanceCull", "Distance Cull", true, "Distance Cull"));
      this.register(new NumberSetting(this, "cullDistance", "Cull Distance", 64, 16, 128, 1, "Cull Distance"));
      this.register(new BooleanSetting(this, "applyToItems", "Apply To Items", true, "Apply To Items"));
      this.register(new BooleanSetting(this, "applyToMobs", "Apply To Mobs", true, "Apply To Mobs"));
   }
   // TODO(D-41): culling happens inside WorldRenderer.tickEntities and the
   // build-time culling frustum, not in a module tick.
}
