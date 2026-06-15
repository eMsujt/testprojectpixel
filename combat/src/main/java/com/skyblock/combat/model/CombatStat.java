package com.skyblock.combat.model;

import com.skyblock.core.stat.Stat;

/**
 * @deprecated Use {@link Stat} instead.
 *
 * <p>This enum is a subset of {@link Stat} kept for source compatibility.
 * Each constant delegates its accessors to the corresponding {@link Stat} entry.</p>
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
    ATTACK_SPEED(Stat.ATTACK_SPEED),
    FEROCITY(Stat.FEROCITY);

    private final Stat delegate;

    CombatStat(Stat delegate) {
        this.delegate = delegate;
    }

    /** @deprecated Use {@link Stat#getDisplayName()}. */
    @Deprecated
    public String getDisplayName() {
        return delegate.getDisplayName();
    }

    /** @deprecated Use {@link Stat#getSymbol()}. */
    @Deprecated
    public String getSymbol() {
        return delegate.getSymbol();
    }

    /** @deprecated Use {@link Stat#getBaseValue()}. */
    @Deprecated
    public double getBaseValue() {
        return delegate.getBaseValue();
    }
}
