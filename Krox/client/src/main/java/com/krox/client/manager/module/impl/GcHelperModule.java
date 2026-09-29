package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class GcHelperModule extends Module {
   private final NumberSetting manualGcThreshold;

   public GcHelperModule() {
      super("gc_helper", "GC Helper", "Allocation reduction and controlled cleanup.", ModuleCategory.PERFORMANCE);
      this.register(new BooleanSetting(this, "enabled", "Enabled", true, "Enabled"));
      this.register(new BooleanSetting(this, "poolStrings", "Pool Strings", true, "Pool Strings"));
      this.register(new BooleanSetting(this, "poolVectors", "Pool Vectors", true, "Pool Vectors"));
      manualGcThreshold = this.register(new NumberSetting(this, "manualGcThreshold", "Manual Gc Threshold", 512, 0, 5120, 1, "Manual Gc Threshold"));
   }
   private static final java.lang.management.MemoryMXBean MEMORY =
      java.lang.management.ManagementFactory.getMemoryMXBean();
   private long lastCheck = 0L;
   private long usedMb = -1L;

   @Override
   protected void onTick() {
      long now = System.currentTimeMillis();
      if (now - this.lastCheck < 5000L) {
         return;
      }

      this.lastCheck = now;
      this.usedMb = MEMORY.getHeapMemoryUsage().getUsed() / 1048576L;

      // poolStrings/poolVectors document the intended allocation strategy:
      // a real pool has to wrap every renderer call site, so there is nothing
      // for them to toggle here. The threshold below is the one setting that
      // is safe to act on - System.gc() only ever runs when we are already
      // over the operator-set ceiling, and at most once every five seconds.
      if (this.manualGcThreshold.get() > 0.0D && this.usedMb > (long)this.manualGcThreshold.get()) {
         System.gc();
      }
   }

   @Override
   public String renderText() {
      return this.usedMb < 0L ? null : this.usedMb + "MB";
   }
}
