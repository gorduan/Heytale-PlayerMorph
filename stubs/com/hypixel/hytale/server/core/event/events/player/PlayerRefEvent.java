package com.hypixel.hytale.server.core.event.events.player;

import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;

/**
 * Base class for player-related events.
 */
public abstract class PlayerRefEvent<KeyType> implements IEvent<KeyType> {
    @Nonnull
    protected final PlayerRef playerRef;

    public PlayerRefEvent(@Nonnull PlayerRef playerRef) {
        this.playerRef = playerRef;
    }

    @Nonnull
    public PlayerRef getPlayerRef() {
        return this.playerRef;
    }
}
