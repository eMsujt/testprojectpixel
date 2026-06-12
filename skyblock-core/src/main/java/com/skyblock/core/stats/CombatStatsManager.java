package com.skyblock.core.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking per-player combat performance statistics (kills, deaths,
 * damage dealt, and damage taken).
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CombatStatsManager {

    private static final CombatStatsManager INSTANCE = new CombatStatsManager();

    /** Per-player combat stat record. */
    public static final class CombatStats {
        private int kills;
        private int deaths;
        private double damageDealt;
        private double damageTaken;

        CombatStats() {
        }

        public int getKills() {
            return kills;
        }

        public int getDeaths() {
            return deaths;
        }

        public double getDamageDealt() {
            return damageDealt;
        }

        public double getDamageTaken() {
            return damageTaken;
        }
    }

    private final Map<UUID, CombatStats> playerStats = new HashMap<>();

    private CombatStatsManager() {
    }

    /**
     * Returns the single shared {@code CombatStatsManager} instance.
     *
     * @return the singleton instance
     */
    public static CombatStatsManager getInstance() {
        return INSTANCE;
    }

    private CombatStats getOrCreate(UUID playerId) {
        return playerStats.computeIfAbsent(playerId, id -> new CombatStats());
    }

    /**
     * Records a kill for the given player.
     *
     * @param playerId the player who made the kill
     */
    public void recordKill(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        getOrCreate(playerId).kills++;
    }

    /**
     * Records a death for the given player.
     *
     * @param playerId the player who died
     */
    public void recordDeath(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        getOrCreate(playerId).deaths++;
    }

    /**
     * Adds to the cumulative damage dealt by the given player.
     *
     * @param playerId the attacker's UUID
     * @param amount   the damage amount (ignored if &lt;= 0)
     */
    public void addDamageDealt(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            return;
        }
        getOrCreate(playerId).damageDealt += amount;
    }

    /**
     * Adds to the cumulative damage taken by the given player.
     *
     * @param playerId the defender's UUID
     * @param amount   the damage amount (ignored if &lt;= 0)
     */
    public void addDamageTaken(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount <= 0) {
            return;
        }
        getOrCreate(playerId).damageTaken += amount;
    }

    /**
     * Returns the combat stats for the given player, or an empty record if
     * none have been recorded yet.
     *
     * @param playerId the player to look up
     * @return the player's {@link CombatStats}
     */
    public CombatStats getStats(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerStats.getOrDefault(playerId, new CombatStats());
    }

    /**
     * Resets all combat stats for the given player.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerStats.remove(playerId) != null;
    }
}
