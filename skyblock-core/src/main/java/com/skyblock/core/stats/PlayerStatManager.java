package com.skyblock.core.stats;

import com.skyblock.core.model.Stat;
import com.skyblock.core.stat.StatManager;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

/**
 * @deprecated Use {@link StatManager} directly.
 */
@Deprecated
public final class PlayerStatManager {

    private static final PlayerStatManager INSTANCE = new PlayerStatManager();

    private PlayerStatManager() {}

    /** @deprecated Use {@link StatManager#getInstance()}. */
    @Deprecated
    public static PlayerStatManager getInstance() {
        return INSTANCE;
    }

    /** @deprecated Use {@link StatManager#getStat(UUID, Stat)}. */
    @Deprecated
    public double getStat(UUID playerId, Stat stat) {
        return StatManager.getInstance().getStat(playerId, stat);
    }

    /** @deprecated Use {@link StatManager#setBaseStat(UUID, Stat, double)}. */
    @Deprecated
    public void setStat(UUID playerId, Stat stat, double value) {
        StatManager.getInstance().setBaseStat(playerId, stat, value);
    }

    /** @deprecated Use {@link StatManager#addBonus(UUID, Stat, double)}. */
    @Deprecated
    public double addStat(UUID playerId, Stat stat, double amount) {
        return StatManager.getInstance().addBonus(playerId, stat, amount);
    }

    /** @deprecated Use {@link StatManager#getStat(UUID, Stat)} for each {@link Stat} value. */
    @Deprecated
    public Map<Stat, Double> getAllStats(UUID playerId) {
        Map<Stat, Double> result = new EnumMap<>(Stat.class);
        StatManager mgr = StatManager.getInstance();
        for (Stat s : Stat.values()) {
            result.put(s, mgr.getStat(playerId, s));
        }
        return result;
    }

    /** @deprecated Use {@link StatManager#remove(UUID)}. */
    @Deprecated
    public boolean reset(UUID playerId) {
        return StatManager.getInstance().remove(playerId);
    }
}
