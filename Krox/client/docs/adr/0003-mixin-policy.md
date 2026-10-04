# ADR 0003 — Mixin policy

**Status:** Accepted
**Date:** 2026-10-04

## Context

The spec's injection map (PRD §3.2) lists 13 mixins, and its policy section
(PRD §3.1) is emphatic: minimal, verified against the *actual* 1.21.11 code,
documented, regression-tested, fallback-safe. Forbidden: giant overwrites,
speculative injections, invented mappings, rendering mixins without an ADR.
The release gate is **zero unresolved Mixin errors**.

An unresolved mixin is not a compile error. It is a class that loads, matches
nothing, and is silently skipped — or worse, matches the wrong overload and
corrupts behaviour at runtime with no stack trace pointing at the mixin.

## Decision

**Five rules.**

### 1. Minimal surface. Decorate, never replace.

Inject into a method that exists. Prefer `@Inject` at `HEAD`/`TAIL` over
`@Overwrite`. A `TAIL` inject that paints pixels is safe precisely because the
vanilla method already ran.

`MixinTitleScreen` is the reference implementation of this rule: one
`@Inject(method = "render", at = @At("TAIL"))`, no `ci.cancel()`, so every
vanilla button — Realms included — and the splash text stay live. The previous
version swapped the screen from an `init` HEAD inject with `ci.cancel()`, which
is exactly what Pillar 1 forbids.

### 2. Verify the target against the real Yarn 1.21.11 mappings.

Not what the spec says, not what a 1.20 tutorial says — what
`1.21.11+build.6` actually declares. The build failing *is* the verification: a
method name that does not exist stops compilation; a return-type or descriptor
mismatch stops it too. `defaultRequire: 1` then catches the residual case at
runtime.

### 3. `injectors.defaultRequire: 1`.

A broken injection fails loudly at mod load instead of degrading into a silent
no-op. This is the spec's release gate, expressed as a config value rather than
a review step.

### 4. Fallback-safe. Every injection body degrades to vanilla.

Every injection body is wrapped in `try/catch(Throwable)` and then returns. A
rendering mixin that throws must leave the vanilla frame on screen, not a blank
one. This is already the shape of every mixin in `com.krox.client.mixin`.

### 5. Documented in `docs/adr/mixin-register.md`.

Target, method, injection point, fallback behaviour, risk rating. One row per
injection. A new mixin that is not in the register is not finished.

## Bug-eradication mixins (PRD §1.10)

The three bugs get one mixin each, at the narrowest point that fixes **every**
caller — not one mixin per symptom path:

- **Bug 1, input bleeding / stuck keys** — `MixinKeyboard` at `onKey` HEAD.
  On any screen close, `InputRestorer` calls `KeyBinding.unpressAll()` and clears
  the tracked key/mouse state. One injection covers every screen transition.
- **Bug 2, screen routing errors** — `MixinScreen` at `init` TAIL and `removed`
  HEAD, posting to `ScreenRouter`. The screen lifecycle *is* the routing boundary;
  injecting anywhere else means intercepting a decision the screen already made.
- **Bug 3, config crash loop** — needs **no mixin at all**. It is fixed in
  `ConfigManager`: safe Gson parse in a `try/catch`, corrupt file copied to
  `backups/`, defaults loaded, run continues. Adding a mixin for a file-parse
  bug would be the speculative-injection failure mode this policy forbids.

## Consequences

- Adding a rendering mixin requires an ADR (PRD §3.1). The register entry is
  necessary but not sufficient.
- The register grows one row per injection, not per class. A class with two
  injections has two rows.