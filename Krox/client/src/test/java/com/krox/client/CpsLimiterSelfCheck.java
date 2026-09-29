package com.krox.client;

import com.krox.client.manager.module.impl.CpsLimiterModule;

/**
 * Self-check for the CPS limiter's rate window and block gate. Plain main() with
 * throws rather than JUnit: the mod has no test source set, and one check does not
 * justify pulling a test framework into a build that has never needed one.
 *
 * <p>The one thing a reader cannot confirm by reading the module is whether the
 * sliding window actually slides. rate() prunes on read, which is what makes a
 * stopped click stream decay to 0 -- a bug that reads as plausible right up until
 * someone watches the counter and it is stuck.
 *
 * <p>Run it: see docs/qa/README.md.
 */
public final class CpsLimiterSelfCheck {
   public static void main(String[] args) throws Exception {
      decayToZeroWithoutNewClicks();
      rateCountsOnlyClicksInsideTheWindow();
      blocksOnlyWhenEnabledAndNotWarnOnly();
      renderTextTracksMode();
      System.out.println("CpsLimiterSelfCheck: all checks passed");
   }

   /**
    * The regression from CPS-c: pruning used to live in record(), so a module that
    * stopped receiving clicks reported its last count forever.
    */
   private static void decayToZeroWithoutNewClicks() throws Exception {
      CpsLimiterModule m = new CpsLimiterModule();
      for (int i = 0; i < 5; i++) {
         m.record();
      }

      check(m.rate() == 5, "5 recorded clicks read back as 5, got " + m.rate());

      // Past the 1s window: nothing new was recorded, yet every click is now stale.
      Thread.sleep(1100L);
      check(m.rate() == 0, "rate decays to 0 with no new clicks, got " + m.rate());
   }

   /** A click just inside the window counts; one just outside it does not. */
   private static void rateCountsOnlyClicksInsideTheWindow() throws Exception {
      CpsLimiterModule m = new CpsLimiterModule();
      m.record();
      m.record();
      Thread.sleep(600L);
      m.record();

      // Two clicks are 600ms old (inside), one is 0ms old (inside): all three.
      check(m.rate() == 3, "clicks inside the window all count, got " + m.rate());

      Thread.sleep(600L);
      // The first two are now ~1200ms old and must have been pruned.
      check(m.rate() == 1, "clicks outside the window are pruned, got " + m.rate());
   }

   /**
    * shouldBlock() is the gate MixinMouse asks before letting a press reach the
    * game. It is off by default, off while warn-only, and only fires over the
    * ceiling -- three ways to be wrong that all look identical in game.
    */
   private static void blocksOnlyWhenEnabledAndNotWarnOnly() {
      CpsLimiterModule m = new CpsLimiterModule();
      for (int i = 0; i < 10; i++) {
         m.record();
      }

      // Assert against the default ceiling first: setting maxCps below makes the
      // next expectation true, so ordering these the other way round passes for
      // the wrong reason -- which is exactly what the unset register() did.
      check(!m.exceeded(), "10 clicks against the 15 default ceiling does not exceed");
      check(!m.shouldBlock(), "disabled module never blocks");

      set(m, "maxCps", 5.0);
      check(m.exceeded(), "10 clicks exceeds a ceiling of 5");
      check(!m.shouldBlock(), "enabled=false still does not block, only flags");

      set(m, "enabled", true);
      check(!m.shouldBlock(), "warnOnly=true never blocks, only warns");

      set(m, "warnOnly", false);
      check(m.shouldBlock(), "enabled + blocking mode + over ceiling blocks");

      set(m, "maxCps", 30.0);
      check(!m.exceeded(), "10 clicks is under a ceiling of 30");
      check(!m.shouldBlock(), "under the ceiling does not block");
   }

   /** A disabled module is invisible; a blocking one is prefixed so it is obvious. */
   private static void renderTextTracksMode() {
      CpsLimiterModule m = new CpsLimiterModule();
      check(m.renderText() == null, "disabled module renders no text");

      set(m, "enabled", true);
      check("CPS 0/15".equals(m.renderText()), "enabled module reads CPS 0/15, got " + m.renderText());

      set(m, "maxCps", 1.0);
      for (int i = 0; i < 5; i++) {
         m.record();
      }

      // Still warnOnly, so over the ceiling but flagged without the blocking prefix.
      check("CPS 5/1".equals(m.renderText()), "warnOnly over ceiling reads unprefixed, got " + m.renderText());

      set(m, "warnOnly", false);
      check("! CPS 5/1".equals(m.renderText()), "blocking mode prefixes '! ', got " + m.renderText());
   }

   /**
    * Settings are looked up by id because this module's own fields are private and
    * this check is not in the module's package.
    */
   private static void set(CpsLimiterModule m, String id, Object value) {
      m.getSettings().stream()
         .filter(s -> id.equals(s.getId()))
         .findFirst()
         .ifPresentOrElse(
            s -> {
               if (s instanceof com.krox.client.manager.module.BooleanSetting b) {
                  b.set((Boolean)value);
               } else if (s instanceof com.krox.client.manager.module.NumberSetting n) {
                  n.set((Double)value);
               } else {
                  throw new IllegalStateException("unhandled setting type: " + s.getClass());
               }
            },
            () -> {
               throw new IllegalStateException("no setting '" + id + "' on cps_limiter");
            }
         );
   }

   private static void check(boolean ok, String what) {
      if (!ok) {
         throw new AssertionError("CPS limiter self-check failed: " + what);
      }
   }

   private CpsLimiterSelfCheck() {
   }
}
