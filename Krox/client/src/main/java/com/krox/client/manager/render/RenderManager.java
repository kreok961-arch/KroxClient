package com.krox.client.manager.render;

import com.krox.client.KroxClient;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class RenderManager {
   private final Queue<Runnable> pending = new ConcurrentLinkedQueue<>();

   public void initialize() {
      KroxClient.LOGGER.info("[Krox] RenderManager initialized.");
   }

   public void post(Runnable r) {
      this.pending.add(r);
   }

   public void tick() {
      Runnable r;
      while ((r = this.pending.poll()) != null) {
         try {
            r.run();
         } catch (Throwable var3) {
            KroxClient.LOGGER.error("[Krox] Render-thread task threw - continuing.", var3);
         }
      }
   }
}
