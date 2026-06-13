package com.skyblock.core.collection;

import com.skyblock.core.collection.CollectionManager.CollectionCategory;
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

        if (args[0].equalsIgnoreCase("category")) {
            if (args.length < 2) {
                player.sendMessage("Usage: /collection category <" +
                        Arrays.stream(CollectionCategory.values())
                              .map(c -> c.name().toLowerCase())
                              .reduce((a, b) -> a + "|" + b).orElse("") + ">");
                return true;
            }
            CollectionCategory category = parseCategory(args[1]);
            if (category == null) {
                player.sendMessage("Unknown category: " + args[1]);
                return true;
            }
            sendCategoryList(player, category);
            return true;
        }

        CollectionType type = parseType(args[0]);
        if (type == null) {
            player.sendMessage("Unknown collection: " + args[0] + ". Use /collection to see all collections.");
            return true;
        }

        long total = collectionManager.getItems(player.getUniqueId(), type);
        player.sendMessage("=== " + type.displayName + " Collection ===");
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
            if ("category".startsWith(lower)) {
                completions.add("category");
            }
            for (CollectionType t : CollectionType.values()) {
                if (t.itemKey.startsWith(lower)) {
                    completions.add(t.itemKey);
                }
            }
            return completions;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("category")) {
            String lower = args[1].toLowerCase();
            List<String> completions = new java.util.ArrayList<>();
            for (CollectionCategory c : CollectionCategory.values()) {
                String name = c.name().toLowerCase();
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
            player.sendMessage("- " + t.displayName + ": " + total);
        }
        player.sendMessage("Use /collection <name> to view a collection.");
    }

    private void sendCategoryList(Player player, CollectionCategory category) {
        Map<CollectionType, Long> all = collectionManager.getAll(player.getUniqueId());
        player.sendMessage("=== " + category.getDisplayName() + " Collections ===");
        for (CollectionType t : category.getTypes()) {
            long total = all.getOrDefault(t, 0L);
            player.sendMessage("- " + t.displayName + ": " + total);
        }
    }

    private static CollectionType parseType(String input) {
        for (CollectionType t : CollectionType.values()) {
            if (t.name().equalsIgnoreCase(input) || t.itemKey.equalsIgnoreCase(input)) {
                return t;
            }
        }
        return null;
    }

    private static CollectionCategory parseCategory(String input) {
        for (CollectionCategory c : CollectionCategory.values()) {
            if (c.name().equalsIgnoreCase(input)) {
                return c;
            }
        }
        return null;
    }
}
