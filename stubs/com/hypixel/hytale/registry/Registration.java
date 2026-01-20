package com.hypixel.hytale.registry;

import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;

/**
 * Base class for registration handles.
 */
public class Registration {
    @Nonnull
    protected final BooleanSupplier isEnabled;
    @Nonnull
    protected final Runnable unregister;
    private boolean registered = true;

    public Registration(@Nonnull BooleanSupplier isEnabled, @Nonnull Runnable unregister) {
        this.isEnabled = isEnabled;
        this.unregister = unregister;
    }

    public void unregister() {
        if (this.registered && this.isEnabled.getAsBoolean()) {
            this.unregister.run();
        }
        this.registered = false;
    }

    public boolean isRegistered() {
        return this.registered && this.isEnabled.getAsBoolean();
    }
}
