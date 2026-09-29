package com.krox.client.mixin;

import com.krox.client.KroxClient;
import com.krox.client.manager.module.impl.CpsLimiterModule;
import com.krox.client.manager.module.impl.CpsModule;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Routes in-game mouse input to the HUD so widgets can be dragged (defect D-02), and
 * feeds the CPS modules.
 *
 * <p>Mouse is the only input path that fires while no Screen is open, so a click that
 * lands on a HUD panel starts a drag here instead of reaching the game as an attack.
 *
 * <p>CPS also counts here: GLFW action 1 is the press edge, so a held button is one
 * count, and both CPS modules see the same event the click does.
 */
@Mixin(Mouse.class)
public abstract class MixinMouse {
   @Shadow
   public net.minecraft.client.MinecraftClient client;

   @Inject(method = "onMouseButton", at = {@At("HEAD")}, cancellable = true)
   private void krox_onMouseButton(long window, MouseInput input, int action, CallbackInfo ci) {
      try {
         if (KroxClient.get() == null || this.client == null || this.client.currentScreen != null) {
            return;
         }

         if (action != 1) {
            KroxClient.get().getClientManager().hud().onMouseRelease();
            return;
         }

         boolean isAttack = input.button() <= 1;

         // Count before gating: a press the limiter drops is still a physical click, and
         // the limiter needs it in its window or it would never see the next one.
         if (isAttack) {
            krox_recordCps(input);
         }

         if (isAttack && krox_limiterBlocks(input)) {
            // Release any drag first, so cancelling a press cannot strand a held button.
            KroxClient.get().getClientManager().hud().onMouseRelease();
            ci.cancel();
            return;
         }

         // A press that lands on a widget is a drag handle, not an attack.
         if (KroxClient.get().getClientManager().hud().onMouseClick(this.client.mouse.getX(), this.client.mouse.getY())) {
            ci.cancel();
         }
      } catch (Throwable var5) {
         KroxClient.LOGGER.error("[Krox] HUD mouse handler threw.", var5);
      }
   }

   /**
    * Presses are the only thing CPS counts, and a press is action 1 -- GLFW's
    * "button was pressed", not "button was held", so one press is one count.
    */
   private void krox_recordCps(MouseInput input) {
      if (KroxClient.get().getClientManager().modules().byId("cps") instanceof CpsModule cps) {
         cps.record(input.button() == 0);
      }

      if (KroxClient.get().getClientManager().modules().byId("cps_limiter") instanceof CpsLimiterModule limiter) {
         limiter.record();
      }
   }

   /**
    * @return true when the operator enabled CPS Limiter in blocking mode and the rate is
    *         over the ceiling.
    */
   private boolean krox_limiterBlocks(MouseInput input) {
      if (input.button() > 1) {
         return false;
      }

      return KroxClient.get().getClientManager().modules().byId("cps_limiter") instanceof CpsLimiterModule limiter
         && limiter.shouldBlock();
   }

   /**
    * The drag itself. This cannot live in onMouseButton: the button callback only fires
    * on a state change, and a drag is many moves with no state change at all. onCursorPos
    * fires for every cursor update, and Mouse.getX/getY give its scaled coordinates, so
    * it is the only path that tracks a pointer mid-drag.
    */
   @Inject(method = "onCursorPos", at = {@At("TAIL")})
   private void krox_hudDrag(long window, double x, double y, CallbackInfo ci) {
      try {
         if (KroxClient.get() == null || this.client == null || this.client.currentScreen != null) {
            return;
         }

         KroxClient.get().getClientManager().hud().onMouseDrag(this.client.mouse.getX(), this.client.mouse.getY());
      } catch (Throwable var5) {
         KroxClient.LOGGER.error("[Krox] HUD drag handler threw.", var5);
      }
   }
}
