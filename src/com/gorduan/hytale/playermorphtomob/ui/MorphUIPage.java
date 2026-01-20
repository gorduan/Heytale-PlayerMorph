package com.gorduan.hytale.playermorphtomob.ui;

import com.gorduan.hytale.playermorphtomob.MorphManager;
import com.gorduan.hytale.playermorphtomob.PlayerMorphToMobPlugin;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.logging.Level;

/**
 * Interactive UI page for the morph selector.
 * VERIFIZIERT durch Waypoints-1.2.0 und hytale-docs.pages.dev:
 * - Dynamische Liste mit append(selector, path)
 * - EventData mit new EventData().append() für Events
 * - UI-Pfade relativ zu Common/UI/Custom/
 */
public class MorphUIPage extends InteractiveCustomUIPage<MorphUIEventData> {

    private static final HytaleLogger LOGGER = HytaleLogger.get("MorphUIPage");

    // VERIFIZIERT durch Waypoints: Pfade relativ zu Common/UI/Custom/
    private static final String UI_PATH = "Pages/MorphSelector.ui";
    private static final String ENTRY_PATH = "Pages/MorphEntry.ui";

    private final PlayerMorphToMobPlugin plugin;
    private final Player player;
    private final List<String> morphList;

    /**
     * VERIFIZIERT durch Waypoints-1.2.0:
     * - CustomPageLifetime.CanDismiss
     * - Player-Objekt für spätere Operationen speichern
     */
    public MorphUIPage(@Nonnull PlayerMorphToMobPlugin plugin,
                       @Nonnull PlayerRef playerRef,
                       @Nonnull Player player,
                       @Nonnull List<String> morphList) {
        super(playerRef, CustomPageLifetime.CanDismiss, MorphUIEventData.CODEC);
        this.plugin = plugin;
        this.player = player;
        this.morphList = morphList;
    }

    /**
     * VERIFIZIERT durch Waypoints-1.2.0:
     * - commands.append(uiPath) lädt die Haupt-UI
     * - commands.append(selector, entryPath) fügt Einträge in Container
     * - commands.set(selector + " #Element.Property", value) setzt Werte
     * - events.addEventBinding() mit new EventData().append() für Events
     */
    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder commands,
                      @Nonnull UIEventBuilder events,
                      @Nonnull Store<EntityStore> store) {

        // UI-Datei laden
        commands.append(UI_PATH);

        // Close-Button Event
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#CloseButton",
                new EventData().append("Action", "close"),
                false
        );

        // "Unmorph" Eintrag als erstes
        commands.append("#MorphList", ENTRY_PATH);
        String unmorphSelector = "#MorphList[0]";
        commands.set(unmorphSelector + " #MorphName.Text", "Unmorph (Reset)");
        commands.set(unmorphSelector + " #SelectButton.Text", "Reset");
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                unmorphSelector + " #SelectButton",
                new EventData().append("MorphId", "unmorph"),
                false
        );

        // Mob-Einträge dynamisch hinzufügen
        for (int i = 0; i < morphList.size(); i++) {
            String morphId = morphList.get(i);
            commands.append("#MorphList", ENTRY_PATH);
            String entrySelector = "#MorphList[" + (i + 1) + "]";
            commands.set(entrySelector + " #MorphName.Text", morphId);
            events.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    entrySelector + " #SelectButton",
                    new EventData().append("MorphId", morphId),
                    false
            );
        }

        LOGGER.at(Level.FINE).log("MorphUIPage built with %d morphs for %s",
                morphList.size(), playerRef.getUsername());
    }

    /**
     * VERIFIZIERT durch Waypoints-1.2.0 und hytale-docs:
     * - MorphId aus EventData lesen
     * - "unmorph" für Reset-Aktion
     * - close() nach erfolgreicher Aktion
     */
    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull MorphUIEventData data) {
        LOGGER.at(Level.INFO).log("UI Event: morphId=%s, action=%s", data.morphId, data.action);

        MorphManager morphManager = plugin.getMorphManager();

        // Action-basierte Events (Close Button)
        if ("close".equalsIgnoreCase(data.action)) {
            close();
            return;
        }

        // MorphId-basierte Events (Klick auf Listeneintrag)
        if (data.morphId != null && !data.morphId.isEmpty()) {
            if ("unmorph".equalsIgnoreCase(data.morphId)) {
                // Reset morph
                if (morphManager.resetMorph(playerRef)) {
                    player.sendMessage(Message.raw("Morph reset."));
                } else {
                    player.sendMessage(Message.raw("You are not morphed."));
                }
            } else {
                // Apply morph
                if (morphManager.applyMorph(playerRef, data.morphId)) {
                    player.sendMessage(Message.raw("Morphed into " + data.morphId));
                } else {
                    player.sendMessage(Message.raw("Failed to morph into " + data.morphId));
                }
            }
            close();
        }
    }

    @Override
    public void onDismiss(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        LOGGER.at(Level.FINE).log("MorphUIPage dismissed for %s", playerRef.getUsername());
    }
}
