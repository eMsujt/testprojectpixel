package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import com.skyblock.core.scoreboard.SkyBlockScoreboard;
import com.skyblock.core.stats.StatsManager;
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
        int dom = cal.getCurrentDayOfMonth();
        String dateLine = ChatColor.AQUA + cal.getCurrentMonth().getDisplayName()
                + " " + dom + ordinal(dom);

        StatsManager.PlayerStats stats =
                StatsManager.getInstance().getCachedStats(player.getUniqueId());
        int maxHealth = (int) stats.getStat(Stat.HEALTH);
        int currentHealth = maxHealth > 0
                ? (int) Math.ceil(player.getHealth() / player.getMaxHealth() * maxHealth)
                : (int) player.getHealth();
        int defense = (int) stats.getStat(Stat.DEFENSE);

        double coins = EconomyManager.getInstance().getBalance(player.getUniqueId());

        List<String> lines = Arrays.asList(
            " ",
            dateLine,
            "  ",
            ChatColor.RED + "❤ " + ChatColor.GREEN + "Health "
                    + ChatColor.GREEN + currentHealth + ChatColor.RED + "/" + maxHealth,
            ChatColor.GREEN + "❈ Defense " + ChatColor.GREEN + defense,
            "   ",
            ChatColor.GOLD + "Purse: " + ChatColor.WHITE + formatCoins(coins),
            "    ",
            ChatColor.GRAY + "⏣ " + ChatColor.AQUA + player.getWorld().getName(),
            "     ",
            ChatColor.GRAY + "www.hypixel.net"
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

    private static String ordinal(int n) {
        if (n >= 11 && n <= 13) return "th";
        switch (n % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
}
