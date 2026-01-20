package com.hypixel.hytale.server.core.ui.builder;

import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import javax.annotation.Nonnull;

public class UIEventBuilder {
    public static final CustomUIEventBinding[] EMPTY_EVENT_BINDING_ARRAY = new CustomUIEventBinding[0];

    public UIEventBuilder() {}

    @Nonnull
    public UIEventBuilder addEventBinding(@Nonnull CustomUIEventBindingType type,
                                           @Nonnull String selector,
                                           @Nonnull EventData data,
                                           boolean stopPropagation) { return this; }

    @Nonnull
    public CustomUIEventBinding[] getEvents() { return EMPTY_EVENT_BINDING_ARRAY; }
}
