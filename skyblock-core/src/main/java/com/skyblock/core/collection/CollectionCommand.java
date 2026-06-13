package com.skyblock.core.collection;

import com.skyblock.core.collection.CollectionManager.CollectionType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Handles the {@code /collection} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /collection}          — list all collection types</li>
 *   <li>{@code /collection <type>}   — show the player's total for that collection</li>
 *   <li>{@code /collection reset}    — reset all collection progress</li>
 * </ul>
 * </p>
 */
public final class CollectionCommand implements TabExecutor {

    private final CollectionManager collectionManager;

    public CollectionCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendCollectionList(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            boolean had = collectionManager.reset(player.getUniqueId());
            player.sendMessage(had ? "Your collection progress has been reset."
                                   : "You have no collection progress to reset.");
            return true;
        }

        CollectionType type = parseType(args[0]);
        if (type == null) {
            player.sendMessage("Unknown collection: " + args[0] + ". Use /collection to see all collections.");
            return true;
        }

        long total = collectionManager.getItems(player.getUniqueId(), type);
        player.sendMessage("=== " + type.name().toLowerCase() + " Collection ===");
        player.sendMessage("Total gathered: " + total);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            List<String> completions = new java.util.ArrayList<>();
            if ("reset".startsWith(lower)) {
                completions.add("reset");
            }
            for (CollectionType t : CollectionType.values()) {
                String name = t.name().toLowerCase();
                if (name.startsWith(lower)) {
                    completions.add(name);
                }
            }
            return completions;
        }
        return Collections.emptyList();
    }

    private void sendCollectionList(Player player) {
        Map<CollectionType, Long> all = collectionManager.getAll(player.getUniqueId());
        player.sendMessage("=== Collections ===");
        for (CollectionType t : CollectionType.values()) {
            long total = all.getOrDefault(t, 0L);
            player.sendMessage("- " + t.name().toLowerCase() + ": " + total);
        }
        player.sendMessage("Use /collection <name> to view a collection.");
    }

    private static CollectionType parseType(String input) {
        for (CollectionType t : CollectionType.values()) {
            if (t.name().equalsIgnoreCase(input)) {
                return t;
            }
        }
        return null;
    }
}
