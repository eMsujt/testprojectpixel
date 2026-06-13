package com.skyblock.core.level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /skyblock-level} command (alias: {@code /sblevel}).
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /skyblock-level info}          — show your current level, total XP, and XP to next level</li>
 *   <li>{@code /skyblock-level addxp <amount>} — (op) grant XP to yourself</li>
 *   <li>{@code /skyblock-level setxp <amount>} — (op) set your XP to an exact value</li>
 * </ul>
 * </p>
 */
public final class SkyblockLevelCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = Arrays.asList("info", "addxp", "setxp");

    private final SkyblockLevelManager levelManager;

    public SkyblockLevelCommand(SkyblockLevelManager levelManager) {
        this.levelManager = levelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /" + label + " <info|addxp|setxp>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info"   -> handleInfo(player, label);
            case "addxp"  -> handleAddXP(player, label, args);
            case "setxp"  -> handleSetXP(player, label, args);
            default       -> player.sendMessage("Unknown subcommand. Usage: /" + label + " <info|addxp|setxp>");
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

    private void handleInfo(Player player, String label) {
        long totalXP = levelManager.getXP(player.getUniqueId());
        int level    = levelManager.getLevel(player.getUniqueId());
        long toNext  = levelManager.xpToNextLevel(player.getUniqueId());

        player.sendMessage("=== SkyBlock Level ===");
        player.sendMessage("Level:       " + level + " / " + SkyblockLevelManager.MAX_LEVEL);
        player.sendMessage("Total XP:    " + totalXP);
        if (level < SkyblockLevelManager.MAX_LEVEL) {
            player.sendMessage("XP to next:  " + toNext);
        } else {
            player.sendMessage("You have reached the maximum SkyBlock level!");
        }
    }

    private void handleAddXP(Player player, String label, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /" + label + " addxp <amount>");
            return;
        }
        long amount = parsePositiveLong(player, args[1]);
        if (amount <= 0) return;

        int levelBefore = levelManager.getLevel(player.getUniqueId());
        long newXP      = levelManager.addXP(player.getUniqueId(), amount);
        int levelAfter  = levelManager.getLevel(player.getUniqueId());

        player.sendMessage("Added " + amount + " SkyBlock XP. Total: " + newXP
                + " (Level " + levelAfter + ")");
        if (levelAfter > levelBefore) {
            player.sendMessage("Level up! You are now SkyBlock level " + levelAfter + ".");
        }
    }

    private void handleSetXP(Player player, String label, String[] args) {
        if (!player.isOp()) {
            player.sendMessage("You do not have permission to use this subcommand.");
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /" + label + " setxp <amount>");
            return;
        }
        long xp = parseNonNegativeLong(player, args[1]);
        if (xp < 0) return;

        levelManager.setXP(player.getUniqueId(), xp);
        int level = levelManager.getLevel(player.getUniqueId());
        player.sendMessage("SkyBlock XP set to " + xp + " (Level " + level + ").");
    }

    /** Parses a positive long, sending an error to the player on failure; returns -1 on error. */
    private long parsePositiveLong(Player player, String input) {
        try {
            long value = Long.parseLong(input);
            if (value <= 0) {
                player.sendMessage("Amount must be a positive number.");
                return -1;
            }
            return value;
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + input);
            return -1;
        }
    }

    /** Parses a non-negative long, sending an error to the player on failure; returns -1 on error. */
    private long parseNonNegativeLong(Player player, String input) {
        try {
            long value = Long.parseLong(input);
            if (value < 0) {
                player.sendMessage("Amount must not be negative.");
                return -1;
            }
            return value;
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid amount: " + input);
            return -1;
        }
    }
}
