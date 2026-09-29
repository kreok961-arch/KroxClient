package com.krox.client.manager.module;

/**
 * ARGB colour setting. Persisted as a single packed int so the config stays flat.
 */
public final class ColorSetting extends Setting {
   private final int defaultValue;
   private int value;

   public ColorSetting(Module owner, String id, String name, int defaultValue, String description) {
      super(owner, id, name, description);
      this.defaultValue = defaultValue;
      this.value = defaultValue;
   }

   public int get() {
      return this.value;
   }

   public int getDefault() {
      return this.defaultValue;
   }

   public int getAlpha() {
      return this.value >> 24 & 0xFF;
   }

   public int getRed() {
      return this.value >> 16 & 0xFF;
   }

   public int getGreen() {
      return this.value >> 8 & 0xFF;
   }

   public int getBlue() {
      return this.value & 0xFF;
   }

   public void set(int v) {
      this.value = v;
      this.save();
      this.owner.onSettingChanged(this);
   }

   /** Applies an alpha multiplier to the current colour, clamped to 0-1. */
   public int withAlpha(float ratio) {
      int a = Math.max(0, Math.min(255, Math.round(this.getAlpha() * ratio)));
      return a << 24 | this.value & 0xFFFFFF;
   }

   public void reset() {
      this.set(this.defaultValue);
   }

   /** Swaps to a compact hex form for the settings row, e.g. "#DC2626". */
   public String toHex() {
      return String.format("#%02X%02X%02X", this.getRed(), this.getGreen(), this.getBlue());
   }

   @Override
   public void save() {
      this.cfg().set(this.cfgKey(), this.value);
   }

   @Override
   public void load() {
      this.value = this.cfg().getInt(this.cfgKey(), this.defaultValue);
   }

   @Override
   public String toString() {
      return this.toHex();
   }
}
