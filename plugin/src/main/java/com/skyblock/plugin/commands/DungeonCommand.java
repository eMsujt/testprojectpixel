package com.skyblock.plugin.commands;

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
        player.sendMessage("=== Dungeons ===");
        player.sendMessage("Class: " + (dungeonClass != null ? dungeonClass.getDisplayName() : "None"));
        for (DungeonManager.DungeonType type : DungeonManager.DungeonType.values()) {
            int completions = manager.getCompletionCount(id, type);
            int bestScore = manager.getBestScore(id, type);
            player.sendMessage(type.name() + " — Completions: " + completions + ", Best Score: " + bestScore);
        }
        return true;
    }
}
