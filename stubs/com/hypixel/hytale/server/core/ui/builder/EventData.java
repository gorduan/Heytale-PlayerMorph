package com.hypixel.hytale.server.core.ui.builder;

import javax.annotation.Nonnull;

public class EventData {
    public EventData() {}

    @Nonnull
    public static EventData of(@Nonnull String key, @Nonnull String value) { return new EventData(); }

    @Nonnull
    public static EventData empty() { return new EventData(); }

    @Nonnull
    public EventData append(@Nonnull String key, @Nonnull String value) { return this; }
}
