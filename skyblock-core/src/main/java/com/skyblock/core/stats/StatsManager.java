package com.skyblock.core.stats;

import com.skyblock.core.model.Stat;
import com.skyblock.core.manager.StatManager;

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

    private static final StatsManager INSTANCE = new StatsManager();

    /**
     * Immutable snapshot of all stats for a single player at a point in time.
     */
    public static final class PlayerStats {
        private final Map<Stat, Double> stats;
        private final CombatStatsManager.CombatStats combat;

        PlayerStats(Map<Stat, Double> stats, CombatStatsManager.CombatStats combat) {
            this.stats = stats;
            this.combat = combat;
        }

        /**
         * Returns the effective value (base + bonus) of a SkyBlock stat.
         *
         * @param stat the stat to look up
         * @return the effective value, {@code 0} if unknown
         */
        public double getStat(Stat stat) {
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
        Map<Stat, Double> statValues = new EnumMap<>(Stat.class);
        for (Stat stat : Stat.values()) {
            statValues.put(stat, statManager.getStat(playerId, stat));
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

    /**
     * Returns the player's effective maximum health after applying the
     * Hypixel SkyBlock defence-to-HP formula:
     * {@code effectiveHp = health × (1 + defense / 100)}.
     *
     * @param playerId the player's UUID
     * @return the player's effective HP
     */
    public double getEffectiveHealth(UUID playerId) {
        PlayerStats snap = getCachedStats(playerId);
        double health = snap.getStat(Stat.HEALTH);
        double defense = snap.getStat(Stat.DEFENSE);
        return health * (1.0 + defense / 100.0);
    }

    /**
     * Applies the Hypixel SkyBlock defence-to-HP formula to raw inputs:
     * {@code effectiveHp = baseHP × (1 + defense / 100)}.
     *
     * @param defense the player's defence stat
     * @param baseHP  the player's base maximum health
     * @return the effective HP
     */
    public static double getEffectiveHealth(double defense, double baseHP) {
        return baseHP * (1.0 + defense / 100.0);
    }

    /**
     * Returns the magic find contributed by a player's luck stat, derived as
     * {@code luckStat / 2}.
     *
     * @param luckStat the player's luck stat
     * @return the magic find from luck
     */
    public static double getMagicFind(double luckStat) {
        return luckStat / 2.0;
    }
}
