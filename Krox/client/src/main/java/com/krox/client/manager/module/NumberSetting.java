package com.krox.client.manager.module;

public final class NumberSetting extends Setting {
   private double value;
   private final double defaultValue;

   /** Not final: {@link #setValue} widens the bounds at runtime. */
   private double min;
   private double max;
   private double step;

   public NumberSetting(Module owner, String id, String name, double defaultValue, double min, double max, double step, String description) {
      super(owner, id, name, description);
      this.defaultValue = defaultValue;
      this.min = min;
      this.max = max;
      this.step = step;
      this.value = defaultValue;
   }

   public double get() {
      return this.value;
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public double getStep() {
      return this.step;
   }

   public double getDefault() {
      return this.defaultValue;
   }

   public void set(double v) {
      v = Math.max(this.min, Math.min(this.max, v));
      if (this.step > 0.0) {
         v = (double)Math.round(v / this.step) * this.step;
      }

      this.value = v;
      this.save();
      if (this.owner != null) {
         this.owner.onSettingChanged(this);
      }
   }

   /**
    * Widens the range and re-clamps. The spec revises some slider bounds after the
    * setting is built, and the GUI reads min/max/step straight off this object.
    */
   public void setValue(double v, double min, double max, double step) {
      this.min = min;
      this.max = max;
      this.step = step;
      this.set(v);
   }

   @Override
   public void save() {
      this.cfg().set(this.cfgKey(), this.value);
   }

   @Override
   public void load() {
      this.value = this.cfg().getDouble(this.cfgKey(), this.defaultValue);
   }
}
