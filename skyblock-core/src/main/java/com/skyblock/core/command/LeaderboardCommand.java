package com.skyblock.core.command;

import com.skyblock.core.leaderboard.LeaderboardManager;
import com.skyblock.core.leaderboard.LeaderboardManager.LeaderboardCategory;
import com.skyblock.core.leaderboard.LeaderboardManager.LeaderboardEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the {@code /leaderboard} command.
 *
 * <p>Usage:
 * <ul>
 *   <li>{@code /leaderboard}              — list available categories</li>
 *   <li>{@code /leaderboard <category>}   — show top 10 for that category</li>
 * </ul>
 * </p>
 */
public final class LeaderboardCommand implements TabExecutor {

    private static final int DEFAULT_LIMIT = 10;

    private final LeaderboardManager leaderboardManager;

    public LeaderboardCommand(LeaderboardManager leaderboardManager) {
        this.leaderboardManager = leaderboardManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendCategoryList(sender);
            return true;
        }

        LeaderboardCategory category = parseCategory(args[0]);
        if (category == null) {
            sender.sendMessage("Unknown category: " + args[0] + ". Use /leaderboard to see categories.");
            return true;
        }

        sendLeaderboard(sender, category);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String lower = args[0].toLowerCase();
            return Arrays.stream(LeaderboardCategory.values())
                    .map(c -> c.name().toLowerCase())
                    .filter(s -> s.startsWith(lower))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void sendCategoryList(CommandSender sender) {
        sender.sendMessage("=== Leaderboard Categories ===");
        for (LeaderboardCategory category : LeaderboardCategory.values()) {
            sender.sendMessage("- " + category.name().toLowerCase());
        }
        sender.sendMessage("Use /leaderboard <category> to view rankings.");
    }

    private void sendLeaderboard(CommandSender sender, LeaderboardCategory category) {
        List<LeaderboardEntry> entries = leaderboardManager.getTopEntries(category, DEFAULT_LIMIT);
        sender.sendMessage("=== Top " + DEFAULT_LIMIT + " — " + category.name().toLowerCase() + " ===");
        if (entries.isEmpty()) {
            sender.sendMessage("No data recorded yet.");
            return;
        }
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry entry = entries.get(i);
            long score = Math.round(entry.getScore());
            sender.sendMessage((i + 1) + ". " + entry.getPlayerName() + " — " + score);
        }
    }

    private static LeaderboardCategory parseCategory(String input) {
        for (LeaderboardCategory category : LeaderboardCategory.values()) {
            if (category.name().equalsIgnoreCase(input)) {
                return category;
            }
        }
        return null;
    }
}
