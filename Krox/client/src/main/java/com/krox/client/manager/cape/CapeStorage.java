package com.krox.client.manager.cape;

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

public final class CapeStorage {
   private static final Path CAPE_DIR = FabricLoader.getInstance().getConfigDir().resolve("krox").resolve("capes");
   private final Map<String, CapeProfile> profiles = new LinkedHashMap<>();

   public void loadAll() {
      this.profiles.clear();

      try {
         Files.createDirectories(CAPE_DIR);
      } catch (IOException var5) {
         KroxClient.LOGGER.error("[Krox] Could not create cape dir {}.", CAPE_DIR, var5);
         return;
      }

      try (Stream<Path> s = Files.list(CAPE_DIR)) {
         s.filter(p -> p.toString().toLowerCase().endsWith(".png")).forEach(this::tryAdd);
      } catch (IOException var7) {
         KroxClient.LOGGER.error("[Krox] Cape dir listing failed.", var7);
      }

      KroxClient.LOGGER.info("[Krox] CapeStorage loaded {} profile(s) from {}.", this.profiles.size(), CAPE_DIR);
   }

   private void tryAdd(Path p) {
      String filename = p.getFileName().toString();
      String id = filename.substring(0, filename.length() - 4).toLowerCase(Locale.ROOT);
      String display = filename.substring(0, filename.length() - 4);
      this.profiles.put(id, new CapeProfile(id, display, p));
   }

   public List<CapeProfile> list() {
      return new ArrayList<>(this.profiles.values());
   }

   public CapeProfile byId(String id) {
      return this.profiles.get(id);
   }

   public Path capeDir() {
      return CAPE_DIR;
   }
}
