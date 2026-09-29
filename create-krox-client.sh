#!/usr/bin/env bash

set -e

ROOT="$(pwd)/client"

echo "Creating Krox Client base structure..."

# ============================================================
# CREATE DIRECTORIES
# ============================================================

mkdir -p \
  "$ROOT/launcher-integration" \
  "$ROOT/src/main/java/com/krox/client/config" \
  "$ROOT/src/main/java/com/krox/client/gui" \
  "$ROOT/src/main/java/com/krox/client/skin" \
  "$ROOT/src/main/java/com/krox/client/cape" \
  "$ROOT/src/main/java/com/krox/client/texture" \
  "$ROOT/src/main/java/com/krox/client/render" \
  "$ROOT/src/main/java/com/krox/client/mixin" \
  "$ROOT/src/main/java/com/krox/client/util" \
  "$ROOT/src/main/resources"

# ============================================================
# README
# ============================================================

cat > "$ROOT/README.md" <<'EOF'
# Krox Client Base

Fabric/Mixin starter module for KroxLauncherV2.

This module is intended as the foundation for:

- Fabric client entrypoint
- Mixin support
- Krox custom UI
- Skin management
- Cape management
- Texture management
- Rendering hooks
- Selective Optix feature porting
- Automatic launcher integration

## Intended pipeline

client source
→ Gradle build
→ remapped/distributable Fabric JAR
→ automatically packaged into KroxLauncher
→ prepared during launcher initialization
→ inactive until supported Minecraft + Fabric exists
→ automatically deployed into the correct instance mods directory
EOF

# ============================================================
# SETTINGS.GRADLE
# ============================================================

cat > "$ROOT/settings.gradle" <<'EOF'
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        maven {
            url = 'https://maven.fabricmc.net/'
        }
    }
}

rootProject.name = 'krox-client'
EOF

# ============================================================
# GRADLE PROPERTIES
# ============================================================

cat > "$ROOT/gradle.properties" <<'EOF'
minecraft_version=REPLACE_AFTER_SOURCE_INSPECTION
yarn_mappings=REPLACE_AFTER_SOURCE_INSPECTION
loader_version=REPLACE_AFTER_SOURCE_INSPECTION
fabric_api_version=REPLACE_AFTER_SOURCE_INSPECTION
loom_version=REPLACE_AFTER_SOURCE_INSPECTION

mod_version=0.1.0
maven_group=com.krox
archives_base_name=krox-client

org.gradle.jvmargs=-Xmx2G
org.gradle.parallel=true
EOF

# ============================================================
# BUILD.GRADLE
# ============================================================

cat > "$ROOT/build.gradle" <<'EOF'
plugins {
    id 'fabric-loom' version "${loom_version}"
}

version = project.mod_version
group = project.maven_group

base {
    archivesName = project.archives_base_name
}

repositories {
    mavenCentral()

    maven {
        url = 'https://maven.fabricmc.net/'
    }
}

dependencies {
    minecraft "com.mojang:minecraft:${project.minecraft_version}"

    mappings "net.fabricmc:yarn:${project.yarn_mappings}:v2"

    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"

    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_api_version}"
}

processResources {
    inputs.property "version", project.version

    filesMatching("fabric.mod.json") {
        expand "version": project.version
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }

    withSourcesJar()
}
EOF

# ============================================================
# GITIGNORE
# ============================================================

cat > "$ROOT/.gitignore" <<'EOF'
.gradle/
build/
.idea/
*.iml
EOF

# ============================================================
# FABRIC MOD JSON
# ============================================================

cat > "$ROOT/src/main/resources/fabric.mod.json" <<'EOF'
{
  "schemaVersion": 1,
  "id": "kroxclient",
  "version": "${version}",
  "name": "Krox Client",
  "description": "Krox custom Fabric client module.",
  "environment": "client",

  "entrypoints": {
    "client": [
      "com.krox.client.KroxClient"
    ]
  },

  "mixins": [
    "kroxclient.mixins.json"
  ],

  "depends": {
    "fabricloader": "*",
    "minecraft": "*"
  }
}
EOF

# ============================================================
# MIXIN CONFIG
# ============================================================

cat > "$ROOT/src/main/resources/kroxclient.mixins.json" <<'EOF'
{
  "required": true,
  "package": "com.krox.client.mixin",
  "compatibilityLevel": "JAVA_17",

  "client": [],

  "injectors": {
    "defaultRequire": 1
  }
}
EOF

# ============================================================
# COMPATIBILITY CONFIG
# ============================================================

cat > "$ROOT/src/main/resources/krox-client-compatibility.json" <<'EOF'
{
  "artifactId": "krox-client",

  "supportedTargets": [
    {
      "minecraftVersion": "1.21.11",
      "loader": "fabric",
      "enabled": true
    }
  ]
}
EOF

# ============================================================
# KROX CLIENT ENTRYPOINT
# ============================================================

cat > "$ROOT/src/main/java/com/krox/client/KroxClient.java" <<'EOF'
package com.krox.client;

import com.krox.client.cape.KroxCapeManager;
import com.krox.client.gui.KroxUiManager;
import com.krox.client.skin.KroxSkinManager;
import net.fabricmc.api.ClientModInitializer;

