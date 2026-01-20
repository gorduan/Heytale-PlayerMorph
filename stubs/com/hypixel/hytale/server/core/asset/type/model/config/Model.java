package com.hypixel.hytale.server.core.asset.type.model.config;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class Model {
    private final String modelAssetId;
    private final float scale;

    public Model(@Nonnull String modelAssetId) {
        this.modelAssetId = modelAssetId;
        this.scale = 1.0f;
    }

    public Model(@Nonnull String modelAssetId, float scale) {
        this.modelAssetId = modelAssetId;
        this.scale = scale;
    }

    @Nonnull
    public String getModelAssetId() { return modelAssetId; }

    public float getScale() { return scale; }

    @Nonnull
    public ModelReference toReference() {
        return new ModelReference(modelAssetId, scale, null, false);
    }

    /**
     * Erstellt ein Model mit benutzerdefinierter Skalierung.
     */
    @Nullable
    public static Model createScaledModel(@Nullable ModelAsset modelAsset, float scale) {
        if (modelAsset == null) return null;
        return new Model(modelAsset.getId(), scale);
    }

    /**
     * VERIFIZIERT durch morphmod-1.1.0:
     * Erstellt ein Model mit der Standard-Skalierung des ModelAssets (1:1).
     * Dies ist die empfohlene Methode für Morph-Operationen.
     */
    @Nullable
    public static Model createUnitScaleModel(@Nullable ModelAsset modelAsset) {
        if (modelAsset == null) return null;
        return new Model(modelAsset.getId(), 1.0f);
    }

    /**
     * VERIFIZIERT durch HytaleServer-decompiled Model.java:
     * Inner class für Modell-Referenzen (Persistenz).
     */
    public static class ModelReference {
        public static final BuilderCodec<ModelReference> CODEC = null; // Stub - echte Implementation in Server
        public static final ModelReference DEFAULT_PLAYER_MODEL = new ModelReference("Player", -1.0f, null, false);

        private String modelAssetId;
        private float scale;
        private Map<String, String> randomAttachmentIds;
        private boolean staticModel;

        protected ModelReference() {}

        public ModelReference(String modelAssetId, float scale, Map<String, String> randomAttachmentIds) {
            this(modelAssetId, scale, randomAttachmentIds, false);
        }

        public ModelReference(String modelAssetId, float scale, Map<String, String> randomAttachmentIds, boolean staticModel) {
            this.modelAssetId = modelAssetId;
            this.scale = scale;
            this.randomAttachmentIds = randomAttachmentIds;
            this.staticModel = staticModel;
        }

        public String getModelAssetId() { return modelAssetId; }
        public float getScale() { return scale; }
        public Map<String, String> getRandomAttachmentIds() { return randomAttachmentIds; }
        public boolean isStaticModel() { return staticModel; }

        @Nullable
        public Model toModel() {
            if (modelAssetId == null) return null;
            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(modelAssetId);
            if (modelAsset == null) return null;
            return Model.createScaledModel(modelAsset, scale);
        }
    }
}
