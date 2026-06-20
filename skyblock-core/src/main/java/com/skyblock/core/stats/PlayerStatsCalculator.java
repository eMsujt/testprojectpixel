package com.skyblock.core.stats;

import com.skyblock.core.model.Stat;

import java.util.Objects;
import java.util.UUID;

/**
 * Singleton facade over {@link StatsManager} that provides a clean
 * {@link #calculate(UUID)} entry point and derived-stat helpers.
 */
public final class PlayerStatsCalculator {

    private static final PlayerStatsCalculator INSTANCE = new PlayerStatsCalculator();

    private final StatsManager statsManager = StatsManager.getInstance();

    private PlayerStatsCalculator() {}

    public static PlayerStatsCalculator getInstance() {
        return INSTANCE;
    }

    /**
     * Returns a fresh aggregated stat snapshot for the given player.
     *
     * @param playerId the player's UUID
     * @return the computed {@link StatsManager.PlayerStats}
     */
    public StatsManager.PlayerStats calculate(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return statsManager.getStats(playerId);
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
        StatsManager.PlayerStats snap = statsManager.getCachedStats(playerId);
        double health = snap.getStat(Stat.HEALTH);
        double defense = snap.getStat(Stat.DEFENSE);
        return health * (1.0 + defense / 100.0);
    }

    /**
     * Evicts the cached stats for the given player (e.g., on logout).
     *
     * @param playerId the player to evict
     */
    public void evict(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        statsManager.remove(playerId);
    }
}
