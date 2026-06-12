package com.skyblock.core.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton manager for per-player SkyBlock boss bars.
 *
 * <p>Each player has one persistent {@link BossBar} created on join and
 * removed on quit. The title and progress can be updated at any time.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class BossBarManager {

    private static final BossBarManager INSTANCE = new BossBarManager();

    /** Default color used when creating a new boss bar. */
    public static final BarColor DEFAULT_COLOR = BarColor.PURPLE;

    /** Default style used when creating a new boss bar. */
    public static final BarStyle DEFAULT_STYLE = BarStyle.SOLID;

    /** Active boss bars keyed by player UUID. */
    private final Map<UUID, BossBar> bossBars = new HashMap<>();

    private BossBarManager() {
    }

    /**
     * Returns the single shared {@code BossBarManager} instance.
     *
     * @return the singleton instance
     */
    public static BossBarManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates and shows a boss bar for the given player.
     * If a bar already exists for the player it is removed first.
     *
     * @param player the player to show the bar to, must not be null
     * @param title  the initial bar title, must not be null
     */
    public void createBar(Player player, String title) {
        if (player == null) throw new IllegalArgumentException("player must not be null");
        if (title == null) throw new IllegalArgumentException("title must not be null");
        removeBar(player.getUniqueId());
        BossBar bar = Bukkit.createBossBar(title, DEFAULT_COLOR, DEFAULT_STYLE);
        bar.addPlayer(player);
        bossBars.put(player.getUniqueId(), bar);
    }

    /**
     * Updates the title of a player's boss bar.
     * Does nothing if the player has no active bar.
     *
     * @param uuid  the player UUID
     * @param title the new title
     */
    public void setTitle(UUID uuid, String title) {
        BossBar bar = bossBars.get(uuid);
        if (bar != null) {
            bar.setTitle(title);
        }
    }

    /**
     * Updates the progress (0.0–1.0) of a player's boss bar.
     * Does nothing if the player has no active bar.
     *
     * @param uuid     the player UUID
     * @param progress value between 0.0 and 1.0 inclusive
     */
    public void setProgress(UUID uuid, double progress) {
        BossBar bar = bossBars.get(uuid);
        if (bar != null) {
            bar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        }
    }

    /**
     * Updates the color of a player's boss bar.
     * Does nothing if the player has no active bar.
     *
     * @param uuid  the player UUID
     * @param color the new bar color
     */
    public void setColor(UUID uuid, BarColor color) {
        BossBar bar = bossBars.get(uuid);
        if (bar != null && color != null) {
            bar.setColor(color);
        }
    }

    /**
     * Returns the boss bar for the given player, or {@code null} if none exists.
     *
     * @param uuid the player UUID
     * @return the active {@link BossBar}, or {@code null}
     */
    public BossBar getBar(UUID uuid) {
        return bossBars.get(uuid);
    }

    /**
     * Removes and hides the boss bar for the given player.
     * Does nothing if the player has no active bar.
     *
     * @param uuid the player UUID
     */
    public void removeBar(UUID uuid) {
        BossBar bar = bossBars.remove(uuid);
        if (bar != null) {
            bar.removeAll();
        }
    }
}
