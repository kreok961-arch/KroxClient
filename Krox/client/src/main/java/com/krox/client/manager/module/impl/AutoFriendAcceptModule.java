package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.StringSetting;

public final class AutoFriendAcceptModule extends Module {
   public AutoFriendAcceptModule() {
      super("auto_friend_accept", "Auto Friend Accept", "Auto-accept friend requests from whitelist.", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new StringSetting(this, "whitelist", "Whitelist", "", "Whitelist"));
      this.register(new StringSetting(this, "blacklist", "Blacklist", "", "Blacklist"));
      this.register(new BooleanSetting(this, "requireConfirmation", "Require Confirmation", true, "Require Confirmation"));
      this.register(new BooleanSetting(this, "notifyOnAccept", "Notify On Accept", true, "Notify On Accept"));
   }
   // TODO(D-34): a friend request is a server-side message this client never
   // receives, so there is nothing to accept or filter from here.
}
