package com.hypixel.hytale.server.core.ui;

import javax.annotation.Nonnull;

public class Value<T> {
    private final T value;

    public Value(@Nonnull T value) { this.value = value; }

    @Nonnull
    public static <T> Value<T> ref(@Nonnull String documentPath, @Nonnull String valueName) {
        return new Value<>(null);
    }

    public T get() { return value; }
}
