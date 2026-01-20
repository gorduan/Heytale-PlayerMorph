package com.hypixel.hytale.server.core;

import javax.annotation.Nonnull;

public class Message {
    protected Message() {}

    @Nonnull
    public static Message of(@Nonnull String text) { return new Message(); }

    @Nonnull
    public static Message raw(@Nonnull String text) { return new Message(); }

    @Nonnull
    public static Message translation(@Nonnull String key) { return new Message(); }

    @Nonnull
    public Message param(@Nonnull String key, @Nonnull String value) { return this; }

    @Nonnull
    public Message param(@Nonnull String key, int value) { return this; }

    @Nonnull
    public Message param(@Nonnull String key, @Nonnull Message message) { return this; }

    @Nonnull
    public Message insert(@Nonnull String text) { return this; }

    @Nonnull
    public Message insert(@Nonnull Message message) { return this; }

    @Nonnull
    public Message color(@Nonnull String hexColor) { return this; }
}
