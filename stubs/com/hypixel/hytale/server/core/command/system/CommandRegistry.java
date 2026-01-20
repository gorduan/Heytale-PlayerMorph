package com.hypixel.hytale.server.core.command.system;

import javax.annotation.Nonnull;

/**
 * Registry for commands.
 */
public class CommandRegistry {
    public CommandRegistry() {}

    public CommandRegistration registerCommand(@Nonnull AbstractCommand command) {
        return new CommandRegistration(command, () -> true, () -> {});
    }

    public void shutdown() {}
}
