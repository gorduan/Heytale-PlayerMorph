package com.hypixel.hytale.codec.builder;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.function.consumer.TriConsumer;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

/**
 * BuilderCodec - Stub matching real Hytale API.
 * Supports the .append().add() pattern for field registration.
 */
public class BuilderCodec<T> implements Codec<T> {

    private final Supplier<T> supplier;

    protected BuilderCodec(BuilderBase<T, ?> builder) {
        this.supplier = builder.supplier;
    }

    public T getDefaultValue() {
        return supplier.get();
    }

    @Nonnull
    public static <T> Builder<T> builder(@Nonnull Class<T> clazz, @Nonnull Supplier<T> supplier) {
        return new Builder<>(clazz, supplier);
    }

    /**
     * BuilderBase - Base class for Builder with field registration methods.
     */
    public static abstract class BuilderBase<T, S extends BuilderBase<T, S>> {
        protected final Class<T> tClass;
        protected final Supplier<T> supplier;

        protected BuilderBase(Class<T> tClass, Supplier<T> supplier) {
            this.tClass = tClass;
            this.supplier = supplier;
        }

        @SuppressWarnings("unchecked")
        private S self() {
            return (S) this;
        }

        /**
         * Append a field using simple BiConsumer/Function.
         * Returns a FieldBuilder - call .add() to complete registration.
         */
        @Nonnull
        public <FieldType> BuilderField.FieldBuilder<T, FieldType, S> append(
                @Nonnull KeyedCodec<FieldType> codec,
                @Nonnull BiConsumer<T, FieldType> setter,
                @Nonnull Function<T, FieldType> getter) {
            return append(codec,
                    (T t, FieldType fieldType, ExtraInfo extraInfo) -> setter.accept(t, fieldType),
                    (T t, ExtraInfo extraInfo) -> getter.apply(t));
        }

        /**
         * Append a field using TriConsumer/BiFunction with ExtraInfo.
         * Returns a FieldBuilder - call .add() to complete registration.
         */
        @Nonnull
        public <FieldType> BuilderField.FieldBuilder<T, FieldType, S> append(
                @Nonnull KeyedCodec<FieldType> codec,
                @Nonnull TriConsumer<T, FieldType, ExtraInfo> setter,
                @Nonnull BiFunction<T, ExtraInfo, FieldType> getter) {
            return new BuilderField.FieldBuilder<>(self(), codec, setter, getter, null);
        }

        /**
         * Build the codec.
         */
        @Nonnull
        public BuilderCodec<T> build() {
            return new BuilderCodec<>(this);
        }
    }

    /**
     * Builder - Concrete builder implementation.
     */
    public static class Builder<T> extends BuilderBase<T, Builder<T>> {
        protected Builder(Class<T> tClass, Supplier<T> supplier) {
            super(tClass, supplier);
        }
    }
}
