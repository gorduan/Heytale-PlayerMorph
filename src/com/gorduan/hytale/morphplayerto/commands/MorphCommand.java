package com.gorduan.hytale.morphplayerto.commands;

import com.gorduan.hytale.morphplayerto.MorphManager;
import com.gorduan.hytale.morphplayerto.MorphPlayerToPlugin;
import com.gorduan.hytale.morphplayerto.ui.MorphUIPage;
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
 * Supports flexible argument parsing for various subcommands.
 * All ECS operations are executed in the World thread for thread safety.
 *
 * Usage:
 *   /morph                      - Opens GUI
 *   /morph ui                   - Opens GUI
 *   /morph list                 - List available mobs
 *   /morph unmorph              - Reset own morph
 *   /morph unmorph <player>     - Reset another player's morph
 *   /morph <mobId>              - Morph into mob (self)
 *   /morph <mobId> <player>     - Morph another player into mob
 *   /morph <mobId> --hiddenname - Morph and hide nametag
 *   /morph help                 - Show help
 *
 * Aliases: /morphplayerto, /mpt
 */
public class MorphCommand extends AbstractCommand {

    private final MorphPlayerToPlugin plugin;

    public MorphCommand(@Nonnull MorphPlayerToPlugin plugin) {
        super("morph", "Morph commands");
        this.plugin = plugin;

        // Register command aliases and allow flexible argument parsing
        addAliases("morphplayerto", "mpt");
        setAllowsExtraArguments(true);

        // Set permission group for GameMode-based access control
        setPermissionGroup(GameMode.Adventure);
    }

    /**
     * Executes the morph command.
     * Wraps all ECS operations in world.execute() for thread safety.
     * Returns a CompletableFuture for async command handling.
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

        CompletableFuture<Void> future = new CompletableFuture<>();

        // Execute ECS operations in the World thread to prevent threading errors
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
     * Handles command parsing and routing.
     * Called within the World thread context.
     */
    private void handleCommand(@Nonnull CommandContext ctx, @Nonnull Player player) {
        // Parse input arguments manually for flexible command handling
        String input = ctx.getInputString();
        String[] args = input != null ? input.trim().split("\\s+") : new String[]{};

        // Skip the command name itself
        int start = 0;
        if (args.length > 0 && (args[0].equalsIgnoreCase("morph")
                || args[0].equalsIgnoreCase("morphplayerto")
                || args[0].equalsIgnoreCase("mpt"))) {
            start = 1;
        }

        // No arguments - open UI
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

        // Unmorph (with optional player name)
        if (cmd.equalsIgnoreCase("unmorph") || cmd.equalsIgnoreCase("reset")) {
            if (args.length > start + 1) {
                String targetName = args[start + 1];
                handleUnmorphOther(ctx, player, targetName);
            } else {
                handleUnmorph(ctx, player);
            }
            return;
        }

        // Check for --hiddenname flag anywhere in arguments
        boolean hideNametag = false;
        for (int i = start; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("--hiddenname") || args[i].equalsIgnoreCase("--hidenametag")) {
                hideNametag = true;
                break;
            }
        }

        // Check if a mob ID with optional player name was provided
        String mobInput = cmd;
        String targetPlayerName = null;

        // Find target player name (next arg after mob that isn't a flag)
        if (args.length > start + 1) {
            String nextArg = args[start + 1];
            if (!nextArg.startsWith("--")) {
                targetPlayerName = nextArg;
            }
        }

        // Use fuzzy matching to find the model
        String matchedMobId = morphManager.findMatchingModel(mobInput);

        if (matchedMobId != null) {
            if (targetPlayerName != null) {
                // Morph another player
                handleMorphOther(ctx, player, matchedMobId, targetPlayerName, hideNametag);
            } else {
                // Morph self
                if (!player.hasPermission("morphplayerto.morph.self")) {
                    ctx.sendMessage(Message.raw("You don't have permission to morph."));
                    return;
                }
                if (morphManager.applyMorph(player.getPlayerRef(), matchedMobId)) {
                    ctx.sendMessage(Message.raw("Morphed into " + matchedMobId));
                    // Hide nametag if flag was set
                    if (hideNametag) {
                        if (morphManager.setNametagHidden(player.getPlayerRef(), true)) {
                            ctx.sendMessage(Message.raw("Nametag hidden."));
                        }
                    }
                } else {
                    ctx.sendMessage(Message.raw("Failed to morph into " + matchedMobId));
                }
            }
        } else {
            ctx.sendMessage(Message.raw("Unknown mob: " + mobInput));
            ctx.sendMessage(Message.raw("Use /morphplayerto list to see available mobs."));
        }
    }

    /**
     * Morphs another player into a mob model.
     * Uses NameMatching for fuzzy player name lookup across all worlds.
     */
    private void handleMorphOther(@Nonnull CommandContext ctx, @Nonnull Player sender,
                                   @Nonnull String mobId, @Nonnull String targetName, boolean hideNametag) {
        if (!sender.hasPermission("morphplayerto.morph.others")) {
            ctx.sendMessage(Message.raw("You don't have permission to morph other players."));
            return;
        }

        PlayerRef targetRef = findPlayerByName(targetName);
        if (targetRef == null) {
            ctx.sendMessage(Message.raw("Player not found: " + targetName));
            return;
        }

        MorphManager morphManager = plugin.getMorphManager();
        if (morphManager.applyMorph(targetRef, mobId)) {
            ctx.sendMessage(Message.raw("Morphed " + targetRef.getUsername() + " into " + mobId));
            targetRef.sendMessage(Message.raw("You have been morphed into " + mobId + " by " + sender.getPlayerRef().getUsername()));
            // Hide nametag if flag was set
            if (hideNametag) {
                if (morphManager.setNametagHidden(targetRef, true)) {
                    ctx.sendMessage(Message.raw("Nametag hidden for " + targetRef.getUsername()));
                }
            }
        } else {
            ctx.sendMessage(Message.raw("Failed to morph " + targetRef.getUsername() + " into " + mobId));
        }
    }

    /**
     * Resets another player's morph back to their original model.
     */
    private void handleUnmorphOther(@Nonnull CommandContext ctx, @Nonnull Player sender,
                                     @Nonnull String targetName) {
        if (!sender.hasPermission("morphplayerto.morph.others")) {
            ctx.sendMessage(Message.raw("You don't have permission to unmorph other players."));
            return;
        }

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
     * Finds a player by name across all worlds.
     * Uses fuzzy matching for partial name support.
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
        ctx.sendMessage(Message.raw("=== MorphPlayerTo Help ==="));
        ctx.sendMessage(Message.raw("/morph - Open morph GUI"));
        ctx.sendMessage(Message.raw("/morph ui - Open morph GUI"));
        ctx.sendMessage(Message.raw("/morph list - List available mobs"));
        ctx.sendMessage(Message.raw("/morph <mobId> - Morph yourself into mob"));
        ctx.sendMessage(Message.raw("/morph <mobId> <player> - Morph another player"));
        ctx.sendMessage(Message.raw("/morph <mobId> --hiddenname - Morph and hide nametag"));
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

        if (!player.hasPermission("morphplayerto.morph.self")) {
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
     * Opens the morph selection UI for the player.
     * Retrieves PlayerRef from the entity store and passes the available mob list.
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
     * Sets the permission group for GameMode-based access control.
     */
    public void setPermissionGroup(@Nonnull GameMode gameMode) {
        // Method stub - implemented by parent class or Hytale API
    }
}
