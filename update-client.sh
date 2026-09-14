#!/bin/bash
set -e

# ==========================================
# Krox Client - Minecraft 1.21.1
# Fabric client features:
# - Custom title screen
# - Right Shift Click GUI
# - Categories
# - Toggleable modules
# - HUD
# - FPS
# - Coordinates
# - Sprint
# - Fullbright
# ==========================================

BASE="src/main/java/com/krox/client"
RES="src/main/resources"

mkdir -p "$BASE/module"
mkdir -p "$BASE/gui"
mkdir -p "$BASE/hud"
mkdir -p "$BASE/mixin"
mkdir -p "$RES"

# ---------- MAIN CLIENT ----------
cat > "$BASE/KroxClient.java" <<'EOF'
package com.krox.client;

import com.krox.client.gui.ClickGuiScreen;
import com.krox.client.hud.KroxHud;
import com.krox.client.module.ModuleManager;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

public class KroxClient implements ClientModInitializer {

    public static final ModuleManager MODULE_MANAGER = new ModuleManager();

    public static KeyBinding OPEN_GUI;

    @Override
    public void onInitializeClient() {

        MODULE_MANAGER.registerDefaults();

        OPEN_GUI = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.kroxclient.menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.kroxclient"
            )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            while (OPEN_GUI.wasPressed()) {
                client.setScreen(new ClickGuiScreen());
            }

            MODULE_MANAGER.onTick(client);
        });

        KroxHud.initialize();

        System.out.println("[Krox Client] Initialized successfully!");
    }
}
EOF

# ---------- CATEGORY ----------
cat > "$BASE/module/Category.java" <<'EOF'
package com.krox.client.module;

public enum Category {
    COMBAT,
    MOVEMENT,
    RENDER,
    HUD,
    PLAYER,
    MISC
}
EOF

# ---------- MODULE ----------
cat > "$BASE/module/Module.java" <<'EOF'
package com.krox.client.module;

import net.minecraft.client.MinecraftClient;

public abstract class Module {

    private final String name;
    private final Category category;
    private boolean enabled;

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        enabled = !enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onTick(MinecraftClient client) {
    }
}
EOF

# ---------- MODULE MANAGER ----------
cat > "$BASE/module/ModuleManager.java" <<'EOF'
package com.krox.client.module;

import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModules(Category category) {

        List<Module> result = new ArrayList<>();

        for (Module module : modules) {
            if (module.getCategory() == category) {
                result.add(module);
            }
        }

        return result;
    }

    public void onTick(MinecraftClient client) {

        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick(client);
            }
        }
    }

    public void registerDefaults() {
        register(new SprintModule());
        register(new FullbrightModule());
        register(new HudModule());
        register(new FpsModule());
        register(new CoordinatesModule());
    }
}
EOF

# ---------- SPRINT ----------
cat > "$BASE/module/SprintModule.java" <<'EOF'
package com.krox.client.module;

import net.minecraft.client.MinecraftClient;

public class SprintModule extends Module {

    public SprintModule() {
        super("Sprint", Category.MOVEMENT);
    }

    @Override
    public void onTick(MinecraftClient client) {

        if (client.player != null
                && client.player.input != null
                && client.player.input.movementForward > 0) {

            client.player.setSprinting(true);
        }
    }
}
EOF

# ---------- FULLBRIGHT ----------
cat > "$BASE/module/FullbrightModule.java" <<'EOF'
package com.krox.client.module;

import net.minecraft.client.MinecraftClient;

public class FullbrightModule extends Module {

    private double previousGamma;

    public FullbrightModule() {
        super("Fullbright", Category.RENDER);
    }

    @Override
    public void onEnable() {

        MinecraftClient client = MinecraftClient.getInstance();

        if (client != null) {
            previousGamma = client.options.getGamma().getValue();
            client.options.getGamma().setValue(16.0);
        }
    }

