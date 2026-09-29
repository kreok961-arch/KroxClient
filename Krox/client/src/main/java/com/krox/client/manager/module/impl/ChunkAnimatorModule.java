package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class ChunkAnimatorModule extends Module {
   public enum AnimationTypeMode {
      FADE_,
      SCALE_,
      SLIDE_
   }

   public enum DirectionMode {
      Y_AXIS_,
      FROM_CENTER_
   }

   public ChunkAnimatorModule() {
      super("chunk_animator", "Chunk Animator", "Animated chunk fade-in when loading.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new EnumSetting<AnimationTypeMode>(this, "animationType", "Animation Type", AnimationTypeMode.FADE_, "Animation Type"));
      this.register(new NumberSetting(this, "durationMs", "Duration Ms", 400, 100, 1000, 1, "Duration Ms"));
      this.register(new EnumSetting<DirectionMode>(this, "direction", "Direction", DirectionMode.Y_AXIS_, "Direction"));
   }
   // TODO(D-27): chunk enter/exit animations hook the chunk render section
   // build, which has no Module-facing entry point.
}
