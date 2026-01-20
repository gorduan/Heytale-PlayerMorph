package com.hypixel.hytale.server.core.command.system;

import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractCommand {

    protected AbstractCommand(@Nullable String name, @Nullable String description) {}

    protected AbstractCommand(@Nullable String name, @Nullable String description, boolean requiresConfirmation) {}

    public void addAliases(String... aliases) {}

    public void addSubCommand(@Nonnull AbstractCommand command) {}

    public void requirePermission(@Nonnull String permission) {}

    /**
     * VERIFIZIERT durch morphmod-1.1.0:
     * Erlaubt zusätzliche Argumente nach den definierten Args.
     * Nützlich für manuelle Argument-Verarbeitung via ctx.getInputString().
     */
    public void setAllowsExtraArguments(boolean allow) {}

    /**
     * VERIFIZIERT durch morphmod-1.1.0:
     * Setzt die GameMode-basierte Permission-Gruppe für diesen Command.
     */
    public void setPermissionGroup(@Nonnull com.hypixel.hytale.protocol.GameMode gameMode) {}

    @Nonnull
    public <D> RequiredArg<D> withRequiredArg(@Nonnull String name, @Nonnull String description,
                                               @Nonnull ArgumentType<D> argType) {
        return new RequiredArg<>();
    }

    @Nonnull
    public <D> OptionalArg<D> withOptionalArg(@Nonnull String name, @Nonnull String description,
                                               @Nonnull ArgumentType<D> argType) {
        return new OptionalArg<>();
    }

    @Nonnull
    public FlagArg withFlagArg(@Nonnull String name, @Nonnull String description) {
        return new FlagArg();
    }

    @Nullable
    protected abstract CompletableFuture<Void> execute(@Nonnull CommandContext context);

    @Nullable
    public String getName() { return null; }
}
