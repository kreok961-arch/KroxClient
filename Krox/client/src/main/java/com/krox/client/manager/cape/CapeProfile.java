package com.krox.client.manager.cape;

import java.nio.file.Path;

public final class CapeProfile {
   private final String id;
   private final String displayName;
   private final Path path;

   public CapeProfile(String id, String displayName, Path path) {
      this.id = id;
      this.displayName = displayName;
      this.path = path;
   }

   public String getId() {
      return this.id;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public Path getPath() {
      return this.path;
   }
}
