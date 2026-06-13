package com.skyblock.core.collections;

import com.skyblock.core.collections.CollectionManager.Collection;
import com.skyblock.core.collections.CollectionManager.CollectionCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /collection} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /collection}                     — list all collection types</li>
 *   <li>{@code /collection <type>}              — show the player's total for that collection</li>
 *   <li>{@code /collection category <category>} — list all collections in a category</li>
 *   <li>{@code /collection reset}               — reset all collection progress</li>
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

        Collection collection = parseCollection(args[0]);
        if (collection == null) {
            player.sendMessage("Unknown collection: " + args[0] + ". Use /collection to see all collections.");
            return true;
        }

        long total = collectionManager.getItems(player.getUniqueId(), collection);
        player.sendMessage("=== " + collection.name() + " Collection ===");
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
            for (Collection c : Collection.values()) {
                String name = c.name().toLowerCase();
                if (name.startsWith(lower)) {
                    completions.add(name);
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
        player.sendMessage("=== Collections ===");
        for (Collection c : Collection.values()) {
            long total = collectionManager.getItems(player.getUniqueId(), c);
            player.sendMessage("- " + c.name() + ": " + total);
        }
        player.sendMessage("Use /collection <name> to view a collection.");
    }

    private void sendCategoryList(Player player, CollectionCategory category) {
        player.sendMessage("=== " + category.getDisplayName() + " Collections ===");
        for (Collection c : category.getCollections()) {
            long total = collectionManager.getItems(player.getUniqueId(), c);
            player.sendMessage("- " + c.name() + ": " + total);
        }
    }

    private static Collection parseCollection(String input) {
        for (Collection c : Collection.values()) {
            if (c.name().equalsIgnoreCase(input)) {
                return c;
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
