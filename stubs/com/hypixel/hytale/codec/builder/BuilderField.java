package com.hypixel.hytale.codec.builder;

import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.function.consumer.TriConsumer;
import javax.annotation.Nonnull;
import java.util.function.BiFunction;

/**
 * BuilderField - Stub matching real Hytale API.
 * Used for BuilderCodec field definitions.
 */
public class BuilderField<Type, FieldType> {

    @Nonnull
    protected final KeyedCodec<FieldType> codec;

    protected BuilderField(@Nonnull FieldBuilder<Type, FieldType, ?> builder) {
        this.codec = builder.codec;
    }

    protected BuilderField(@Nonnull KeyedCodec<FieldType> codec,
                          TriConsumer<Type, FieldType, ExtraInfo> setter,
                          BiFunction<Type, ExtraInfo, FieldType> getter,
                          TriConsumer<Type, Type, ExtraInfo> inherit) {
        this.codec = codec;
    }

    @Nonnull
    public KeyedCodec<FieldType> getCodec() {
        return this.codec;
    }

    /**
     * FieldBuilder - returned by BuilderCodec.append().
     * Call .add() to complete field registration and return to the parent builder.
     */
    public static class FieldBuilder<T, FieldType, Builder extends BuilderCodec.BuilderBase<T, Builder>> {

        @Nonnull
        protected final Builder parentBuilder;
        @Nonnull
        protected final KeyedCodec<FieldType> codec;

        public FieldBuilder(Builder parentBuilder,
                           KeyedCodec<FieldType> codec,
                           TriConsumer<T, FieldType, ExtraInfo> setter,
                           BiFunction<T, ExtraInfo, FieldType> getter,
                           TriConsumer<T, T, ExtraInfo> inherit) {
            this.parentBuilder = parentBuilder;
            this.codec = codec;
        }

        /**
         * Complete field registration and return to parent builder.
         */
        @Nonnull
        public Builder add() {
            return this.parentBuilder;
        }
    }
}
