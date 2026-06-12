package com.skyblock.core.scoreboard;

import com.skyblock.core.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scheduler.BukkitTask;

/**
 * Singleton that maintains a sidebar scoreboard for every online player,
 * refreshed every 2 seconds (40 ticks).
 *
 * <p>Call {@link #start(Plugin)} once from {@code onEnable} and
 * {@link #stop()} from {@code onDisable}.</p>
 */
public final class ScoreboardManager {

    private static final ScoreboardManager INSTANCE = new ScoreboardManager();

    private static final long UPDATE_INTERVAL_TICKS = 40L;
    private static final String OBJECTIVE_NAME = "skyblock_sidebar";

    private BukkitTask task;

    private ScoreboardManager() {}

    /**
     * Returns the single shared {@code ScoreboardManager} instance.
     *
     * @return the singleton instance
     */
    public static ScoreboardManager getInstance() {
        return INSTANCE;
    }

    /**
     * Starts the repeating update task.
     *
     * @param plugin the owning plugin (used to schedule the task)
     */
    public void start(Plugin plugin) {
        if (task != null) {
            return;
        }
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::updateAll, UPDATE_INTERVAL_TICKS, UPDATE_INTERVAL_TICKS);
    }

    /** Cancels the repeating update task. */
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    /** Updates the sidebar scoreboard for every online player. */
    private void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            update(player);
        }
    }

    /**
     * Rebuilds the sidebar scoreboard for a single player.
     *
     * @param player the player whose scoreboard should be refreshed
     */
    public void update(Player player) {
        Scoreboard board = player.getScoreboard();

        // Replace the objective each update to avoid stale score entries.
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
}
