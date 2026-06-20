package com.skyblock.core.manager;

import com.skyblock.core.scoreboard.SkyBlockScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ScoreboardManager {

    private static final ScoreboardManager INSTANCE = new ScoreboardManager();

    private static final long UPDATE_INTERVAL_TICKS = 20L;
    private static final String TITLE = ChatColor.YELLOW + "" + ChatColor.BOLD + "SKYBLOCK";

    private final Map<UUID, SkyBlockScoreboard> boards = new HashMap<>();
    private final Map<UUID, BukkitTask> tasks = new HashMap<>();
    private Plugin plugin;

    private ScoreboardManager() {}

    public static ScoreboardManager getInstance() {
        return INSTANCE;
    }

    public void start(Plugin plugin) {
        this.plugin = plugin;
        for (Player player : Bukkit.getOnlinePlayers()) {
            initPlayer(player);
        }
    }

    public void stop() {
        for (BukkitTask task : tasks.values()) {
            task.cancel();
        }
        tasks.clear();
        for (SkyBlockScoreboard board : boards.values()) {
            board.delete();
        }
        boards.clear();
    }

    public void initPlayer(Player player) {
        stopForPlayer(player);
        SkyBlockScoreboard board = new SkyBlockScoreboard(player, TITLE);
        boards.put(player.getUniqueId(), board);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    tasks.remove(player.getUniqueId());
                    return;
                }
                updateScoreboard(player);
            }
        }.runTaskTimer(plugin, 0L, UPDATE_INTERVAL_TICKS);
        tasks.put(player.getUniqueId(), task);
    }

    public void stopForPlayer(Player player) {
        BukkitTask existing = tasks.remove(player.getUniqueId());
        if (existing != null) {
            existing.cancel();
        }
        SkyBlockScoreboard board = boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    public void updateScoreboard(Player player) {
        SkyBlockScoreboard board = boards.get(player.getUniqueId());
        if (board == null) {
            return;
        }
        CalendarManager cal = CalendarManager.getInstance();
        String calendarDate = ChatColor.AQUA + cal.getCurrentMonth().getDisplayName()
                + " " + cal.getCurrentDayOfMonth();
        double coins = EconomyManager.getInstance().getBalance(player.getUniqueId());
        List<String> lines = Arrays.asList(
            calendarDate,
            " ",
            ChatColor.YELLOW + "Coins",
            ChatColor.WHITE + formatCoins(coins),
            "  ",
            ChatColor.AQUA + "Location",
            ChatColor.WHITE + player.getWorld().getName(),
            "   ",
            ChatColor.GRAY + "skyblock.example.net"
        );
        board.updateLines(lines);
    }

    private static String formatCoins(double coins) {
        if (coins >= 1_000_000) {
            return String.format("%.1fM", coins / 1_000_000);
        }
        if (coins >= 1_000) {
            return String.format("%.1fK", coins / 1_000);
        }
        return String.format("%.0f", coins);
    }
}
