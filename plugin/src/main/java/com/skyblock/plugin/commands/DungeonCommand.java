package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.DungeonManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class DungeonCommand implements CommandExecutor {

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
            case "stats" -> handleStats(player);
            default      -> sendHelp(player);
        }
        return true;
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        DungeonManager manager = DungeonManager.getInstance();
        int currentFloor = manager.getDungeonFloor(id);
        int highestFloor = manager.getHighestFloor(id);
        Map<String, Integer> completions = manager.getFloorCompletions(id);

        player.sendMessage("=== Dungeon Stats ===");
        player.sendMessage("Current floor: F" + currentFloor);
        player.sendMessage("Highest floor reached: " + (highestFloor > 0 ? "F" + highestFloor : "None"));
        if (completions.isEmpty()) {
            player.sendMessage("No floor completions yet.");
        } else {
            player.sendMessage("Floor completions:");
            for (Map.Entry<String, Integer> entry : completions.entrySet()) {
                player.sendMessage("  " + entry.getKey() + " — " + entry.getValue() + "x");
            }
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Dungeon Commands ===");
        player.sendMessage("/dungeon        — show your dungeon stats");
        player.sendMessage("/dungeon stats  — show current floor, highest floor, and completions");
    }
}
