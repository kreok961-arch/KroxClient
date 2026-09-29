package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.StringSetting;

public final class OfflineAccountModule extends Module {
   public OfflineAccountModule() {
      super("offline_account", "Offline / Local Account Switcher", "Fast local profile switching.", ModuleCategory.ACCOUNT);
      this.register(new BooleanSetting(this, "showOfflineOption", "Show Offline Option", true, "Show Offline Option"));
      this.register(new StringSetting(this, "defaultUsername", "Default Username", "", "Default Username"));
      this.register(new BooleanSetting(this, "allowDuplicate", "Allow Duplicate", false, "Allow Duplicate"));
   }
   // TODO(D-48): the offline name field lives on the account screen; Module
   // has no screen hook, and the session itself is owned by the vanilla
   // launcher, not the client.
}
