package com.skyblock.core.command;

import com.skyblock.core.collections.CollectionManager;
import com.skyblock.core.collections.CollectionManager.Collection;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /collection} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /collection}                  — list all collection names</li>
 *   <li>{@code /collection <name>}            — show progress for that collection</li>
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
            sendCollectionList(sender);
            return true;
        }

        Collection collection = parseCollection(args[0]);
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
            return Arrays.stream(Collection.values())
                    .map(c -> c.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void sendCollectionList(CommandSender sender) {
        sender.sendMessage("=== Collections ===");
        for (Collection collection : Collection.values()) {
            sender.sendMessage("- " + collection.name().toLowerCase());
        }
        sender.sendMessage("Use /collection <name> to view your progress.");
    }

    private void sendCollectionProgress(Player player, Collection collection) {
        long items = collectionManager.getItems(player.getUniqueId(), collection);
        int tier = collectionManager.getTier(player.getUniqueId(), collection);
        long toNext = collectionManager.getItemsToNextTier(player.getUniqueId(), collection);

        player.sendMessage("=== " + collection.name().toLowerCase() + " Collection ===");
        player.sendMessage("Tier: " + tier + " / " + CollectionManager.MAX_TIER);
        player.sendMessage("Items: " + items);
        if (tier < CollectionManager.MAX_TIER) {
            player.sendMessage("To next tier: " + toNext);
        } else {
            player.sendMessage("Collection maxed out!");
        }
    }

    private static Collection parseCollection(String input) {
        for (Collection collection : Collection.values()) {
            if (collection.name().equalsIgnoreCase(input)) {
                return collection;
            }
        }
        return null;
    }
}
