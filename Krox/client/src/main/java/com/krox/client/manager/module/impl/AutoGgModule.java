package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.manager.module.StringSetting;

public final class AutoGgModule extends Module {
   public AutoGgModule() {
      super("auto_gg", "Auto GG", "Send configurable \"gg\" message after matches (manual trigger).", ModuleCategory.WORLD);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      this.register(new StringSetting(this, "message", "Message", "gg", "Message"));
      this.register(new NumberSetting(this, "delayMs", "Delay Ms", 1000, 0, 5000, 1, "Delay Ms"));
      this.register(new NumberSetting(this, "cooldownMs", "Cooldown Ms", 10000, 0, 60000, 1, "Cooldown Ms"));
      this.register(new BooleanSetting(this, "onlyOnHypixel", "Only On Hypixel", true, "Only On Hypixel"));
   }
   // TODO(D-39): a 'gg' is sent in response to a round result, which the
   // client only learns from chat. ClientPlayNetworkHandler keeps no public
   // accessor for the last chat packet, so there is no trigger to hook.
   // Needs a mixin on ClientPlayNetworkHandler.onChatMessage(..). The send
   // itself is a plain client-side UI action and stays allowed.
}
