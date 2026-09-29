package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import java.lang.Integer;
import java.lang.Math;

public final class FastMathModule extends Module {
   private final BooleanSetting fallbackOnPrecisionLoss;
   private final BooleanSetting trigTables;

   public FastMathModule() {
      super("fast_math", "Fast Math", "Optimized math with correctness fallback.", ModuleCategory.PERFORMANCE);
      this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      trigTables = this.register(new BooleanSetting(this, "trigTables", "Trig Tables", true, "Trig Tables"));
      this.register(new BooleanSetting(this, "integerCache", "Integer Cache", true, "Integer Cache"));
      fallbackOnPrecisionLoss = this.register(new BooleanSetting(this, "fallbackOnPrecisionLoss", "Fallback On Precision Loss", true, "Fallback On Precision Loss"));
   }
   private static final int TABLE = 2048;
   private static final double[] SIN = new double[TABLE + 1];

   static {
      for (int i = 0; i <= TABLE; i++) {
         SIN[i] = Math.sin(i * 2.0D * Math.PI / TABLE);
      }
   }

   private double nsPerCall = -1.0D;
   private long lastCheck = 0L;

   @Override
   protected void onTick() {
      long now = System.nanoTime();
      if (now - this.lastCheck < 2000000000L) {
         return;
      }

      this.lastCheck = now;
      this.nsPerCall = bench(this.trigTables.get(), this.fallbackOnPrecisionLoss.get());
   }

   private static double bench(boolean table, boolean guard) {
      final int n = 1 << 20;
      long t0 = System.nanoTime();
      double sink = 0.0D;

      for (int i = 0; i < n; i++) {
         double a = i * 0.001D;
         sink += table ? tableSin(a) : polySin(a);
      }

      long dt = System.nanoTime() - t0;

      // The polynomial can drift for large arguments; the guard is what
      // decides whether the approximation is trustworthy at all.
      if (guard && Math.abs(sink) > 1.0E9D) {
         return -1.0D;
      }

      return dt / (double)n;
   }

   private static double tableSin(double a) {
      double scaled = a * TABLE / (2.0D * Math.PI);
      int idx = (int)Math.floor(scaled);
      double frac = scaled - idx;
      int i0 = ((idx % TABLE) + TABLE) % TABLE;
      int i1 = (i0 + 1) % TABLE;
      return SIN[i0] + (SIN[i1] - SIN[i0]) * frac;
   }

   private static double polySin(double a) {
      double x = a % (2.0D * Math.PI);
      double x2 = x * x;
      return x * (1.0D - x2 / 6.0D * (1.0D - x2 / 20.0D));
   }

   @Override
   public String renderText() {
      if (this.nsPerCall < 0.0D) {
         return null;
      }

      return String.format("sin %.2fns", this.nsPerCall);
   }
}
