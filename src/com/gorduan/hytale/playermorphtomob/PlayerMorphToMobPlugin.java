package com.gorduan.hytale.playermorphtomob;

import com.gorduan.hytale.playermorphtomob.commands.MorphCommand;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * PlayerMorphToMob - A Hytale plugin that allows players to transform into mob models.
 *
 * Features:
 * - Morph self or other players into mob models
 * - Persistent morphs (survive reconnect)
 * - GUI for mob selection
 * - Thread-safe ECS operations
 *
 * @author Gorduan
 * @version 1.1.0
 */
public class PlayerMorphToMobPlugin extends JavaPlugin {

    private static PlayerMorphToMobPlugin instance;
    private MorphManager morphManager;
    private ScheduledExecutorService scheduler;

    public PlayerMorphToMobPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static PlayerMorphToMobPlugin getInstance() {
        return instance;
    }

    /**
     * Called during plugin setup phase.
     * Registers commands, events, and initializes the morph manager.
     */
    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("Setting up PlayerMorphToMob v1.1.0...");

        // Initialize scheduler for delayed morph restoration
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Initialize morph manager with data folder for persistence
        morphManager = MorphManager.getInstance();
        Path dataFolder = Path.of("Mods", "PlayerMorphToMob");
        morphManager.initialize(dataFolder);

        // Register morph command with plugin reference
        MorphCommand morphCommand = new MorphCommand(this);
        getCommandRegistry().registerCommand(morphCommand);

        // Register event listeners for morph persistence
        getEventRegistry().registerGlobal(PlayerConnectEvent.class, this::onPlayerConnect);
        getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, this::onPlayerDisconnect);

        getLogger().at(Level.INFO).log("PlayerMorphToMob setup complete.");
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("PlayerMorphToMob v1.1.0 enabled!");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("PlayerMorphToMob shutting down...");

        if (scheduler != null) {
            scheduler.shutdown();
        }

        if (morphManager != null) {
            morphManager.cleanup();
        }
    }

    /**
     * Called when a player connects.
     * Restores saved morphs after a short delay to ensure the entity is fully loaded.
     * All ECS operations are executed in the World thread for thread safety.
     */
    private void onPlayerConnect(@Nonnull PlayerConnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();
        String playerName = playerRef.getUsername();

        if (morphManager.hasSavedMorph(playerName)) {
            getLogger().at(Level.INFO).log("Player %s has saved morph, scheduling restoration...", playerName);

            // Delay restoration to ensure entity is fully loaded
            scheduler.schedule(() -> {
                try {
                    Ref<EntityStore> ref = playerRef.getReference();
                    if (ref == null || !ref.isValid()) {
                        getLogger().at(Level.WARNING).log("Cannot restore morph for %s: Invalid reference", playerName);
                        return;
                    }

                    // Execute ECS operations in World thread
                    World world = ref.getStore().getExternalData().getWorld();
                    world.execute(() -> {
                        try {
                            if (morphManager.restoreSavedMorph(playerRef)) {
                                getLogger().at(Level.INFO).log("Successfully restored morph for %s", playerName);
                            } else {
                                getLogger().at(Level.WARNING).log("Failed to restore morph for %s", playerName);
                            }
                        } catch (Exception e) {
                            getLogger().at(Level.WARNING).log("Error restoring morph for %s: %s", playerName, e.getMessage());
                        }
                    });
                } catch (Exception e) {
                    getLogger().at(Level.WARNING).log("Error scheduling morph restoration for %s: %s", playerName, e.getMessage());
                }
            }, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * Called when a player disconnects.
     * Cleans up in-memory morph data while preserving persisted state.
     */
    private void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
        String playerName = event.getPlayerRef().getUsername();
        if (morphManager.isMorphed(playerName)) {
            morphManager.forceRemove(playerName);
            getLogger().at(Level.FINE).log("Cleaned up morph data for disconnected player: %s", playerName);
        }
    }

    public MorphManager getMorphManager() {
        return morphManager;
    }
}
