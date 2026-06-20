package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Player-facing facade over {@link StatsManager} / {@link StatManager}.
 * Uses the canonical {@link Stat} enum — do not introduce a duplicate StatType.
 */
public final class PlayerStatsManager {

    private static final PlayerStatsManager INSTANCE = new PlayerStatsManager();

    private PlayerStatsManager() {}

    public static PlayerStatsManager getInstance() {
        return INSTANCE;
    }

    public double getStat(UUID playerId, Stat stat) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        return StatsManager.getInstance().get(playerId, stat);
    }

    public Map<Stat, Double> getAllStats(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return StatsManager.getInstance().getAll(playerId);
    }

    public void setStat(UUID playerId, Stat stat, double value) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        StatsManager.getInstance().set(playerId, stat, value);
    }

    public double addBonus(UUID playerId, Stat stat, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(stat, "stat");
        return StatsManager.getInstance().addBonus(playerId, stat, amount);
    }

    public void clearBonuses(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        StatsManager.getInstance().clearBonuses(playerId);
    }

    public void removePlayer(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        StatsManager.getInstance().remove(playerId);
    }
}
