package com.krox.client.manager;

import com.krox.client.KroxClient;
import com.krox.client.manager.cape.KroxCapeManager;
import com.krox.client.manager.config.ConfigManager;
import com.krox.client.manager.cosmetics.CosmeticsManager;
import com.krox.client.manager.hud.HudManager;
import com.krox.client.manager.keybind.KeybindManager;
import com.krox.client.manager.module.ModuleManager;
import com.krox.client.manager.notification.NotificationManager;
import com.krox.client.manager.render.RenderManager;
import com.krox.client.manager.skin.KroxSkinManager;
import com.krox.client.manager.texture.TextureManager;
import com.krox.client.manager.ui.GuiManager;
import com.krox.client.util.KroxTheme;

public final class ClientManager {
   private static final ClientManager INSTANCE = new ClientManager();
   private ConfigManager configManager;
   private ModuleManager moduleManager;
   private KeybindManager keybindManager;
   private GuiManager guiManager;
   private HudManager hudManager;
   private NotificationManager notificationManager;
   private TextureManager textureManager;
   private KroxSkinManager skinManager;
   private KroxCapeManager capeManager;
   private CosmeticsManager cosmeticsManager;
   private RenderManager renderManager;
   private boolean initialized = false;

   private ClientManager() {
   }

   public static ClientManager get() {
      return INSTANCE;
   }

   public void initialize() {
      if (this.initialized) {
         KroxClient.LOGGER.warn("[Krox] ClientManager.initialize() called twice - ignoring.");
      } else {
         KroxTheme.init();
         this.textureManager = new TextureManager();
         this.textureManager.initialize();
         this.configManager = new ConfigManager();
         this.configManager.initialize();
         this.notificationManager = new NotificationManager();
         this.notificationManager.initialize();
         this.moduleManager = new ModuleManager();
         this.moduleManager.initialize();
         this.keybindManager = new KeybindManager();
         this.keybindManager.initialize();
         this.skinManager = new KroxSkinManager();
         this.skinManager.initialize();
         this.capeManager = new KroxCapeManager();
         this.capeManager.initialize();
         this.cosmeticsManager = new CosmeticsManager();
         this.cosmeticsManager.initialize();
         this.renderManager = new RenderManager();
         this.renderManager.initialize();
         this.hudManager = new HudManager();
         this.hudManager.initialize();
         this.guiManager = new GuiManager();
         this.guiManager.initialize();
         this.initialized = true;
         KroxClient.LOGGER.info("[Krox] ClientManager: all subsystems initialized.");
      }
   }

   public ConfigManager config() {
      return this.configManager;
   }

   public ModuleManager modules() {
      return this.moduleManager;
   }

   public KeybindManager keybinds() {
      return this.keybindManager;
   }

   public GuiManager gui() {
      return this.guiManager;
   }

   public HudManager hud() {
      return this.hudManager;
   }

   public NotificationManager notifications() {
      return this.notificationManager;
   }

   public TextureManager textures() {
      return this.textureManager;
   }

   public KroxSkinManager skins() {
      return this.skinManager;
   }

   public KroxCapeManager capes() {
      return this.capeManager;
   }

   public CosmeticsManager cosmetics() {
      return this.cosmeticsManager;
   }

   public RenderManager render() {
      return this.renderManager;
   }
}
