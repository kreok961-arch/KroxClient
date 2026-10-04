# ADR 0005 — Config persistence: split the files, keep the mechanism

**Status:** Accepted
**Date:** 2026-10-04

## Context

The spec (PRD Part 4) wants a different storage shape from what ships:

| Spec | Currently ships |
|---|---|
| `modules.json` | one `config.json` |
| `layout.json` | — |
| `settings.json` | — |
| `version.json` (schemaVersion, lastMigrated, clientVersion) | — |
| `backups/<name>_<timestamp>.json`, retain 10 | `config.json.broken`, retain 1 |
| debounced 2s async save | save on demand, synchronous |
| per-file failure containment | whole-file `try/catch` → defaults |

What already exists in `ConfigManager` is worth keeping: it resolves its paths
lazily rather than in a static initializer (because
`FabricLoader.getConfigDir()` dereferences a null field until a game is present,
which is what killed out-of-game self-checks with
`ExceptionInInitializerError`), it writes `.tmp` then `ATOMIC_MOVE`, and every
typed getter falls back to a default.

## Decision

**Split the files. Keep the mechanism.**

Reuse the existing lazy-path resolution, `.tmp` + `ATOMIC_MOVE` write, and
safe-getter pattern verbatim. Change only the *storage shape*.

- `modules.json` — module enable state.
- `layout.json` — HUD element x/y/scale/anchor, keyed by element id.
- `settings.json` — every setting value.
- `version.json` — `schemaVersion`, `lastMigrated`, `clientVersion`.
- `backups/<filename>_<timestamp>.json` — retain the last 10, notify the user.

**Load:** create-if-missing → parse each file in its own `try/catch` → validate →
migrate if the schema is older → apply → fire `ConfigReloadEvent`.

**Failure is contained per file, not per run.** One unparseable file logs,
notifies, loads defaults *for that file only*, and the run continues. A corrupt
`settings.json` must not cost the user their HUD layout.

**Save:** `markDirty()` → 2s debounce → write `.tmp` → verify → atomic move →
update `version.json`.

**Migration:** compare `schemaVersion` on load. An older schema runs through a
`ConfigMigrator` chain (`v<N> → v<N+1>`). A schema *newer* than the client is
logged and parsed anyway — a downgrade must not destroy a newer install's data.

**Migration of the old file:** an existing `config.json` is read in, written to
the new shape, and copied into `backups/`. It is never deleted.

`ConfigManager` keeps its public typed getters (`getBool`, `getInt`, `getDouble`,
`getString`, `set`), so no module changes to accommodate this.

## Rationale

The split is not cosmetic — it is what makes Pillar 4 work. If layout shared a
blob with module state, a corrupt `settings.json` would reset the HUD, which is
precisely the "position loss" Pillar 4 forbids. One corrupt file must be able to
cost exactly one file.

The debounce is not premature. `NumberSetting.set` fires on every set, and a
slider drag is a set per mouse-move. Without the debounce, dragging one slider
is dozens of disk writes a second. Two seconds is the spec's number and it is
the right order of magnitude for "the user stopped dragging".

## Consequences

- Four files to reason about instead of one, bought with per-file failure
  containment and a layout that survives a settings corruption.
- `ConfigManager` grows a debounce scheduler. It uses the existing
  `KroxScheduler` rather than adding a thread pool.