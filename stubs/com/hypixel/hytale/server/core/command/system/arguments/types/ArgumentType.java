package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Abstract base class for command argument types.
 */
public abstract class ArgumentType<DataType> {

    @Nonnull
    public static final String[] EMPTY_EXAMPLES = new String[0];

    private final Message name;
    @Nonnull
    private final Message argumentUsage;
    @Nonnull
    protected final String[] examples;
    protected int numberOfParameters;

    protected ArgumentType(@Nonnull Message name, @Nonnull Message argumentUsage, int numberOfParameters, String... examples) {
        this.name = name;
        this.argumentUsage = argumentUsage;
        this.numberOfParameters = numberOfParameters;
        this.examples = examples == null ? EMPTY_EXAMPLES : examples;
    }

    protected ArgumentType(@Nonnull String name, @Nonnull Message argumentUsage, int numberOfParameters, String... examples) {
        this(Message.translation(name), argumentUsage, numberOfParameters, examples);
    }

    protected ArgumentType(String name, @Nonnull String argumentUsage, int numberOfParameters, String... examples) {
        this(Message.translation(name), Message.translation(argumentUsage), numberOfParameters, examples);
    }

    @Nullable
    public abstract DataType parse(@Nonnull String[] input, @Nonnull ParseResult parseResult);

    @Nonnull
    public Message getArgumentUsage() {
        return this.argumentUsage;
    }

    public int getNumberOfParameters() {
        return this.numberOfParameters;
    }

    @Nonnull
    public Message getName() {
        return this.name;
    }

    @Nonnull
    public String[] getExamples() {
        return this.examples;
    }

    public boolean isListArgument() {
        return false;
    }
}
