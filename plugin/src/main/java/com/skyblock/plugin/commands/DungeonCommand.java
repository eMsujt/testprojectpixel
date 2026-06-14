package com.skyblock.plugin.commands;

import com.skyblock.core.dungeon.DungeonManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

public final class DungeonCommand implements CommandExecutor {

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
            case "stats"  -> handleStats(player);
            case "floors" -> handleFloors(player);
            case "info"   -> handleInfo(player, args);
            default       -> sendHelp(player);
        }
        return true;
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        DungeonManager manager = DungeonManager.getInstance();
        Map<Integer, DungeonManager.FloorRecord> records = manager.getRecords(id);
        OptionalInt highest = manager.getHighestCompletedFloor(id);

        player.sendMessage("=== Dungeon Stats ===");
        if (highest.isEmpty()) {
            player.sendMessage("You have not completed any dungeon floors.");
            return;
        }
        player.sendMessage("Highest floor completed: F" + highest.getAsInt());
        player.sendMessage("Floor breakdown:");
        for (Map.Entry<Integer, DungeonManager.FloorRecord> entry : records.entrySet()) {
            DungeonManager.FloorRecord record = entry.getValue();
            player.sendMessage("  F" + entry.getKey() + " — " + record.getCompletions()
                    + " completions, best score: " + record.getBestScore());
        }
    }

    private void handleFloors(Player player) {
        player.sendMessage("=== Dungeon Floors ===");
        for (Map.Entry<String, int[]> entry : DungeonManager.FLOOR_METADATA.entrySet()) {
            int[] meta = entry.getValue();
            player.sendMessage(entry.getKey() + " — Power req: " + meta[0] + ", Secrets: " + meta[1]);
        }
    }

    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Usage: /skyblock dungeon info <floor>");
            return;
        }
        String floorKey = args[1].toUpperCase();
        int[] meta = DungeonManager.FLOOR_METADATA.get(floorKey);
        if (meta == null) {
            player.sendMessage("Unknown floor: " + args[1] + ". Use /skyblock dungeon floors to list valid floors.");
            return;
        }
        UUID id = player.getUniqueId();
        DungeonManager manager = DungeonManager.getInstance();
        int floorNum = parseFloorNumber(floorKey);
        int completions = floorNum > 0 ? manager.getCompletions(id, floorNum) : 0;
        OptionalInt bestScore = floorNum > 0 ? manager.getBestScore(id, floorNum) : OptionalInt.empty();

        player.sendMessage("=== Floor " + floorKey + " ===");
        player.sendMessage("Required power: " + meta[0]);
        player.sendMessage("Secrets: " + meta[1]);
        player.sendMessage("Your completions: " + completions);
        player.sendMessage("Your best score: " + (bestScore.isPresent() ? bestScore.getAsInt() : "N/A"));
    }

    private int parseFloorNumber(String floorKey) {
        try {
            return Integer.parseInt(floorKey.substring(1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Dungeon Commands ===");
        player.sendMessage("/skyblock dungeon stats         — show your dungeon completion stats");
        player.sendMessage("/skyblock dungeon floors        — list all dungeon floors with requirements");
        player.sendMessage("/skyblock dungeon info <floor>  — show info for a specific floor (e.g. F1, M3)");
    }
}
