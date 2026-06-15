package com.skyblock.plugin.command.dungeon;

import com.skyblock.core.manager.DungeonManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class DungeonCommand implements CommandExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("stats", "history");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "history" -> { handleHistory(player); return true; }
                case "stats"   -> { handleStats(player);   return true; }
                default        -> { sendHelp(player);       return true; }
            }
        }

        handleStats(player);
        return true;
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        DungeonManager manager = DungeonManager.getInstance();

        DungeonManager.DungeonClass dungeonClass = manager.getClass(id);
        player.sendMessage("=== Your Dungeons ===");
        player.sendMessage("  Class: " + (dungeonClass != null ? dungeonClass.getDisplayName() : "None"));

        player.sendMessage("  Floor Completions:");
        for (DungeonManager.DungeonFloor floor : DungeonManager.DungeonFloor.values()) {
            int completions = manager.getFloorCompletionCount(id, floor);
            long bestTime = manager.getFloorBestTime(id, floor);
            String timeStr = bestTime == Long.MAX_VALUE ? "N/A" : (bestTime / 1000) + "s";
            player.sendMessage("    " + floor.getDisplayName() + ": " + completions + " completions (Best: " + timeStr + ")");
        }

        DungeonManager.DungeonRun activeRun = manager.getActiveRun(id);
        if (activeRun != null) {
            player.sendMessage("  Active Run: " + activeRun.getType().name());
        }
    }

    private void handleHistory(Player player) {
        UUID id = player.getUniqueId();
        List<String> history = DungeonManager.getInstance().getDungeonHistory(id);
        player.sendMessage("=== Dungeon History ===");
        if (history.isEmpty()) {
            player.sendMessage("No dungeon history found.");
            return;
        }
        for (int i = 0; i < history.size(); i++) {
            player.sendMessage((i + 1) + ". " + history.get(i));
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Dungeon Commands ===");
        player.sendMessage("/dungeon stats    — view your dungeon stats");
        player.sendMessage("/dungeon history  — view your dungeon run history");
    }
}
