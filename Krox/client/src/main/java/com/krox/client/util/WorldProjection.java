package com.krox.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Projects world coordinates into scaled screen space.
 *
 * <p>1.21.11 removed the line pipeline these overlays used to draw with
 * ({@code RenderLayer.LINES} and {@code VertexConsumer.build()} are both gone)
 * and {@code WorldRenderContext} exposes no {@link DrawContext}, so the
 * world-space overlays project to the screen and draw through the HUD pass
 * instead.
 *
 * <p>The view matrix mirrors {@code Camera.setRotation}, which builds
 * {@code rotationYXZ(PI - yawRad, -pitchRad, 0)}; the projection comes from
 * {@code GameRenderer.getBasicProjectionMatrix}. The pair yields clip space
 * with z in [-1, 1] and y pointing up, so screen x = (ndc.x + 1) / 2 * width and
 * screen y = (1 - ndc.y) / 2 * height.
 */
public final class WorldProjection {
   private static final Vector4f P = new Vector4f();

   private WorldProjection() {
   }

   /**
    * Writes a world point into {@code out} as {screenX, screenY, ndcZ}.
    *
    * @return false when the point is behind the camera, outside the depth
    *         range, or the world is not loaded; {@code out} is then untouched.
    */
   public static boolean project(DrawContext ctx, double wx, double wy, double wz, float[] out) {
      MinecraftClient mc = MinecraftClient.getInstance();

      if (mc.world == null || mc.gameRenderer == null || mc.gameRenderer.getCamera() == null) {
         return false;
      }

      net.minecraft.util.math.Vec3d cam = mc.gameRenderer.getCamera().getCameraPos();
      Matrix4f view = new Matrix4f().translation((float)-cam.x, (float)-cam.y, (float)-cam.z);
      Matrix4f proj = mc.gameRenderer.getBasicProjectionMatrix(
         mc.options == null ? 70.0F : (float)mc.options.getFov().getValue());
      P.set((float)wx, (float)wy, (float)wz, 1.0F);
      view.transform(P);
      proj.transform(P);

      if (P.w <= 0.0F) {
         return false;
      }

      float ndcX = P.x / P.w;
      float ndcY = P.y / P.w;
      float ndcZ = P.z / P.w;

      if (ndcZ < -1.0F || ndcZ > 1.0F) {
         return false;
      }

      out[0] = (ndcX + 1.0F) * 0.5F * ctx.getScaledWindowWidth();
      out[1] = (1.0F - ndcY) * 0.5F * ctx.getScaledWindowHeight();
      out[2] = ndcZ;
      return true;
   }

   /** Depth-cued alpha: fully opaque at the near plane, 0.15 at the far one. */
   public static int fade(int rgb, float ndcZ) {
      int a = (int)Math.round(255.0D * Math.max(0.15D, Math.min(1.0D, 1.0D - (ndcZ + 1.0D) * 0.5D)));
      return (a << 24) | (rgb & 0xFFFFFF);
   }

   /**
    * Screen-space line. DrawContext exposes no line primitive, so this walks
    * the longer axis one pixel at a time.
    */
   public static void line(DrawContext ctx, float x0, float y0, float x1, float y1, int color) {
      int steps = (int)Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0));

      for (int i = 0; i <= steps; i++) {
         float t = steps == 0 ? 0.0F : (float)i / steps;
         int x = (int)(x0 + (x1 - x0) * t);
         int y = (int)(y0 + (y1 - y0) * t);
         ctx.fill(x, y, x + 1, y + 1, color);
      }
   }
}
