package com.krox.client.manager.skin;

import java.nio.file.Path;

public final class SkinProfile {
   private final String id;
   private final String displayName;
   private final Path path;
   private final boolean wide;

   public SkinProfile(String id, String displayName, Path path, boolean wide) {
      this.id = id;
      this.displayName = displayName;
      this.path = path;
      this.wide = wide;
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

   public boolean isWide() {
      return this.wide;
   }
}
