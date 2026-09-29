package com.krox.client.manager.module.impl;

import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.Module;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;

public final class CpsLimiterModule extends Module {
   private static final long WINDOW_MS = 1000L;

   private final BooleanSetting enabled;
   private final NumberSetting maxCps;
   private final BooleanSetting warnOnly;
   private final java.util.ArrayDeque<Long> clicks = new java.util.ArrayDeque<>();

   public CpsLimiterModule() {
      super("cps_limiter", "CPS Limiter", "Optional click-rate display and self-imposed limiter. Respects server rules; not a cheat.", ModuleCategory.COMBAT);
      enabled = this.register(new BooleanSetting(this, "enabled", "Enabled", false, "Enabled"));
      maxCps = this.register(new NumberSetting(this, "maxCps", "Max Cps", 15, 1, 30, 1, "Max Cps"));
      warnOnly = this.register(new BooleanSetting(this, "warnOnly", "Warn Only", true, "show warning, do not block"));
   }

   /** Called by the mouse mixin for every press, before the click reaches the game. */
   public void record() {
      this.clicks.addLast(System.currentTimeMillis());
   }

   /** Clicks in the last second. Prunes on read, so a stopped click stream decays to 0. */
   public int rate() {
      long cutoff = System.currentTimeMillis() - WINDOW_MS;
      this.clicks.removeIf(t -> t < cutoff);
      return this.clicks.size();
   }

   /** Whether the current rate is over the operator's ceiling. */
   public boolean exceeded() {
      return this.rate() > this.maxCps.get();
   }

   /**
    * The one question the mouse mixin asks before letting a press through. Off unless the
    * module is on and the operator asked for blocking rather than a warning.
    */
   public boolean shouldBlock() {
      return this.enabled.get() && !this.warnOnly.get() && this.exceeded();
   }

   @Override
   public String renderText() {
      if (!this.enabled.get()) {
         return null;
      }

      return (this.exceeded() && !this.warnOnly.get() ? "! " : "")
         + "CPS " + this.rate() + "/" + (int)this.maxCps.get();
   }
}
