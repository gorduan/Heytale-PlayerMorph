package com.hypixel.hytale.server.core.modules.entity.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class ModelComponent implements Component<EntityStore> {
    private final Model model;

    public static ComponentType<EntityStore, ModelComponent> getComponentType() {
        return new ComponentType<>();
    }

    public ModelComponent(@Nonnull Model model) {
        this.model = model;
    }

    public Model getModel() { return this.model; }

    @Override
    @Nonnull
    public Component<EntityStore> clone() { return new ModelComponent(this.model); }
}
