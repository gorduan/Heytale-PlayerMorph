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
 * Verwaltet die Persistenz von Morph-Daten in einer Properties-Datei.
 * Speichert Morphs im Format: username=modelId|scale
 *
 * VERIFIZIERT durch PMA-1.0.3: Ähnliches Pattern für models.properties
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
     * Lädt die gespeicherten Morphs aus der Datei.
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
     * Speichert alle Morphs in die Datei.
     */
    public void save() {
        try {
            // Stelle sicher dass der Ordner existiert
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
     * Speichert einen Morph für einen Spieler.
     *
     * @param username Der Spielername
     * @param modelId Die Model-ID
     * @param scale Die Skalierung (1.0 für Standard)
     */
    public void saveMorph(@Nonnull String username, @Nonnull String modelId, float scale) {
        String value = modelId + SEPARATOR + scale;
        morphData.setProperty(username.toLowerCase(), value);
        save();
        LOGGER.at(Level.FINE).log("Saved morph for %s: %s (scale: %.2f)", username, modelId, scale);
    }

    /**
     * Speichert einen Morph mit Standardskalierung.
     */
    public void saveMorph(@Nonnull String username, @Nonnull String modelId) {
        saveMorph(username, modelId, 1.0f);
    }

    /**
     * Entfernt den gespeicherten Morph eines Spielers.
     */
    public void removeMorph(@Nonnull String username) {
        if (morphData.remove(username.toLowerCase()) != null) {
            save();
            LOGGER.at(Level.FINE).log("Removed morph for %s", username);
        }
    }

    /**
     * Prüft ob ein Spieler einen gespeicherten Morph hat.
     */
    public boolean hasSavedMorph(@Nonnull String username) {
        return morphData.containsKey(username.toLowerCase());
    }

    /**
     * Gibt die gespeicherte Model-ID zurück.
     */
    @Nullable
    public String getSavedModelId(@Nonnull String username) {
        String value = morphData.getProperty(username.toLowerCase());
        if (value == null) return null;

        int separatorIndex = value.indexOf(SEPARATOR);
        if (separatorIndex == -1) {
            return value; // Alte Format-Kompatibilität (nur modelId)
        }
        return value.substring(0, separatorIndex);
    }

    /**
     * Gibt die gespeicherte Skalierung zurück.
     */
    public float getSavedScale(@Nonnull String username) {
        String value = morphData.getProperty(username.toLowerCase());
        if (value == null) return 1.0f;

        int separatorIndex = value.indexOf(SEPARATOR);
        if (separatorIndex == -1) {
            return 1.0f; // Standard wenn kein Scale gespeichert
        }

        try {
            return Float.parseFloat(value.substring(separatorIndex + 1));
        } catch (NumberFormatException e) {
            return 1.0f;
        }
    }

    /**
     * Gibt die Anzahl der gespeicherten Morphs zurück.
     */
    public int getStoredMorphCount() {
        return morphData.size();
    }
}
