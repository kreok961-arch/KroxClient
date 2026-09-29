package com.krox.client.manager.keybind;

import com.krox.client.KroxClient;
import com.krox.client.manager.module.Module;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.client.util.InputUtil.Type;

public final class KeybindManager {
   private static final Category KROX_CATEGORY = Category.MISC;
   private net.minecraft.client.option.KeyBinding openGuiBinding;
   private final Map<String, net.minecraft.client.option.KeyBinding> moduleBindings = new HashMap<>();

   public void initialize() {
      this.openGuiBinding = KeyBindingHelper.registerKeyBinding(new net.minecraft.client.option.KeyBinding("key.krox.open_gui", Type.KEYSYM, 344, KROX_CATEGORY));
      KroxClient.LOGGER.info("[Krox] KeybindManager initialized (open_gui = RIGHT_SHIFT).");
   }

   public void onTick() {
      if (this.openGuiBinding != null && this.openGuiBinding.wasPressed()) {
         KroxClient.get().getClientManager().gui().openClickGui();
      }

      for (Entry<String, net.minecraft.client.option.KeyBinding> e : this.moduleBindings.entrySet()) {
         if (e.getValue().wasPressed()) {
            Module m = KroxClient.get().getClientManager().modules().byId(e.getKey());
            if (m != null) {
               m.toggle();
            }
         }
      }
   }

   public void updateModuleBinding(Module module, int keyCode) {
      String id = module.getId();
      net.minecraft.client.option.KeyBinding existing = this.moduleBindings.get(id);
      if (existing != null) {
         existing.setBoundKey(Type.KEYSYM.createFromCode(keyCode));
      } else {
         net.minecraft.client.option.KeyBinding kb = KeyBindingHelper.registerKeyBinding(new net.minecraft.client.option.KeyBinding("key.krox.module." + id, Type.KEYSYM, keyCode, KROX_CATEGORY));
         this.moduleBindings.put(id, kb);
      }
   }

   public net.minecraft.client.option.KeyBinding getOpenGuiBinding() {
      return this.openGuiBinding;
   }
}
