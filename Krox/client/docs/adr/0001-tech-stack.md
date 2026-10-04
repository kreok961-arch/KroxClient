# ADR 0001 — Tech stack identity: keep what ships, rename only the file

**Status:** Accepted
**Date:** 2026-10-04

## Context

The KROX CLIENT master prompt specifies a fresh identity for Phase 1:

| Spec (PRD §1.1 / Part 2) | What this repository actually ships |
|---|---|
| root package `com.krionix.kroxclient` | `com.krox.client` |
| mod id `kroxclient` | `krox` |
| assets at `assets/kroxclient/` | `assets/krox/` |
| Fabric API `0.141.6+1.21.11` | `0.141.3+1.21.11` |
| Loom `1.17-SNAPSHOT` | `1.17.20` |
| artifact `kroxclient-16.0.0.jar` | `krox-client-16.0.0.jar` |

The shipped values are not guesses. `gradle.properties` records where they came from:

> Pinned from the real KROXCLIENT jar's own manifest (Fabric-Loom-Version 1.17.20,
> Fabric-Gradle-Version 9.7.1, Fabric-Loader-Version 0.19.5, MC 1.21.11) so the
> recovered source rebuilds against the exact toolchain that produced it.

So the spec's values describe an intended *target*, and the repository is a
*recovery* of a build that already exists and already runs.

Two of the spec's pins are also not resolvable in this environment. The Gradle
cache holds exactly one Fabric API (`0.141.3+1.21.11`) and exactly one Loom
(`1.17.20`); `0.141.6` and `1.17-SNAPSHOT` would require network access to
resolve, and `1.17-SNAPSHOT` is a moving target that changes under a build.

## Decision

**Keep the existing identity. Change nothing a running install depends on. The
only rename is the artifact filename.**

Concretely:

1. Keep package root `com.krox.client`, mod id `krox`, asset root `assets/krox/`,
   and mixin package `com.krox.client.mixin`.
2. Set `archives_base_name=kroxclient` in `gradle.properties` so the build emits
   `kroxclient-16.0.0.jar` — the filename the spec asks for.
3. Hold `fabric_api_version=0.141.3+1.21.11`. Same MC version, same Yarn
   mappings, patch-level API bump; not load-bearing for anything this client
   uses. Revisit when an online build can resolve `0.141.6` and the build still
   passes.
4. Hold `loom_version=1.17.20` rather than `1.17-SNAPSHOT`. The snapshot moves
   under you; `1.17.20` is the version that produced the reference jar.

## Rationale

Renaming the package root touches 90+ module classes, every import statement,
`fabric.mod.json`, `krox.mixins.json`, and every resource path in
`src/main/resources/assets/`. A single missed reference fails the build or — far
worse — fails *silently* at runtime, because a mixin class whose `@Mixin` target
does not resolve is exactly the "unresolved Mixin error" the spec makes a
release gate.

Renaming the mod id additionally breaks every existing install's saved
`config/krox/` ids and keybind ids. That is user data, and Pillar 4's whole
promise is that saved HUD state survives.

A filename is free. Nobody's install references the jar name, and the spec's
deliverable list only asks for the filename.

## Consequences

- Accepted cost: the delivered jar is named per spec but its `fabric.mod.json`
  says `"id": "krox"`. Anyone reading the manifest learns the mod is `krox`.
  That is consistent with the existing install and every existing config path.
- Deferred: if the product ever wants `kroxclient` as the real mod id, that is a
  separate ADR with a config-migration story attached. Not part of Phase 1.