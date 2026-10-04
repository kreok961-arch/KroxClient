# ADR 0002 — Event bus: build a small custom one

**Status:** Accepted
**Date:** 2026-10-04

## Context

The spec (PRD §2.4) requires an event bus that is *separate* from Fabric's
callbacks, with:

- priorities `HIGHEST → HIGH → NORMAL → LOW → LOWEST`, ties broken by registration
  order;
- events carrying a timestamp, a source, and cancellability.

Fabric's own `Event`/`EventFactory` has no priority and no cancellation. Both are
load-bearing for this client:

- **Priority** — HUD elements are rendered by z-order, and an input mixin must be
  able to run before a module's own handler without either one knowing about the
  other.
- **Cancellation** — Bug 1 (input bleeding) and Bug 2 (screen routing) are both
  "this handler vetoed what the next one wanted to do". Without a cancellable
  event, every mixin has to thread a boolean through by hand.

## Decision

Build `com.krox.client.core.event` — five small files, no framework:

- `KroxEvent` — `timestamp`, `cancellable` (default **false**, `isCancellable()`),
  `cancel()`, `isCancelled()`.
- `EventPriority` — `HIGHEST(0), HIGH(1), NORMAL(2), LOW(3), LOWEST(4)`.
- `EventListener<E extends KroxEvent>` — functional interface, so modules
  register with a lambda instead of a named class per handler.
- `EventBus` — `CopyOnWriteArrayList` per event class; `post` stable-sorts a copy
  by priority (preserving insertion order within a tier) and invokes every
  listener.
- `KroxEvents` — the single instance plus a static `post` convenience.

Two non-obvious rules, both of which exist because of a real failure mode:

1. **Every listener call is wrapped in `try/catch(Throwable)`.** One module
   throwing must not abort the rest of a HUD render or a client tick. This is
   the same defensive shape already used by `KroxClient.onInitializeClient` and
   by every mixin injection body in this repo.
2. **`post` sorts a copy, not the live list.** Listeners register and deregister
   while the bus is dispatching (a module disabling itself from its own handler
   is the normal case). `CopyOnWriteArrayList` gives safe iteration; sorting a
   snapshot keeps the ordering stable without mutating shared state.

Fabric callbacks remain the *entry points*. A mixin does one thing: call
`KroxEvents.post(...)`. Everything downstream goes through our bus.

## Rationale

~6 files of ~30 lines each remove both problems Fabric cannot solve, and the
alternative — priorities and cancellation hand-threaded through every mixin
inject — spreads a bug-prone convention across the exact files the spec's mixin
policy says must stay minimal.

## Consequences

- Two event systems exist. The boundary is: Fabric API is how Minecraft calls
  us; our bus is how we call ourselves. No listener is ever registered against
  both for the same concern.
- `post` returns `void`; callers that need a veto read `isCancelled()` off the
  event instance they passed in. That is sufficient for all eight spec events.