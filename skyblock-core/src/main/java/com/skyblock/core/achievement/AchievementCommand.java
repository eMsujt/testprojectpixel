package com.skyblock.core.achievement;

import com.skyblock.core.achievement.AchievementManager.AchievementData;
import com.skyblock.core.achievement.AchievementManager.AchievementStatus;
import com.skyblock.core.achievement.AchievementManager.AchievementType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Handles the {@code /achievement} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /achievement list}                  — list all available achievement types</li>
 *   <li>{@code /achievement start <type> <goal>}   — start tracking an achievement with a target goal</li>
 *   <li>{@code /achievement status [type]}         — show status/progress for one or all achievements</li>
 *   <li>{@code /achievement reset}                 — reset all achievement progress</li>
 * </ul>
 * </p>
 */
public final class AchievementCommand implements TabExecutor {

    private final AchievementManager achievementManager;

    public AchievementCommand(AchievementManager achievementManager) {
        this.achievementManager = achievementManager;
    }

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
            case "list"   -> handleList(player);
            case "start"  -> handleStart(player, args);
            case "status" -> handleStatus(player, args);
            case "reset"  -> handleReset(player);
            default       -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("list", "start", "status", "reset").stream()
                    .filter(s -> s.startsWith(lower))
                    .toList();
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("start") || sub.equals("status")) {
                String lower = args[1].toLowerCase();
                return Arrays.stream(AchievementType.values())
                        .map(t -> t.name().toLowerCase())
                        .filter(s -> s.startsWith(lower))
                        .toList();
            }
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== Achievement Types ===");
        for (AchievementType type : AchievementType.values()) {
            player.sendMessage("- " + type.name().toLowerCase());
        }
    }

    private void handleStart(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("Usage: /achievement start <type> <goal>");
            return;
        }
        AchievementType type;
        try {
            type = AchievementType.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage("Unknown achievement type: " + args[1]);
            return;
        }
        long goal;
        try {
            goal = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Goal must be a number, got: " + args[2]);
            return;
        }
        if (goal <= 0) {
            player.sendMessage("Goal must be a positive number.");
            return;
        }
        AchievementData existing = achievementManager.getAchievementData(player.getUniqueId(), type);
        if (existing != null && existing.status == AchievementStatus.IN_PROGRESS) {
            player.sendMessage("Achievement " + type.name() + " is already in progress.");
            return;
        }
        achievementManager.startAchievement(player.getUniqueId(), type, goal);
        player.sendMessage("Achievement started: " + type.name() + " (goal: " + goal + ")");
    }

    private void handleStatus(Player player, String[] args) {
        if (args.length >= 2) {
            AchievementType type;
            try {
                type = AchievementType.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Unknown achievement type: " + args[1]);
                return;
            }
            sendAchievementStatus(player, type);
            return;
        }
        player.sendMessage("=== Achievement Status ===");
        boolean any = false;
        for (AchievementType type : AchievementType.values()) {
            AchievementData data = achievementManager.getAchievementData(player.getUniqueId(), type);
            if (data != null) {
                player.sendMessage(type.name() + ": " + data.progress + "/" + data.goal
                        + " [" + data.status.name() + "]");
                any = true;
            }
        }
        if (!any) {
            player.sendMessage("You have no active achievements.");
        }
    }

    private void sendAchievementStatus(Player player, AchievementType type) {
        AchievementData data = achievementManager.getAchievementData(player.getUniqueId(), type);
        if (data == null) {
            player.sendMessage("Achievement " + type.name() + " has not been started.");
            return;
        }
        player.sendMessage("=== " + type.name() + " ===");
        player.sendMessage("Progress: " + data.progress + "/" + data.goal);
        player.sendMessage("Status: " + data.status.name());
    }

    private void handleReset(Player player) {
        achievementManager.reset(player.getUniqueId());
        player.sendMessage("All achievement progress has been reset.");
    }

    private void sendHelp(Player player) {
        player.sendMessage("=== Achievement Commands ===");
        player.sendMessage("/achievement list — list all achievement types");
        player.sendMessage("/achievement start <type> <goal> — start tracking an achievement");
        player.sendMessage("/achievement status [type] — show achievement progress");
        player.sendMessage("/achievement reset — reset all achievement progress");
    }
}
