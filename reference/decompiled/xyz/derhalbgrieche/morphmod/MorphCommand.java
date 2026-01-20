/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.protocol.GameMode
 *  com.hypixel.hytale.server.core.Message
 *  com.hypixel.hytale.server.core.asset.type.model.config.Model
 *  com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset
 *  com.hypixel.hytale.server.core.command.system.AbstractCommand
 *  com.hypixel.hytale.server.core.command.system.CommandContext
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage
 *  com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
 *  com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.World
 */
package xyz.derhalbgrieche.morphmod;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import xyz.derhalbgrieche.morphmod.Main;
import xyz.derhalbgrieche.morphmod.ui.MorphGui;

public class MorphCommand
extends AbstractCommand {
    private final Main main;

    public MorphCommand(Main main) {
        super("morph", "Morph commands");
        this.main = main;
        this.setAllowsExtraArguments(true);
        this.setPermissionGroup(GameMode.Adventure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected CompletableFuture<Void> execute(CommandContext ctx) {
        if (!ctx.isPlayer()) {
            return CompletableFuture.completedFuture(null);
        }
        Player player = (Player)ctx.senderAs(Player.class);
        if (player.getWorld() == null) {
            return CompletableFuture.completedFuture(null);
        }
        Set<World> set = this.main.pollingWorlds;
        synchronized (set) {
            if (this.main.pollingWorlds.add(player.getWorld())) {
                System.out.println("[MorphMod] Added world via command: " + player.getWorld().getName());
            }
        }
        CompletableFuture<Void> future = new CompletableFuture<Void>();
        player.getWorld().execute(() -> {
            try {
                this.handle(ctx, player);
                future.complete(null);
            }
            catch (Exception e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    private void handle(CommandContext ctx, Player player) {
        int start;
        String input = ctx.getInputString();
        String[] args = input != null ? input.trim().split("\\s+") : new String[]{};
        int n = start = args.length > 0 && args[0].equalsIgnoreCase("morph") ? 1 : 0;
        if (args.length <= start) {
            this.openUI(player);
            return;
        }
        String cmd = args[start];
        if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("--help")) {
            ctx.sendMessage(Message.raw((String)"Usage: /morph [ui|list|unmorph|unlock|id]"));
            return;
        }
        UUID uuid = this.main.getPlayerUUID(player);
        if (cmd.equals("list")) {
            Set morphs = this.main.unlockedMorphs.getOrDefault(uuid, Collections.emptySet());
            ctx.sendMessage(Message.raw((String)("Morphs: " + String.join((CharSequence)", ", morphs))));
        } else if (cmd.equals("ui")) {
            this.openUI(player);
        } else if (cmd.equals("unmorph")) {
            this.main.unmorphPlayer(player);
        } else if (cmd.equals("test") && args.length > start + 1) {
            String id = args[start + 1];
            if (this.apply(player, id)) {
                ctx.sendMessage(Message.raw((String)("Applied " + id)));
            } else {
                ctx.sendMessage(Message.raw((String)("Failed to apply " + id)));
            }
        } else if (cmd.equals("debug")) {
            try {
                Ref ref = player.getReference();
                boolean hasModel = ref.getStore().getComponent(ref, ModelComponent.getComponentType()) != null;
                boolean hasSkin = ref.getStore().getComponent(ref, PlayerSkinComponent.getComponentType()) != null;
                ctx.sendMessage(Message.raw((String)("Has ModelComponent: " + hasModel)));
                ctx.sendMessage(Message.raw((String)("Has PlayerSkinComponent: " + hasSkin)));
                ctx.sendMessage(Message.raw((String)"Skin Methods:"));
                for (Method m : PlayerSkinComponent.class.getDeclaredMethods()) {
                    ctx.sendMessage(Message.raw((String)("- " + m.getName())));
                }
            }
            catch (Exception e) {
                ctx.sendMessage(Message.raw((String)("Debug fail: " + e.getMessage())));
            }
        } else if (cmd.equals("unlock") && args.length > start + 1) {
            if (!player.hasPermission("morph.unlock")) {
                ctx.sendMessage(Message.raw((String)"You do not have permission to use this command."));
                return;
            }
            String id = args[start + 1];
            this.main.unlockedMorphs.computeIfAbsent(uuid, k -> new HashSet()).add(id);
            ctx.sendMessage(Message.raw((String)("Unlocked " + id)));
            this.main.saveData();
        } else {
            Set morphs = this.main.unlockedMorphs.getOrDefault(uuid, Collections.emptySet());
            if (morphs.contains(cmd)) {
                if (this.apply(player, cmd)) {
                    ctx.sendMessage(Message.raw((String)("Morphed into " + cmd)));
                } else {
                    ctx.sendMessage(Message.raw((String)"Failed."));
                }
            } else {
                ctx.sendMessage(Message.raw((String)("Not unlocked: " + cmd)));
            }
        }
    }

    private void openUI(Player player) {
        UUID uuid = this.main.getPlayerUUID(player);
        Set morphs = this.main.unlockedMorphs.getOrDefault(uuid, Collections.emptySet());
        ArrayList<String> morphList = new ArrayList<String>(morphs);
        try {
            Ref ref = player.getReference();
            Store store = ref.getStore();
            PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
            if (playerRef != null) {
                player.getPageManager().openCustomPage(ref, store, (CustomUIPage)new MorphGui(this.main, playerRef, morphList));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(Message.raw((String)"Failed to open UI."));
        }
    }

    private boolean apply(Player player, String id) {
        try {
            ModelAsset asset = (ModelAsset)ModelAsset.getAssetMap().getAsset((Object)id);
            if (asset == null) {
                return false;
            }
            Model model = Model.createUnitScaleModel((ModelAsset)asset);
            ModelComponent comp = new ModelComponent(model);
            Ref ref = player.getReference();
            ref.getStore().putComponent(ref, ModelComponent.getComponentType(), (Component)comp);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
