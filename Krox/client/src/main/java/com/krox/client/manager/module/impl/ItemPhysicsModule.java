package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class ItemPhysicsModule extends Module {
   public ItemPhysicsModule() {
      super("item_physics", "3D Item Physics", "Physics-based rotation of dropped items.", ModuleCategory.RENDER);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new NumberSetting(this, "rotationSpeed", "Rotation Speed", 0.5, 0, 2, 0.1, "Rotation Speed"));
      this.register(new BooleanSetting(this, "tumble", "Tumble", true, "Tumble"));
      this.register(new BooleanSetting(this, "groundAlign", "Ground Align", true, "Ground Align"));
      this.register(new BooleanSetting(this, "bob", "Bob", true, "Bob"));
   }
   // TODO(D-22): 3D drop transforms are applied in the item-entity renderer;
   // Module can neither move nor rotate another entity's render transform.
}
