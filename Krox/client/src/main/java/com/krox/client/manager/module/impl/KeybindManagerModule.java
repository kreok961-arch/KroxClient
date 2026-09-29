package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class KeybindManagerModule extends Module {
   public KeybindManagerModule() {
      super("keybind_manager", "Keybind Manager", "Central keybind remapping and conflict detection.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "showConflicts", "Show Conflicts", true, "Show Conflicts"));
      this.register(new BooleanSetting(this, "searchable", "Searchable", true, "Searchable"));
      this.register(new BooleanSetting(this, "groupByCategory", "Group By Category", true, "Group By Category"));
      this.register(new BooleanSetting(this, "resetAll", "Reset All", false, "Reset All"));
   }
   // TODO(D-31): the keybind screen is a GuiScreen of its own and Module has
   // no screen hook, so searchable/groupByCategory/resetAll have no UI to act
   // on. resetAll is emitted as a plain BooleanSetting because the spec
   // gives it no type.
}
