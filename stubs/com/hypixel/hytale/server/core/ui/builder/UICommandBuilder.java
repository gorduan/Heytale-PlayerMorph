package com.hypixel.hytale.server.core.ui.builder;

import com.hypixel.hytale.server.core.ui.Value;
import javax.annotation.Nonnull;
import java.util.List;

/**
 * UICommandBuilder - Stub matching real Hytale API.
 * Has specific set() overloads, not generic.
 */
public class UICommandBuilder {
    public static final CustomUICommand[] EMPTY_COMMAND_ARRAY = new CustomUICommand[0];

    public UICommandBuilder() {}

    @Nonnull
    public UICommandBuilder clear(@Nonnull String selector) { return this; }

    @Nonnull
    public UICommandBuilder remove(@Nonnull String selector) { return this; }

    @Nonnull
    public UICommandBuilder append(@Nonnull String documentPath) { return this; }

    @Nonnull
    public UICommandBuilder append(@Nonnull String selector, @Nonnull String documentPath) { return this; }

    @Nonnull
    public UICommandBuilder appendInline(@Nonnull String selector, @Nonnull String document) { return this; }

    @Nonnull
    public UICommandBuilder insertBefore(@Nonnull String selector, @Nonnull String documentPath) { return this; }

    @Nonnull
    public UICommandBuilder insertBeforeInline(@Nonnull String selector, @Nonnull String document) { return this; }

    // Specific set() overloads - must match real API exactly
    @Nonnull
    public <T> UICommandBuilder set(@Nonnull String selector, @Nonnull Value<T> ref) { return this; }

    @Nonnull
    public UICommandBuilder setNull(@Nonnull String selector) { return this; }

    @Nonnull
    public UICommandBuilder set(@Nonnull String selector, @Nonnull String str) { return this; }

    @Nonnull
    public UICommandBuilder set(@Nonnull String selector, boolean b) { return this; }

    @Nonnull
    public UICommandBuilder set(@Nonnull String selector, float n) { return this; }

    @Nonnull
    public UICommandBuilder set(@Nonnull String selector, int n) { return this; }

    @Nonnull
    public UICommandBuilder set(@Nonnull String selector, double n) { return this; }

    @Nonnull
    public UICommandBuilder setObject(@Nonnull String selector, @Nonnull Object data) { return this; }

    @Nonnull
    public <T> UICommandBuilder set(@Nonnull String selector, @Nonnull T[] data) { return this; }

    @Nonnull
    public <T> UICommandBuilder set(@Nonnull String selector, @Nonnull List<T> data) { return this; }

    @Nonnull
    public CustomUICommand[] getCommands() { return EMPTY_COMMAND_ARRAY; }
}
