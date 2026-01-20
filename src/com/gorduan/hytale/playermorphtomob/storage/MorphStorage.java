package com.gorduan.hytale.playermorphtomob.storage;

import com.hypixel.hytale.logger.HytaleLogger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Manages persistence of morph data using a properties file.
 * Stores morphs in format: username=modelId|scale
 */
public class MorphStorage {

    private static final HytaleLogger LOGGER = HytaleLogger.get("MorphStorage");
    private static final String FILE_NAME = "morphs.properties";
    private static final String SEPARATOR = "|";

    private final Path dataFolder;
    private final Path dataFile;
    private final Properties morphData;

    public MorphStorage(@Nonnull Path dataFolder) {
        this.dataFolder = dataFolder;
        this.dataFile = dataFolder.resolve(FILE_NAME);
        this.morphData = new Properties();
        load();
    }

    /**
     * Loads saved morphs from the data file.
     */
    public void load() {
        if (!Files.exists(dataFile)) {
            LOGGER.at(Level.INFO).log("No morph data file found, starting fresh");
            return;
        }

        try (InputStream is = Files.newInputStream(dataFile)) {
            morphData.load(is);
            LOGGER.at(Level.INFO).log("Loaded %d morph entries from storage", morphData.size());
        } catch (IOException e) {
            LOGGER.at(Level.WARNING).log("Failed to load morph data: %s", e.getMessage());
        }
    }

    /**
     * Saves all morphs to the data file.
     */
    public void save() {
        try {
            // Ensure data folder exists
            if (!Files.exists(dataFolder)) {
                Files.createDirectories(dataFolder);
            }

            try (OutputStream os = Files.newOutputStream(dataFile)) {
                morphData.store(os, "PlayerMorphToMob - Saved Morphs");
                LOGGER.at(Level.FINE).log("Saved %d morph entries to storage", morphData.size());
            }
        } catch (IOException e) {
            LOGGER.at(Level.WARNING).log("Failed to save morph data: %s", e.getMessage());
        }
    }

    /**
     * Saves a morph for a player with scale.
     *
     * @param username The player username
     * @param modelId  The model ID
     * @param scale    The scale factor (1.0 for default)
     */
    public void saveMorph(@Nonnull String username, @Nonnull String modelId, float scale) {
        String value = modelId + SEPARATOR + scale;
        morphData.setProperty(username.toLowerCase(), value);
        save();
        LOGGER.at(Level.FINE).log("Saved morph for %s: %s (scale: %.2f)", username, modelId, scale);
    }

    /**
     * Saves a morph with default scale.
     */
    public void saveMorph(@Nonnull String username, @Nonnull String modelId) {
        saveMorph(username, modelId, 1.0f);
    }

    /**
     * Removes a player's saved morph.
     */
    public void removeMorph(@Nonnull String username) {
        if (morphData.remove(username.toLowerCase()) != null) {
            save();
            LOGGER.at(Level.FINE).log("Removed morph for %s", username);
        }
    }

    /**
     * Checks if a player has a saved morph.
     */
    public boolean hasSavedMorph(@Nonnull String username) {
        return morphData.containsKey(username.toLowerCase());
    }

    /**
     * Returns the saved model ID for a player.
     */
    @Nullable
    public String getSavedModelId(@Nonnull String username) {
        String value = morphData.getProperty(username.toLowerCase());
        if (value == null) return null;

        int separatorIndex = value.indexOf(SEPARATOR);
        if (separatorIndex == -1) {
            return value; // Legacy format compatibility (modelId only)
        }
        return value.substring(0, separatorIndex);
    }

    /**
     * Returns the saved scale for a player.
     */
    public float getSavedScale(@Nonnull String username) {
        String value = morphData.getProperty(username.toLowerCase());
        if (value == null) return 1.0f;

        int separatorIndex = value.indexOf(SEPARATOR);
        if (separatorIndex == -1) {
            return 1.0f; // Default if no scale stored
        }

        try {
            return Float.parseFloat(value.substring(separatorIndex + 1));
        } catch (NumberFormatException e) {
            return 1.0f;
        }
    }

    /**
     * Returns the count of stored morphs.
     */
    public int getStoredMorphCount() {
        return morphData.size();
    }
}
