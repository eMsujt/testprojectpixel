package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.CollectionsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CollectionsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleList(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"    -> handleList(player);
            case "view"    -> handleView(player, args);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void handleList(Player player) {
        UUID id = player.getUniqueId();
        Map<String, Long> counts = CollectionsManager.getInstance().getCollectionCounts(id);
        player.sendMessage("=== Collections ===");
        if (counts.isEmpty()) {
            player.sendMessage("No collections tracked yet.");
            return;
        }
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            player.sendMessage(entry.getKey() + ": " + entry.getValue());
        }
    }

    private void handleView(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /collections view <collection>");
            return;
        }
        String collection = args[1].toUpperCase();
        long count = CollectionsManager.getInstance().getCollectionCount(player.getUniqueId(), collection);
        player.sendMessage("=== " + collection + " ===");
        player.sendMessage("Count: " + count);
    }

    private void handleHistory(Player player) {
        UUID id = player.getUniqueId();
        List<String> history = CollectionsManager.getInstance().getCollectionsHistory(id);
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
        player.sendMessage("/collections            — list all your collections");
        player.sendMessage("/collections list       — list all your collections");
        player.sendMessage("/collections view <col> — view count for a specific collection");
        player.sendMessage("/collections history    — view your collection history");
    }
}
