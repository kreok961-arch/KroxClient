package com.krox.client.manager.module;

import com.krox.client.KroxClient;

public final class KeybindSetting extends Setting {
   private int keyCode;
   private final int defaultKeyCode;

   public KeybindSetting(Module owner, String id, String name, int defaultKeyCode, String description) {
      super(owner, id, name, description);
      this.defaultKeyCode = defaultKeyCode;
      this.keyCode = defaultKeyCode;
   }

   public int get() {
      return this.keyCode;
   }

   public int getDefault() {
      return this.defaultKeyCode;
   }

   public void set(int keyCode) {
      this.keyCode = keyCode;
      this.save();
      if (KroxClient.get() != null && KroxClient.get().getClientManager() != null && KroxClient.get().getClientManager().keybinds() != null) {
         KroxClient.get().getClientManager().keybinds().updateModuleBinding(this.owner, keyCode);
      }
   }

   @Override
   public void save() {
      this.cfg().set(this.cfgKey(), this.keyCode);
   }

   @Override
   public void load() {
      this.keyCode = this.cfg().getInt(this.cfgKey(), this.defaultKeyCode);
   }
}
