package com.skyblock.combat;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks each player's combat stats and performs damage calculations.
 *
 * <p>Stats default to {@link CombatStat#getBaseValue()} until modified.
 * Not thread-safe; synchronize externally if accessed from multiple
 * threads.</p>
 */
public final class CombatManager {

    private final Map<UUID, Map<CombatStat, Double>> playerStats = new HashMap<>();

    /**
     * Returns the player's current value for a stat.
     *
     * @param playerId the player's UUID
     * @param stat     the stat to look up
     * @return the current value, or the stat's base value if never modified
     */
    public double getStat(UUID playerId, CombatStat stat) {
        Objects.requireNonNull(stat, "stat");
        Map<CombatStat, Double> stats = playerStats.get(playerId);
        if (stats == null) {
            return stat.getBaseValue();
        }
        return stats.getOrDefault(stat, stat.getBaseValue());
    }

    /**
     * Sets the player's value for a stat, replacing any previous value.
     *
     * @param playerId the player's UUID
     * @param stat     the stat to set
     * @param value    the new value
     */
    public void setStat(UUID playerId, CombatStat stat, double value) {
        Objects.requireNonNull(stat, "stat");
        playerStats.computeIfAbsent(playerId, id -> new EnumMap<>(CombatStat.class))
                .put(stat, value);
    }

    /**
     * Adds a bonus (or, if negative, a penalty) to the player's stat.
     *
     * @param playerId the player's UUID
     * @param stat     the stat to modify
     * @param amount   the amount to add, may be negative
     * @return the stat's value after the change
     */
    public double addStat(UUID playerId, CombatStat stat, double amount) {
        double updated = getStat(playerId, stat) + amount;
        setStat(playerId, stat, updated);
        return updated;
    }

    /**
     * Resets all of the player's stats back to their base values.
     *
     * @param playerId the player's UUID
     */
    public void resetStats(UUID playerId) {
        playerStats.remove(playerId);
    }

    /**
     * Calculates the damage of a hit by the player using their current
     * strength, crit chance and crit damage stats.
     *
     * @param playerId   the attacker's UUID
     * @param baseDamage the raw damage of the weapon or attack, before bonuses
     * @return the final damage dealt, never negative
     * @see CombatEngine#calculateDamage(double, double, double, double)
     */
    public double calculateDamage(UUID playerId, double baseDamage) {
        return CombatEngine.calculateDamage(
                baseDamage,
                getStat(playerId, CombatStat.STRENGTH),
                getStat(playerId, CombatStat.CRIT_CHANCE),
                getStat(playerId, CombatStat.CRIT_DAMAGE));
    }
}
