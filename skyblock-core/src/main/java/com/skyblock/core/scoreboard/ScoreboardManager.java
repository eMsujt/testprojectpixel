package com.skyblock.core.scoreboard;

import com.skyblock.core.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ScoreboardManager {

    private static final ScoreboardManager INSTANCE = new ScoreboardManager();

    private static final long UPDATE_INTERVAL_TICKS = 20L;
    private static final String OBJECTIVE_NAME = "skyblock_sidebar";

    private final Map<UUID, BukkitTask> tasks = new HashMap<>();
    private Plugin plugin;

    private ScoreboardManager() {}

    public static ScoreboardManager getInstance() {
        return INSTANCE;
    }

    public void start(Plugin plugin) {
        this.plugin = plugin;
        for (Player player : Bukkit.getOnlinePlayers()) {
            startForPlayer(player);
        }
        Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), plugin);
    }

    public void stop() {
        for (BukkitTask task : tasks.values()) {
            task.cancel();
        }
        tasks.clear();
    }

    public void startForPlayer(Player player) {
        stopForPlayer(player);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    tasks.remove(player.getUniqueId());
                    return;
                }
                update(player);
            }
        }.runTaskTimer(plugin, 0L, UPDATE_INTERVAL_TICKS);
        tasks.put(player.getUniqueId(), task);
    }

    public void stopForPlayer(Player player) {
        BukkitTask existing = tasks.remove(player.getUniqueId());
        if (existing != null) {
            existing.cancel();
        }
    }

    public void update(Player player) {
        Scoreboard board = player.getScoreboard();

        Objective existing = board.getObjective(OBJECTIVE_NAME);
        if (existing != null) {
            existing.unregister();
        }

        Objective obj = board.registerNewObjective(OBJECTIVE_NAME, "dummy",
                ChatColor.GOLD + "" + ChatColor.BOLD + "SKYBLOCK");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        double coins = EconomyManager.getInstance().getBalance(player.getUniqueId());

        obj.getScore(ChatColor.YELLOW + "Coins").setScore(6);
        obj.getScore(ChatColor.WHITE + formatCoins(coins)).setScore(5);
        obj.getScore(" ").setScore(4);
        obj.getScore(ChatColor.AQUA + "Location").setScore(3);
        obj.getScore(ChatColor.WHITE + player.getWorld().getName()).setScore(2);
        obj.getScore("  ").setScore(1);
        obj.getScore(ChatColor.GRAY + "skyblock.example.net").setScore(0);
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

    private class ScoreboardListener implements Listener {
        @EventHandler
        public void onJoin(PlayerJoinEvent event) {
            startForPlayer(event.getPlayer());
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent event) {
            stopForPlayer(event.getPlayer());
        }
    }
}
