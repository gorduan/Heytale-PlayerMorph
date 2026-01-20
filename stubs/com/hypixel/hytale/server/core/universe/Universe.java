package com.hypixel.hytale.server.core.universe;

import com.hypixel.hytale.server.core.universe.world.World;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * VERIFIZIERT durch HytaleServer-decompiled Universe.java
 * Singleton für Zugriff auf alle Welten und Spieler.
 */
public class Universe {

    private static Universe instance = new Universe();

    @Nonnull
    public static Universe get() {
        return instance;
    }

    /**
     * Gibt alle Welten zurück.
     * VERIFIZIERT: Map<String, World> mit Weltname als Key.
     */
    @Nonnull
    public Map<String, World> getWorlds() {
        return new HashMap<>(); // Stub - wird zur Laufzeit vom Server bereitgestellt
    }
}
