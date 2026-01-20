package com.hypixel.hytale.server.core.command.system.arguments.types;

import com.hypixel.hytale.server.core.command.system.ParseResult;
import com.hypixel.hytale.server.core.universe.PlayerRef;

/**
 * Standard argument types for commands.
 */
public final class ArgTypes {

    public static final SingleArgumentType<Boolean> BOOLEAN = new SingleArgumentType<Boolean>(
            "server.commands.parsing.argtype.boolean.name",
            "server.commands.parsing.argtype.boolean.usage",
            "true", "false"
    ) {
        @Override
        public Boolean parse(String input, ParseResult parseResult) {
            return Boolean.parseBoolean(input);
        }
    };

    public static final SingleArgumentType<Integer> INTEGER = new SingleArgumentType<Integer>(
            "server.commands.parsing.argtype.integer.name",
            "server.commands.parsing.argtype.integer.usage",
            "-1", "0", "1"
    ) {
        @Override
        public Integer parse(String input, ParseResult parseResult) {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };

    public static final SingleArgumentType<String> STRING = new SingleArgumentType<String>(
            "server.commands.parsing.argtype.string.name",
            "server.commands.parsing.argtype.string.usage",
            "example"
    ) {
        @Override
        public String parse(String input, ParseResult parseResult) {
            return input;
        }
    };

    public static final SingleArgumentType<Float> FLOAT = new SingleArgumentType<Float>(
            "server.commands.parsing.argtype.float.name",
            "server.commands.parsing.argtype.float.usage",
            "3.14", "-2.5"
    ) {
        @Override
        public Float parse(String input, ParseResult parseResult) {
            try {
                return Float.parseFloat(input);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };

    public static final SingleArgumentType<Double> DOUBLE = new SingleArgumentType<Double>(
            "server.commands.parsing.argtype.double.name",
            "server.commands.parsing.argtype.double.usage",
            "3.14159", "0.0"
    ) {
        @Override
        public Double parse(String input, ParseResult parseResult) {
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    };

    public static final SingleArgumentType<PlayerRef> PLAYER_REF = new SingleArgumentType<PlayerRef>(
            "server.commands.parsing.argtype.player.name",
            "server.commands.parsing.argtype.player.usage",
            "john_doe", "user123"
    ) {
        @Override
        public PlayerRef parse(String input, ParseResult parseResult) {
            // The actual implementation looks up by name
            return null;
        }
    };

    private ArgTypes() {}
}
