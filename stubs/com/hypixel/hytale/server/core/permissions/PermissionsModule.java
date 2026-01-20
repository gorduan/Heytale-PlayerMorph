package com.hypixel.hytale.server.core.permissions;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import javax.annotation.Nonnull;

public class PermissionsModule {
    private static final PermissionsModule INSTANCE = new PermissionsModule();

    public static PermissionsModule get() {
        return INSTANCE;
    }

    public boolean hasPermission(@Nonnull PlayerRef playerRef, @Nonnull String permission) {
        return true;
    }

    public static boolean checkPermission(@Nonnull PlayerRef playerRef, @Nonnull String permission) {
        return get().hasPermission(playerRef, permission);
    }
}
