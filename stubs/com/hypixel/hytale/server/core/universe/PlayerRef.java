package com.hypixel.hytale.server.core.universe;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * VERIFIZIERT durch morphmod-1.1.0:
 * PlayerRef ist auch ein Component und kann aus dem Store geholt werden.
 */
public class PlayerRef implements Component<EntityStore> {

    private static final ComponentType<EntityStore, PlayerRef> COMPONENT_TYPE = new ComponentType<>();

    /**
     * VERIFIZIERT durch morphmod-1.1.0:
     * PlayerRef kann als Component aus dem Store geholt werden.
     */
    @Nonnull
    public static ComponentType<EntityStore, PlayerRef> getComponentType() {
        return COMPONENT_TYPE;
    }

    @Nullable
    public Ref<EntityStore> getReference() { return null; }

    @Nullable
    public Player getPlayer() { return null; }

    @Nonnull
    public String getUsername() { return ""; }

    @Nonnull
    public UUID getUuid() { return UUID.randomUUID(); }

    public void sendMessage(@Nonnull Message message) {}

    @Nonnull
    public PageManager getPageManager() { return new PageManager(); }

    @Override
    @Nonnull
    public PlayerRef clone() { return new PlayerRef(); }
}