public final class KroxClient implements ClientModInitializer {

    public static final String MOD_ID = "kroxclient";

    private static final KroxSkinManager SKINS =
            new KroxSkinManager();

    private static final KroxCapeManager CAPES =
            new KroxCapeManager();

    private static final KroxUiManager UI =
            new KroxUiManager();

    @Override
    public void onInitializeClient() {
        UI.initialize();
    }

    public static KroxSkinManager skins() {
        return SKINS;
    }

    public static KroxCapeManager capes() {
        return CAPES;
    }
}
EOF

# ============================================================
# CLIENT CONFIG
# ============================================================

cat > "$ROOT/src/main/java/com/krox/client/config/KroxClientConfig.java" <<'EOF'
package com.krox.client.config;

public final class KroxClientConfig {

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
EOF

# ============================================================
# UI MANAGER
# ============================================================

cat > "$ROOT/src/main/java/com/krox/client/gui/KroxUiManager.java" <<'EOF'
package com.krox.client.gui;

public final class KroxUiManager {

    public void initialize() {
        // Future Krox UI initialization.
    }
}
EOF

# ============================================================
# SCREEN BRIDGE
# ============================================================

cat > "$ROOT/src/main/java/com/krox/client/gui/KroxScreenBridge.java" <<'EOF'
package com.krox.client.gui;

public final class KroxScreenBridge {

    // Future Minecraft-version-specific screen integration.
}
EOF

# ============================================================
# SKIN MANAGER
# ============================================================

cat > "$ROOT/src/main/java/com/krox/client/skin/KroxSkinManager.java" <<'EOF'
package com.krox.client.skin;

import java.util.Optional;

public final class KroxSkinManager {

    private String activeSkinId;

    public Optional<String> getActiveSkinId() {
        return Optional.ofNullable(activeSkinId);
    }

    public void setActiveSkinId(String activeSkinId) {
        this.activeSkinId = activeSkinId;
    }
}
EOF

# ============================================================
# CAPE MANAGER
# ============================================================

cat > "$ROOT/src/main/java/com/krox/client/cape/KroxCapeManager.java" <<'EOF'
package com.krox.client.cape;

import java.util.Optional;

public final class KroxCapeManager {

    private String activeCapeId;

    public Optional<String> getActiveCapeId() {
        return Optional.ofNullable(activeCapeId);
    }

    public void setActiveCapeId(String activeCapeId) {
        this.activeCapeId = activeCapeId;
    }
}
EOF

# ============================================================
# TEXTURE SERVICE
# ============================================================

cat > "$ROOT/src/main/java/com/krox/client/texture/KroxTextureService.java" <<'EOF'
package com.krox.client.texture;

public final class KroxTextureService {

    // Future texture registration and caching.
}
EOF

# ============================================================
# RENDER BRIDGE
# ============================================================

cat > "$ROOT/src/main/java/com/krox/client/render/KroxRenderBridge.java" <<'EOF'
package com.krox.client.render;

public final class KroxRenderBridge {

    // Future verified rendering hooks.
}
EOF

# ============================================================
# MIXIN PACKAGE
# ============================================================

cat > "$ROOT/src/main/java/com/krox/client/mixin/package-info.java" <<'EOF'
package com.krox.client.mixin;
EOF

# ============================================================
# PATH UTILITY
# ============================================================

cat > "$ROOT/src/main/java/com/krox/client/util/KroxClientPaths.java" <<'EOF'
package com.krox.client.util;

import java.nio.file.Path;

public final class KroxClientPaths {

    private KroxClientPaths() {
    }

    public static Path child(Path root, String name) {
        return root.resolve(name);
    }
}
EOF

# ============================================================
# LAUNCHER MANIFEST
# ============================================================

cat > "$ROOT/launcher-integration/krox-client-manifest.json" <<'EOF'
{
  "id": "krox-client",

  "artifact": "GENERATED_BY_GRADLE",

  "activation": {
    "minecraftVersions": [
      "1.21.11"
    ],

    "loaders": [
      "fabric"
    ]
  }
}
EOF

# ============================================================
# LAUNCHER INTEGRATION README
# ============================================================

cat > "$ROOT/launcher-integration/README.md" <<'EOF'
# Krox Client Launcher Integration

The final Krox Client JAR should be built automatically.

Rules:

1. Build the client with Gradle.
2. Use the final remapped/distributable Fabric JAR.
3. Package the artifact into the launcher.
4. Prepare it during launcher initialization.
5. Keep it inactive until a supported Minecraft + Fabric installation exists.
6. For Minecraft 1.21.11 + Fabric, deploy the Krox Client JAR into the correct instance mods directory.
7. Preserve unrelated user mods.
8. Replace only the previous Krox Client artifact during updates.
EOF

# ============================================================
# DONE
# ============================================================

echo ""
echo "========================================"
echo " KROX CLIENT BASE CREATED SUCCESSFULLY "
echo "========================================"
echo ""
echo "Location:"
echo "$ROOT"
echo ""

if command -v tree >/dev/null 2>&1; then
    tree "$ROOT"
else
    find "$ROOT" -print
fi