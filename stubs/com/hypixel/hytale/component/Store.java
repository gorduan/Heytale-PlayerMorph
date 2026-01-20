package com.hypixel.hytale.component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * KORRIGIERT: Store ist eine KLASSE, kein Interface (verifiziert durch dekompilierten Server)
 */
public class Store<ECS_TYPE> {

    @Nullable
    public <T extends Component<ECS_TYPE>> T getComponent(@Nonnull Ref<ECS_TYPE> ref,
                                                          @Nonnull ComponentType<ECS_TYPE, T> type) {
        return null;
    }

    public <T extends Component<ECS_TYPE>> void putComponent(@Nonnull Ref<ECS_TYPE> ref,
                                                              @Nonnull ComponentType<ECS_TYPE, T> type,
                                                              @Nonnull T component) {
    }

    public <T extends Component<ECS_TYPE>> void removeComponent(@Nonnull Ref<ECS_TYPE> ref,
                                                                 @Nonnull ComponentType<ECS_TYPE, T> type) {
    }

    /**
     * VERIFIZIERT durch HytaleServer-decompiled Store.java:
     * Gibt die externe Daten-Instanz zurück (z.B. EntityStore).
     */
    @Nonnull
    public ECS_TYPE getExternalData() {
        return null; // Stub - wird zur Laufzeit vom Server bereitgestellt
    }
}
