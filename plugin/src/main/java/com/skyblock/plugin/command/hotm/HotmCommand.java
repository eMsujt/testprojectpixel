package com.skyblock.plugin.command.hotm;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class HotmCommand implements CommandExecutor {

    private static final String[][] UPGRADES = {
        {"Mining Speed Boost", "1"},
        {"Vein Seeker",        "1"},
        {"Maniac Miner",       "1"},
        {"Efficient Miner",    "10"},
        {"Mining Fortune",     "10"},
        {"Quick Forge",        "20"},
        {"Titanium Insanium",  "5"},
        {"Professional",       "20"},
        {"Lonesome Miner",     "45"},
        {"Great Explorer",     "20"},
        {"Goblin Killer",      "1"},
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }
        player.sendMessage("=== Heart of the Mountain ===");
        for (String[] upgrade : UPGRADES) {
            player.sendMessage(upgrade[0] + " (max level: " + upgrade[1] + ")");
        }
        return true;
    }
}
