package com.krox.client.manager.module;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Multi-select setting. Persisted as a comma-separated list of constant names,
 * which keeps the config flat and survives constants being reordered.
 */
public final class MultiEnumSetting<E extends Enum<E>> extends Setting {
   private final Class<E> enumClass;
   private final Set<E> values;

   public MultiEnumSetting(Module owner, String id, String name, Class<E> enumClass, E[] defaults, String description) {
      super(owner, id, name, description);
      this.enumClass = enumClass;
      this.values = EnumSet.noneOf(enumClass);

      for (E e : defaults) {
         this.values.add(e);
      }
   }

   public Set<E> get() {
      return this.values;
   }

   public E[] getValues() {
      return this.enumClass.getEnumConstants();
   }

   public boolean isSelected(E v) {
      return this.values.contains(v);
   }

   public void toggle(E v) {
      if (!this.values.remove(v)) {
         this.values.add(v);
      }

      this.save();
      if (this.owner != null) {
         this.owner.onSettingChanged(this);
      }
   }

   @Override
   public void save() {
      this.cfg().set(this.cfgKey(), this.values.stream().map(Enum::name).collect(Collectors.joining(",")));
   }

   @Override
   public void load() {
      this.values.clear();
      String raw = this.cfg().getString(this.cfgKey(), "");

      if (raw.isEmpty()) {
         return;
      }

      Arrays.stream(raw.split(",")).map(String::trim).forEach(name -> {
         for (E e : this.enumClass.getEnumConstants()) {
            if (e.name().equals(name)) {
               this.values.add(e);
            }
         }
      });
   }
}
