package com.krox.client.manager.module;

public final class EnumSetting<T extends Enum<T>> extends Setting {
   private T value;
   private final T defaultValue;
   private final T[] values;

   public EnumSetting(Module owner, String id, String name, T defaultValue, String description) {
      super(owner, id, name, description);
      this.defaultValue = defaultValue;
      this.values = defaultValue.getDeclaringClass().getEnumConstants();
      this.value = defaultValue;
   }

   public T get() {
      return this.value;
   }

   public T[] getValues() {
      return this.values;
   }

   public T getDefault() {
      return this.defaultValue;
   }

   public void set(T v) {
      this.value = v;
      this.save();
      if (this.owner != null) {
         this.owner.onSettingChanged(this);
      }
   }

   @Override
   public void save() {
      this.cfg().set(this.cfgKey(), this.value.name());
   }

   @Override
   public void load() {
      String s = this.cfg().getString(this.cfgKey(), this.defaultValue.name());

      for (T t : this.values) {
         if (t.name().equals(s)) {
            this.value = t;
            return;
         }
      }

      this.value = this.defaultValue;
   }
}
