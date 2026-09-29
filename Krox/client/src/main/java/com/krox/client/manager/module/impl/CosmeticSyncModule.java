package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class CosmeticSyncModule extends Module {
   public CosmeticSyncModule() {
      super("cosmetic_sync", "Cosmetic Sync", "Persist cosmetic state per KROX profile.", ModuleCategory.ACCOUNT);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new BooleanSetting(this, "syncOnProfileSwitch", "Sync On Profile Switch", true, "Sync On Profile Switch"));
      this.register(new BooleanSetting(this, "notifyOnSync", "Notify On Sync", false, "Notify On Sync"));
   }
   // TODO(D-50): syncing cosmetics is a profile-level network action.
   // CosmeticsManager only mirrors the local, already-loaded sets, so there
   // is nothing to sync to on enable.
}
