package com.skyblock.plugin.items;

import com.skyblock.core.stat.Stat;

/**
 * @deprecated Use {@link Stat} instead.
 *
 * <p>This enum is a subset of {@link Stat} kept for source compatibility.
 * Each constant delegates its accessors to the corresponding {@link Stat} entry.</p>
 */
@Deprecated
public enum StatType {

    HEALTH(Stat.HEALTH),
    DEFENSE(Stat.DEFENSE),
    STRENGTH(Stat.STRENGTH),
    CRIT_CHANCE(Stat.CRIT_CHANCE),
    CRIT_DAMAGE(Stat.CRIT_DAMAGE),
    SPEED(Stat.SPEED),
    INTELLIGENCE(Stat.INTELLIGENCE),
    ATTACK_SPEED(Stat.ATTACK_SPEED);

    private final Stat delegate;

    StatType(Stat delegate) {
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
