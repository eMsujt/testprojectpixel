package com.skyblock.core.collections;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CollectionsCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS;

    static {
        List<String> subs = new java.util.ArrayList<>(Arrays.asList("category", "reset"));
        for (CollectionsManager.CollectionType c : CollectionsManager.CollectionType.values()) {
            subs.add(c.name().toLowerCase());
        }
        SUBCOMMANDS = Collections.unmodifiableList(subs);
    }

    private final CollectionsManager collectionsManager;

    public CollectionsCommand(CollectionsManager collectionsManager) {
        if (collectionsManager == null) {
            throw new IllegalArgumentException("collectionsManager must not be null");
        }
        this.collectionsManager = collectionsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleAll(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reset" -> {
                boolean had = collectionsManager.reset(player.getUniqueId());
                player.sendMessage(had ? "Your collection progress has been reset."
                                       : "You have no collection progress to reset.");
            }
            case "category" -> {
                if (args.length < 2) {
                    player.sendMessage("Usage: /collections category <" +
                            Arrays.stream(CollectionsManager.CollectionCategory.values())
                                  .map(c -> c.name().toLowerCase())
                                  .collect(Collectors.joining("|")) + ">");
                    return true;
                }
                CollectionsManager.CollectionCategory category = parseCategory(args[1]);
                if (category == null) {
                    player.sendMessage("Unknown category: " + args[1]);
                    return true;
                }
                handleCategory(player, category);
            }
            default -> {
                CollectionsManager.CollectionType type = parseType(args[0]);
                if (type == null) {
                    player.sendMessage("Unknown collection: " + args[0] +
                            ". Use /collections to see all collections.");
                    return true;
                }
                handleCollection(player, type);
            }
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
        if (args.length == 2 && args[0].equalsIgnoreCase("category")) {
            String prefix = args[1].toLowerCase();
            return Arrays.stream(CollectionsManager.CollectionCategory.values())
                    .map(c -> c.name().toLowerCase())
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleAll(Player player) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== Collections ===");
        for (CollectionsManager.CollectionType c : CollectionsManager.CollectionType.values()) {
            long total = collectionsManager.getItems(id, c);
            int tier = collectionsManager.getTier(id, c);
            player.sendMessage(String.format("  %-22s %d  (Tier %d)", c.name(), total, tier));
        }
        player.sendMessage("Use /collections <name> to view a specific collection.");
    }

    private void handleCollection(Player player, CollectionsManager.CollectionType type) {
        UUID id = player.getUniqueId();
        long total = collectionsManager.getItems(id, type);
        int tier = collectionsManager.getTier(id, type);
        long toNext = collectionsManager.getItemsToNextTier(id, type);
        player.sendMessage("=== " + type.getDisplayName() + " Collection ===");
        player.sendMessage("  Total gathered : " + total);
        player.sendMessage("  Tier           : " + tier);
        if (toNext > 0) {
            player.sendMessage("  To next tier   : " + toNext);
        }
    }

    private void handleCategory(Player player, CollectionsManager.CollectionCategory category) {
        UUID id = player.getUniqueId();
        player.sendMessage("=== " + category.getDisplayName() + " Collections ===");
        for (CollectionsManager.CollectionType c : category.getCollections()) {
            long total = collectionsManager.getItems(id, c);
            int tier = collectionsManager.getTier(id, c);
            player.sendMessage(String.format("  %-22s %d  (Tier %d)", c.name(), total, tier));
        }
    }

    private static CollectionsManager.CollectionType parseType(String input) {
        for (CollectionsManager.CollectionType c : CollectionsManager.CollectionType.values()) {
            if (c.name().equalsIgnoreCase(input)) {
                return c;
            }
        }
        return null;
    }

    private static CollectionsManager.CollectionCategory parseCategory(String input) {
        for (CollectionsManager.CollectionCategory c : CollectionsManager.CollectionCategory.values()) {
            if (c.name().equalsIgnoreCase(input)) {
                return c;
            }
        }
        return null;
    }
}
