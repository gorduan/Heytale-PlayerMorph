package com.hypixel.hytale.event;

import java.util.function.Consumer;
import javax.annotation.Nonnull;

/**
 * Registry for events.
 */
public class EventRegistry {
    public EventRegistry() {}

    @SuppressWarnings("unchecked")
    public <EventType extends IBaseEvent<Void>> EventRegistration<Void, EventType> register(
            @Nonnull Class<? super EventType> eventClass,
            @Nonnull Consumer<EventType> consumer) {
        return new EventRegistration<>((Class<EventType>) eventClass, () -> true, () -> {});
    }

    @SuppressWarnings("unchecked")
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> register(
            @Nonnull Class<? super EventType> eventClass,
            @Nonnull KeyType key,
            @Nonnull Consumer<EventType> consumer) {
        return new EventRegistration<>((Class<EventType>) eventClass, () -> true, () -> {});
    }

    @SuppressWarnings("unchecked")
    public <KeyType, EventType extends IBaseEvent<KeyType>> EventRegistration<KeyType, EventType> registerGlobal(
            @Nonnull Class<? super EventType> eventClass,
            @Nonnull Consumer<EventType> consumer) {
        return new EventRegistration<>((Class<EventType>) eventClass, () -> true, () -> {});
    }

    public void shutdown() {}
}
