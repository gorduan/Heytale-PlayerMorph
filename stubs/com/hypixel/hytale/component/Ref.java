package com.hypixel.hytale.component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * KORRIGIERT: Ref ist eine KLASSE, kein Interface (verifiziert durch dekompilierten Server)
 */
public class Ref<ECS_TYPE> {
    public static final Ref<?>[] EMPTY_ARRAY = new Ref[0];

    @Nonnull
    private final Store<ECS_TYPE> store;
    private volatile int index;

    public Ref(@Nonnull Store<ECS_TYPE> store) {
        this(store, Integer.MIN_VALUE);
    }

    public Ref(@Nonnull Store<ECS_TYPE> store, int index) {
        this.store = store;
        this.index = index;
    }

    @Nonnull
    public Store<ECS_TYPE> getStore() {
        return this.store;
    }

    public int getIndex() {
        return this.index;
    }

    public boolean isValid() {
        return this.index != Integer.MIN_VALUE;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ref<?> ref = (Ref<?>) o;
        return index == ref.index && store.equals(ref.store);
    }

    @Override
    public int hashCode() {
        int result = store.hashCode();
        result = 31 * result + index;
        return result;
    }

    @Nonnull
    @Override
    public String toString() {
        return "Ref{store=" + store.getClass() + "@" + store.hashCode() + ", index=" + index + "}";
    }
}
