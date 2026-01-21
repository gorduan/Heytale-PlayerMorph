package com.hypixel.hytale.assetstore.map;

import com.hypixel.hytale.assetstore.JsonAsset;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Stub für DefaultAssetMap - basierend auf dekompiliertem Server.
 * VERIFIZIERT: getAssetMap() gibt alle registrierten Assets zurück,
 * inklusive solcher von anderen Mods.
 */
public class DefaultAssetMap<K, T extends JsonAsset<K>> {

    @Nullable
    public T getAsset(K key) {
        return null;
    }

    /**
     * Gibt alle registrierten Assets als Map zurück.
     * Dies inkludiert sowohl Game-Assets als auch Assets von anderen Mods.
     */
    @Nonnull
    public Map<K, T> getAssetMap() {
        return Collections.emptyMap();
    }

    /**
     * Gibt alle registrierten Assets als Collection zurück.
     */
    @Nonnull
    public Collection<T> getAssets() {
        return getAssetMap().values();
    }

    public int getAssetCount() {
        return getAssetMap().size();
    }
}
