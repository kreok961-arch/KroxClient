package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.KeybindSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class QuickCraftingModule extends Module {
   public QuickCraftingModule() {
      super("quick_crafting", "Quick Crafting Helper", "Crafting and inventory organization tools.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new BooleanSetting(this, "sortInventory", "Sort Inventory", true, "Sort Inventory"));
      this.register(new BooleanSetting(this, "sortChests", "Sort Chests", true, "Sort Chests"));
      this.register(new BooleanSetting(this, "quickCraftAll", "Quick Craft All", false, "Quick Craft All"));
      this.register(new KeybindSetting(this, "sortKeybind", "Sort Keybind", 0, "Sort Keybind"));
   }
   // TODO(D-37): sorting the inventory or a chest writes into another
   // container's slots, which is server-authoritative. The spec's sort
   // action also carries no type, so no Setting is emitted for it.
}
