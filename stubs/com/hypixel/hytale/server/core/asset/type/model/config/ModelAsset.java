package com.hypixel.hytale.server.core.asset.type.model.config;

import com.hypixel.hytale.assetstore.JsonAsset;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Stub für ModelAsset - basierend auf dekompiliertem Server
 * Implementiert JsonAsset<String>
 */
public class ModelAsset implements JsonAsset<String> {

    protected String id;
    protected String model;
    protected String texture;

    @Nonnull
    public static DefaultAssetMap<String, ModelAsset> getAssetMap() {
        return new DefaultAssetMap<>();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getTexture() {
        return texture;
    }
}
