package com.skyblock.core.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Drives the player tab list header and footer.
 *
 * <p>A single repeating task runs every {@link #UPDATE_INTERVAL_TICKS} ticks and
 * refreshes the header/footer of every online player with the Hypixel-style
 * server banner, online count and the player's purse.</p>
 */
public final class TabListManager {

    private static final TabListManager INSTANCE = new TabListManager();

    /** Interval between tab list refreshes, in server ticks (2 seconds). */
    private static final long UPDATE_INTERVAL_TICKS = 40L;

    private static final String HEADER = ChatColor.GOLD + "" + ChatColor.BOLD + "SKYBLOCK"
            + ChatColor.RESET + "\n" + ChatColor.GRAY + "skyblock.example.net";

    private BukkitTask task;
    private Plugin plugin;

    private TabListManager() {}

    public static TabListManager getInstance() {
        return INSTANCE;
    }

    public void start(Plugin plugin) {
        this.plugin = plugin;
        stop();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    update(player);
                }
            }
        }.runTaskTimer(plugin, 0L, UPDATE_INTERVAL_TICKS);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void update(Player player) {
        double coins = EconomyManager.getInstance().getBalance(player.getUniqueId());
        String footer = ChatColor.YELLOW + "Players: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size()
                + ChatColor.RESET + "\n" + ChatColor.YELLOW + "Purse: " + ChatColor.WHITE + formatCoins(coins);
        player.setPlayerListHeaderFooter(HEADER, footer);
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
