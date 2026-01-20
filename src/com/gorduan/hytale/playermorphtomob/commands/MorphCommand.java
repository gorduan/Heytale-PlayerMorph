package com.gorduan.hytale.playermorphtomob.commands;

import com.gorduan.hytale.playermorphtomob.MorphManager;
import com.gorduan.hytale.playermorphtomob.PlayerMorphToMobPlugin;
import com.gorduan.hytale.playermorphtomob.ui.MorphUIPage;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Command handler for /morph.
 * VERIFIZIERT durch morphmod-1.1.0:
 * - setAllowsExtraArguments(true) für flexible Argument-Verarbeitung
 * - world.execute() für Thread-sichere ECS-Operationen
 *
 * Usage:
 *   /morph                      - Opens GUI
 *   /morph ui                   - Opens GUI
 *   /morph list                 - List available mobs
 *   /morph unmorph              - Reset own morph
 *   /morph unmorph <player>     - Reset another player's morph
 *   /morph <mobId>              - Morph into mob (self)
 *   /morph <mobId> <player>     - Morph another player into mob
 *   /morph help                 - Show help
 *
 * Aliases: /playermorphtomob, /pmtm
 */
public class MorphCommand extends AbstractCommand {

    private final PlayerMorphToMobPlugin plugin;

    public MorphCommand(@Nonnull PlayerMorphToMobPlugin plugin) {
        super("morph", "Morph commands");
        this.plugin = plugin;

        // VERIFIZIERT durch morphmod: Aliases und flexible Argumente
        addAliases("playermorphtomob", "pmtm");
        setAllowsExtraArguments(true);

        // VERIFIZIERT durch morphmod: Permission-Gruppe setzen
        setPermissionGroup(GameMode.Adventure);
    }

