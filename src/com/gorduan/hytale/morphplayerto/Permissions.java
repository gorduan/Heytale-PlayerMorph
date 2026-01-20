package com.gorduan.hytale.morphplayerto;

/**
 * Permission constants for the MorphPlayerTo plugin.
 * All permissions follow the morphplayerto.* namespace.
 */
public final class Permissions {

    private Permissions() {
        // Utility class
    }

    /**
     * Base permission node for all morph-related permissions.
     */
    public static final String BASE = "morphplayerto";

    /**
     * Permission to morph yourself.
     * Default: true for players in Adventure mode or higher.
     */
    public static final String MORPH_SELF = BASE + ".morph.self";

    /**
     * Permission to morph other players.
     * Default: false (admin only).
     */
    public static final String MORPH_OTHERS = BASE + ".morph.others";

    /**
     * Permission to use the morph GUI.
     * Default: true for players with MORPH_SELF.
     */
    public static final String USE_GUI = BASE + ".gui";

    /**
     * Permission to list available morphs.
     * Default: true for all players.
     */
    public static final String LIST = BASE + ".list";

    /**
     * Permission to bypass morph cooldowns.
     * Default: false (admin only).
     */
    public static final String BYPASS_COOLDOWN = BASE + ".bypass.cooldown";

    /**
     * Permission to use any morph (no restrictions).
     * Default: false (admin only).
     */
    public static final String ALL_MORPHS = BASE + ".all";
}
