package com.gorduan.hytale.playermorphtomob;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

/**
 * Permission constants and helper methods for PlayerMorphToMob.
 * Provides both PlayerRef and Player overloads for flexibility.
 */
public final class Permissions {

    // Base permission node
    public static final String BASE = "playermorphtomob";

    // Wildcard for all permissions
    public static final String ALL = BASE + ".*";

    // Command access
    public static final String COMMAND = BASE + ".command";

    // GUI access
    public static final String GUI = BASE + ".gui";

    // Morph permissions
    public static final String MORPH_SELF = BASE + ".morph.self";
    public static final String MORPH_OTHERS = BASE + ".morph.others";

    // Nametag control
    public static final String NAMETAG_TOGGLE = BASE + ".nametag.toggle";

    // Admin permissions
    public static final String ADMIN = BASE + ".admin";
    public static final String RELOAD = BASE + ".reload";

    private Permissions() {
        // Utility class - no instantiation
    }

    /**
     * Checks if a player has the specified permission via PermissionsModule.
     */
    public static boolean hasPermission(@Nonnull PlayerRef playerRef, @Nonnull String permission) {
        return PermissionsModule.get().hasPermission(playerRef, permission);
    }

    /**
     * Checks permission directly on Player object.
     */
    public static boolean hasPermission(@Nonnull Player player, @Nonnull String permission) {
        return player.hasPermission(permission);
    }

    /**
     * Checks if a player can morph themselves.
     */
    public static boolean canMorphSelf(@Nonnull PlayerRef playerRef) {
        return hasPermission(playerRef, MORPH_SELF) || hasPermission(playerRef, ADMIN);
    }

    /**
     * Checks if a player can morph themselves (Player overload).
     */
    public static boolean canMorphSelf(@Nonnull Player player) {
        return hasPermission(player, MORPH_SELF) || hasPermission(player, ADMIN);
    }

    /**
     * Checks if a player can morph other players.
     */
    public static boolean canMorphOthers(@Nonnull PlayerRef playerRef) {
        return hasPermission(playerRef, MORPH_OTHERS) || hasPermission(playerRef, ADMIN);
    }

    /**
     * Checks if a player can morph other players (Player overload).
     */
    public static boolean canMorphOthers(@Nonnull Player player) {
        return hasPermission(player, MORPH_OTHERS) || hasPermission(player, ADMIN);
    }

    /**
     * Checks if a player can toggle nametag visibility.
     */
    public static boolean canToggleNametag(@Nonnull PlayerRef playerRef) {
        return hasPermission(playerRef, NAMETAG_TOGGLE) || hasPermission(playerRef, ADMIN);
    }

    /**
     * Checks if a player can toggle nametag visibility (Player overload).
     */
    public static boolean canToggleNametag(@Nonnull Player player) {
        return hasPermission(player, NAMETAG_TOGGLE) || hasPermission(player, ADMIN);
    }

    /**
     * Checks if a player can access the morph GUI.
     */
    public static boolean canUseGUI(@Nonnull PlayerRef playerRef) {
        return hasPermission(playerRef, GUI) || hasPermission(playerRef, ADMIN);
    }

    /**
     * Checks if a player can access the morph GUI (Player overload).
     */
    public static boolean canUseGUI(@Nonnull Player player) {
        return hasPermission(player, GUI) || hasPermission(player, ADMIN);
    }

    /**
     * Checks if a player is an admin.
     */
    public static boolean isAdmin(@Nonnull PlayerRef playerRef) {
        return hasPermission(playerRef, ADMIN);
    }

    /**
     * Checks if a player is an admin (Player overload).
     */
    public static boolean isAdmin(@Nonnull Player player) {
        return hasPermission(player, ADMIN);
    }

    /**
     * Checks if a player can use the base command.
     */
    public static boolean canUseCommand(@Nonnull PlayerRef playerRef) {
        return hasPermission(playerRef, COMMAND) || hasPermission(playerRef, ADMIN);
    }

    /**
     * Checks if a player can use the base command (Player overload).
     */
    public static boolean canUseCommand(@Nonnull Player player) {
        return hasPermission(player, COMMAND) || hasPermission(player, ADMIN);
    }
}
