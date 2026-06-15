package com.skyblock.combat.engine;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Static damage calculations for SkyBlock combat.
 *
 * <p>Implements the classic SkyBlock damage formula: strength scales the
 * base damage multiplicatively, and a successful crit roll (driven by
 * crit chance) multiplies the result by the player's crit damage bonus.</p>
 */
public final class CombatEngine {

    private CombatEngine() {
        // static utility class, never instantiated
    }

    /**
     * Calculates the final damage of a hit, rolling for a critical strike.
     *
     * <p>The base damage is first scaled by strength
     * ({@code baseDamage * (1 + strength / 100)}). A crit roll is then made
     * against {@code critChance}; on success the damage is further multiplied
     * by {@code 1 + critDamage / 100}.</p>
     *
     * @param baseDamage the raw damage of the weapon or attack, before bonuses
     * @param strength   the attacker's strength stat, e.g. {@code 150.0}
     * @param critChance the chance to crit as a percentage in {@code [0, 100]};
     *                   values outside the range are clamped
     * @param critDamage the bonus damage on a crit as a percentage,
     *                   e.g. {@code 50.0} for +50%
     * @return the final damage dealt, never negative
     */
    public static double calculateDamage(double baseDamage, double strength,
                                         double critChance, double critDamage) {
        double damage = applyStrength(baseDamage, strength);
        if (rollCrit(critChance)) {
            damage = applyCrit(damage, critDamage);
        }
        return Math.max(0.0, damage);
    }

    /**
     * Scales base damage by the attacker's strength stat.
     *
     * @param baseDamage the raw damage before bonuses
     * @param strength   the attacker's strength stat
     * @return {@code baseDamage * (1 + strength / 100)}
     */
    public static double applyStrength(double baseDamage, double strength) {
        return baseDamage * (1 + strength / 100.0);
    }

    /**
     * Applies the critical-hit multiplier to already strength-scaled damage.
     *
     * @param damage     the damage after strength scaling
     * @param critDamage the bonus damage on a crit as a percentage
     * @return {@code damage * (1 + critDamage / 100)}
     */
    public static double applyCrit(double damage, double critDamage) {
        return damage * (1 + critDamage / 100.0);
    }

    /**
     * Rolls whether a hit is a critical strike.
     *
     * @param critChance the chance to crit as a percentage; clamped to
     *                   {@code [0, 100]}, so {@code 100} always crits and
     *                   {@code 0} never does
     * @return {@code true} if the hit crits
     */
    public static boolean rollCrit(double critChance) {
        double clamped = Math.min(100.0, Math.max(0.0, critChance));
        return ThreadLocalRandom.current().nextDouble(100.0) < clamped;
    }
}
