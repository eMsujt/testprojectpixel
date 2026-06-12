package com.skyblock.core.notification;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Bukkit listener that flushes queued notifications on player join and
 * cleans up any transient state on player quit.
 */
public final class NotificationListener implements Listener {

    private final NotificationManager notificationManager;

    /**
     * Creates a listener backed by the given {@link NotificationManager}.
     *
     * @param notificationManager the manager; must not be null
     */
    public NotificationListener(NotificationManager notificationManager) {
        if (notificationManager == null) {
            throw new IllegalArgumentException("notificationManager must not be null");
        }
        this.notificationManager = notificationManager;
    }

    /**
     * Flushes any pending notifications to the player when they join.
     *
     * @param event the player join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        notificationManager.flush(playerId, event.getPlayer());
    }

    /**
     * No-op on quit: pending messages are intentionally kept so they are
     * delivered on the next login. Only called here to allow future cleanup hooks.
     *
     * @param event the player quit event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // pending messages survive logouts by design; nothing to clean up
    }
}
