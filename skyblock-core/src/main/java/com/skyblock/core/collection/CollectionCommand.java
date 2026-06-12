package com.skyblock.core.collection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Handles the {@code /collection} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /collection}          — list all tracked collections for the player</li>
 *   <li>{@code /collection <name>}   — show the player's total for that collection</li>
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

        String name = args[0].toUpperCase();
        long total = collectionManager.getItems(player.getUniqueId(), name);
        player.sendMessage("=== " + name + " Collection ===");
        player.sendMessage("Total gathered: " + total);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            if (sender instanceof Player player) {
                List<String> completions = new ArrayList<>();
                if ("reset".startsWith(lower)) {
                    completions.add("reset");
                }
                for (String col : collectionManager.getAll(player.getUniqueId()).keySet()) {
                    if (col.toLowerCase().startsWith(lower)) {
                        completions.add(col.toLowerCase());
                    }
                }
                return completions;
            }
        }
        return Collections.emptyList();
    }

    private void sendCollectionList(Player player) {
        Map<String, Long> all = collectionManager.getAll(player.getUniqueId());
        if (all.isEmpty()) {
            player.sendMessage("You have no collection progress yet.");
            return;
        }
        player.sendMessage("=== Your Collections ===");
        for (Map.Entry<String, Long> entry : all.entrySet()) {
            player.sendMessage("- " + entry.getKey().toLowerCase() + ": " + entry.getValue());
        }
        player.sendMessage("Use /collection <name> to view details.");
    }
}
