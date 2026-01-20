package com.hypixel.hytale.server.core.universe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class PlayerLookup {
    private static final PlayerLookup INSTANCE = new PlayerLookup();

    public static PlayerLookup get() {
        return INSTANCE;
    }

    @Nullable
    public PlayerRef getPlayer(@Nonnull String name) {
        return null;
    }

    @Nonnull
    public Optional<PlayerRef> findPlayer(@Nonnull String name) {
        return Optional.ofNullable(getPlayer(name));
    }
}
