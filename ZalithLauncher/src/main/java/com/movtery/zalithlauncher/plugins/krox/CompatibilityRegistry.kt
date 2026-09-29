package com.movtery.zalithlauncher.plugins.krox

import org.json.JSONObject

/**
 * Central gate for Krox Client auto-activation.
 * Only 1.21.11 + Fabric instances receive krox-client-*.jar.
 */
object CompatibilityRegistry {
    private const val REQUIRED_MC = "1.21.11"
    const val KROX_JAR_PREFIX = "krox-client-"
    const val KROX_JAR_SUFFIX = ".jar"

    fun isCompatible(minecraftVersion: String?, loader: String?): Boolean {
        if (minecraftVersion == null || loader == null) return false
        return minecraftVersion.trim() == REQUIRED_MC && loader.trim().equals("fabric", ignoreCase = true)
    }

    fun kroxJarName(version: String): String = "krox-client-$version.jar"

    fun isKroxJar(name: String): Boolean = name.startsWith(KROX_JAR_PREFIX) && name.endsWith(KROX_JAR_SUFFIX)

    /** Resolve gameDir/mods safely; instance game dir varies. */
    fun resolveModsDir(gameDir: java.io.File): java.io.File = java.io.File(gameDir, "mods")
}
