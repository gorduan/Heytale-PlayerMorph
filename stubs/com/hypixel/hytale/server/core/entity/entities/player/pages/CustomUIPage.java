package com.hypixel.hytale.server.core.entity.entities.player.pages;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public abstract class CustomUIPage {
    @Nonnull
    protected final PlayerRef playerRef;
    @Nonnull
    protected CustomPageLifetime lifetime;

    public CustomUIPage(@Nonnull PlayerRef playerRef, @Nonnull CustomPageLifetime lifetime) {
        this.playerRef = playerRef;
        this.lifetime = lifetime;
    }

    public abstract void build(@Nonnull Ref<EntityStore> ref,
                               @Nonnull UICommandBuilder commands,
                               @Nonnull UIEventBuilder events,
                               @Nonnull Store<EntityStore> store);

    protected void rebuild() {}

    protected void sendUpdate() {}

    protected void sendUpdate(UICommandBuilder commandBuilder) {}

    protected void sendUpdate(UICommandBuilder commandBuilder, boolean clear) {}

    protected void close() {
        Ref<EntityStore> ref = this.playerRef.getReference();
        if (ref != null) {
            Store<EntityStore> store = ref.getStore();
            Player player = store.getComponent(ref, Player.getComponentType());
            player.getPageManager().setPage(ref, store, Page.None);
        }
    }

    public void onDismiss(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {}
}
