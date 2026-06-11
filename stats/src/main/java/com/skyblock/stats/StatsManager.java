package com.skyblock.stats;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks each player's current stat values, defaulting to the base value
 * defined on each {@link PlayerStat} constant.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple
 * threads.</p>
 */
public final class StatsManager {

    private final Map<UUID, EnumMap<PlayerStat, Double>> playerStats = new HashMap<>();

    /**
     * Returns the player's current value for the given stat.
     *
     * @param playerId the player's UUID
     * @param stat     the stat to query
     * @return the current value, or the stat's base value if never set
     */
    public double getStat(UUID playerId, PlayerStat stat) {
        EnumMap<PlayerStat, Double> stats = playerStats.get(playerId);
        if (stats == null) {
            return stat.getBaseValue();
        }
        return stats.getOrDefault(stat, stat.getBaseValue());
    }

    /**
     * Sets the player's value for the given stat.
     *
     * @param playerId the player's UUID
     * @param stat     the stat to set
     * @param value    the new value
     */
    public void setStat(UUID playerId, PlayerStat stat, double value) {
        playerStats.computeIfAbsent(playerId, k -> new EnumMap<>(PlayerStat.class))
                .put(stat, value);
    }

    /**
     * Adds {@code delta} to the player's current value for the given stat.
     *
     * @param playerId the player's UUID
     * @param stat     the stat to modify
     * @param delta    the amount to add (may be negative)
     */
    public void addStat(UUID playerId, PlayerStat stat, double delta) {
        setStat(playerId, stat, getStat(playerId, stat) + delta);
    }

    /**
     * Resets all of the player's stats back to their base values.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        playerStats.remove(playerId);
    }
}
