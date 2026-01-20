package com.hypixel.hytale.server.core.cosmetics;

import com.hypixel.hytale.protocol.PlayerSkin;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CosmeticsModule {
    private static final CosmeticsModule INSTANCE = new CosmeticsModule();

    @Nonnull
    public static CosmeticsModule get() { return INSTANCE; }

    @Nullable
    public Model createModel(@Nonnull PlayerSkin playerSkin) { return null; }

    @Nullable
    public Model createModel(@Nonnull PlayerSkin playerSkin, float scale) { return null; }
}
