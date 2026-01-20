package com.gorduan.hytale.playermorphtomob;

import com.gorduan.hytale.playermorphtomob.data.MorphData;
import com.gorduan.hytale.playermorphtomob.storage.MorphStorage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.cosmetics.CosmeticsModule;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

/**
 * Manages morph operations for players.
 * Handles applying mob models to players, resetting to original models,
 * and persisting morph state across sessions.
 */
public class MorphManager {

    private static final HytaleLogger LOGGER = HytaleLogger.get("MorphManager");
    private static MorphManager instance;

    private final Map<String, MorphData> activeMorphs = new HashMap<>();
    private MorphStorage storage;

    // Cached list of dynamically loaded mob models
    private List<String> cachedMobList = null;
    private long lastCacheRefresh = 0;
    private static final long CACHE_REFRESH_INTERVAL_MS = 60000; // 1 minute cache

    // Fallback mob list for when dynamic loading fails
    private static final List<String> FALLBACK_MOBS = Arrays.asList(
            "Trork", "Skeleton", "Wolf_Black", "Spider", "Goblin"
    );

    private MorphManager() {}

    public static MorphManager getInstance() {
        if (instance == null) {
            instance = new MorphManager();
        }
        return instance;
    }

    /**
     * Initializes the MorphManager with persistence storage.
     *
     * @param dataFolder The folder for plugin data
     */
    public void initialize(@Nonnull Path dataFolder) {
        this.storage = new MorphStorage(dataFolder);
        LOGGER.at(Level.INFO).log("MorphManager initialized with %d stored morphs", storage.getStoredMorphCount());
    }

