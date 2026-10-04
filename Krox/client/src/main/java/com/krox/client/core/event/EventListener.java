package com.krox.client.core.event;

/** Functional so callers register a lambda instead of a named class per handler. */
@FunctionalInterface
public interface EventListener<E extends KroxEvent> {
   void onEvent(E event);
}