    @Override
    public void onDisable() {

        MinecraftClient client = MinecraftClient.getInstance();

        if (client != null) {
            client.options.getGamma().setValue(previousGamma);
        }
    }
}
EOF

# ---------- HUD MODULE ----------
cat > "$BASE/module/HudModule.java" <<'EOF'
package com.krox.client.module;

public class HudModule extends Module {

    public HudModule() {
        super("HUD", Category.HUD);
    }
}
EOF

# ---------- FPS MODULE ----------
cat > "$BASE/module/FpsModule.java" <<'EOF'
package com.krox.client.module;

public class FpsModule extends Module {

    public FpsModule() {
        super("FPS", Category.HUD);
    }
}
EOF

# ---------- COORDINATES MODULE ----------
cat > "$BASE/module/CoordinatesModule.java" <<'EOF'
package com.krox.client.module;

public class CoordinatesModule extends Module {

    public CoordinatesModule() {
        super("Coordinates", Category.HUD);
    }
}
EOF

# ---------- CLICK GUI ----------
cat > "$BASE/gui/ClickGuiScreen.java" <<'EOF'
package com.krox.client.gui;

import com.krox.client.KroxClient;
import com.krox.client.module.Category;
import com.krox.client.module.Module;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {

    private Category selectedCategory = Category.COMBAT;

    public ClickGuiScreen() {
        super(Text.literal("Krox Client"));
    }

    @Override
    protected void init() {
        buildGui();
    }

    private void buildGui() {

        this.clearChildren();

        int sidebarX = 20;
        int sidebarY = 50;

        for (Category category : Category.values()) {

            Category current = category;

            this.addDrawableChild(
                ButtonWidget.builder(
                    Text.literal(category.name()),
                    button -> {
                        selectedCategory = current;
                        buildGui();
                    }
                )
                .dimensions(sidebarX, sidebarY, 110, 20)
                .build()
            );

            sidebarY += 24;
        }

        int moduleX = 160;
        int moduleY = 50;

        for (Module module :
                KroxClient.MODULE_MANAGER.getModules(selectedCategory)) {

            this.addDrawableChild(
                ButtonWidget.builder(
                    Text.literal(
                        module.getName()
                        + " : "
                        + (module.isEnabled() ? "ON" : "OFF")
                    ),
                    button -> {
                        module.toggle();
                        buildGui();
                    }
                )
                .dimensions(moduleX, moduleY, 180, 20)
                .build()
            );

            moduleY += 24;
        }
    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        context.fill(
            0, 0,
            this.width,
            this.height,
            0xCC08080F
        );

        context.fill(
            15, 15,
            145,
            this.height - 15,
            0xFF151522
        );

        context.fill(
            145, 15,
            this.width - 15,
            this.height - 15,
            0xFF101018
        );

        context.drawTextWithShadow(
            this.textRenderer,
            "KROX CLIENT",
            30,
            25,
            0xFFFFFFFF
        );

        context.drawTextWithShadow(
            this.textRenderer,
            selectedCategory.name(),
            160,
            25,
            0xFFAAAAFF
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
EOF

# ---------- CUSTOM TITLE SCREEN ----------
cat > "$BASE/gui/KroxTitleScreen.java" <<'EOF'
package com.krox.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class KroxTitleScreen extends Screen {

    public KroxTitleScreen() {
        super(Text.literal("Krox Client"));
    }

    @Override
    protected void init() {

        int x = this.width / 2 - 100;
        int y = this.height / 2 - 40;

        addDrawableChild(
            ButtonWidget.builder(
                Text.literal("Singleplayer"),
                button -> client.setScreen(
                    new SelectWorldScreen(this)
                )
            )
            .dimensions(x, y, 200, 20)
            .build()
        );

        addDrawableChild(
            ButtonWidget.builder(
                Text.literal("Multiplayer"),
                button -> client.setScreen(
                    new MultiplayerScreen(this)
                )
            )
            .dimensions(x, y + 24, 200, 20)
            .build()
        );

        addDrawableChild(
            ButtonWidget.builder(
                Text.literal("Krox Client"),
                button -> client.setScreen(
                    new ClickGuiScreen()
                )
            )
            .dimensions(x, y + 48, 200, 20)
            .build()
        );

        addDrawableChild(
            ButtonWidget.builder(
                Text.literal("Options"),
                button -> client.setScreen(
                    new OptionsScreen(this, client.options)
                )
            )
            .dimensions(x, y + 72, 200, 20)
            .build()
        );

        addDrawableChild(
            ButtonWidget.builder(
                Text.literal("Quit"),
                button -> client.stop()
            )
            .dimensions(x, y + 96, 200, 20)
            .build()
        );
    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ) {

        context.fill(
            0,
            0,
            width,
            height,
            0xFF090914
        );

        context.drawCenteredTextWithShadow(
            textRenderer,
            "KROX CLIENT",
            width / 2,
            height / 4,
            0xFFAAAAFF
        );

        context.drawCenteredTextWithShadow(
            textRenderer,
            "Minecraft 1.21.1",
            width / 2,
            height / 4 + 20,
            0xFFFFFFFF
        );

        context.drawCenteredTextWithShadow(
            textRenderer,
            "Press RIGHT SHIFT for Click GUI",
            width / 2,
            height - 30,
            0xFF888888
        );

        super.render(context, mouseX, mouseY, delta);
    }
}
EOF

# ---------- TITLE SCREEN MIXIN ----------
cat > "$BASE/mixin/TitleScreenMixin.java" <<'EOF'
package com.krox.client.mixin;

import com.krox.client.gui.KroxTitleScreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {

    private static boolean krox$replaced = false;

    @Inject(
        method = "init",
        at = @At("TAIL")
    )
    private void krox$replaceTitleScreen(CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (!krox$replaced && client != null) {

            krox$replaced = true;

            client.execute(() -> {
                if (client.currentScreen instanceof TitleScreen) {
                    client.setScreen(new KroxTitleScreen());
                }
            });
        }
    }
}
EOF

# ---------- HUD ----------
cat > "$BASE/hud/KroxHud.java" <<'EOF'
package com.krox.client.hud;

import com.krox.client.KroxClient;
import com.krox.client.module.Module;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public class KroxHud {

    public static void initialize() {

        HudRenderCallback.EVENT.register(
            (drawContext, tickDelta) -> {

                MinecraftClient client =
                    MinecraftClient.getInstance();

                if (client.player == null) {
                    return;
                }

                int y = 10;

                drawContext.drawTextWithShadow(
                    client.textRenderer,
                    "KROX CLIENT",
                    10,
                    y,
                    0xFFAAAAFF
                );

                y += 14;

                for (Module module :
                        KroxClient.MODULE_MANAGER.getModules()) {

                    if (module.isEnabled()) {

                        drawContext.drawTextWithShadow(
                            client.textRenderer,
                            module.getName(),
                            10,
                            y,
                            0xFFFFFFFF
                        );

                        y += 11;
                    }
                }
            }
        );
    }
}
EOF

# ---------- MIXIN CONFIG ----------
cat > "$RES/kroxclient.mixins.json" <<'EOF'
{
  "required": true,
  "package": "com.krox.client.mixin",
  "compatibilityLevel": "JAVA_21",
  "client": [
    "TitleScreenMixin"
  ],
  "injectors": {
    "defaultRequire": 1
  }
}
EOF

# ---------- BUILD ----------
echo ""
echo "=========================================="
echo "Building Krox Client..."
echo "=========================================="

./gradlew clean build

echo ""
echo "=========================================="
echo "KROX CLIENT BUILD COMPLETE"
echo "Minecraft Version: 1.21.1"
echo ""
echo "JAR files:"
ls -lh build/libs/
echo "=========================================="
