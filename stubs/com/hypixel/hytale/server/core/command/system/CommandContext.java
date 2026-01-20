package com.hypixel.hytale.server.core.command.system;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.command.system.arguments.system.Argument;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

/**
 * VERIFIZIERT durch morphmod-1.1.0:
 * CommandContext für Command-Ausführung.
 */
public class CommandContext {
    @Nonnull
    public CommandSender sender() { return new CommandSender() {
        public void sendMessage(com.hypixel.hytale.server.core.Message message) {}
        public boolean hasPermission(String permission) { return true; }
        public String getDisplayName() { return ""; }
    }; }

    public boolean isPlayer() { return true; }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T extends CommandSender> T senderAs(@Nonnull Class<T> senderType) {
        return (T) sender();
    }

    public Ref<EntityStore> senderAsPlayerRef() { return null; }

    public <DataType> DataType get(@Nonnull Argument<?, DataType> argument) { return null; }

    public boolean provided(@Nonnull Argument<?, ?> argument) { return false; }

    /**
     * VERIFIZIERT durch morphmod-1.1.0:
     * Gibt den gesamten Input-String zurück.
     * Nützlich für manuelle Argument-Verarbeitung mit setAllowsExtraArguments(true).
     */
    @Nonnull
    public String getInputString() { return ""; }

    /**
     * VERIFIZIERT durch morphmod-1.1.0:
     * Sendet eine Nachricht an den Command-Sender.
     * Kurzform für sender().sendMessage().
     */
    public void sendMessage(@Nonnull com.hypixel.hytale.server.core.Message message) {
        sender().sendMessage(message);
    }
}
