package com.skyblock.core.stats;

import com.skyblock.core.model.Stat;
import com.skyblock.core.stat.StatManager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing per-player effective stat overrides on top of
 * {@link StatManager}'s base/bonus system.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class PlayerStatManager {

    private static final PlayerStatManager INSTANCE = new PlayerStatManager();

    /** Per-player flat stat overrides (supersede StatManager defaults when present). */
    private final Map<UUID, Map<Stat, Double>> playerStats = new HashMap<>();

    private PlayerStatManager() {
    }

    /**
     * Returns the single shared {@code PlayerStatManager} instance.
     *
     * @return the singleton instance
     */
    public static PlayerStatManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the effective stat value for the given player. If no override is
     * stored, falls back to {@link StatManager#getStat}.
     *
     * @param playerId the player to look up
     * @param stat     the stat to retrieve
     * @return the effective stat value
     */
    public double getStat(UUID playerId, Stat stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        Map<Stat, Double> stats = playerStats.get(playerId);
        if (stats != null && stats.containsKey(stat)) {
            return stats.get(stat);
        }
        return StatManager.getInstance().getStat(playerId, stat);
    }

    /**
     * Stores a stat override for the given player.
     *
     * @param playerId the player to update
     * @param stat     the stat to set
     * @param value    the new value
     */
    public void setStat(UUID playerId, Stat stat, double value) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        playerStats.computeIfAbsent(playerId, id -> new EnumMap<>(Stat.class))
                .put(stat, value);
    }

    /**
     * Adds {@code amount} to the player's stored stat override, initialising
     * from the effective value if no override exists yet.
     *
     * @param playerId the player to update
     * @param stat     the stat to modify
     * @param amount   the delta (may be negative)
     * @return the new value
     */
    public double addStat(UUID playerId, Stat stat, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        double current = getStat(playerId, stat);
        double updated = current + amount;
        playerStats.computeIfAbsent(playerId, id -> new EnumMap<>(Stat.class))
                .put(stat, updated);
        return updated;
    }

    /**
     * Returns all stored stat overrides for the given player.
     *
     * @param playerId the player to look up
     * @return a copy of the stored overrides, or an empty map if none
     */
    public Map<Stat, Double> getAllStats(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<Stat, Double> stats = playerStats.get(playerId);
        if (stats == null) {
            return new EnumMap<>(Stat.class);
        }
        return new EnumMap<>(stats);
    }

    /**
     * Removes all stat overrides for the given player.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had stored overrides
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerStats.remove(playerId) != null;
    }
}
