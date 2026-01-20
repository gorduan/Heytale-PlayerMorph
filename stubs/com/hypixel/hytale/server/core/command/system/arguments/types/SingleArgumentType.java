package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Argument type that parses a single parameter.
 */
public abstract class SingleArgumentType<DataType> extends ArgumentType<DataType> {

    protected SingleArgumentType(Message name, @Nonnull String argumentUsage, String... examples) {
        super(name, Message.translation(argumentUsage), 1, examples);
    }

    protected SingleArgumentType(String name, @Nonnull Message argumentUsage, String... examples) {
        super(name, argumentUsage, 1, examples);
    }

    public SingleArgumentType(String name, @Nonnull String argumentUsage, String... examples) {
        super(name, argumentUsage, 1, examples);
    }

    @Override
    @Nullable
    public DataType parse(@Nonnull String[] input, @Nonnull ParseResult parseResult) {
        return this.parse(input[0], parseResult);
    }

    @Nullable
    public abstract DataType parse(String input, ParseResult parseResult);
}
