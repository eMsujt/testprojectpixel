package com.skyblock.core.stats;

import com.skyblock.core.stat.StatManager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton that provides an aggregated snapshot of every SkyBlock stat for a
 * player, combining the base/bonus values from {@link StatManager} with the
 * combat performance counters from {@link CombatStatsManager}.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class StatsManager {

    /** Every player stat tracked in SkyBlock. */
    public enum StatType {
        MAX_HEALTH,
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

    /** Maps this package's StatType to the backing stat.StatManager.StatType. */
    private static final Map<StatType, StatManager.StatType> STAT_MAP;

    static {
        STAT_MAP = new EnumMap<>(StatType.class);
        STAT_MAP.put(StatType.MAX_HEALTH,   StatManager.StatType.HEALTH);
        STAT_MAP.put(StatType.DEFENSE,      StatManager.StatType.DEFENSE);
        STAT_MAP.put(StatType.STRENGTH,     StatManager.StatType.STRENGTH);
        STAT_MAP.put(StatType.SPEED,        StatManager.StatType.SPEED);
        STAT_MAP.put(StatType.CRIT_CHANCE,  StatManager.StatType.CRIT_CHANCE);
        STAT_MAP.put(StatType.CRIT_DAMAGE,  StatManager.StatType.CRIT_DAMAGE);
        STAT_MAP.put(StatType.INTELLIGENCE, StatManager.StatType.INTELLIGENCE);
        STAT_MAP.put(StatType.FEROCITY,     StatManager.StatType.FEROCITY);
        STAT_MAP.put(StatType.ATTACK_SPEED, StatManager.StatType.ATTACK_SPEED);
        STAT_MAP.put(StatType.MAGIC_FIND,   StatManager.StatType.MAGIC_FIND);
        STAT_MAP.put(StatType.TRUE_DEFENSE, StatManager.StatType.TRUE_DEFENSE);
        STAT_MAP.put(StatType.VITALITY,     StatManager.StatType.VITALITY);
    }

    private static final StatsManager INSTANCE = new StatsManager();

    /**
     * Immutable snapshot of all stats for a single player at a point in time.
     */
    public static final class PlayerStats {
        private final Map<StatType, Double> stats;
        private final CombatStatsManager.CombatStats combat;

        PlayerStats(Map<StatType, Double> stats, CombatStatsManager.CombatStats combat) {
            this.stats = stats;
            this.combat = combat;
        }

        /**
         * Returns the effective value (base + bonus) of a SkyBlock stat.
         *
         * @param stat the stat to look up
         * @return the effective value, {@code 0} if unknown
         */
        public double getStat(StatType stat) {
            return stats.getOrDefault(stat, 0.0);
        }

        /**
         * Returns the player's combat statistics.
         *
         * @return the {@link CombatStatsManager.CombatStats} snapshot
         */
        public CombatStatsManager.CombatStats getCombat() {
            return combat;
        }
    }

    /** Per-player cached snapshots; rebuilt on each explicit refresh. */
    private final Map<UUID, PlayerStats> cache = new HashMap<>();

    private StatsManager() {
    }

    /**
     * Returns the single shared {@code StatsManager} instance.
     *
     * @return the singleton instance
     */
    public static StatsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns a fresh {@link PlayerStats} snapshot for the given player,
     * reading current values from {@link StatManager} and
     * {@link CombatStatsManager}.
     *
     * @param playerId the player to look up
     * @return the aggregated snapshot
     */
    public PlayerStats getStats(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        StatManager statManager = StatManager.getInstance();
        Map<StatType, Double> statValues = new EnumMap<>(StatType.class);
        for (StatType type : StatType.values()) {
            StatManager.StatType backing = STAT_MAP.get(type);
            statValues.put(type, statManager.getStat(playerId, backing));
        }
        CombatStatsManager.CombatStats combat =
                CombatStatsManager.getInstance().getStats(playerId);
        PlayerStats snapshot = new PlayerStats(statValues, combat);
        cache.put(playerId, snapshot);
        return snapshot;
    }

    /**
     * Returns the last cached snapshot for the given player, or a fresh one if
     * none has been computed yet.
     *
     * @param playerId the player to look up
     * @return the cached or freshly built {@link PlayerStats}
     */
    public PlayerStats getCachedStats(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        PlayerStats cached = cache.get(playerId);
        return cached != null ? cached : getStats(playerId);
    }

    /**
     * Removes all cached data for the given player.
     *
     * @param playerId the player to evict
     * @return {@code true} if the player had a cached entry
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return cache.remove(playerId) != null;
    }
}
