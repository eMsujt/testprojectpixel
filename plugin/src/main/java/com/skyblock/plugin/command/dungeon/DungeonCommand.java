package com.skyblock.plugin.command.dungeon;

import com.skyblock.core.dungeon.DungeonManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class DungeonCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

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
        return true;
    }
}
