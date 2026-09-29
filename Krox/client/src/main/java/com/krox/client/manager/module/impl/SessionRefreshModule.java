package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class SessionRefreshModule extends Module {
   public SessionRefreshModule() {
      super("session_refresh", "Session Token Refresh", "Secure legitimate session refreshing.", ModuleCategory.ACCOUNT);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new NumberSetting(this, "refreshBeforeExpiry", "Refresh Before Expiry", 5, 0, 50, 1, "Refresh Before Expiry"));
      this.register(new BooleanSetting(this, "notifyOnRefresh", "Notify On Refresh", false, "Notify On Refresh"));
   }
   // TODO(D-47): refreshing a session needs the auth token and a network
   // call, both out of bounds for a module. Deliberately inert: this must
   // never read or log a token.
}
