package com.krox.client.manager.module;

/**
 * Free-text setting (HUD prefixes, chat macros, filter strings).
 */
public final class StringSetting extends Setting {
   private String value;
   private final String defaultValue;

   public StringSetting(Module owner, String id, String name, String defaultValue, String description) {
      super(owner, id, name, description);
      this.defaultValue = defaultValue;
      this.value = defaultValue;
   }

   public String get() {
      return this.value;
   }

   public String getDefault() {
      return this.defaultValue;
   }

   public void set(String v) {
      this.value = v == null ? "" : v;
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
      this.value = this.cfg().getString(this.cfgKey(), this.defaultValue);
   }
}
