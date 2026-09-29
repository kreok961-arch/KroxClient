package com.krox.client.manager.notification;

import com.krox.client.KroxClient;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class NotificationManager {
   private static final int MAX_VISIBLE = 6;
   private static final long DEFAULT_LIFETIME_MS = 3500L;
   private final Deque<NotificationManager.Notification> active = new ArrayDeque<>();

   public void initialize() {
      KroxClient.LOGGER.info("[Krox] NotificationManager initialized (max visible = {})", 6);
   }

   public void post(String title, String message) {
      this.post(title, message, 3500L);
   }

   public void post(String title, String message, long lifetimeMs) {
      synchronized (this.active) {
         this.active.addLast(new NotificationManager.Notification(title, message, System.currentTimeMillis(), lifetimeMs));

         while (this.active.size() > 6) {
            this.active.removeFirst();
         }
      }
   }

   public List<NotificationManager.Notification> snapshot() {
      long now = System.currentTimeMillis();
      synchronized (this.active) {
         this.active.removeIf(n -> now - n.createdAt > n.lifetimeMs);
         return new ArrayList<>(this.active);
      }
   }

   public static final class Notification {
      public final String title;
      public final String message;
      public final long createdAt;
      public final long lifetimeMs;

      Notification(String title, String message, long createdAt, long lifetimeMs) {
         this.title = title;
         this.message = message;
         this.createdAt = createdAt;
         this.lifetimeMs = lifetimeMs;
      }
   }
}
