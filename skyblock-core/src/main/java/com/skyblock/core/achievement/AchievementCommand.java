package com.skyblock.core.achievement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the {@code /achievement} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /achievement list}              — list all achievements</li>
 *   <li>{@code /achievement info <id>}         — show details for one achievement</li>
 *   <li>{@code /achievement view}              — show which achievements you have completed</li>
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
        if (args.length == 0) {
            sendUsage(sender, label);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list":
                sendList(sender);
                return true;
            case "info":
                if (args.length < 2) {
                    sender.sendMessage("Usage: /" + label + " info <id>");
                    return true;
                }
                sendInfo(sender, args[1]);
                return true;
            case "view":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Only players can view their achievements.");
                    return true;
                }
                sendView(sender, (Player) sender);
                return true;
            default:
                sendUsage(sender, label);
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.asList("list", "info", "view").stream()
                    .filter(s -> s.startsWith(lower))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            String lower = args[1].toLowerCase();
            List<String> ids = new ArrayList<>();
            for (AchievementManager.Achievement a : achievementManager.getAllAchievements()) {
                if (a.getId().startsWith(lower)) ids.add(a.getId());
            }
            return ids;
        }
        return Collections.emptyList();
    }

    private void sendUsage(CommandSender sender, String label) {
        sender.sendMessage("Usage: /" + label + " <list|info <id>|view>");
    }

    private void sendList(CommandSender sender) {
        List<AchievementManager.Achievement> all = achievementManager.getAllAchievements();
        sender.sendMessage("=== Achievements (" + all.size() + " total) ===");
        for (AchievementManager.Achievement a : all) {
            sender.sendMessage("- " + a.getId() + " [" + a.getPoints() + " pts] — " + a.getName());
        }
        sender.sendMessage("Use /achievement info <id> for details.");
    }

    private void sendInfo(CommandSender sender, String id) {
        AchievementManager.Achievement a = achievementManager.getAchievement(id.toLowerCase());
        if (a == null) {
            sender.sendMessage("Unknown achievement: " + id);
            return;
        }
        sender.sendMessage("=== " + a.getName() + " ===");
        sender.sendMessage("ID: " + a.getId());
        sender.sendMessage("Description: " + a.getDescription());
        sender.sendMessage("Points: " + a.getPoints());
    }

    private void sendView(CommandSender sender, Player player) {
        Set<String> done = achievementManager.getCompletedIds(player.getUniqueId());
        int total = achievementManager.getAllAchievements().size();
        int points = achievementManager.getTotalPoints(player.getUniqueId());
        sender.sendMessage("=== Your Achievements: " + done.size() + "/" + total + " (" + points + " pts) ===");
        if (done.isEmpty()) {
            sender.sendMessage("You haven't completed any achievements yet.");
            return;
        }
        for (String id : done) {
            AchievementManager.Achievement a = achievementManager.getAchievement(id);
            if (a != null) {
                sender.sendMessage("- [" + a.getPoints() + " pts] " + a.getName());
            }
        }
    }
}
