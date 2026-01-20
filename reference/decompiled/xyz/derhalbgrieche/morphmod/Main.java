/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hypixel.hytale.component.ArchetypeChunk
 *  com.hypixel.hytale.component.Component
 *  com.hypixel.hytale.component.Ref
 *  com.hypixel.hytale.component.Store
 *  com.hypixel.hytale.server.core.Message
 *  com.hypixel.hytale.server.core.asset.type.model.config.Model
 *  com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset
 *  com.hypixel.hytale.server.core.command.system.AbstractCommand
 *  com.hypixel.hytale.server.core.entity.entities.Player
 *  com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
 *  com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage
 *  com.hypixel.hytale.server.core.modules.entity.damage.Damage$EntitySource
 *  com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent
 *  com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent
 *  com.hypixel.hytale.server.core.plugin.JavaPlugin
 *  com.hypixel.hytale.server.core.plugin.JavaPluginInit
 *  com.hypixel.hytale.server.core.universe.PlayerRef
 *  com.hypixel.hytale.server.core.universe.world.World
 */
package xyz.derhalbgrieche.morphmod;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import xyz.derhalbgrieche.morphmod.MorphCommand;

public class Main
extends JavaPlugin {
    Map<UUID, Set<String>> unlockedMorphs = new HashMap<UUID, Set<String>>();
    private Path dataFile;
    private final Set<Ref> processedDeaths = Collections.synchronizedSet(new HashSet());
    private final Timer timer = new Timer("MorphMod-Poller", true);
    final Set<World> pollingWorlds = Collections.synchronizedSet(new HashSet());

    public Main(JavaPluginInit init) {
        super(init);
    }

    protected void setup() {
        System.out.println("[MorphMod] Setup (Polling V1)");
        this.getCommandRegistry().registerCommand((AbstractCommand)new MorphCommand(this));
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, this::onPlayerConnect);
        try {
            Path dataDir = this.getDataDirectory();
            if (!Files.exists(dataDir, new LinkOption[0])) {
                Files.createDirectories(dataDir, new FileAttribute[0]);
            }
            this.dataFile = dataDir.resolve("morphs.dat");
            this.loadData();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unmorphPlayer(Player player) {
        try {
            if (this.applyModel(player, "player")) {
                CompletableFuture.delayedExecutor(500L, TimeUnit.MILLISECONDS).execute(() -> {
                    if (player.getWorld() != null) {
                        player.getWorld().execute(() -> {
                            try {
                                PlayerSkinComponent skin;
                                Ref ref = player.getReference();
                                if (ref.getStore().getComponent(ref, ModelComponent.getComponentType()) != null) {
                                    ref.getStore().removeComponent(ref, ModelComponent.getComponentType());
                                }
                                if ((skin = (PlayerSkinComponent)ref.getStore().getComponent(ref, PlayerSkinComponent.getComponentType())) != null) {
                                    skin.setNetworkOutdated();
                                    ref.getStore().putComponent(ref, PlayerSkinComponent.getComponentType(), (Component)skin);
                                }
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                });
                player.sendMessage(Message.raw((String)"Unmorphing..."));
            } else {
                Ref ref = player.getReference();
                if (ref.getStore().getComponent(ref, ModelComponent.getComponentType()) != null) {
                    ref.getStore().removeComponent(ref, ModelComponent.getComponentType());
                }
                player.sendMessage(Message.raw((String)"Unmorphed (Fallback)."));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(Message.raw((String)("Unmorph Failed: " + e.getMessage())));
        }
    }

    public boolean applyModel(Player player, String id) {
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

    protected void start() {
        System.out.println("[MorphMod] Start");
        this.timer.scheduleAtFixedRate(new TimerTask(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                try {
                    Set<World> set = Main.this.pollingWorlds;
                    synchronized (set) {
                        for (World world : Main.this.pollingWorlds) {
                            world.execute(() -> Main.this.checkDeaths(world));
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000L, 200L);
    }

    protected void shutdown() {
        this.timer.cancel();
        this.saveData();
    }

    private void onPlayerConnect(PlayerConnectEvent event) {
        World world;
        Player player = event.getPlayer();
        if (player != null && (world = player.getWorld()) != null && this.pollingWorlds.add(world)) {
            System.out.println("[MorphMod] Started polling for world: " + world.getName());
        }
    }

    private void checkDeaths(World world) {
        try {
            Store store = world.getEntityStore().getStore();
            store.forEachChunk((chunk, buffer) -> {
                int size = chunk.size();
                for (int i = 0; i < size; ++i) {
                    Ref ref;
                    DeathComponent dc = (DeathComponent)chunk.getComponent(i, DeathComponent.getComponentType());
                    if (dc == null || !this.processedDeaths.add(ref = chunk.getReferenceTo(i))) continue;
                    this.handleDeath(dc, (ArchetypeChunk)chunk, i);
                }
            });
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void handleDeath(DeathComponent dc, ArchetypeChunk chunk, int index) {
        Damage damage = dc.getDeathInfo();
        if (damage != null && damage.getSource() instanceof Damage.EntitySource) {
            Damage.EntitySource source = (Damage.EntitySource)damage.getSource();
            Ref killerRef = source.getRef();
            try {
                UUID uuid;
                String id;
                ModelComponent victimModel;
                Player killer = (Player)killerRef.getStore().getComponent(killerRef, Player.getComponentType());
                if (killer != null && (victimModel = (ModelComponent)chunk.getComponent(index, ModelComponent.getComponentType())) != null && victimModel.getModel() != null && (id = victimModel.getModel().getModelAssetId()) != null && this.unlockedMorphs.computeIfAbsent(uuid = this.getPlayerUUID(killer), k -> new HashSet()).add(id)) {
                    System.out.println("[MorphMod] Unlocked " + id);
                    killer.sendMessage(Message.raw((String)"Unlocked: ").insert(Message.raw((String)id).color("aqua")));
                    this.saveData();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private void loadData() {
        if (!Files.exists(this.dataFile, new LinkOption[0])) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(this.dataFile, new OpenOption[0]));){
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                this.unlockedMorphs = (Map)obj;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void saveData() {
        if (this.dataFile == null) {
            return;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(this.dataFile, new OpenOption[0]));){
            oos.writeObject(this.unlockedMorphs);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    UUID getPlayerUUID(Player player) {
        try {
            Ref ref = player.getReference();
            Store store = ref.getStore();
            PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
            if (playerRef != null) {
                return playerRef.getUuid();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return player.getUuid();
    }
}
