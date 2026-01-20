package com.hypixel.hytale.server.core.modules.entity.player;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.protocol.PlayerSkin;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class PlayerSkinComponent implements Component<EntityStore> {
    public static ComponentType<EntityStore, PlayerSkinComponent> getComponentType() {
        return new ComponentType<>();
    }

    @Nonnull
    public PlayerSkin getPlayerSkin() { return new PlayerSkin(); }

    public void setNetworkOutdated() {}

    @Override
    @Nonnull
    public Component<EntityStore> clone() { return new PlayerSkinComponent(); }
}
