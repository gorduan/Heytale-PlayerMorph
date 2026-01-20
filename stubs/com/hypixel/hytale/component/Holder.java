package com.hypixel.hytale.component;

import javax.annotation.Nullable;

/**
 * VERIFIZIERT durch HytaleServer-decompiled:
 * Holder ermöglicht Zugriff auf Components einer Entity.
 */
public interface Holder<S> {
    @Nullable
    <C extends Component<S>> C getComponent(ComponentType<S, C> componentType);
}
