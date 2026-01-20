package com.hypixel.hytale.server.core.entity.entities;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/**
 * VERIFIZIERT durch morphmod-1.1.0:
 * Player Klasse mit ECS Component und World-Zugriff.
 */
public class Player implements Component<EntityStore>, CommandSender {
    private static final ComponentType<EntityStore, Player> COMPONENT_TYPE = new ComponentType<>();

    public static ComponentType<EntityStore, Player> getComponentType() { return COMPONENT_TYPE; }

    @Nonnull
    public PageManager getPageManager() { return new PageManager(); }

    public PlayerRef getPlayerRef() { return new PlayerRef(); }

    public Ref<EntityStore> getReference() { return null; }

    /**
     * VERIFIZIERT durch morphmod-1.1.0:
     * Gibt die World zurück, in der sich der Spieler befindet.
     * Wird für world.execute() benötigt.
     */
    @Nullable
    public World getWorld() { return new World(); }

    /**
     * Gibt die UUID des Spielers zurück.
     */
    @Nonnull
    public UUID getUuid() { return UUID.randomUUID(); }

    @Override
    public void sendMessage(@Nonnull Message message) {}

    @Override
    public boolean hasPermission(String permission) { return true; }

    @Override
    public String getDisplayName() { return ""; }

    @Override
    @Nonnull
    public Player clone() { return new Player(); }
}
