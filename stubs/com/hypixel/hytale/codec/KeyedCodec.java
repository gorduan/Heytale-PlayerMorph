package com.hypixel.hytale.codec;

import javax.annotation.Nonnull;

public class KeyedCodec<T> {
    private final String key;
    private final Codec<T> codec;

    public KeyedCodec(@Nonnull String key, @Nonnull Codec<T> codec) {
        this.key = key;
        this.codec = codec;
    }

    @Nonnull
    public String getKey() { return key; }

    @Nonnull
    public Codec<T> getChildCodec() { return codec; }
}
