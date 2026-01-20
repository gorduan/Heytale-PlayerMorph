package com.hypixel.hytale.server.core.command.system.arguments.system;

import javax.annotation.Nonnull;

public class FlagArg extends Argument<FlagArg, Boolean> {
    public FlagArg() {}

    @Override
    @Nonnull
    protected FlagArg getThis() { return this; }
}
