package com.hypixel.hytale.server.core.plugin;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.command.system.CommandRegistry;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public abstract class JavaPlugin {
    public JavaPlugin(@Nonnull JavaPluginInit init) {}

    protected void setup() {}
    protected void start() {}
    protected void shutdown() {}

    @Nonnull
    public HytaleLogger getLogger() { return HytaleLogger.forEnclosingClass(); }

    @Nonnull
    public CommandRegistry getCommandRegistry() { return new CommandRegistry(); }

    @Nonnull
    public EventRegistry getEventRegistry() { return new EventRegistry(); }

    @Nonnull
    public Path getDataDirectory() { return Path.of("."); }

    @Nonnull
    public String getBasePermission() { return ""; }
}
