package com.skyblock.stats;

import com.skyblock.core.stat.Stat;

/**
 * @deprecated Use {@link Stat} instead.
 *
 * <p>This enum is a full-constant mirror of {@link Stat} kept for source compatibility.
 * Each constant delegates its accessors to the corresponding {@link Stat} entry.</p>
 */
@Deprecated
public enum PlayerStat {

    HEALTH(Stat.HEALTH),
    DEFENSE(Stat.DEFENSE),
    STRENGTH(Stat.STRENGTH),
    INTELLIGENCE(Stat.INTELLIGENCE),
    CRIT_CHANCE(Stat.CRIT_CHANCE),
    CRIT_DAMAGE(Stat.CRIT_DAMAGE),
    ATTACK_SPEED(Stat.ATTACK_SPEED),
    ABILITY_DAMAGE(Stat.ABILITY_DAMAGE),
    TRUE_DEFENSE(Stat.TRUE_DEFENSE),
    FEROCITY(Stat.FEROCITY),
    SPEED(Stat.SPEED),
    MAGIC_FIND(Stat.MAGIC_FIND),
    PET_LUCK(Stat.PET_LUCK),
    SEA_CREATURE_CHANCE(Stat.SEA_CREATURE_CHANCE),
    FISHING_SPEED(Stat.FISHING_SPEED),
    MINING_SPEED(Stat.MINING_SPEED),
    MINING_FORTUNE(Stat.MINING_FORTUNE),
    FARMING_FORTUNE(Stat.FARMING_FORTUNE),
    FORAGING_FORTUNE(Stat.FORAGING_FORTUNE),
    PRISTINE(Stat.PRISTINE),
    COMBAT_WISDOM(Stat.COMBAT_WISDOM),
    MINING_WISDOM(Stat.MINING_WISDOM),
    FARMING_WISDOM(Stat.FARMING_WISDOM),
    HEALTH_REGEN(Stat.HEALTH_REGEN),
    VITALITY(Stat.VITALITY),
    SWING_RANGE(Stat.SWING_RANGE);

    final Stat delegate;

    PlayerStat(Stat delegate) {
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
