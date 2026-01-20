package com.gorduan.hytale.playermorphtomob.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import javax.annotation.Nullable;

/**
 * Data class for UI events from the MorphSelector page.
 * Uses BuilderCodec with .append().add() pattern matching real Hytale API.
 */
public class MorphUIEventData {

    // Morph ID when clicking a list entry
    @Nullable
    public String morphId;

    // Action when clicking a button
    @Nullable
    public String action;

    /**
     * Codec using .append().add() pattern.
     * Each .append() returns FieldBuilder, .add() returns back to Builder.
     */
    public static final BuilderCodec<MorphUIEventData> CODEC =
        BuilderCodec.builder(MorphUIEventData.class, MorphUIEventData::new)
            .append(new KeyedCodec<>("MorphId", Codec.STRING),
                (data, value) -> data.morphId = value,
                data -> data.morphId)
            .add()
            .append(new KeyedCodec<>("Action", Codec.STRING),
                (data, value) -> data.action = value,
                data -> data.action)
            .add()
            .build();

    public MorphUIEventData() {
        this.morphId = null;
        this.action = null;
    }

    @Override
    public String toString() {
        return "MorphUIEventData{morphId='" + morphId + "', action='" + action + "'}";
    }
}
