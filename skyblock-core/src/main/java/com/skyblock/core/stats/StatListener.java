package com.skyblock.core.stats;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bukkit listener that evicts the cached {@link StatsManager.PlayerStats}
 * snapshot when a player disconnects.
 */
public final class StatListener implements Listener {

    private final StatsManager statsManager;

    /**
     * Creates a listener backed by the given {@link StatsManager}.
     *
     * @param statsManager the stats manager, must not be null
     * @throws IllegalArgumentException if {@code statsManager} is null
     */
    public StatListener(StatsManager statsManager) {
        if (statsManager == null) {
            throw new IllegalArgumentException("statsManager must not be null");
        }
        this.statsManager = statsManager;
    }

    /**
     * Evicts cached stat data for a player when they leave the server.
     *
     * @param event the player-quit event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        statsManager.remove(event.getPlayer().getUniqueId());
    }
}
