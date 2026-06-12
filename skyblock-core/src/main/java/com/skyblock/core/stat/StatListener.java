package com.skyblock.core.stat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bukkit listener that manages per-player stat lifecycle in {@link StatManager}:
 * cleans up stat data when a player disconnects.
 */
public final class StatListener implements Listener {

    private final StatManager statManager;

    /**
     * Creates a listener backed by the given {@link StatManager}.
     *
     * @param statManager the stat manager, must not be null
     * @throws IllegalArgumentException if {@code statManager} is null
     */
    public StatListener(StatManager statManager) {
        if (statManager == null) {
            throw new IllegalArgumentException("statManager must not be null");
        }
        this.statManager = statManager;
    }

    /**
     * Clears all stat and bonus data for a player when they leave the server.
     *
     * @param event the player-quit event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        statManager.remove(event.getPlayer().getUniqueId());
    }
}
