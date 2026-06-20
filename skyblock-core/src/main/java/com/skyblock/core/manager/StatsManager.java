package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Convenience facade over {@link StatManager} providing a single
 * {@link #getAll(UUID)} snapshot and per-stat setters for player stats.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class StatsManager {

    private static final StatsManager INSTANCE = new StatsManager();

    private StatsManager() {}

    public static StatsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns a snapshot of all effective (base + bonus) stat values for the given player.
     *
     * @param playerId the player to look up
     * @return unmodifiable map of every {@link Stat} to its effective value
     */
    public Map<Stat, Double> getAll(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        StatManager sm = StatManager.getInstance();
        Map<Stat, Double> snapshot = new EnumMap<>(Stat.class);
        for (Stat stat : Stat.values()) {
            snapshot.put(stat, sm.getStat(playerId, stat));
        }
        return Collections.unmodifiableMap(snapshot);
    }

    /**
     * Returns the effective value (base + bonus) of the given stat for a player.
     *
     * @param playerId the player to look up
     * @param stat     the stat to retrieve
     * @return the effective stat value
     */
    public double get(UUID playerId, Stat stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        return StatManager.getInstance().getStat(playerId, stat);
    }

    /**
     * Overrides the base value of a stat for the given player.
     *
     * @param playerId the player to update
     * @param stat     the stat to set
     * @param value    the new base value
     */
    public void set(UUID playerId, Stat stat, double value) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        StatManager.getInstance().setBaseStat(playerId, stat, value);
    }

    /**
     * Adds a bonus to a stat for the given player.
     *
     * @param playerId the player to update
     * @param stat     the stat to modify
     * @param amount   the bonus amount to add (may be negative)
     * @return the new total bonus for the stat
     */
    public double addBonus(UUID playerId, Stat stat, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        return StatManager.getInstance().addBonus(playerId, stat, amount);
    }

    /**
     * Clears all bonus values for the given player (e.g. after equipment recalculation).
     *
     * @param playerId the player whose bonuses should be reset
     */
    public void clearBonuses(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        StatManager.getInstance().clearBonuses(playerId);
    }

    /**
     * Removes all tracked stat data for the given player.
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had any data
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return StatManager.getInstance().remove(playerId);
    }
}
