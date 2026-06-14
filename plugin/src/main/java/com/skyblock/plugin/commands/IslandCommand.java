package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.IslandManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    private void sendHelp(Player player) {
        player.sendMessage("=== Island Commands ===");
        player.sendMessage("/island           — show your island stats");
        player.sendMessage("/island stats     — show your island level and biome");
        player.sendMessage("/island biome     — show your current biome");
        player.sendMessage("/island visitors  — show your visitor count");
    }
}
