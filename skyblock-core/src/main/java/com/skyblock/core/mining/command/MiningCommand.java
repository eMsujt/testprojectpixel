package com.skyblock.core.mining.command;

import com.skyblock.core.mining.manager.MiningManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /mining} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /mining}           — show mining level, XP, and speed bonus</li>
 *   <li>{@code /mining ores}      — list all ore types and their XP values</li>
 *   <li>{@code /mining speedbonus} — show the full speed-bonus table</li>
 * </ul>
 * </p>
 */
public final class MiningCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("ores", "speedbonus", "zones");

    private final MiningManager miningManager;

    public MiningCommand(MiningManager miningManager) {
        if (miningManager == null) {
            throw new IllegalArgumentException("miningManager must not be null");
        }
        this.miningManager = miningManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleStatus(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "ores"       -> handleOres(player);
            case "speedbonus" -> handleSpeedBonus(player);
            case "zones"      -> handleZones(player);
            default           -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return SUBCOMMANDS.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleStatus(Player player) {
        UUID id = player.getUniqueId();
        int level = miningManager.getLevel(id);
        double xp = miningManager.getXp(id);
        int speedBonus = miningManager.getSpeedBonusForPlayer(id);
        player.sendMessage("=== Mining ===");
        player.sendMessage("Level: " + level + "  XP: " + String.format("%.1f", xp));
        player.sendMessage("Speed Bonus: +" + speedBonus);
    }

    private void handleOres(Player player) {
        player.sendMessage("=== Ore XP Values ===");
        for (MiningManager.OreType ore : MiningManager.OreType.values()) {
            player.sendMessage("  " + ore.name() + ": " + ore.getXp() + " XP");
        }
    }

    private void handleSpeedBonus(Player player) {
        player.sendMessage("=== Mining Speed Bonus Table ===");
        for (MiningManager.MiningSpeedBonus entry : miningManager.getSpeedTable()) {
            String range = entry.getMinLevel() == entry.getMaxLevel()
                    ? "Level " + entry.getMinLevel()
                    : "Level " + entry.getMinLevel() + "-" + entry.getMaxLevel();
            player.sendMessage("  " + range + ": +" + entry.getSpeedBonus());
        }
    }

    private void handleZones(Player player) {
        int level = miningManager.getLevel(player.getUniqueId());
        player.sendMessage("=== Mining Zones (your level: " + level + ") ===");
        for (MiningManager.MiningZone zone : MiningManager.MiningZone.values()) {
            String status = level >= zone.getMinLevel() ? "Unlocked" : "Requires level " + zone.getMinLevel();
            player.sendMessage("  " + zone.getDisplayName() + ": " + status);
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Mining Commands ===");
        player.sendMessage("/mining                — show mining level, XP, and speed bonus");
        player.sendMessage("/mining ores           — list ore types and their XP values");
        player.sendMessage("/mining speedbonus     — show the full speed-bonus table");
        player.sendMessage("/mining zones          — list mining zones and their level requirements");
    }
}
