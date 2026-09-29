package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.EnumSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;

public final class MicrosoftOauthModule extends Module {
   public enum DefaultMethodMode {
      OAUTH_,
      OFFLINE_
   }

   public MicrosoftOauthModule() {
      super("microsoft_oauth", "Microsoft OAuth2", "Official supported authentication flow.", ModuleCategory.ACCOUNT);
      this.register(new EnumSetting<DefaultMethodMode>(this, "defaultMethod", "Default Method", DefaultMethodMode.OAUTH_, "Default Method"));
      this.register(new BooleanSetting(this, "storeRefreshToken", "Store Refresh Token", true, "Store Refresh Token"));
      this.register(new BooleanSetting(this, "autoRefresh", "Auto Refresh", true, "Auto Refresh"));
   }
   // TODO(D-47): performing the OAuth flow means a network call, a system
   // browser launch and token storage - all forbidden until the owner
   // signs off. Deliberately inert: this module must not touch
   // credentials or the network.
}
