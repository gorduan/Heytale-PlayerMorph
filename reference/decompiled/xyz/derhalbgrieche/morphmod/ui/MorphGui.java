/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.codec.Codec
 *  com.hypixel.hytale.codec.KeyedCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec
 *  com.hypixel.hytale.codec.builder.BuilderCodec$Builder
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
 *  com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
 *  com.hypixel.hytale.server.core.asset.type.model.config.Model
 *  com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
 *  com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
 *  com.hypixel.hytale.server.core.ui.builder.EventData
 *  com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
 *  com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.storage.EntityStore
 *  javax.annotation.Nonnull
 */
package xyz.derhalbgrieche.morphmod.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.List;
import javax.annotation.Nonnull;
import xyz.derhalbgrieche.morphmod.Main;

public class MorphGui
extends InteractiveCustomUIPage<MorphData> {
    private final Main main;
    private final List<String> morphs;

    public MorphGui(Main main, PlayerRef playerRef, List<String> morphs) {
        super(playerRef, CustomPageLifetime.CanDismiss, MorphData.CODEC);
        this.main = main;
        this.morphs = morphs;
    }

    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/MorphMod/MorphGui.ui");
        uiCommandBuilder.clear("#MorphList");
        uiCommandBuilder.append("#MorphList", "Pages/MorphMod/MorphEntry.ui");
        String unmorphPath = "#MorphList[0]";
        uiCommandBuilder.set(unmorphPath + " #MorphName.Text", "Unmorph");
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, unmorphPath, EventData.of((String)"MorphId", (String)"unmorph"), false);
        for (int i = 0; i < this.morphs.size(); ++i) {
            String morphId = this.morphs.get(i);
            uiCommandBuilder.append("#MorphList", "Pages/MorphMod/MorphEntry.ui");
            String entryPath = "#MorphList[" + (i + 1) + "]";
            uiCommandBuilder.set(entryPath + " #MorphName.Text", morphId);
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, entryPath, EventData.of((String)"MorphId", (String)morphId), false);
        }
    }

    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull MorphData data) {
        if (data.morphId != null) {
            if (data.morphId.equals("unmorph")) {
                Player player = (Player)store.getComponent(ref, Player.getComponentType());
                if (player != null) {
                    this.main.unmorphPlayer(player);
                }
            } else {
                this.applyMorph(ref, data.morphId);
            }
            this.close();
        }
    }

    private void applyMorph(Ref<EntityStore> playerRef, String id) {
        try {
            ModelAsset asset = (ModelAsset)ModelAsset.getAssetMap().getAsset((Object)id);
            if (asset != null) {
                Model model = Model.createUnitScaleModel((ModelAsset)asset);
                ModelComponent comp = new ModelComponent(model);
                playerRef.getStore().putComponent(playerRef, ModelComponent.getComponentType(), (Component)comp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class MorphData {
        public static final BuilderCodec<MorphData> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(MorphData.class, MorphData::new).addField(new KeyedCodec("MorphId", (Codec)Codec.STRING), (d, v) -> {
            d.morphId = v;
        }, d -> d.morphId)).build();
        public String morphId;
    }
}
