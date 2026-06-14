package com.skyblock.plugin.command.collections;

import com.skyblock.core.collections.CollectionsManager;
import com.skyblock.core.collections.CollectionsManager.CollectionCategory;
import com.skyblock.core.collections.CollectionsManager.CollectionType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class CollectionsCommand implements CommandExecutor {

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
            handleView(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "view" -> handleView(player);
            case "info" -> handleInfo(player, args);
            default     -> sendHelp(player);
        }
        return true;
    }

    private void handleView(Player player) {
        player.sendMessage("=== Collections ===");
        for (CollectionCategory category : CollectionCategory.values()) {
            player.sendMessage("  " + category.getDisplayName() + ":");
            for (CollectionType type : category.getCollections()) {
                int tier = manager.getTier(player.getUniqueId(), type);
                player.sendMessage("    " + type.getDisplayName() + ": Tier " + tier);
            }
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /collections info <collection>");
            return;
        }
        String name = args[1].toUpperCase().replace(' ', '_');
        CollectionType type;
        try {
            type = CollectionType.valueOf(name);
        } catch (IllegalArgumentException e) {
            List<String> names = Arrays.stream(CollectionType.values())
                    .map(t -> t.name().toLowerCase())
                    .collect(Collectors.joining(", "));
            player.sendMessage("Unknown collection '" + args[1] + "'. Valid: " + names);
            return;
        }
        long items = manager.getItems(player.getUniqueId(), type);
        int tier = manager.getTier(player.getUniqueId(), type);
        long toNext = manager.getItemsToNextTier(player.getUniqueId(), type);
        player.sendMessage("=== " + type.getDisplayName() + " ===");
        player.sendMessage("  Items: " + items);
        player.sendMessage("  Tier: " + tier);
        if (toNext > 0) {
            player.sendMessage("  To next tier: " + toNext);
        } else {
            player.sendMessage("  Max tier reached.");
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Collections Commands ===");
        player.sendMessage("/collections — view all collection tiers");
        player.sendMessage("/collections view — view all collection tiers");
        player.sendMessage("/collections info <collection> — view details for a collection");
    }
}
