package com.krox.client.manager.module.impl;

import com.krox.client.manager.hud.HudElement;
import com.krox.client.manager.module.BooleanSetting;
import com.krox.client.manager.module.ModuleCategory;
import com.krox.client.manager.module.NumberSetting;
import java.lang.Math;

public final class CoordinatesModule extends HudElement {
   private final NumberSetting decimalPlaces;
   private final BooleanSetting showBiome;
   private final BooleanSetting showChunk;
   private final BooleanSetting showDirection;
   private final BooleanSetting showLight;
   private final BooleanSetting showNetherCoords;
   private final BooleanSetting showX;
   private final BooleanSetting showY;
   private final BooleanSetting showZ;

   public CoordinatesModule() {
      super("coordinates", "Coordinates", "Player position, direction, biome, light.", ModuleCategory.HUD);
      showX = this.register(new BooleanSetting(this, "showX", "Show X", true, "Show X"));
      showY = this.register(new BooleanSetting(this, "showY", "Show Y", true, "Show Y"));
      showZ = this.register(new BooleanSetting(this, "showZ", "Show Z", true, "Show Z"));
      showDirection = this.register(new BooleanSetting(this, "showDirection", "Show Direction", true, "Show Direction"));
      showBiome = this.register(new BooleanSetting(this, "showBiome", "Show Biome", true, "Show Biome"));
      showLight = this.register(new BooleanSetting(this, "showLight", "Show Light", false, "Show Light"));
      showChunk = this.register(new BooleanSetting(this, "showChunk", "Show Chunk", false, "Show Chunk"));
      showNetherCoords = this.register(new BooleanSetting(this, "showNetherCoords", "Show Nether Coords", false, "nether conversion"));
      decimalPlaces = this.register(new NumberSetting(this, "decimalPlaces", "Decimal Places", 0, 0, 3, 1, "Decimal Places"));
   }
   @Override
   public String renderText() {
      net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
      if (mc.player == null || mc.world == null) {
         return null;
      }

      int places = (int)this.decimalPlaces.get();
      String fmt = "%." + places + "f";
      double scale = mc.world.getDimension().coordinateScale();
      double x = mc.player.getX();
      double y = mc.player.getY();
      double z = mc.player.getZ();
      StringBuilder sb = new StringBuilder();

      if (this.showNetherCoords.get()) {
         x /= scale;
         z /= scale;
      }

      if (this.showX.get()) {
         sb.append("X ").append(String.format(fmt, x));
      }

      if (this.showY.get()) {
         if (sb.length() > 0) {
            sb.append("  ");
         }

         sb.append("Y ").append(String.format(fmt, y));
      }

      if (this.showZ.get()) {
         if (sb.length() > 0) {
            sb.append("  ");
         }

         sb.append("Z ").append(String.format(fmt, z));
      }

      if (this.showDirection.get()) {
         if (sb.length() > 0) {
            sb.append("  ");
         }

         sb.append(cardinal(mc.player.getYaw()));
      }

      if (this.showChunk.get()) {
         if (sb.length() > 0) {
            sb.append("  ");
         }

         sb.append("C ").append(mc.player.getChunkPos().x)
            .append(", ").append(mc.player.getChunkPos().z);
      }

      if (this.showBiome.get()) {
         String biome = mc.world.getBiomeAccess().getBiome(mc.player.getBlockPos())
            .getKey().map(k -> k.getValue().getPath()).orElse("?");

         if (sb.length() > 0) {
            sb.append("  ");
         }

         sb.append(biome);
      }

      if (this.showLight.get()) {
         if (sb.length() > 0) {
            sb.append("  ");
         }

         sb.append("L ").append(mc.world.getLightLevel(mc.player.getBlockPos()));
      }

      return sb.toString();
   }

   private static String cardinal(float yaw) {
      float wrapped = net.minecraft.util.math.MathHelper.wrapDegrees(yaw + 180.0F);
      int octant = (int)Math.floor(wrapped / 45.0F + 0.5F) & 7;
      String[] names = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
      return names[octant];
   }
}