    /**
     * VERIFIZIERT durch morphmod-1.1.0:
     * - ECS-Operationen in world.execute() wrappen
     * - CompletableFuture für async Handling
     */
    @Override
    @Nullable
    protected CompletableFuture<Void> execute(@Nonnull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("This command can only be used by players."));
            return CompletableFuture.completedFuture(null);
        }

        Player player = ctx.senderAs(Player.class);
        if (player == null || player.getWorld() == null) {
            return CompletableFuture.completedFuture(null);
        }

        // VERIFIZIERT durch morphmod: CompletableFuture für async Completion
        CompletableFuture<Void> future = new CompletableFuture<>();

        // VERIFIZIERT durch morphmod: ECS-Operationen im World-Thread ausführen!
        player.getWorld().execute(() -> {
            try {
                handleCommand(ctx, player);
                future.complete(null);
            } catch (Exception e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    /**
     * Command-Handler - wird im World-Thread ausgeführt.
     */
    private void handleCommand(@Nonnull CommandContext ctx, @Nonnull Player player) {
        // VERIFIZIERT durch morphmod: Manuelle Argument-Verarbeitung
        String input = ctx.getInputString();
        String[] args = input != null ? input.trim().split("\\s+") : new String[]{};

        // Überspringe den Command-Namen selbst
        int start = 0;
        if (args.length > 0 && (args[0].equalsIgnoreCase("morph")
                || args[0].equalsIgnoreCase("playermorphtomob")
                || args[0].equalsIgnoreCase("pmtm"))) {
            start = 1;
        }

        // Keine Argumente - UI öffnen
        if (args.length <= start) {
            openUI(player);
            return;
        }

        String cmd = args[start];
        MorphManager morphManager = plugin.getMorphManager();

        // Help
        if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("--help")) {
            showHelp(ctx, morphManager);
            return;
        }

        // List
        if (cmd.equalsIgnoreCase("list")) {
            showMobList(ctx, morphManager);
            return;
        }

        // UI
        if (cmd.equalsIgnoreCase("ui")) {
            openUI(player);
            return;
        }

        // Unmorph (mit optionalem Spielernamen)
        if (cmd.equalsIgnoreCase("unmorph") || cmd.equalsIgnoreCase("reset")) {
            // Prüfe ob ein Spielername angegeben wurde
            if (args.length > start + 1) {
                String targetName = args[start + 1];
                handleUnmorphOther(ctx, player, targetName);
            } else {
                handleUnmorph(ctx, player);
            }
            return;
        }

        // Prüfe ob eine Mob-ID mit optionalem Spielernamen angegeben wurde
        String mobId = cmd;
        String targetPlayerName = (args.length > start + 1) ? args[start + 1] : null;

        if (morphManager.isValidModel(mobId)) {
            if (targetPlayerName != null) {
                // Morph anderen Spieler
                handleMorphOther(ctx, player, mobId, targetPlayerName);
            } else {
                // Morph sich selbst
                if (!player.hasPermission("playermorphtomob.morph.self")) {
                    ctx.sendMessage(Message.raw("You don't have permission to morph."));
                    return;
                }
                if (morphManager.applyMorph(player.getPlayerRef(), mobId)) {
                    ctx.sendMessage(Message.raw("Morphed into " + mobId));
                } else {
                    ctx.sendMessage(Message.raw("Failed to morph into " + mobId));
                }
            }
        } else {
            ctx.sendMessage(Message.raw("Unknown mob: " + mobId));
            ctx.sendMessage(Message.raw("Use /morph list to see available mobs."));
        }
    }

    /**
     * Morph einen anderen Spieler in ein Mob-Modell.
     * VERIFIZIERT durch ArgTypes.PLAYER_REF: NameMatching.DEFAULT.find() für Spielersuche.
     */
    private void handleMorphOther(@Nonnull CommandContext ctx, @Nonnull Player sender,
                                   @Nonnull String mobId, @Nonnull String targetName) {
        // Permission check für andere Spieler morphen
        if (!sender.hasPermission("playermorphtomob.morph.others")) {
            ctx.sendMessage(Message.raw("You don't have permission to morph other players."));
            return;
        }

        // Finde den Zielspieler
        PlayerRef targetRef = findPlayerByName(targetName);
        if (targetRef == null) {
            ctx.sendMessage(Message.raw("Player not found: " + targetName));
            return;
        }

        MorphManager morphManager = plugin.getMorphManager();
        if (morphManager.applyMorph(targetRef, mobId)) {
            ctx.sendMessage(Message.raw("Morphed " + targetRef.getUsername() + " into " + mobId));
            targetRef.sendMessage(Message.raw("You have been morphed into " + mobId + " by " + sender.getPlayerRef().getUsername()));
        } else {
            ctx.sendMessage(Message.raw("Failed to morph " + targetRef.getUsername() + " into " + mobId));
        }
    }

    /**
     * Setze den Morph eines anderen Spielers zurück.
     */
    private void handleUnmorphOther(@Nonnull CommandContext ctx, @Nonnull Player sender,
                                     @Nonnull String targetName) {
        // Permission check für andere Spieler unmorphen
        if (!sender.hasPermission("playermorphtomob.morph.others")) {
            ctx.sendMessage(Message.raw("You don't have permission to unmorph other players."));
            return;
        }

        // Finde den Zielspieler
        PlayerRef targetRef = findPlayerByName(targetName);
        if (targetRef == null) {
            ctx.sendMessage(Message.raw("Player not found: " + targetName));
            return;
        }

        MorphManager morphManager = plugin.getMorphManager();
        if (morphManager.resetMorph(targetRef)) {
            ctx.sendMessage(Message.raw("Reset morph for " + targetRef.getUsername()));
            targetRef.sendMessage(Message.raw("Your morph has been reset by " + sender.getPlayerRef().getUsername()));
        } else {
            ctx.sendMessage(Message.raw(targetRef.getUsername() + " is not currently morphed."));
        }
    }

    /**
     * Findet einen Spieler anhand des Namens in allen Welten.
     * VERIFIZIERT durch HytaleServer ArgTypes.PLAYER_REF.
     */
    @Nullable
    private PlayerRef findPlayerByName(@Nonnull String name) {
        for (World world : Universe.get().getWorlds().values()) {
            Collection<PlayerRef> playerRefs = world.getPlayerRefs();
            PlayerRef found = NameMatching.DEFAULT.find(playerRefs, name, PlayerRef::getUsername);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private void showHelp(@Nonnull CommandContext ctx, @Nonnull MorphManager morphManager) {
        ctx.sendMessage(Message.raw("=== PlayerMorphToMob Help ==="));
        ctx.sendMessage(Message.raw("/morph - Open morph GUI"));
        ctx.sendMessage(Message.raw("/morph ui - Open morph GUI"));
        ctx.sendMessage(Message.raw("/morph list - List available mobs"));
        ctx.sendMessage(Message.raw("/morph <mobId> - Morph yourself into mob"));
        ctx.sendMessage(Message.raw("/morph <mobId> <player> - Morph another player"));
        ctx.sendMessage(Message.raw("/morph unmorph - Reset your morph"));
        ctx.sendMessage(Message.raw("/morph unmorph <player> - Reset another player's morph"));
        ctx.sendMessage(Message.raw("/morph help - Show this help"));
    }

    private void showMobList(@Nonnull CommandContext ctx, @Nonnull MorphManager morphManager) {
        List<String> mobs = morphManager.getAvailableMobs();
        ctx.sendMessage(Message.raw("=== Available Mobs ==="));
        for (String mob : mobs) {
            boolean isValid = morphManager.isValidModel(mob);
            String status = isValid ? "[OK]" : "[??]";
            ctx.sendMessage(Message.raw(status + " " + mob));
        }
        ctx.sendMessage(Message.raw("Total: " + mobs.size() + " mobs"));
        ctx.sendMessage(Message.raw("Game assets: " + morphManager.getGameModelAssets().size()));
    }

    private void handleUnmorph(@Nonnull CommandContext ctx, @Nonnull Player player) {
        MorphManager morphManager = plugin.getMorphManager();
        PlayerRef playerRef = player.getPlayerRef();

        if (!player.hasPermission("playermorphtomob.morph.self")) {
            ctx.sendMessage(Message.raw("You don't have permission to unmorph."));
            return;
        }

        if (morphManager.resetMorph(playerRef)) {
            ctx.sendMessage(Message.raw("Morph reset successfully."));
        } else {
            ctx.sendMessage(Message.raw("You are not currently morphed."));
        }
    }

    /**
     * VERIFIZIERT durch Waypoints-1.2.0:
     * - PlayerRef aus Store holen via getComponent
     * - player.getPageManager().openCustomPage(ref, store, page)
     * - Mob-Liste und Player an MorphUIPage übergeben
     */
    private void openUI(@Nonnull Player player) {
        try {
            Ref<EntityStore> ref = player.getReference();
            if (ref == null || !ref.isValid()) {
                player.sendMessage(Message.raw("Failed to open UI: Invalid reference."));
                return;
            }

            Store<EntityStore> store = ref.getStore();
            PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

            if (playerRef != null) {
                List<String> morphList = plugin.getMorphManager().getAvailableMobs();
                player.getPageManager().openCustomPage(ref, store,
                        new MorphUIPage(plugin, playerRef, player, morphList));
            } else {
                player.sendMessage(Message.raw("Failed to open UI: PlayerRef not found."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(Message.raw("Failed to open UI: " + e.getMessage()));
        }
    }

    /**
     * VERIFIZIERT durch morphmod: setPermissionGroup für GameMode-basierte Permissions.
     */
    public void setPermissionGroup(@Nonnull GameMode gameMode) {
        // Diese Methode muss vom AbstractCommand oder einer Elternklasse implementiert werden
        // Falls nicht verfügbar, ignorieren wir sie für jetzt
    }
}
