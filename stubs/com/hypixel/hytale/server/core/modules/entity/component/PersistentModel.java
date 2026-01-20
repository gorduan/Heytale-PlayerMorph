package com.hypixel.hytale.server.core.modules.entity.component;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/**
 * VERIFIZIERT durch HytaleServer-decompiled PersistentModel.java:
 * Component für persistente Model-Speicherung über Sessions hinweg.
 */
public class PersistentModel implements Component<EntityStore> {
    @Nonnull
    public static final BuilderCodec<PersistentModel> CODEC = null; // Stub - echte Implementation in Server

    private Model.ModelReference modelReference;

    private PersistentModel() {}

    public PersistentModel(@Nonnull Model.ModelReference modelReference) {
        this.modelReference = modelReference;
    }

    @Nonnull
    public static ComponentType<EntityStore, PersistentModel> getComponentType() {
        return new ComponentType<>();
    }

    @Nonnull
    public Model.ModelReference getModelReference() {
        return this.modelReference;
    }

    public void setModelReference(@Nonnull Model.ModelReference modelReference) {
        this.modelReference = modelReference;
    }

    @Override
    @Nonnull
    public Component<EntityStore> clone() {
        PersistentModel clone = new PersistentModel();
        clone.modelReference = this.modelReference;
        return clone;
    }
}