    /**
     * Applies a morph to a player by replacing their model with a mob model.
     * Creates a unit-scale model from the specified ModelAsset.
     *
     * @param playerRef The player reference
     * @param modelId   The model asset ID to morph into
     * @return true if morph was applied successfully
     */
    public boolean applyMorph(@Nonnull PlayerRef playerRef, @Nonnull String modelId) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            LOGGER.at(Level.WARNING).log("Cannot morph: Invalid player reference");
            return false;
        }

        Store<EntityStore> store = ref.getStore();
        String playerName = playerRef.getUsername();

        // Get current model component
        ModelComponent currentModel = store.getComponent(ref, ModelComponent.getComponentType());
        if (currentModel == null) {
            LOGGER.at(Level.WARNING).log("Cannot morph: ModelComponent not found for player %s", playerName);
            return false;
        }

        // Get player skin component for later reset
        PlayerSkinComponent skinComponent = store.getComponent(ref, PlayerSkinComponent.getComponentType());

        // Store original data if not already morphed
        if (!activeMorphs.containsKey(playerName)) {
            MorphData morphData = new MorphData(
                    currentModel.getModel().getModelAssetId(),
                    skinComponent
            );
            activeMorphs.put(playerName, morphData);
        }

        // Get the model asset from registry
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(modelId);
        if (modelAsset == null) {
            LOGGER.at(Level.WARNING).log("Cannot morph: Model asset '%s' not found", modelId);
            return false;
        }

        // Create unit-scale model (preserves original model proportions)
        Model newModel = Model.createUnitScaleModel(modelAsset);
        if (newModel == null) {
            LOGGER.at(Level.WARNING).log("Cannot morph: Failed to create model for '%s'", modelId);
            return false;
        }

        // Apply the new model via ECS
        store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(newModel));

        // Update morph data with current model
        MorphData morphData = activeMorphs.get(playerName);
        morphData.setCurrentModelId(modelId);

        // Persist morph to storage
        if (storage != null) {
            storage.saveMorph(playerName, modelId);
        }

        LOGGER.at(Level.INFO).log("Applied morph to player %s: %s", playerName, modelId);
        return true;
    }

    /**
     * Resets a player's morph back to their original player model.
     * Uses CosmeticsModule to create a new model from the player's skin,
     * then applies it via putComponent (not removeComponent).
     *
     * @param playerRef The player reference
     * @return true if reset was successful
     */
    public boolean resetMorph(@Nonnull PlayerRef playerRef) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null || !ref.isValid()) {
            LOGGER.at(Level.WARNING).log("Cannot reset morph: Invalid player reference");
            return false;
        }

        Store<EntityStore> store = ref.getStore();
        String playerName = playerRef.getUsername();

        MorphData morphData = activeMorphs.get(playerName);
        if (morphData == null) {
            LOGGER.at(Level.FINE).log("Player %s is not morphed", playerName);
            return false;
        }

        // Get PlayerSkinComponent for model reset
        PlayerSkinComponent skinComponent = store.getComponent(ref, PlayerSkinComponent.getComponentType());
        if (skinComponent == null) {
            LOGGER.at(Level.WARNING).log("Cannot reset morph: PlayerSkinComponent not found for %s", playerName);
            return false;
        }

        // Create new player model from skin using CosmeticsModule
        CosmeticsModule cosmeticsModule = CosmeticsModule.get();
        Model newModel = cosmeticsModule.createModel(skinComponent.getPlayerSkin());
        if (newModel == null) {
            LOGGER.at(Level.WARNING).log("Cannot reset morph: Failed to create player model for %s", playerName);
            return false;
        }

        // Apply the player model via ECS
        store.putComponent(ref, ModelComponent.getComponentType(), new ModelComponent(newModel));

        // Mark skin as outdated for network sync to other clients
        skinComponent.setNetworkOutdated();

        activeMorphs.remove(playerName);

        // Remove from persistent storage
        if (storage != null) {
            storage.removeMorph(playerName);
        }

        LOGGER.at(Level.INFO).log("Reset morph for player %s", playerName);
        return true;
    }

    /**
     * Checks if a player is currently morphed.
     */
    public boolean isMorphed(@Nonnull String playerName) {
        return activeMorphs.containsKey(playerName);
    }

    /**
     * Gets the morph data for a player.
     */
    @Nullable
    public MorphData getMorphData(@Nonnull String playerName) {
        return activeMorphs.get(playerName);
    }

    /**
     * Forces removal of morph data without restoring the model.
     * Used for player disconnects - keeps persisted morph for next session.
     */
    public void forceRemove(@Nonnull String playerName) {
        activeMorphs.remove(playerName);
        LOGGER.at(Level.FINE).log("Force removed morph data for player %s (persisted morph kept)", playerName);
    }

    /**
     * Checks if a player has a saved morph and restores it.
     * Called on PlayerConnect to restore morphs across sessions.
     *
     * @param playerRef The player
     * @return true if a morph was restored
     */
    public boolean restoreSavedMorph(@Nonnull PlayerRef playerRef) {
        if (storage == null) {
            return false;
        }

        String playerName = playerRef.getUsername();
        String savedModelId = storage.getSavedModelId(playerName);

        if (savedModelId == null) {
            LOGGER.at(Level.FINE).log("No saved morph for player %s", playerName);
            return false;
        }

        LOGGER.at(Level.INFO).log("Restoring saved morph for %s: %s", playerName, savedModelId);
        return applyMorph(playerRef, savedModelId);
    }

    /**
     * Checks if a player has a saved morph in persistent storage.
     */
    public boolean hasSavedMorph(@Nonnull String playerName) {
        return storage != null && storage.hasSavedMorph(playerName);
    }

    /**
     * Returns a list of all available mob model IDs.
     * Dynamically loads models from the game's ModelAsset registry.
     * Results are cached for 1 minute to improve performance.
     */
    public List<String> getAvailableMobs() {
        long now = System.currentTimeMillis();

        // Return cached list if still valid
        if (cachedMobList != null && (now - lastCacheRefresh) < CACHE_REFRESH_INTERVAL_MS) {
            return Collections.unmodifiableList(cachedMobList);
        }

        // Refresh the cache from ModelAsset registry
        try {
            Set<String> modelIds = ModelAsset.getAssetMap().getKeys();

            if (modelIds.isEmpty()) {
                LOGGER.at(Level.WARNING).log("ModelAsset registry is empty, using fallback list");
                cachedMobList = new ArrayList<>(FALLBACK_MOBS);
            } else {
                // Filter out player and internal models, then sort
                cachedMobList = new ArrayList<>();
                for (String modelId : modelIds) {
                    if (!isPlayerModel(modelId) && !isInternalModel(modelId)) {
                        cachedMobList.add(modelId);
                    }
                }
                Collections.sort(cachedMobList);
                LOGGER.at(Level.INFO).log("Loaded %d mob models from game assets", cachedMobList.size());
            }

            lastCacheRefresh = now;
        } catch (Exception e) {
            LOGGER.at(Level.WARNING).log("Failed to load models from registry: %s", e.getMessage());
            if (cachedMobList == null) {
                cachedMobList = new ArrayList<>(FALLBACK_MOBS);
            }
        }

        return Collections.unmodifiableList(cachedMobList);
    }

    /**
     * Checks if a model ID represents a player model (excluded from mob list).
     */
    private boolean isPlayerModel(String modelId) {
        String lower = modelId.toLowerCase();
        return lower.contains("player") || lower.contains("human") || lower.contains("avatar");
    }

    /**
     * Checks if a model ID is an internal/system model (excluded from mob list).
     */
    private boolean isInternalModel(String modelId) {
        String lower = modelId.toLowerCase();
        return lower.startsWith("_") || lower.startsWith("debug") || lower.startsWith("test");
    }

    /**
     * Forces a refresh of the mob list cache.
     * Call this when new mods are loaded.
     */
    public void refreshMobListCache() {
        cachedMobList = null;
        lastCacheRefresh = 0;
        LOGGER.at(Level.INFO).log("Mob list cache cleared, will refresh on next access");
    }

    /**
     * Returns all model asset keys from the game registry.
     */
    public Set<String> getGameModelAssets() {
        return ModelAsset.getAssetMap().getAssetMap().keySet();
    }

    /**
     * Checks if a model ID exists in the game's asset registry.
     */
    public boolean isValidModel(@Nonnull String modelId) {
        return ModelAsset.getAssetMap().getAsset(modelId) != null;
    }

    /**
     * Gets the count of currently active morphs.
     */
    public int getActiveMorphCount() {
        return activeMorphs.size();
    }

    /**
     * Cleanup method called on plugin disable.
     * Clears all active morph data from memory.
     */
    public void cleanup() {
        LOGGER.at(Level.INFO).log("Cleaning up %d active morphs", activeMorphs.size());
        activeMorphs.clear();
    }
}
