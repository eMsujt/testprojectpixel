package com.skyblock.core.cooldown;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking per-player, per-key cooldowns as expiry timestamps.
 *
 * <p>Cooldowns are stored as {@code System.currentTimeMillis()} expiry values.
 * Absent entries and expired entries both mean the cooldown is not active.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CooldownManager {

    private static final CooldownManager INSTANCE = new CooldownManager();

    /** Per-player map of cooldown key → expiry timestamp (ms). */
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    private CooldownManager() {
    }

    /**
     * Returns the single shared {@code CooldownManager} instance.
     *
     * @return the singleton instance
     */
    public static CooldownManager getInstance() {
        return INSTANCE;
    }

    /**
     * Sets a cooldown for the given player and key.
     *
     * @param playerId  the player
     * @param key       the cooldown key (e.g. "ability_fireball")
     * @param durationMs the cooldown duration in milliseconds (must be positive)
     * @throws IllegalArgumentException if {@code durationMs} is not positive
     */
    public void setCooldown(UUID playerId, String key, long durationMs) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(key, "key");
        if (durationMs <= 0) {
            throw new IllegalArgumentException("durationMs must be positive");
        }
        cooldowns.computeIfAbsent(playerId, id -> new HashMap<>())
                .put(key, System.currentTimeMillis() + durationMs);
    }

    /**
     * Returns the remaining cooldown time for the given player and key.
     *
     * @param playerId the player
     * @param key      the cooldown key
     * @return remaining milliseconds, or {@code 0} if the cooldown has expired or was never set
     */
    public long getRemainingMs(UUID playerId, String key) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(key, "key");
        Map<String, Long> keys = cooldowns.get(playerId);
        if (keys == null) return 0L;
        Long expiry = keys.get(key);
        if (expiry == null) return 0L;
        long remaining = expiry - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0L;
    }

    /**
     * Returns whether the player is currently on cooldown for the given key.
     *
     * @param playerId the player
     * @param key      the cooldown key
     * @return {@code true} if the cooldown is active
     */
    public boolean isOnCooldown(UUID playerId, String key) {
        return getRemainingMs(playerId, key) > 0;
    }

    /**
     * Clears a specific cooldown for the given player.
     *
     * @param playerId the player
     * @param key      the cooldown key to clear
     * @return {@code true} if a cooldown was present and removed
     */
    public boolean clearCooldown(UUID playerId, String key) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(key, "key");
        Map<String, Long> keys = cooldowns.get(playerId);
        if (keys == null) return false;
        return keys.remove(key) != null;
    }

    /**
     * Clears all cooldowns for the given player (e.g. on quit).
     *
     * @param playerId the player
     * @return {@code true} if the player had any cooldowns
     */
    public boolean clearAll(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return cooldowns.remove(playerId) != null;
    }

    /**
     * Returns an unmodifiable view of all cooldown keys and expiry timestamps for a player.
     *
     * @param playerId the player
     * @return map of key → expiry timestamp; empty if the player has no cooldowns
     */
    public Map<String, Long> getCooldowns(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, Long> keys = cooldowns.get(playerId);
        return keys == null ? Collections.emptyMap() : Collections.unmodifiableMap(keys);
    }
}
