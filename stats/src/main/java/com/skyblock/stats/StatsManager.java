package com.skyblock.stats;

import com.skyblock.core.stat.Stat;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.stats.PlayerStatManager} with {@link Stat} instead.
 */
@Deprecated
public final class StatsManager {

    private final com.skyblock.core.stats.PlayerStatManager delegate =
            com.skyblock.core.stats.PlayerStatManager.getInstance();

    /** @deprecated Use {@link com.skyblock.core.stats.PlayerStatManager#getStat(UUID, Stat)}. */
    @Deprecated
    public double getStat(UUID playerId, PlayerStat stat) {
        return delegate.getStat(playerId, stat.delegate);
    }

    /** @deprecated Use {@link com.skyblock.core.stats.PlayerStatManager#setStat(UUID, Stat, double)}. */
    @Deprecated
    public void setStat(UUID playerId, PlayerStat stat, double value) {
        delegate.setStat(playerId, stat.delegate, value);
    }

    /** @deprecated Use {@link com.skyblock.core.stats.PlayerStatManager#addStat(UUID, Stat, double)}. */
    @Deprecated
    public void addStat(UUID playerId, PlayerStat stat, double delta) {
        delegate.addStat(playerId, stat.delegate, delta);
    }

    /** @deprecated Use {@link com.skyblock.core.stats.PlayerStatManager#reset(UUID)}. */
    @Deprecated
    public void reset(UUID playerId) {
        delegate.reset(playerId);
    }
}
