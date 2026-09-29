package com.krox.client.manager.skin;

import com.krox.client.KroxClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;

public final class SkinStorage {
   private static final Path SKIN_DIR = FabricLoader.getInstance().getConfigDir().resolve("krox").resolve("skins");
   private final Map<String, SkinProfile> profiles = new LinkedHashMap<>();

   public void loadAll() {
      this.profiles.clear();

      try {
         Files.createDirectories(SKIN_DIR);
      } catch (IOException var5) {
         KroxClient.LOGGER.error("[Krox] Could not create skin dir {}.", SKIN_DIR, var5);
         return;
      }

      try (Stream<Path> s = Files.list(SKIN_DIR)) {
         s.filter(p -> p.toString().toLowerCase().endsWith(".png")).forEach(this::tryAdd);
      } catch (IOException var7) {
         KroxClient.LOGGER.error("[Krox] Skin dir listing failed.", var7);
      }

      KroxClient.LOGGER.info("[Krox] SkinStorage loaded {} profile(s) from {}.", this.profiles.size(), SKIN_DIR);
   }

   private void tryAdd(Path p) {
      String filename = p.getFileName().toString();
      String id = filename.substring(0, filename.length() - 4).toLowerCase(Locale.ROOT);
      String display = filename.substring(0, filename.length() - 4);
      this.profiles.put(id, new SkinProfile(id, display, p, true));
   }

   public List<SkinProfile> list() {
      return new ArrayList<>(this.profiles.values());
   }

   public SkinProfile byId(String id) {
      return this.profiles.get(id);
   }

   public Path skinDir() {
      return SKIN_DIR;
   }
}
