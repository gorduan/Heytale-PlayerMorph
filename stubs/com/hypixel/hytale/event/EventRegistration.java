package com.hypixel.hytale.event;

import com.hypixel.hytale.registry.Registration;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;

/**
 * Registration handle for events.
 */
public class EventRegistration<KeyType, EventType extends IBaseEvent<KeyType>> extends Registration {
    @Nonnull
    protected final Class<EventType> eventClass;

    public EventRegistration(@Nonnull Class<EventType> eventClass, @Nonnull BooleanSupplier isEnabled, @Nonnull Runnable unregister) {
        super(isEnabled, unregister);
        this.eventClass = eventClass;
    }

    public EventRegistration(@Nonnull EventRegistration<KeyType, EventType> registration, @Nonnull BooleanSupplier isEnabled, @Nonnull Runnable unregister) {
        super(isEnabled, unregister);
        this.eventClass = registration.eventClass;
    }

    @Nonnull
    public Class<EventType> getEventClass() {
        return this.eventClass;
    }
}
