package com.hypixel.hytale.server.core.event.events.player;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;

/**
 * Event fired when a player disconnects.
 */
public class PlayerDisconnectEvent extends PlayerRefEvent<Void> {
    public PlayerDisconnectEvent(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }
}
