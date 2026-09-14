#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$HOME/KroxClientBuild"

rm -rf "$PROJECT_DIR"

mkdir -p \
  "$PROJECT_DIR/src/main/java/com/krox/client/mixin" \
  "$PROJECT_DIR/src/main/java/com/krox/client/module/combat" \
  "$PROJECT_DIR/src/main/java/com/krox/client/module/utility" \
  "$PROJECT_DIR/src/main/java/com/krox/client/module/render" \
  "$PROJECT_DIR/src/main/java/com/krox/client/module/performance" \
  "$PROJECT_DIR/src/main/java/com/krox/client/gui" \
  "$PROJECT_DIR/src/main/resources/assets/kroxclient" \
  "$PROJECT_DIR/gradle/wrapper"

cat > "$PROJECT_DIR/settings.gradle" <<'EOF'
pluginManagement {
    repositories {
        maven { url = "https://maven.fabricmc.net/" }
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "KroxClient"
EOF

cat > "$PROJECT_DIR/gradle.properties" <<'EOF'
minecraft_version=1.21.1
yarn_mappings=1.21.1+build.3
loader_version=0.16.0
fabric_version=0.116.15+1.21.1
mod_version=1.0.0
maven_group=com.krox
archives_base_name=KroxClient
org.gradle.jvmargs=-Xmx2G
org.gradle.parallel=true
EOF

cat > "$PROJECT_DIR/build.gradle" <<'EOF'
plugins {
    id "fabric-loom" version "1.6-SNAPSHOT"
}

version = project.mod_version
group = project.maven_group

base {
    archivesName = project.archives_base_name
}

repositories {
    mavenCentral()
    maven { url = "https://maven.fabricmc.net/" }
}

dependencies {
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    mappings "net.fabricmc:yarn:${project.yarn_mappings}:v2"
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"
}

loom {
    splitEnvironmentSourceSets()

    mods {
        "kroxclient" {
            sourceSet sourceSets.main
        }
    }
}

processResources {
    inputs.property "version", project.version
    filesMatching("fabric.mod.json") {
        expand "version": project.version
    }
}

tasks.withType(JavaCompile).configureEach {
    options.encoding = "UTF-8"
    options.release = 17
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
EOF

cat > "$PROJECT_DIR/gradle/wrapper/gradle-wrapper.properties" <<'EOF'
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.8-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
EOF

curl -fsSL \
  "https://raw.githubusercontent.com/gradle/gradle/v8.8.0/gradle/wrapper/gradle-wrapper.jar" \
  -o "$PROJECT_DIR/gradle/wrapper/gradle-wrapper.jar"

curl -fsSL \
  "https://raw.githubusercontent.com/gradle/gradle/v8.8.0/gradlew" \
  -o "$PROJECT_DIR/gradlew"

curl -fsSL \
  "https://raw.githubusercontent.com/gradle/gradle/v8.8.0/gradlew.bat" \
  -o "$PROJECT_DIR/gradlew.bat"

chmod +x "$PROJECT_DIR/gradlew"

cat > "$PROJECT_DIR/src/main/resources/fabric.mod.json" <<'EOF'
{
  "schemaVersion": 1,
  "id": "kroxclient",
  "version": "${version}",
  "name": "Krox Client",
  "description": "A configurable Fabric client-side utility and HUD mod.",
  "authors": ["Krox"],
  "environment": "client",
  "entrypoints": {
    "client": ["com.krox.client.KroxClient"]
  },
  "mixins": ["kroxclient.mixins.json"],
  "depends": {
    "fabricloader": ">=0.16.0",
    "minecraft": "~1.21.1",
    "java": ">=17",
    "fabric-api": "*"
  },
  "icon": "assets/kroxclient/icon.png"
}
EOF

cat > "$PROJECT_DIR/src/main/resources/kroxclient.mixins.json" <<'EOF'
{
  "required": true,
  "package": "com.krox.client.mixin",
  "compatibilityLevel": "JAVA_17",
  "client": [
    "MinecraftClientMixin"
  ],
  "injectors": {
    "defaultRequire": 1
  }
}
EOF

printf '\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x06\x00\x00\x00\x1f\x15\xc4\x89\x00\x00\x00\rIDAT\x08\xd7c\xf8\xcf\xc0\xf0\x1f\x00\x05\x00\x01\xff\x89\x99\x3d\x1d\x00\x00\x00\x00IEND\xaeB\x60\x82' \
  > "$PROJECT_DIR/src/main/resources/assets/kroxclient/icon.png"

cat > "$PROJECT_DIR/src/main/java/com/krox/client/module/Category.java" <<'EOF'
package com.krox.client.module;

public enum Category {
    COMBAT, UTILITY, RENDER, PERFORMANCE
}
EOF

cat > "$PROJECT_DIR/src/main/java/com/krox/client/module/Module.java" <<'EOF'
package com.krox.client.module;

public abstract class Module {
    private final String name;
    private final Category category;
    private boolean enabled;

    protected Module(String name, Category category) {
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

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    protected void onEnable() {}
    protected void onDisable() {}
    public void tick() {}
}
EOF

cat > "$PROJECT_DIR/src/main/java/com/krox/client/module/ModuleManager.java" <<'EOF'
package com.krox.client.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {}

    public static <T extends Module> T register(T module) {
        MODULES.add(module);
        return module;
    }

    public static List<Module> all() {
        return Collections.unmodifiableList(MODULES);
    }

    public static void tick() {
        for (Module module : MODULES) {
            if (module.isEnabled()) {
                module.tick();
            }
        }
    }
}
EOF

cat > "$PROJECT_DIR/src/main/java/com/krox/client/module/combat/CombatModules.java" <<'EOF'
package com.krox.client.module.combat;

import com.krox.client.module.Category;
import com.krox.client.module.Module;

public final class CombatModules {
    private CombatModules() {}

    public static class ToggleSprint extends Module {
        public ToggleSprint() { super("ToggleSprint", Category.COMBAT); }
    }

    public static class ToggleSneak extends Module {
        public ToggleSneak() { super("ToggleSneak", Category.COMBAT); }
    }

    public static class ArmorStatus extends Module {
        public ArmorStatus() { super("ArmorStatus", Category.COMBAT); }
    }

    public static class PotionEffects extends Module {
        public PotionEffects() { super("PotionEffects", Category.COMBAT); }
    }

    public static class CustomCrosshair extends Module {
        public CustomCrosshair() { super("CustomCrosshair", Category.COMBAT); }
    }

    public static class HitboxToggle extends Module {
        public HitboxToggle() { super("HitboxToggle", Category.COMBAT); }
    }

    public static class CPSCounter extends Module {
        private int leftClicks;
        private int rightClicks;

        public CPSCounter() {
            super("CPSCounter", Category.COMBAT);
        }

        public void leftClick() { leftClicks++; }
        public void rightClick() { rightClicks++; }
        public int getLeftClicks() { return leftClicks; }
        public int getRightClicks() { return rightClicks; }
    }

    public static class PvPInfo extends Module {
        public PvPInfo() { super("PvPInfo", Category.COMBAT); }
    }

    public static class TotemCounter extends Module {
        public TotemCounter() { super("TotemCounter", Category.COMBAT); }
    }

    public static class TierTagger extends Module {
        public TierTagger() { super("TierTagger", Category.COMBAT); }
    }

    public static class ComboTracker extends Module {
        private int combo;

        public ComboTracker() {
            super("ComboTracker", Category.COMBAT);
        }

        public void increment() { combo++; }
        public void reset() { combo = 0; }
        public int getCombo() { return combo; }
    }
}
EOF

cat > "$PROJECT_DIR/src/main/java/com/krox/client/module/utility/UtilityModules.java" <<'EOF'
package com.krox.client.module.utility;

import com.krox.client.module.Category;
import com.krox.client.module.Module;

import java.util.ArrayList;
import java.util.List;

public final class UtilityModules {
    private UtilityModules() {}

    public static class HypixelSkyblockUtils extends Module {
        public HypixelSkyblockUtils() {
            super("HypixelSkyblockUtils", Category.UTILITY);
        }

        public boolean cropHitboxesEnabled() {
            return isEnabled();
        }

        public void notifySlayerEvent(String event) {
            if (isEnabled()) {
                System.out.println("[Krox] Slayer: " + event);
            }
        }
    }

    public static class SmartDisconnect extends Module {
        private boolean modalOpen;

        public SmartDisconnect() {
            super("SmartDisconnect", Category.UTILITY);
        }

        public void openModal() { modalOpen = true; }
        public void closeModal() { modalOpen = false; }
        public boolean isModalOpen() { return modalOpen; }
    }

    public static class ChatModifications extends Module {
        public ChatModifications() {
            super("ChatModifications", Category.UTILITY);
        }

        public String renderEmoji(String text) {
            return text
                .replace(":smile:", "☺")
                .replace(":heart:", "♥");
        }
    }

    public static class Waypoints extends Module {
        public record Waypoint(String name, int x, int y, int z) {}

        private final List<Waypoint> points = new ArrayList<>();

        public Waypoints() {
            super("Waypoints", Category.UTILITY);
        }

        public void add(String name, int x, int y, int z) {
            points.add(new Waypoint(name, x, y, z));
        }

        public List<Waypoint> getWaypoints() {
            return List.copyOf(points);
        }
    }
}
EOF

cat > "$PROJECT_DIR/src/main/java/com/krox/client/module/render/RenderModules.java" <<'EOF'
package com.krox.client.module.render;

import com.krox.client.module.Category;
import com.krox.client.module.Module;

public final class RenderModules {
    private RenderModules() {}

    public static class Skin3D extends Module {
        public Skin3D() { super("Skin3D", Category.RENDER); }
    }

    public static class ItemPhysics extends Module {
        public ItemPhysics() { super("ItemPhysics", Category.RENDER); }
    }

    public static class HurtCam extends Module {
        private float strength = 0.35f;

        public HurtCam() {
            super("HurtCam", Category.RENDER);
        }

        public float getStrength() {
            return strength;
        }

        public void setStrength(float value) {
            strength = Math.max(0f, Math.min(1f, value));
        }
    }

    public static class DamageTint extends Module {
        private float alpha = 0.35f;

        public DamageTint() {
            super("DamageTint", Category.RENDER);
        }

        public float getAlpha() {
            return alpha;
        }
    }

    public static class FogCustomizer extends Module {
        private float density = 1f;

        public FogCustomizer() {
            super("FogCustomizer", Category.RENDER);
        }

        public float getDensity() {
            return density;
        }

        public void setDensity(float density) {
            this.density = Math.max(0f, density);
        }
    }

    public static class WeatherChanger extends Module {
        private boolean clearWeather;

        public WeatherChanger() {
            super("WeatherChanger", Category.RENDER);
        }

        public boolean isClearWeather() {
            return clearWeather;
        }

        public void setClearWeather(boolean value) {
            clearWeather = value;
        }
    }

    public static class Snaplook extends Module {
        private boolean freelook;

        public Snaplook() {
            super("Snaplook", Category.RENDER);
        }

        public boolean isFreelook() {
            return freelook;
        }

        public void setFreelook(boolean value) {
            freelook = value;
        }
    }

    public static class MotionBlur extends Module {
        private float amount = 0.25f;

        public MotionBlur() {
            super("MotionBlur", Category.RENDER);
        }

        public float getAmount() {
            return amount;
        }
    }

    public static class BetterSounds extends Module {
        private float bass = 1f;
        private float mid = 1f;
        private float treble = 1f;

        public BetterSounds() {
            super("BetterSounds", Category.RENDER);
        }

        public void setEqualizer(float bass, float mid, float treble) {
            this.bass = bass;
            this.mid = mid;
            this.treble = treble;
        }

        public float[] getEqualizer() {
            return new float[] { bass, mid, treble };
        }
    }
}
EOF

cat > "$PROJECT_DIR/src/main/java/com/krox/client/module/performance/PerformanceModules.java" <<'EOF'
package com.krox.client.module.performance;

import com.krox.client.module.Category;
import com.krox.client.module.Module;

public final class PerformanceModules {
    private PerformanceModules() {}

    public interface SodiumHook {
        boolean isSodiumPresent();
    }

    public interface IrisHook {
        boolean isIrisPresent();
    }

    public interface MoreCullingHook {
        void requestCullRefresh();
    }

    public interface NvidiumHook {
        boolean supportsMeshCulling();
    }

    public static class MemoryGCTrigger extends Module {
        private long lastGc;
        private long intervalMillis = 300000L;

        public MemoryGCTrigger() {
            super("MemoryGCTrigger", Category.PERFORMANCE);
        }

        @Override
        public void tick() {
            long now = System.currentTimeMillis();

            if (now - lastGc >= intervalMillis) {
                Runtime.getRuntime().gc();
                lastGc = now;
            }
        }

        public void setIntervalMillis(long value) {
            intervalMillis = Math.max(30000L, value);
        }
    }
}
EOF

cat > "$PROJECT_DIR/src/main/java/com/krox/client/gui/KroxHud.java" <<'EOF'
package com.krox.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class KroxHud {
    public static final int OBSIDIAN = 0xBF08080A;
    public static final int CHARCOAL = 0xFF121216;
    public static final int CHARCOAL_LIGHT = 0xFF24242C;
    public static final int CRIMSON = 0xFFDC2626;

    private KroxHud() {}

    public static void register() {
        HudRenderCallback.EVENT.register((DrawContext context, net.minecraft.client.render.RenderTickCounter tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player == null || client.textRenderer == null) {
                return;
            }

            int x = 6;
            int y = 6;

            context.fill(x, y, x + 132, y + 38, OBSIDIAN);
            context.fill(x, y, x + 3, y + 38, CRIMSON);

            context.drawTextWithShadow(
                client.textRenderer,
                "KROX CLIENT",
                x + 9,
                y + 7,
                0xFFFFFFFF
            );

            context.drawTextWithShadow(
                client.textRenderer,
                "1.0.0 | Fabric 1.21.1",
                x + 9,
                y + 21,
                0xFFAAAAAA
            );
        });
    }
}
EOF

cat > "$PROJECT_DIR/src/main/java/com/krox/client/KroxClient.java" <<'EOF'
package com.krox.client;

import com.krox.client.gui.KroxHud;
import com.krox.client.module.ModuleManager;
import com.krox.client.module.combat.CombatModules;
import com.krox.client.module.performance.PerformanceModules;
import com.krox.client.module.render.RenderModules;
import com.krox.client.module.utility.UtilityModules;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class KroxClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModuleManager.register(new CombatModules.ToggleSprint());
        ModuleManager.register(new CombatModules.ToggleSneak());
        ModuleManager.register(new CombatModules.ArmorStatus());
        ModuleManager.register(new CombatModules.PotionEffects());
        ModuleManager.register(new CombatModules.CustomCrosshair());
        ModuleManager.register(new CombatModules.HitboxToggle());
        ModuleManager.register(new CombatModules.CPSCounter());
        ModuleManager.register(new CombatModules.PvPInfo());
        ModuleManager.register(new CombatModules.TotemCounter());
        ModuleManager.register(new CombatModules.TierTagger());
        ModuleManager.register(new CombatModules.ComboTracker());

        ModuleManager.register(new UtilityModules.HypixelSkyblockUtils());
        ModuleManager.register(new UtilityModules.SmartDisconnect());
        ModuleManager.register(new UtilityModules.ChatModifications());
        ModuleManager.register(new UtilityModules.Waypoints());

        ModuleManager.register(new RenderModules.Skin3D());
        ModuleManager.register(new RenderModules.ItemPhysics());
        ModuleManager.register(new RenderModules.HurtCam());
        ModuleManager.register(new RenderModules.DamageTint());
        ModuleManager.register(new RenderModules.FogCustomizer());
        ModuleManager.register(new RenderModules.WeatherChanger());
        ModuleManager.register(new RenderModules.Snaplook());
        ModuleManager.register(new RenderModules.MotionBlur());
        ModuleManager.register(new RenderModules.BetterSounds());

        ModuleManager.register(new PerformanceModules.MemoryGCTrigger());

        KroxHud.register();

        ClientTickEvents.END_CLIENT_TICK.register(
            client -> ModuleManager.tick()
        );

        System.out.println("[Krox Client] Initialized.");
    }
}
EOF

cat > "$PROJECT_DIR/src/main/java/com/krox/client/mixin/MinecraftClientMixin.java" <<'EOF'
package com.krox.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
}
EOF

cd "$PROJECT_DIR"

./gradlew build --no-daemon

JAR="$PROJECT_DIR/build/libs/KroxClient-1.0.0.jar"

if [ ! -f "$JAR" ]; then
    echo "Build completed, but KroxClient-1.0.0.jar was not found."
    find "$PROJECT_DIR/build/libs" -maxdepth 1 -type f -name "*.jar"
    exit 1
fi

echo
echo "=========================================="
echo "Build successful!"
echo "JAR created at:"
echo "$JAR"
echo "=========================================="
