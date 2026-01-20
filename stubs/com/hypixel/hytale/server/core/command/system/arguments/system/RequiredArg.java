package com.hypixel.hytale.server.core.command.system.arguments.system;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import javax.annotation.Nonnull;

public class RequiredArg<D> {
    public RequiredArg() {}

    @Nonnull
    public D get(@Nonnull CommandContext context) { return null; }
}
