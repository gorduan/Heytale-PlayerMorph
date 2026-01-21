package com.gorduan.hytale.morphplayerto.data;

import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Stores the morph state for a player.
 * Contains original model information for restoration.
 */
public class MorphData {

    private final String originalModelId;
    private final PlayerSkinComponent originalSkinComponent;
    private final String originalNameplateText;
    private String currentModelId;
    private boolean nametagHidden;
    private final long morphTimestamp;

    public MorphData(@Nonnull String originalModelId, @Nullable PlayerSkinComponent skinComponent,
                     @Nullable String originalNameplateText) {
        this.originalModelId = originalModelId;
        this.originalSkinComponent = skinComponent;
        this.originalNameplateText = originalNameplateText;
        this.currentModelId = null;
        this.nametagHidden = false;
        this.morphTimestamp = System.currentTimeMillis();
    }

    @Nonnull
    public String getOriginalModelId() {
        return originalModelId;
    }

    @Nullable
    public PlayerSkinComponent getOriginalSkinComponent() {
        return originalSkinComponent;
    }

    @Nullable
    public String getCurrentModelId() {
        return currentModelId;
    }

    public void setCurrentModelId(@Nullable String currentModelId) {
        this.currentModelId = currentModelId;
    }

    public long getMorphTimestamp() {
        return morphTimestamp;
    }

    public long getMorphDuration() {
        return System.currentTimeMillis() - morphTimestamp;
    }

    @Nullable
    public String getOriginalNameplateText() {
        return originalNameplateText;
    }

    public boolean isNametagHidden() {
        return nametagHidden;
    }

    public void setNametagHidden(boolean nametagHidden) {
        this.nametagHidden = nametagHidden;
    }

    @Override
    public String toString() {
        return "MorphData{" +
                "originalModelId='" + originalModelId + '\'' +
                ", currentModelId='" + currentModelId + '\'' +
                ", nametagHidden=" + nametagHidden +
                ", morphTimestamp=" + morphTimestamp +
                '}';
    }
}
