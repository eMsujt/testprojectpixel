package com.skyblock.core.collections;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class CollectionsCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("view", "amount", "tier", "history");

    private final CollectionsManager manager;

    public CollectionsCommand(CollectionsManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "view"   -> handleView(player);
            case "amount" -> handleAmount(player, args);
            case "tier"    -> handleTier(player, args);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("amount") || args[0].equalsIgnoreCase("tier"))) {
            String prefix = args[1].toLowerCase();
            return CollectionsManager.COLLECTIONS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleView(Player player) {
        Map<String, Long> amounts = manager.getCollectionAmounts(player.getUniqueId());
        Map<String, Integer> tiers = manager.getCollectionTiers(player.getUniqueId());
        player.sendMessage("=== Collections ===");
        for (String collection : CollectionsManager.COLLECTIONS) {
            long amount = amounts.getOrDefault(collection, 0L);
            int tier = tiers.getOrDefault(collection, 0);
            player.sendMessage("  " + collection + ": " + amount + " (tier " + tier + ")");
        }
    }

    private void handleAmount(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /collections amount <collection>");
            return;
        }
        String collection = args[1].toLowerCase();
        if (!CollectionsManager.COLLECTIONS.contains(collection)) {
            player.sendMessage("Unknown collection '" + collection + "'.");
            return;
        }
        long amount = manager.getAmount(player.getUniqueId(), collection);
        int tier = manager.getTier(player.getUniqueId(), collection);
        player.sendMessage("=== " + collection + " ===");
        player.sendMessage("  Amount: " + amount);
        player.sendMessage("  Tier: " + tier);
    }

    private void handleTier(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /collections tier <collection>");
            return;
        }
        String collection = args[1].toLowerCase();
        if (!CollectionsManager.COLLECTIONS.contains(collection)) {
            player.sendMessage("Unknown collection '" + collection + "'.");
            return;
        }
        int tier = manager.getTier(player.getUniqueId(), collection);
        player.sendMessage("Your " + collection + " collection is tier " + tier + ".");
    }

    private void handleHistory(Player player) {
        List<String> history = manager.getCollectionsHistory(player.getUniqueId());
        if (history.isEmpty()) {
            player.sendMessage("No collection history yet.");
            return;
        }
        player.sendMessage("=== Collection History ===");
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Collections Commands ===");
        player.sendMessage("/collections view — view all your collections");
        player.sendMessage("/collections amount <collection> — view amount and tier for a collection");
        player.sendMessage("/collections tier <collection> — view your tier in a collection");
        player.sendMessage("/collections history — view your collection history");
    }
}
