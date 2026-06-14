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

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "level"     -> handleLevel(player);
            case "stats"     -> handleStats(player);
            case "loot"      -> handleLoot(player);
            case "creatures" -> handleCreatures(player);
            default          -> sendHelp(player);
        }
        return true;
    }

    private void handleLevel(Player player) {
        UUID id = player.getUniqueId();
        FishingManager manager = FishingManager.getInstance();
        int level = manager.getLevel(id);
        double xp = manager.getXp(id);
        player.sendMessage("=== Fishing ===");
        player.sendMessage("Level: " + level + "  XP: " + String.format("%.1f", xp));
    }

    private void handleStats(Player player) {
        UUID id = player.getUniqueId();
        FishingManager manager = FishingManager.getInstance();
        int level = manager.getLevel(id);
        double xp = manager.getXp(id);
        int caught = manager.getTotalFishCaught(id);
        player.sendMessage("=== Fishing Stats ===");
        player.sendMessage("Level: " + level + "  XP: " + String.format("%.1f", xp));
        player.sendMessage("Total fish caught: " + caught);
    }

    private void handleLoot(Player player) {
        FishingManager manager = FishingManager.getInstance();
        int level = manager.getLevel(player.getUniqueId());
        player.sendMessage("=== Available Loot (level " + level + ") ===");
        for (FishingManager.FishingTreasure treasure : FishingManager.FishingTreasure.values()) {
            if (treasure.minLevel <= level) {
                int caught = manager.getTreasureCatchCount(player.getUniqueId(), treasure);
                player.sendMessage("  " + treasure.displayName
                        + " (min lvl " + treasure.minLevel + ")"
                        + " — caught: " + caught);
            }
        }
    }

    private void handleCreatures(Player player) {
        int level = FishingManager.getInstance().getLevel(player.getUniqueId());
        player.sendMessage("=== Sea Creatures (level " + level + ") ===");
        for (FishingManager.SeaCreature creature : FishingManager.SeaCreature.values()) {
            if (creature.minLevel <= level) {
                player.sendMessage("  " + creature.name()
                        + " (min lvl " + creature.minLevel + ")"
                        + " — spawn chance: " + String.format("%.0f%%", creature.spawnChance * 100));
            }
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Fishing Commands ===");
        player.sendMessage("/skyblock fishing level     — show your fishing level and XP");
        player.sendMessage("/skyblock fishing stats     — show detailed fishing statistics");
        player.sendMessage("/skyblock fishing loot      — list loot available at your level");
        player.sendMessage("/skyblock fishing creatures — list sea creatures you can encounter");
    }
}
