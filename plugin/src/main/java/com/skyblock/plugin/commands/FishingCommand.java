package com.skyblock.plugin.commands;

import com.skyblock.core.fishing.FishingManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class FishingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        UUID id = player.getUniqueId();
        FishingManager manager = FishingManager.getInstance();

        String sub = args.length >= 1 ? args[0].toLowerCase() : "level";
        switch (sub) {
            case "level" -> {
                int level = manager.getLevel(id);
                double xp = manager.getXp(id);
                player.sendMessage("Fishing Level: " + level + " | XP: " + String.format("%.1f", xp));
            }
            case "stats" -> {
                int caught = manager.getTotalFishCaught(id);
                player.sendMessage("=== Fishing Stats ===");
                player.sendMessage("Total fish caught: " + caught);
                player.sendMessage("Treasure counts:");
                for (FishingManager.FishingTreasure treasure : FishingManager.FishingTreasure.values()) {
                    int count = manager.getTreasureCatchCount(id, treasure);
                    if (count > 0) {
                        player.sendMessage("  " + treasure.displayName + ": " + count);
                    }
                }
            }
            case "loot" -> {
                int level = manager.getLevel(id);
                player.sendMessage("=== Available Loot (Level " + level + ") ===");
                for (FishingManager.FishingTreasure treasure : FishingManager.FishingTreasure.values()) {
                    if (treasure.minLevel <= level) {
                        player.sendMessage(String.format("  %s (min lvl %d, %.0f%% chance)",
                                treasure.displayName, treasure.minLevel, treasure.dropChance * 100));
                    }
                }
            }
            case "creatures" -> {
                int level = manager.getLevel(id);
                player.sendMessage("=== Sea Creatures (Level " + level + ") ===");
                for (FishingManager.SeaCreature creature : FishingManager.SeaCreature.values()) {
                    if (creature.minLevel <= level) {
                        player.sendMessage(String.format("  %s (min lvl %d, %.0f%% chance)",
                                creature.name(), creature.minLevel, creature.spawnChance * 100));
                    }
                }
            }
            default -> player.sendMessage("Usage: /skyblock fishing <level|stats|loot|creatures>");
        }
        return true;
    }
}
