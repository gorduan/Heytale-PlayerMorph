package com.hypixel.hytale.server.core.command.system.arguments.system;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import javax.annotation.Nonnull;

public abstract class Argument<Arg extends Argument<Arg, DataType>, DataType> {

    public boolean provided(@Nonnull CommandContext context) {
        return context.provided(this);
    }

    public DataType get(@Nonnull CommandContext context) {
        return context.get(this);
    }

    @Nonnull
    protected abstract Arg getThis();
}
