package com.hypixel.hytale.server.core.command.system.arguments.system;

import javax.annotation.Nonnull;

public class OptionalArg<D> extends Argument<OptionalArg<D>, D> {
    public OptionalArg() {}

    @Override
    @Nonnull
    protected OptionalArg<D> getThis() { return this; }
}
