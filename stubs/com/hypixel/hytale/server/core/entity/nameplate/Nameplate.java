package com.hypixel.hytale.server.core.entity.nameplate;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

/**
 * Stub for Nameplate - the visual name displayed above entities.
 * Use removeComponent to hide, putComponent to show.
 */
public class Nameplate implements Component<EntityStore> {

    private String text = "";

    public Nameplate() {
    }

    public Nameplate(@Nonnull String text) {
        this.text = text;
    }

    @Nonnull
    public String getText() {
        return text;
    }

    public void setText(@Nonnull String text) {
        this.text = text;
    }

    public boolean consumeNetworkOutdated() {
        return false;
    }

    public static ComponentType<EntityStore, Nameplate> getComponentType() {
        return null;
    }

    @Override
    @Nonnull
    public Nameplate clone() {
        return new Nameplate(this.text);
    }
}
