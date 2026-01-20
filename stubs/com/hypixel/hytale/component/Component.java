package com.hypixel.hytale.component;

import javax.annotation.Nonnull;

public interface Component<ECS_TYPE> {
    @Nonnull
    Component<ECS_TYPE> clone();
}
