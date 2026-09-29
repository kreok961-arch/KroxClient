# Adaptation Plan: KroxClient Integration

## Objective
Integrate the user-supplied JARs (AxolotlClient, Flashback, OptixClient) into KroxClient v16.0 while adhering to the "Decompile-First" rule and two-phase delivery model.

## Core Strategy
1. **Flashback Mod Integration**: Replace the legacy Replay functionality with Flashback.
2. **Modularization**: Utilize the existing `KroxClientBundler` for managing JAR deployment into `gameDir/mods`.
3. **Compatibility**: Implement strict compatibility checks using `CompatibilityRegistry` before deploying any JAR.

## Planned Steps

### Phase A: Foundation & Verification
- Establish `Krox/client/` foundation services (BlurService, ModuleRegistry, etc.).
- Implement skeleton UI shell (Title Screen, HUD Editor).
- Wire JAR-loading logic to recognize and test compatibility for the three JARs.
- Produce Phase A test JAR via CI/CD.

### Phase B: Full Feature Set
- Fully implement all 86 modules and premium features.
- Integrate Cosmetics (via AxolotlClient) and Replay (via Flashback).
- Finalize UI/UX polish and mobile/GLES optimization.
- Execute full QA and release v16.0.

## Integration Details
- **JAR Management**: The `KroxClientBundler` object ensures idempotent deployment and handles version/SHA markers to prevent unnecessary copies.
- **Compatibility**: All integrations must pass `CompatibilityRegistry.isCompatible()` checks before being moved to the active `modsDir`.
- **Cleanup**: Stale Krox-related JARs will be removed automatically during the deployment process.
