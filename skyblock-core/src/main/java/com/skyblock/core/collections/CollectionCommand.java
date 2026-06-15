package com.skyblock.core.collections;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            sendCollectionList(sender);
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

        Collection collection = Collection.parse(args[0]);
        if (collection == null) {
            sender.sendMessage("Unknown collection: " + args[0] + ". Use /collection to see all collections.");
            return true;
        }

        sendCollectionProgress(player, collection);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            List<String> completions = new ArrayList<>();
            if ("reset".startsWith(lower)) completions.add("reset");
            if ("category".startsWith(lower)) completions.add("category");
            for (Collection c : Collection.values()) {
                if (c.name().toLowerCase().startsWith(lower)) completions.add(c.name().toLowerCase());
            }
            return completions;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("category")) {
            String lower = args[1].toLowerCase();
            List<String> completions = new ArrayList<>();
            for (CollectionCategory c : CollectionCategory.values()) {
                String name = c.name().toLowerCase();
                if (name.startsWith(lower)) completions.add(name);
            }
            return completions;
        }
        return Collections.emptyList();
    }

    private void sendCollectionList(CommandSender sender) {
        sender.sendMessage("=== Collections ===");
        for (Collection c : Collection.values()) {
            sender.sendMessage("- " + c.name().toLowerCase());
        }
        sender.sendMessage("Use /collection <name> to view your progress.");
    }

    private void sendCollectionProgress(Player player, Collection collection) {
        long items = collectionManager.getItems(player.getUniqueId(), collection);
        int tier = collectionManager.getTier(player.getUniqueId(), collection);
        long toNext = collectionManager.getItemsToNextTier(player.getUniqueId(), collection);
        player.sendMessage("=== " + collection.getDisplayName() + " Collection ===");
        player.sendMessage("Tier: " + tier + " / " + CollectionManager.MAX_TIER);
        player.sendMessage("Items: " + items);
        if (tier < CollectionManager.MAX_TIER) {
            player.sendMessage("To next tier: " + toNext);
        } else {
            player.sendMessage("Collection maxed out!");
        }
    }

    private void sendCategoryList(Player player, CollectionCategory category) {
        player.sendMessage("=== " + category.getDisplayName() + " Collections ===");
        for (Collection c : category.getCollections()) {
            long total = collectionManager.getItems(player.getUniqueId(), c);
            player.sendMessage("- " + c.name() + ": " + total);
        }
    }

    private static CollectionCategory parseCategory(String input) {
        for (CollectionCategory c : CollectionCategory.values()) {
            if (c.name().equalsIgnoreCase(input)) return c;
        }
        return null;
    }
}
