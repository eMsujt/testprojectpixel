package com.skyblock.core.notification;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton managing per-player SkyBlock notifications.
 *
 * <p>Notifications are queued while a player is offline and flushed when they
 * join. Players can opt out; suppressed messages are silently discarded.</p>
 *
 * <p>Not thread-safe; access from the main server thread only.</p>
 */
public final class NotificationManager {

    private static final NotificationManager INSTANCE = new NotificationManager();

    /** Pending notifications for players who are currently offline. */
    private final Map<UUID, Deque<String>> pending = new HashMap<>();

    /** Players who have disabled notifications. */
    private final Set<UUID> disabled = new HashSet<>();

    private NotificationManager() {}

    public static NotificationManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Sending
    // -------------------------------------------------------------------------

    /**
     * Sends {@code message} to an online player immediately, or queues it for
     * delivery on their next login if they are offline.
     *
     * @param playerId the target player's UUID
     * @param message  the notification text (may contain colour codes)
     * @param player   the online {@link org.bukkit.entity.Player}, or {@code null} if offline
     */
    public void send(UUID playerId, String message, org.bukkit.entity.Player player) {
        if (disabled.contains(playerId)) {
            return;
        }
        if (player != null && player.isOnline()) {
            player.sendMessage(message);
        } else {
            pending.computeIfAbsent(playerId, k -> new ArrayDeque<>()).add(message);
        }
    }

    /**
     * Flushes all queued notifications to {@code player} and clears the queue.
     *
     * @param playerId the player's UUID
     * @param player   the online {@link org.bukkit.entity.Player}
     */
    public void flush(UUID playerId, org.bukkit.entity.Player player) {
        Deque<String> queue = pending.remove(playerId);
        if (queue == null || disabled.contains(playerId)) {
            return;
        }
        for (String msg : queue) {
            player.sendMessage(msg);
        }
    }

    // -------------------------------------------------------------------------
    // Preferences
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if notifications are enabled for the given player.
     *
     * @param playerId the player's UUID
     */
    public boolean isEnabled(UUID playerId) {
        return !disabled.contains(playerId);
    }

    /**
     * Enables notifications for the given player (default state).
     *
     * @param playerId the player's UUID
     */
    public void enable(UUID playerId) {
        disabled.remove(playerId);
    }

    /**
     * Disables notifications for the given player.
     *
     * @param playerId the player's UUID
     */
    public void disable(UUID playerId) {
        disabled.add(playerId);
    }

    /**
     * Toggles notification state and returns the new state.
     *
     * @param playerId the player's UUID
     * @return {@code true} if notifications are now enabled, {@code false} if disabled
     */
    public boolean toggle(UUID playerId) {
        if (disabled.contains(playerId)) {
            disabled.remove(playerId);
            return true;
        } else {
            disabled.add(playerId);
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Cleanup
    // -------------------------------------------------------------------------

    /**
     * Removes all state associated with the given player.
     * Call on permanent player removal, not on every logout (queued messages survive logouts).
     *
     * @param playerId the player's UUID
     */
    public void remove(UUID playerId) {
        pending.remove(playerId);
        disabled.remove(playerId);
    }
}
