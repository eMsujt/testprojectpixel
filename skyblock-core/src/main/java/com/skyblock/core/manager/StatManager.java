package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton tracking each player's stats per {@link Stat}.
 *
 * <p>Base values represent the player's intrinsic stat; bonuses accumulate from
 * equipment, potions, and buffs on top. Not thread-safe; synchronize externally
 * if accessed from multiple threads.</p>
 */
public final class StatManager {

    /** Default base values applied to a new player. */
    private static final Map<Stat, Double> BASE_VALUES;

    static {
        BASE_VALUES = new EnumMap<>(Stat.class);
        for (Stat stat : Stat.values()) {
            BASE_VALUES.put(stat, stat.getBaseValue());
        }
    }

    private static final StatManager INSTANCE = new StatManager();

    /** Per-player base stat overrides; absent entries fall back to {@link #BASE_VALUES}. */
    private final Map<UUID, Map<Stat, Double>> playerStats = new HashMap<>();

    /** Per-player bonus stats accumulated from equipment, potions, etc. */
    private final Map<UUID, Map<Stat, Double>> playerBonuses = new HashMap<>();

    private StatManager() {
    }

    /**
     * Returns the single shared {@code StatManager} instance.
     *
     * @return the singleton instance
     */
    public static StatManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the effective value of a stat for the given player (base + bonus).
     *
     * @param playerId the player to look up
     * @param stat     the stat to retrieve
     * @return the effective stat value
     */
    public double getStat(UUID playerId, Stat stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        return getBaseStat(playerId, stat) + getBonus(playerId, stat);
    }

    /**
     * Returns the base stat value for the given player, falling back to the
     * global default if the player has no override.
     *
     * @param playerId the player to look up
     * @param stat     the stat to retrieve
     * @return the base stat value
     */
    public double getBaseStat(UUID playerId, Stat stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        Map<Stat, Double> stats = playerStats.get(playerId);
        if (stats != null && stats.containsKey(stat)) {
            return stats.get(stat);
        }
        return BASE_VALUES.getOrDefault(stat, 0.0);
    }

    /**
     * Sets the base value of a stat for the given player.
     *
     * @param playerId the player to update
     * @param stat     the stat to set
     * @param value    the new base value
     */
    public void setBaseStat(UUID playerId, Stat stat, double value) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        playerStats.computeIfAbsent(playerId, id -> new EnumMap<>(Stat.class))
                .put(stat, value);
    }

    /**
     * Returns the total bonus for a stat accumulated from equipment/buffs.
     *
     * @param playerId the player to look up
     * @param stat     the stat to retrieve
     * @return the total bonus, {@code 0} if none
     */
    public double getBonus(UUID playerId, Stat stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        Map<Stat, Double> bonuses = playerBonuses.get(playerId);
        return bonuses == null ? 0.0 : bonuses.getOrDefault(stat, 0.0);
    }

    /**
     * Adds a bonus to a stat for the given player.
     *
     * @param playerId the player to update
     * @param stat     the stat to modify
     * @param amount   the bonus amount to add (may be negative to remove a bonus)
     * @return the total bonus for the stat after the addition
     */
    public double addBonus(UUID playerId, Stat stat, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        Map<Stat, Double> bonuses = playerBonuses.computeIfAbsent(
                playerId, id -> new EnumMap<>(Stat.class));
        double total = bonuses.getOrDefault(stat, 0.0) + amount;
        bonuses.put(stat, total);
        return total;
    }

    /**
     * Clears all bonuses for the given player (e.g. when recalculating equipment).
     *
     * @param playerId the player whose bonuses should be reset
     */
    public void clearBonuses(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerBonuses.remove(playerId);
    }

    /**
     * Returns an unmodifiable set of all player UUIDs with any tracked stat data.
     *
     * @return set of tracked player UUIDs
     */
    public Set<UUID> getTrackedPlayers() {
        Set<UUID> all = new HashSet<>(playerStats.keySet());
        all.addAll(playerBonuses.keySet());
        return Collections.unmodifiableSet(all);
    }

    /**
     * Removes all stat data for the given player.
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadStats = playerStats.remove(playerId) != null;
        boolean hadBonuses = playerBonuses.remove(playerId) != null;
        return hadStats || hadBonuses;
    }
}
