package com.krox.client.manager.module;

public final class BooleanSetting extends Setting {
   private boolean value;
   private final boolean defaultValue;

   public BooleanSetting(Module owner, String id, String name, boolean defaultValue, String description) {
      super(owner, id, name, description);
      this.defaultValue = defaultValue;
      this.value = defaultValue;
   }

   public boolean get() {
      return this.value;
   }

   public void set(boolean v) {
      this.value = v;
      this.save();
      if (this.owner != null) {
         this.owner.onSettingChanged(this);
      }
   }

   @Override
   public void save() {
      this.cfg().set(this.cfgKey(), this.value);
   }

   @Override
   public void load() {
      this.value = this.cfg().getBool(this.cfgKey(), this.defaultValue);
   }
}
