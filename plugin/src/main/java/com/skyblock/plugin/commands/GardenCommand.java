package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.GardenManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @deprecated Duplicate of {@link com.skyblock.core.garden.GardenCommand}. Use that class instead.
 */
@Deprecated
public final class GardenCommand implements CommandExecutor {

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
            case "stats"   -> handleStats(player);
            case "plots"   -> handlePlots(player);
            case "history" -> handleHistory(player);
            default        -> sendHelp(player);
        }
        return true;
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        GardenManager manager = GardenManager.getInstance();
        player.sendMessage("=== Garden ===");
        player.sendMessage("Level: " + manager.getGardenLevel(id));
        player.sendMessage("Plots: " + manager.getGardenPlots(id));
        player.sendMessage("Unlocked Plots: " + manager.getUnlockedPlots(id));
    }

    private void handlePlots(Player player) {
        UUID id = player.getUniqueId();
        GardenManager manager = GardenManager.getInstance();
        player.sendMessage("=== Garden Plots ===");
        player.sendMessage("Total Plots: " + manager.getGardenPlots(id));
        player.sendMessage("Unlocked: " + manager.getUnlockedPlots(id));
    }

    private void handleHistory(Player player) {
        UUID id = player.getUniqueId();
        java.util.List<String> history = GardenManager.getInstance().getHarvestHistory(id);
        player.sendMessage("=== Garden Harvest History ===");
        if (history.isEmpty()) {
            player.sendMessage("No harvest history found.");
        } else {
            for (int i = 0; i < history.size(); i++) {
                player.sendMessage((i + 1) + ". " + history.get(i));
            }
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Garden Commands ===");
        player.sendMessage("/garden          — show your garden stats");
        player.sendMessage("/garden stats    — show your garden level and plots");
        player.sendMessage("/garden plots    — show your plot counts");
        player.sendMessage("/garden history  — show your crop harvest history");
    }
}
