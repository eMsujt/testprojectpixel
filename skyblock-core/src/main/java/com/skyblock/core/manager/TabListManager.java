package com.skyblock.core.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Drives the player tab list header and footer to match Hypixel's banner.
 *
 * <p>A custom multi-column tab body (the Info/Skills/Players widgets) needs
 * player-list packets, which this plugin does not ship; this reproduces the
 * 1:1 header ("You are playing on MC.HYPIXEL.NET") and footer (the store line)
 * that frame the list.</p>
 */
public final class TabListManager {

    private static final TabListManager INSTANCE = new TabListManager();

    /** Interval between tab list refreshes, in server ticks (2 seconds). */
    private static final long UPDATE_INTERVAL_TICKS = 40L;

    private static final String HEADER = "\n" + ChatColor.GREEN + "You are playing on" + "\n"
            + ChatColor.YELLOW + ChatColor.BOLD + "MC.HYPIXEL.NET" + ChatColor.RESET + "\n";

    private static final String FOOTER = "\n" + ChatColor.YELLOW + "Ranks, Boosters & MORE! "
            + ChatColor.GOLD + ChatColor.BOLD + "STORE.HYPIXEL.NET" + ChatColor.RESET + "\n";

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
        player.setPlayerListHeaderFooter(HEADER, FOOTER);
    }
}
