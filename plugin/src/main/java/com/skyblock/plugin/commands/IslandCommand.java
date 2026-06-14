package com.skyblock.plugin.commands;

import com.skyblock.core.island.IslandManager;
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

        UUID id = player.getUniqueId();
        IslandManager manager = IslandManager.getInstance();
        player.sendMessage("=== Island ===");
        player.sendMessage("Island Level: " + manager.getIslandLevel(id));
        manager.getIsland(id).ifPresentOrElse(island -> {
            player.sendMessage("Members: " + island.getMembers().size());
            String warp = island.getWarpName();
            player.sendMessage("Warp: " + (warp != null ? warp : "None"));
            for (IslandManager.IslandUpgrade upgrade : IslandManager.IslandUpgrade.values()) {
                player.sendMessage(upgrade.getDisplayName() + ": " + island.getUpgradeLevel(upgrade)
                        + "/" + upgrade.getMaxLevel());
            }
        }, () -> player.sendMessage("You do not own an island."));
        return true;
    }
}
