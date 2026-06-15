package com.skyblock.combat.manager;

import com.skyblock.combat.engine.CombatEngine;
import com.skyblock.core.model.Stat;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.entity.EntityType;

/**
 * Tracks each player's combat stats and performs damage calculations.
 *
 * <p>Stats default to {@link Stat#getBaseValue()} until modified.
 * Not thread-safe; synchronize externally if accessed from multiple
 * threads.</p>
 */
public final class CombatManager {

    private final Map<UUID, Map<Stat, Double>> playerStats = new HashMap<>();
    private final Map<UUID, Map<EntityType, Integer>> killCounts = new HashMap<>();

    /**
     * Returns the player's current value for a stat.
     *
     * @param playerId the player's UUID
     * @param stat     the stat to look up
     * @return the current value, or the stat's base value if never modified
     */
    public double getStat(UUID playerId, Stat stat) {
        Objects.requireNonNull(stat, "stat");
        Map<Stat, Double> stats = playerStats.get(playerId);
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
    public void setStat(UUID playerId, Stat stat, double value) {
        Objects.requireNonNull(stat, "stat");
        playerStats.computeIfAbsent(playerId, id -> new EnumMap<>(Stat.class))
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
    public double addStat(UUID playerId, Stat stat, double amount) {
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
     * Records a kill of the given entity type for the player and returns the
     * updated kill count for that entity type.
     *
     * @param playerId   the killer's UUID
     * @param entityType the type of entity killed, must not be null
     * @return the player's total kill count for this entity type after the kill
     */
    public int addKill(UUID playerId, EntityType entityType) {
        Objects.requireNonNull(entityType, "entityType");
        return killCounts.computeIfAbsent(playerId, id -> new EnumMap<>(EntityType.class))
                .merge(entityType, 1, Integer::sum);
    }

    /**
     * Returns the number of times the player has killed the given entity type.
     *
     * @param playerId   the player's UUID
     * @param entityType the entity type to look up, must not be null
     * @return the kill count, zero if the player has never killed this type
     */
    public int getKillCount(UUID playerId, EntityType entityType) {
        Objects.requireNonNull(entityType, "entityType");
        Map<EntityType, Integer> counts = killCounts.get(playerId);
        if (counts == null) {
            return 0;
        }
        return counts.getOrDefault(entityType, 0);
    }

    /**
     * Returns an unmodifiable view of the player's kill counts keyed by entity type.
     *
     * @param playerId the player's UUID
     * @return an unmodifiable map of entity type to kill count, empty if no kills recorded
     */
    public Map<EntityType, Integer> getKillCounts(UUID playerId) {
        Map<EntityType, Integer> counts = killCounts.get(playerId);
        return counts != null ? Map.copyOf(counts) : Map.of();
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
                getStat(playerId, Stat.STRENGTH),
                getStat(playerId, Stat.CRIT_CHANCE),
                getStat(playerId, Stat.CRIT_DAMAGE));
    }
}
