# ADR 0006 — The waypoint system lives in `com.krox.client.waypoint`

- **Status:** Accepted
- **Date:** 2026-10-04
- **Depends on:** [0001](0001-tech-stack.md).

## Context

The Native Waypoint System spec (Parts 1–13, 55 files) puts the new code in
`com.krionix.kroxclient.waypoint`, and one title line of that document calls the
authoring target `OPENCLAUD E`.

ADR 0001 already Accepted the opposite for the shipping client, and it is not a
theoretical preference — it was decided from real breakage:

- Mod id is `krox`, so `fabric.mod.json` declares `"id": "krox"` and the entrypoint is
  `com.krox.client.KroxClient`.
- `krox.mixins.json` declares `"package": "com.krox.client.mixin"`. Every mixin class is
  resolved through that string at runtime.
- Every asset path is `assets/krox/...`.
- The persisted config directory is `config/krox/`, already populated by users of the
  existing build.

## Decision

The waypoint system ships in **`com.krox.client.waypoint`**, on the existing root.

The spec's two package references are treated as authoring-template artifacts from a
different naming pass, not as requirements. `krox` is the shipped mod id everywhere
that is externally visible; a second root spelling would make exactly one of the two
wrong.

## Alternatives rejected

| Option | Why not |
| --- | --- |
| `com.krionix.kroxclient.waypoint` | Mod id stays `krox`, so `assets/krox/` and `com.krox.client.KroxClient` disagree with the source root. Touches 90+ existing classes, `fabric.mod.json`, `krox.mixins.json`, and every asset path, and renames `config/krox/` for existing users. |
| Rename the whole client to `krionix.kroxclient` | Same blast radius as above. The mixin release gate (`injectors.defaultRequire: 1`) turns any single missed rename into a crash at launch, and the payoff is a spelling change. |
| Split package — models in `com.krionix...`, render in `com.krox.client...` | Two roots for one feature. The import graph becomes harder to reason about than either single answer. |

## Consequences

- New waypoint files add one package: `com.krox.client.waypoint`.
- Mixins stay in `com.krox.client.mixin` and keep the `Waypoint*Mixin` names, so
  `krox.mixins.json` gains entries without touching its `package` string.
- The category is `ModuleCategory.WAYPOINTS`, with the violet accent `#8B5CF6` carried
  by the waypoint UI code rather than by the enum, which stores only display names.

```ponytail: one package rename would move 90+ classes, rewrite fabric.mod.json,
krox.mixins.json and every asset path, and rename config/krox/ under existing users.
If the krionix root is ever adopted it needs its own ADR with a config-migration story
and a mixin-registry audit, not a find-and-replace.
```

## Revisit when

The mod id itself changes away from `krox`. That is the only trigger that makes this
decision wrong, and it should be decided as an identity change, not a package change.
