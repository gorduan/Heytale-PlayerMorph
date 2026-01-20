package com.hypixel.hytale.server.core.entity.entities.player.pages;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class InteractiveCustomUIPage<T> extends CustomUIPage {
    @Nonnull
    protected final BuilderCodec<T> eventDataCodec;

    public InteractiveCustomUIPage(@Nonnull PlayerRef playerRef,
                                    @Nonnull CustomPageLifetime lifetime,
                                    @Nonnull BuilderCodec<T> eventDataCodec) {
        super(playerRef, lifetime);
        this.eventDataCodec = eventDataCodec;
    }

    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull T data) {}

    protected void sendUpdate(@Nullable UICommandBuilder commandBuilder,
                              @Nullable UIEventBuilder eventBuilder,
                              boolean clear) {}
}
