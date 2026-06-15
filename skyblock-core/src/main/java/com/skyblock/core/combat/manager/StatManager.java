package com.skyblock.core.combat.manager;

import com.skyblock.core.stat.Stat;

import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.stat.StatManager} with {@link Stat} instead.
 *
 * <p>All methods delegate to the canonical singleton. The inner {@link CombatStat}
 * enum is kept for source compatibility; migrate call sites to {@link Stat}.</p>
 */
@Deprecated
public final class StatManager {

    /**
     * @deprecated Use {@link Stat} instead.
     */
    @Deprecated
    public enum CombatStat {
        HEALTH(Stat.HEALTH),
        DEFENSE(Stat.DEFENSE),
        STRENGTH(Stat.STRENGTH),
        SPEED(Stat.SPEED),
        CRIT_CHANCE(Stat.CRIT_CHANCE),
        CRIT_DAMAGE(Stat.CRIT_DAMAGE),
        INTELLIGENCE(Stat.INTELLIGENCE),
        FEROCITY(Stat.FEROCITY),
        ATTACK_SPEED(Stat.ATTACK_SPEED),
        MAGIC_FIND(Stat.MAGIC_FIND),
        TRUE_DEFENSE(Stat.TRUE_DEFENSE),
        VITALITY(Stat.VITALITY);

        public final Stat stat;

        CombatStat(Stat stat) {
            this.stat = stat;
        }
    }

    private static final StatManager INSTANCE = new StatManager();

    private final com.skyblock.core.stat.StatManager delegate =
            com.skyblock.core.stat.StatManager.getInstance();

    private StatManager() {
    }

    /** @deprecated Use {@link com.skyblock.core.stat.StatManager#getInstance()} instead. */
    @Deprecated
    public static StatManager getInstance() {
        return INSTANCE;
    }

    /** @deprecated Use {@link com.skyblock.core.stat.StatManager#getStat(UUID, Stat)}. */
    @Deprecated
    public double getStat(UUID playerId, CombatStat stat) {
        return delegate.getStat(playerId, stat.stat);
    }

    /** @deprecated Use {@link com.skyblock.core.stat.StatManager#getBaseStat(UUID, Stat)}. */
    @Deprecated
    public double getBaseStat(UUID playerId, CombatStat stat) {
        return delegate.getBaseStat(playerId, stat.stat);
    }

    /** @deprecated Use {@link com.skyblock.core.stat.StatManager#setBaseStat(UUID, Stat, double)}. */
    @Deprecated
    public void setBaseStat(UUID playerId, CombatStat stat, double value) {
        delegate.setBaseStat(playerId, stat.stat, value);
    }

    /** @deprecated Use {@link com.skyblock.core.stat.StatManager#getBonus(UUID, Stat)}. */
    @Deprecated
    public double getBonus(UUID playerId, CombatStat stat) {
        return delegate.getBonus(playerId, stat.stat);
    }

    /** @deprecated Use {@link com.skyblock.core.stat.StatManager#addBonus(UUID, Stat, double)}. */
    @Deprecated
    public double addBonus(UUID playerId, CombatStat stat, double amount) {
        return delegate.addBonus(playerId, stat.stat, amount);
    }

    /** @deprecated Use {@link com.skyblock.core.stat.StatManager#clearBonuses(UUID)}. */
    @Deprecated
    public void clearBonuses(UUID playerId) {
        delegate.clearBonuses(playerId);
    }

    /** @deprecated Use {@link com.skyblock.core.stat.StatManager#remove(UUID)}. */
    @Deprecated
    public boolean remove(UUID playerId) {
        return delegate.remove(playerId);
    }
}
