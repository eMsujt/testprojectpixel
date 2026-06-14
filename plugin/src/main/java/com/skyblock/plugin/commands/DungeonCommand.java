package com.skyblock.plugin.commands;

import com.skyblock.plugin.managers.DungeonManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
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
            case "stats"       -> handleStats(player);
            case "floor"       -> handleFloor(player, args);
            case "floor-stats" -> handleFloorStats(player);
            case "class"       -> handleClass(player, args);
            case "history"     -> handleHistory(player);
            default            -> sendHelp(player);
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

    private void handleFloor(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /dungeon floor <number>");
            return;
        }
        int floorNum;
        try {
            floorNum = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid floor number: " + args[1]);
            return;
        }
        if (floorNum < 1 || floorNum > 7) {
            player.sendMessage("Floor must be between 1 and 7.");
            return;
        }
        UUID id = player.getUniqueId();
        DungeonManager manager = DungeonManager.getInstance();
        String floorKey = "F" + floorNum;
        int completions = manager.getCompletions(id, floorKey);
        long bestTime = manager.getBestTime(id, floorKey);

        player.sendMessage("=== Floor " + floorNum + " Stats ===");
        player.sendMessage("Completions: " + completions);
        if (bestTime > 0) {
            player.sendMessage("Best time: " + bestTime + "s");
        } else {
            player.sendMessage("Best time: N/A");
        }
    }

    private void handleFloorStats(Player player) {
        UUID id = player.getUniqueId();
        Map<Integer, Integer> completions = DungeonManager.getInstance().getPlayerFloorCompletions(id);

        player.sendMessage("=== Floor Stats ===");
        if (completions.isEmpty()) {
            player.sendMessage("No floor completions recorded.");
        } else {
            for (Map.Entry<Integer, Integer> entry : completions.entrySet()) {
                player.sendMessage("  F" + entry.getKey() + " — " + entry.getValue() + "x");
            }
        }
    }

    private static final java.util.List<String> VALID_CLASSES =
            java.util.Arrays.asList("Healer", "Mage", "Berserker", "Archer", "Tank");

    private void handleClass(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /dungeon class <name>");
            player.sendMessage("Valid classes: " + String.join(", ", VALID_CLASSES));
            return;
        }
        String input = args[1];
        String matched = null;
        for (String cls : VALID_CLASSES) {
            if (cls.equalsIgnoreCase(input)) {
                matched = cls;
                break;
            }
        }
        if (matched == null) {
            player.sendMessage("Invalid class: " + input + ". Valid classes: " + String.join(", ", VALID_CLASSES));
            return;
        }
        UUID id = player.getUniqueId();
        DungeonManager.getInstance().setPlayerClass(id, matched);
        player.sendMessage("Your dungeon class has been set to: " + matched);
    }

    private void handleHistory(Player player) {
        UUID id = player.getUniqueId();
        List<String> history = DungeonManager.getInstance().getRunHistory(id);
        player.sendMessage("=== Dungeon Run History ===");
        if (history.isEmpty()) {
            player.sendMessage("No dungeon runs recorded.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Dungeon Commands ===");
        player.sendMessage("/dungeon              — show your dungeon stats");
        player.sendMessage("/dungeon stats        — show current floor, highest floor, and completions");
        player.sendMessage("/dungeon floor <n>    — show completions and best time for floor N (1-7)");
        player.sendMessage("/dungeon floor-stats   — show all floor completion counts");
        player.sendMessage("/dungeon class <name> — set your dungeon class (Healer, Mage, Berserker, Archer, Tank)");
        player.sendMessage("/dungeon history      — show your dungeon run history");
    }
}
