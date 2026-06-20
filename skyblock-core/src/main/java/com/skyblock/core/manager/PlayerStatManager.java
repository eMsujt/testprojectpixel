package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import com.skyblock.core.stats.PlayerStatsCalculator;
import com.skyblock.core.stats.StatsManager;

import java.util.Objects;
import java.util.UUID;

/**
 * Singleton facade providing a single entry point for reading and modifying
 * per-player SkyBlock stats. Delegates storage to {@link StatManager} and
 * snapshot calculation to {@link PlayerStatsCalculator}.
 */
public final class PlayerStatManager {

    private static final PlayerStatManager INSTANCE = new PlayerStatManager();

    private final StatManager statManager = StatManager.getInstance();
    private final PlayerStatsCalculator calculator = PlayerStatsCalculator.getInstance();

    private PlayerStatManager() {}

    public static PlayerStatManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the effective value of {@code stat} for the given player,
     * combining base and all active bonuses.
     */
    public double getStat(UUID playerId, Stat stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        return calculator.calculate(playerId).getStat(stat);
    }

    /**
     * Adds {@code delta} as a temporary bonus to {@code stat} for {@code playerId}.
     * Pass a negative value to remove a bonus.
     */
    public void addBonus(UUID playerId, Stat stat, double delta) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        statManager.addBonus(playerId, stat, delta);
    }

    /**
     * Sets the base value of {@code stat} for {@code playerId}, overriding the
     * default from {@link Stat#getBaseValue()}.
     */
    public void setBase(UUID playerId, Stat stat, double value) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        statManager.setBaseStat(playerId, stat, value);
    }

    /** Returns a full calculated snapshot for the given player. */
    public StatsManager.PlayerStats getSnapshot(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return calculator.calculate(playerId);
    }
}
