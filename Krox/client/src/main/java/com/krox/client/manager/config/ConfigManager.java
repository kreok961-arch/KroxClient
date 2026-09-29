package com.krox.client.manager.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.krox.client.KroxClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigManager {
   // Resolved on first use, not in a static initializer: FabricLoader.getConfigDir()
   // dereferences a null field until a game is present, so touching this class from a
   // self-check outside the game used to kill it with ExceptionInInitializerError.
   // In-game the paths are identical -- getConfigDir() is long since resolved by then.
   private static Path configDir;
   private static Path configFile;
   private static Path configTmp;

   private static synchronized Path dir() {
      if (configDir == null) {
         configDir = FabricLoader.getInstance().getConfigDir().resolve("krox");
         configFile = configDir.resolve("config.json");
         configTmp = configDir.resolve("config.json.tmp");
      }

      return configDir;
   }

   private static Path file() {
      dir();
      return configFile;
   }

   private static Path tmp() {
      dir();
      return configTmp;
   }

   private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private JsonObject root = new JsonObject();

   public void initialize() {
      try {
         Files.createDirectories(dir());
      } catch (IOException var2) {
         KroxClient.LOGGER.error("[Krox] Could not create config dir - config will not persist.", var2);
      }

      this.load();
      KroxClient.LOGGER.info("[Krox] ConfigManager initialized at {}", file());
   }

   public void load() {
      if (!Files.exists(file())) {
         KroxClient.LOGGER.info("[Krox] No existing config - using defaults.");
         this.root = new JsonObject();
      } else {
         try {
            String content = Files.readString(file());
            this.root = JsonParser.parseString(content).getAsJsonObject();
         } catch (Throwable var4) {
            KroxClient.LOGGER.error("[Krox] Config file is malformed - using defaults. Backing up to config.json.broken.", var4);

            try {
               Files.copy(file(), dir().resolve("config.json.broken"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException var3) {
            }

            this.root = new JsonObject();
         }
      }
   }

   public void save() {
      try {
         Files.writeString(tmp(), this.gson.toJson(this.root));
         Files.move(tmp(), file(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
      } catch (IOException var2) {
         KroxClient.LOGGER.error("[Krox] Failed to write config.", var2);
      }
   }

   public JsonObject root() {
      return this.root;
   }

   public boolean getBool(String key, boolean def) {
      if (this.root.has(key) && this.root.get(key).isJsonPrimitive()) {
         try {
            return this.root.get(key).getAsBoolean();
         } catch (Throwable var4) {
         }
      }

      return def;
   }

   public int getInt(String key, int def) {
      if (this.root.has(key) && this.root.get(key).isJsonPrimitive()) {
         try {
            return this.root.get(key).getAsInt();
         } catch (Throwable var4) {
         }
      }

      return def;
   }

   public double getDouble(String key, double def) {
      if (this.root.has(key) && this.root.get(key).isJsonPrimitive()) {
         try {
            return this.root.get(key).getAsDouble();
         } catch (Throwable var5) {
         }
      }

      return def;
   }

   public String getString(String key, String def) {
      if (this.root.has(key) && this.root.get(key).isJsonPrimitive()) {
         try {
            return this.root.get(key).getAsString();
         } catch (Throwable var4) {
         }
      }

      return def;
   }

   public void set(String key, boolean v) {
      this.root.addProperty(key, v);
   }

   public void set(String key, int v) {
      this.root.addProperty(key, v);
   }

   public void set(String key, double v) {
      this.root.addProperty(key, v);
   }

   public void set(String key, String v) {
      this.root.addProperty(key, v);
   }

   public Path configDir() {
      return dir();
   }

   public Path configFile() {
      return file();
   }
}
