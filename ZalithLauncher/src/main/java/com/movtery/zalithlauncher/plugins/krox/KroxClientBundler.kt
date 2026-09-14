package com.movtery.zalithlauncher.plugins.krox

import android.content.Context
import java.io.File
import java.security.MessageDigest

/**
 * Bundles krox-client.jar from assets/krox and auto-activates into compatible instances.
 * Idempotent: version + sha256 marker prevents re-copy. Only touches krox-client-*.jar.
 * Lifecycle: BUNDLED -> PREPARED -> WAITING -> DETECTED -> DEPLOYED -> VERIFIED
 */
object KroxClientBundler {
    const val ASSET_PATH = "krox/krox-client.jar"
    const val MARKER_NAME = ".krox-client.marker"
    const val VERSION_FALLBACK = "1.0.0"
    @Volatile var cachedPrepared: File? = null
        private set

    enum class State { BUNDLED, PREPARED, WAITING, DETECTED, DEPLOYED, VERIFIED }

    fun sha256(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(bytes).joinToString("") { "%02x".format(it) }
    }

    fun readMarker(modsDir: File): Pair<String,String>? {
        val f = File(modsDir, MARKER_NAME)
        if (!f.exists()) return null
        return try {
            val t = f.readText().trim().split(":")
            if (t.size >= 2) t[0] to t[1] else null
        } catch (_: Exception) { null }
    }

    fun writeMarker(modsDir: File, version: String, sha: String) {
        try { File(modsDir, MARKER_NAME).writeText("$version:$sha") } catch (_: Exception) {}
    }

    /** Copy from assets to internal prepared file (PREPARED). Returns prepared file or null. */
    fun getPreparedFile(context: Context, version: String = VERSION_FALLBACK): File {
        cachedPrepared?.takeIf { it.exists() }?.let { return it }
        val f = File(context.filesDir, "krox/krox-client-$version.jar")
        if (f.exists()) { cachedPrepared = f; return f }
        return f
    }

    fun prepareFromAssets(context: Context, version: String = VERSION_FALLBACK): File? {
        return try {
            val bytes = context.assets.open(ASSET_PATH).readBytes()
            if (bytes.isEmpty()) return null
            val out = File(context.filesDir, "krox/krox-client-$version.jar")
            out.parentFile?.mkdirs()
            // idempotent: skip if same sha
            val sha = sha256(bytes)
            if (out.exists() && sha256(out.readBytes()) == sha) { cachedPrepared = out; return out }
            out.writeBytes(bytes)
            cachedPrepared = out
            out
        } catch (_: Exception) { null }
    }

    /**
     * Deploy to gameDir/mods if compatible. Only replaces krox-client-*.jar, never other mods.
     * Returns state.
     */
    fun deployIfCompatible(gameDir: File, minecraftVersion: String?, loader: String?, preparedJar: File?, version: String = VERSION_FALLBACK): State {
        if (preparedJar == null || !preparedJar.exists()) return State.BUNDLED
        if (!CompatibilityRegistry.isCompatible(minecraftVersion, loader)) return State.WAITING
        val modsDir = CompatibilityRegistry.resolveModsDir(gameDir)
        modsDir.mkdirs()
        val sha = try { sha256(preparedJar.readBytes()) } catch (_: Exception) { return State.DETECTED }
        val marker = readMarker(modsDir)
        if (marker != null && marker.first == version && marker.second == sha) return State.VERIFIED
        // remove stale krox jars (only krox-client-*.jar)
        modsDir.listFiles()?.forEach { f ->
            if (CompatibilityRegistry.isKroxJar(f.name) && f.name != CompatibilityRegistry.kroxJarName(version)) {
                try { f.delete() } catch (_: Exception) {}
            }
        }
        val target = File(modsDir, CompatibilityRegistry.kroxJarName(version))
        // idempotent: skip copy if same sha
        if (target.exists()) {
            try { if (sha256(target.readBytes()) == sha) { writeMarker(modsDir, version, sha); return State.VERIFIED } } catch (_: Exception) {}
        }
        return try {
            preparedJar.copyTo(target, overwrite = true)
            writeMarker(modsDir, version, sha)
            State.DEPLOYED
        } catch (_: Exception) { State.DETECTED }
    }

    /** Remove only krox jars (user requested uninstall/cleanup). */
    fun removeKroxJars(gameDir: File) {
        val modsDir = CompatibilityRegistry.resolveModsDir(gameDir)
        modsDir.listFiles()?.forEach { f -> if (CompatibilityRegistry.isKroxJar(f.name)) try { f.delete() } catch (_: Exception) {} }
        try { File(modsDir, MARKER_NAME).delete() } catch (_: Exception) {}
    }
}
