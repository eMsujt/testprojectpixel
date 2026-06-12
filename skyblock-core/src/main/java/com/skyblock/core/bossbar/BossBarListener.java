package com.skyblock.core.bossbar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Bukkit listener that creates a boss bar for each player on join and
 * removes it on quit, delegating all state to {@link BossBarManager}.
 */
public final class BossBarListener implements Listener {

    private static final String DEFAULT_TITLE = "SkyBlock";

    private final BossBarManager bossBarManager;

    /**
     * Creates a listener backed by the given {@link BossBarManager}.
     *
     * @param bossBarManager the boss bar manager, must not be null
     * @throws IllegalArgumentException if {@code bossBarManager} is null
     */
    public BossBarListener(BossBarManager bossBarManager) {
        if (bossBarManager == null) {
            throw new IllegalArgumentException("bossBarManager must not be null");
        }
        this.bossBarManager = bossBarManager;
    }

    /**
     * Creates and shows a boss bar for the joining player.
     *
     * @param event the join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        bossBarManager.createBar(player, DEFAULT_TITLE);
    }

    /**
     * Removes the boss bar for the quitting player.
     *
     * @param event the quit event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        bossBarManager.removeBar(player.getUniqueId());
    }
}
