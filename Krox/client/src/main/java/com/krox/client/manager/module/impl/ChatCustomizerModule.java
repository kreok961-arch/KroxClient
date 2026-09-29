package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import com.krox.client.manager.module.StringSetting;

public final class ChatCustomizerModule extends Module {
   public ChatCustomizerModule() {
      super("chat_customizer", "Chat Customizer", "Chat appearance and behavior customization.", ModuleCategory.WORLD);
      this.register(new NumberSetting(this, "scale", "Scale", 1, 0.5, 2, 0.1, "Scale"));
      this.register(new NumberSetting(this, "opacity", "Opacity", 1, 0, 1, 0.01, "Opacity"));
      this.register(new NumberSetting(this, "width", "Width", 320, 200, 800, 1, "Width"));
      this.register(new NumberSetting(this, "height", "Height", 180, 100, 500, 1, "Height"));
      this.register(new BooleanSetting(this, "showTimestamps", "Show Timestamps", false, "Show Timestamps"));
      this.register(new StringSetting(this, "timestampFormat", "Timestamp Format", "HH:mm", "Timestamp Format"));
      this.register(new BooleanSetting(this, "antiSpam", "Anti Spam", false, "Anti Spam"));
      this.register(new NumberSetting(this, "antiSpamThreshold", "Anti Spam Threshold", 3, 0, 30, 1, "Anti Spam Threshold"));
      this.register(new BooleanSetting(this, "infiniteScroll", "Infinite Scroll", true, "Infinite Scroll"));
      this.register(new BooleanSetting(this, "chatAnimation", "Chat Animation", true, "Chat Animation"));
   }
   // TODO(D-32): chat geometry/timestamps need a mixin into ChatHud and
   // ChatScreen; Module cannot resize or re-render the chat.
}
