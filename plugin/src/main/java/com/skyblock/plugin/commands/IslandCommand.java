package com.skyblock.plugin.commands;

import com.skyblock.core.island.manager.IslandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public final class IslandCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleStats(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "stats"    -> handleStats(player);
            case "biome"    -> handleBiome(player);
            case "visitors" -> handleVisitors(player);
            case "visits"   -> handleVisits(player);
            case "history"  -> handleHistory(player);
            default         -> sendHelp(player);
        }
        return true;
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        IslandManager manager = IslandManager.getInstance();
        player.sendMessage("=== Island ===");
        player.sendMessage("Level: " + manager.getIslandLevel(id));
        player.sendMessage("Biome: " + manager.getIslandBiome(id));
        player.sendMessage("Unlocked: " + manager.isIslandUnlocked(id));
        player.sendMessage("Visitors: " + manager.getVisitorCount(id));
    }

    private void handleBiome(Player player) {
        UUID id = player.getUniqueId();
        IslandManager manager = IslandManager.getInstance();
        player.sendMessage("=== Island Biome ===");
        player.sendMessage("Current Biome: " + manager.getIslandBiome(id));
    }

    private void handleVisitors(Player player) {
        UUID id = player.getUniqueId();
        IslandManager manager = IslandManager.getInstance();
        player.sendMessage("=== Island Visitors ===");
        player.sendMessage("Total Visitors: " + manager.getVisitorCount(id));
    }

    private void handleVisits(Player player) {
        UUID id = player.getUniqueId();
        java.util.List<String> log = IslandManager.getInstance().getVisitLog(id);
        player.sendMessage("=== Island Visit Log ===");
        if (log.isEmpty()) {
            player.sendMessage("No visits recorded.");
            return;
        }
        for (int i = 0; i < log.size(); i++) {
            player.sendMessage((i + 1) + ". " + log.get(i));
        }
    }

    private void handleHistory(Player player) {
        UUID id = player.getUniqueId();
        List<String> history = IslandManager.getInstance().getIslandHistory(id);
        player.sendMessage("=== Island History ===");
        if (history.isEmpty()) {
            player.sendMessage("No history recorded.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Island Commands ===");
        player.sendMessage("/island           — show your island stats");
        player.sendMessage("/island stats     — show your island level and biome");
        player.sendMessage("/island biome     — show your current biome");
        player.sendMessage("/island visitors  — show your visitor count");
        player.sendMessage("/island visits    — show your island visit log");
        player.sendMessage("/island history   — show your island event history");
    }
}
