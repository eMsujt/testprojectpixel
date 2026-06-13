package com.skyblock.core.leaderboard;

import com.skyblock.core.leaderboard.LeaderboardManager.LeaderboardEntry;
import com.skyblock.core.leaderboard.LeaderboardManager.LeaderboardType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles the {@code /leaderboard} command.
 *
 * <p>Subcommands:
 * <ul>
 *   <li>{@code /leaderboard}            — list all leaderboard types</li>
 *   <li>{@code /leaderboard <type>}     — show top 10 for the given type</li>
 *   <li>{@code /leaderboard reset}      — clear all leaderboard data</li>
 * </ul>
 * </p>
 */
public final class LeaderboardCommand implements TabExecutor {

    private static final int TOP_LIMIT = 10;

    /** Types whose scores are populated live from source managers on each view request. */
    private static final Set<LeaderboardType> LIVE_TYPES = Collections.unmodifiableSet(
            EnumSet.of(LeaderboardType.SKYBLOCK_LEVEL, LeaderboardType.VAULT_BALANCE, LeaderboardType.STAT_STRENGTH));

    private final LeaderboardManager leaderboardManager;

    public LeaderboardCommand(LeaderboardManager leaderboardManager) {
        if (leaderboardManager == null) {
            throw new IllegalArgumentException("leaderboardManager must not be null");
        }
        this.leaderboardManager = leaderboardManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            handleList(player);
            return true;
        }

        if ("reset".equalsIgnoreCase(args[0])) {
            handleReset(player);
            return true;
        }

        LeaderboardType type = parseType(args[0]);
        if (type == null) {
            player.sendMessage("Unknown leaderboard type: " + args[0]);
            player.sendMessage("Use /leaderboard to see all available types.");
            return true;
        }
        handleTop(player, type);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> options = Arrays.stream(LeaderboardType.values())
                    .map(t -> t.name().toLowerCase())
                    .collect(Collectors.toList());
            options.add("reset");
            return options.stream()
                    .filter(s -> s.startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void handleList(Player player) {
        player.sendMessage("=== Leaderboard Types ===");
        for (LeaderboardType type : LeaderboardType.values()) {
            player.sendMessage("- " + type.getDisplayName() + " (" + type.name().toLowerCase() + ")");
        }
        player.sendMessage("Use /leaderboard <type> to view the top " + TOP_LIMIT + ".");
    }

    private void handleTop(Player player, LeaderboardType type) {
        if (LIVE_TYPES.contains(type)) {
            leaderboardManager.syncFromManagers(buildOnlineNameMap());
        }
        List<LeaderboardEntry> entries = leaderboardManager.getTopEntries(type, TOP_LIMIT);
        player.sendMessage("=== " + type.getDisplayName() + " Top " + TOP_LIMIT + " ===");
        if (entries.isEmpty()) {
            player.sendMessage("No data recorded yet.");
            return;
        }
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry entry = entries.get(i);
            player.sendMessage((i + 1) + ". " + entry.getPlayerName()
                    + " — " + String.format("%.0f", entry.getScore()));
        }
    }

    private void handleReset(Player player) {
        leaderboardManager.clear();
        player.sendMessage("All leaderboard data has been reset.");
    }

    private static Map<UUID, String> buildOnlineNameMap() {
        Map<UUID, String> map = new HashMap<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            map.put(p.getUniqueId(), p.getName());
        }
        return map;
    }

    private static LeaderboardType parseType(String name) {
        for (LeaderboardType type : LeaderboardType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
