package com.skyblock.core.combat;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's combat stats per {@link CombatStat}.
 *
 * <p>Base values are applied on load; bonuses from equipment or buffs are added
 * on top. Not thread-safe; synchronize externally if accessed from multiple
 * threads.</p>
 */
public final class StatManager {

    /** Every combat stat tracked in SkyBlock. */
    public enum CombatStat {
        HEALTH,
        DEFENSE,
        STRENGTH,
        SPEED,
        CRIT_CHANCE,
        CRIT_DAMAGE,
        INTELLIGENCE,
        FEROCITY,
        ATTACK_SPEED,
        MAGIC_FIND,
        TRUE_DEFENSE,
        VITALITY
    }

    /** Default base values applied to a new player. */
    private static final Map<CombatStat, Double> BASE_VALUES;

    static {
        BASE_VALUES = new EnumMap<>(CombatStat.class);
        BASE_VALUES.put(CombatStat.HEALTH, 100.0);
        BASE_VALUES.put(CombatStat.DEFENSE, 0.0);
        BASE_VALUES.put(CombatStat.STRENGTH, 0.0);
        BASE_VALUES.put(CombatStat.SPEED, 100.0);
        BASE_VALUES.put(CombatStat.CRIT_CHANCE, 20.0);
        BASE_VALUES.put(CombatStat.CRIT_DAMAGE, 50.0);
        BASE_VALUES.put(CombatStat.INTELLIGENCE, 0.0);
        BASE_VALUES.put(CombatStat.FEROCITY, 0.0);
        BASE_VALUES.put(CombatStat.ATTACK_SPEED, 0.0);
        BASE_VALUES.put(CombatStat.MAGIC_FIND, 0.0);
        BASE_VALUES.put(CombatStat.TRUE_DEFENSE, 0.0);
        BASE_VALUES.put(CombatStat.VITALITY, 0.0);
    }

    private static final StatManager INSTANCE = new StatManager();

    /** Per-player base stat overrides; absent entries fall back to {@link #BASE_VALUES}. */
    private final Map<UUID, Map<CombatStat, Double>> playerStats = new HashMap<>();

    /** Per-player bonus stats accumulated from equipment, potions, etc. */
    private final Map<UUID, Map<CombatStat, Double>> playerBonuses = new HashMap<>();

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
    public double getStat(UUID playerId, CombatStat stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        double base = getBaseStat(playerId, stat);
        double bonus = getBonus(playerId, stat);
        return base + bonus;
    }

    /**
     * Returns the base stat value for the given player, falling back to the
     * global default if the player has no override.
     *
     * @param playerId the player to look up
     * @param stat     the stat to retrieve
     * @return the base stat value
     */
    public double getBaseStat(UUID playerId, CombatStat stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        Map<CombatStat, Double> stats = playerStats.get(playerId);
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
    public void setBaseStat(UUID playerId, CombatStat stat, double value) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        playerStats.computeIfAbsent(playerId, id -> new EnumMap<>(CombatStat.class))
                .put(stat, value);
    }

    /**
     * Returns the total bonus for a stat accumulated from equipment/buffs.
     *
     * @param playerId the player to look up
     * @param stat     the stat to retrieve
     * @return the total bonus, {@code 0} if none
     */
    public double getBonus(UUID playerId, CombatStat stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        Map<CombatStat, Double> bonuses = playerBonuses.get(playerId);
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
    public double addBonus(UUID playerId, CombatStat stat, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        Map<CombatStat, Double> bonuses = playerBonuses.computeIfAbsent(
                playerId, id -> new EnumMap<>(CombatStat.class));
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
