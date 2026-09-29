package com.krox.client.manager.module;

import com.krox.client.KroxClient;
import com.krox.client.manager.module.impl.FpsModule;
import com.krox.client.manager.module.impl.CpsModule;
import com.krox.client.manager.module.impl.PingModule;
import com.krox.client.manager.module.impl.ArmorHudModule;
import com.krox.client.manager.module.impl.TargetHudModule;
import com.krox.client.manager.module.impl.KeystrokesModule;
import com.krox.client.manager.module.impl.DirectionModule;
import com.krox.client.manager.module.impl.BossbarModule;
import com.krox.client.manager.module.impl.ItemDurabilityModule;
import com.krox.client.manager.module.impl.ScoreboardModule;
import com.krox.client.manager.module.impl.CrosshairModule;
import com.krox.client.manager.module.impl.PotionStatusModule;
import com.krox.client.manager.module.impl.CoordinatesModule;
import com.krox.client.manager.module.impl.ServerIpModule;
import com.krox.client.manager.module.impl.ClockModule;
import com.krox.client.manager.module.impl.ReachModule;
import com.krox.client.manager.module.impl.ComboModule;
import com.krox.client.manager.module.impl.PackDisplayModule;
import com.krox.client.manager.module.impl.MemoryModule;
import com.krox.client.manager.module.impl.SpeedometerModule;
import com.krox.client.manager.module.impl.PitchYawModule;
import com.krox.client.manager.module.impl.InventoryHudModule;
import com.krox.client.manager.module.impl.ReachCircleModule;
import com.krox.client.manager.module.impl.HitColorModule;
import com.krox.client.manager.module.impl.HitmarkersModule;
import com.krox.client.manager.module.impl.ParticleMultiplierModule;
import com.krox.client.manager.module.impl.CpsLimiterModule;
import com.krox.client.manager.module.impl.HurtCamModule;
import com.krox.client.manager.module.impl.DynamicFovModule;
import com.krox.client.manager.module.impl.BlockOutlineModule;
import com.krox.client.manager.module.impl.LowFireModule;
import com.krox.client.manager.module.impl.ProjectilePredictorModule;
import com.krox.client.manager.module.impl.CritFxModule;
import com.krox.client.manager.module.impl.SharpnessFxModule;
import com.krox.client.manager.module.impl.ArmorBreakFxModule;
import com.krox.client.manager.module.impl.SmoothCameraModule;
import com.krox.client.manager.module.impl.AutoRespawnModule;
import com.krox.client.manager.module.impl.ItemPhysicsModule;
import com.krox.client.manager.module.impl.TimeChangerModule;
import com.krox.client.manager.module.impl.SkyboxModule;
import com.krox.client.manager.module.impl.GlassUiModule;
import com.krox.client.manager.module.impl.FullbrightModule;
import com.krox.client.manager.module.impl.MotionBlurModule;
import com.krox.client.manager.module.impl.CapeWingsModule;
import com.krox.client.manager.module.impl.EnchantGlowModule;
import com.krox.client.manager.module.impl.ZoomModule;
import com.krox.client.manager.module.impl.DynamicLightsModule;
import com.krox.client.manager.module.impl.ClearGlassModule;
import com.krox.client.manager.module.impl.CustomFontModule;
import com.krox.client.manager.module.impl.CustomParticlesModule;
import com.krox.client.manager.module.impl.ScreenBlurModule;
import com.krox.client.manager.module.impl.DamageIndicatorsModule;
import com.krox.client.manager.module.impl.ChunkAnimatorModule;
import com.krox.client.manager.module.impl.WeatherCustomizerModule;
import com.krox.client.manager.module.impl.PlayerShadowModule;
import com.krox.client.manager.module.impl.FreelookModule;
import com.krox.client.manager.module.impl.SnaplookModule;
import com.krox.client.manager.module.impl.ReplayModule;
import com.krox.client.manager.module.impl.ChatCustomizerModule;
import com.krox.client.manager.module.impl.AutoGgModule;
import com.krox.client.manager.module.impl.PerspectiveKeyModule;
import com.krox.client.manager.module.impl.SoundPhysicsModule;
import com.krox.client.manager.module.impl.DiscordRpcModule;
import com.krox.client.manager.module.impl.AutoFriendAcceptModule;
import com.krox.client.manager.module.impl.KeybindManagerModule;
import com.krox.client.manager.module.impl.ToggleSprintModule;
import com.krox.client.manager.module.impl.ToggleSneakModule;
import com.krox.client.manager.module.impl.FastPlacementModule;
import com.krox.client.manager.module.impl.ScrollableTooltipsModule;
import com.krox.client.manager.module.impl.ItemPhysicsToggleModule;
import com.krox.client.manager.module.impl.QuickCraftingModule;
import com.krox.client.manager.module.impl.GlesPipelineModule;
import com.krox.client.manager.module.impl.EntityCullingModule;
import com.krox.client.manager.module.impl.ChunkBatchingModule;
import com.krox.client.manager.module.impl.GcHelperModule;
import com.krox.client.manager.module.impl.TextureCacheModule;
import com.krox.client.manager.module.impl.TouchGesturesModule;
import com.krox.client.manager.module.impl.VirtualJoystickModule;
import com.krox.client.manager.module.impl.TouchScalingModule;
import com.krox.client.manager.module.impl.FastMathModule;
import com.krox.client.manager.module.impl.MicrosoftOauthModule;
import com.krox.client.manager.module.impl.OfflineAccountModule;
import com.krox.client.manager.module.impl.SessionRefreshModule;
import com.krox.client.manager.module.impl.SkinSyncModule;
import com.krox.client.manager.module.impl.Wardrobe3dModule;
import com.krox.client.manager.module.impl.CosmeticSyncModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ModuleManager {
   private final Map<String, Module> byId = new LinkedHashMap<>();
   private final List<Module> all = new ArrayList<>();

   public void initialize() {
      this.register(new FpsModule());
      this.register(new CpsModule());
      this.register(new PingModule());
      this.register(new ArmorHudModule());
      this.register(new TargetHudModule());
      this.register(new KeystrokesModule());
      this.register(new DirectionModule());
      this.register(new BossbarModule());
      this.register(new ItemDurabilityModule());
      this.register(new ScoreboardModule());
      this.register(new CrosshairModule());
      this.register(new PotionStatusModule());
      this.register(new CoordinatesModule());
      this.register(new ServerIpModule());
      this.register(new ClockModule());
      this.register(new ReachModule());
      this.register(new ComboModule());
      this.register(new PackDisplayModule());
      this.register(new MemoryModule());
      this.register(new SpeedometerModule());
      this.register(new PitchYawModule());
      this.register(new InventoryHudModule());
      this.register(new ReachCircleModule());
      this.register(new HitColorModule());
      this.register(new HitmarkersModule());
      this.register(new ParticleMultiplierModule());
      this.register(new CpsLimiterModule());
      this.register(new HurtCamModule());
      this.register(new DynamicFovModule());
      this.register(new BlockOutlineModule());
      this.register(new LowFireModule());
      this.register(new ProjectilePredictorModule());
      this.register(new CritFxModule());
      this.register(new SharpnessFxModule());
      this.register(new ArmorBreakFxModule());
      this.register(new SmoothCameraModule());
      this.register(new AutoRespawnModule());
      this.register(new ItemPhysicsModule());
      this.register(new TimeChangerModule());
      this.register(new SkyboxModule());
      this.register(new GlassUiModule());
      this.register(new FullbrightModule());
      this.register(new MotionBlurModule());
      this.register(new CapeWingsModule());
      this.register(new EnchantGlowModule());
      this.register(new ZoomModule());
      this.register(new DynamicLightsModule());
      this.register(new ClearGlassModule());
      this.register(new CustomFontModule());
      this.register(new CustomParticlesModule());
      this.register(new ScreenBlurModule());
      this.register(new DamageIndicatorsModule());
      this.register(new ChunkAnimatorModule());
      this.register(new WeatherCustomizerModule());
      this.register(new PlayerShadowModule());
      this.register(new FreelookModule());
      this.register(new SnaplookModule());
      this.register(new ReplayModule());
      this.register(new ChatCustomizerModule());
      this.register(new AutoGgModule());
      this.register(new PerspectiveKeyModule());
      this.register(new SoundPhysicsModule());
      this.register(new DiscordRpcModule());
      this.register(new AutoFriendAcceptModule());
      this.register(new KeybindManagerModule());
      this.register(new ToggleSprintModule());
      this.register(new ToggleSneakModule());
      this.register(new FastPlacementModule());
      this.register(new ScrollableTooltipsModule());
      this.register(new ItemPhysicsToggleModule());
      this.register(new QuickCraftingModule());
      this.register(new GlesPipelineModule());
      this.register(new EntityCullingModule());
      this.register(new ChunkBatchingModule());
      this.register(new GcHelperModule());
      this.register(new TextureCacheModule());
      this.register(new TouchGesturesModule());
      this.register(new VirtualJoystickModule());
      this.register(new TouchScalingModule());
      this.register(new FastMathModule());
      this.register(new MicrosoftOauthModule());
      this.register(new OfflineAccountModule());
      this.register(new SessionRefreshModule());
      this.register(new SkinSyncModule());
      this.register(new Wardrobe3dModule());
      this.register(new CosmeticSyncModule());

      for (Module m : this.all) {
         boolean savedEnabled = KroxClient.get().getClientManager().config().getBool("modules." + m.getId() + ".enabled", m.isEnabled());
         if (savedEnabled) {
            m.setEnabled(true);
         }
      }

      KroxClient.LOGGER.info("[Krox] ModuleManager initialized - {} modules registered.", this.all.size());
   }

   private void register(Module m) {
      this.byId.put(m.getId(), m);
      this.all.add(m);
   }

   public Module byId(String id) {
      return this.byId.get(id);
   }

   public List<Module> all() {
      return Collections.unmodifiableList(this.all);
   }

   public List<Module> byCategory(ModuleCategory cat) {
      List<Module> out = new ArrayList<>();

      for (Module m : this.all) {
         if (m.getCategory() == cat) {
            out.add(m);
         }
      }

      return out;
   }

   public List<ModuleCategory> activeCategories() {
      Set<ModuleCategory> seen = new LinkedHashSet<>();

      for (Module m : this.all) {
         seen.add(m.getCategory());
      }

      return new ArrayList<>(seen);
   }

   public void tickAll() {
      for (Module m : this.all) {
         m.tickIfEnabled();
      }
   }
}
