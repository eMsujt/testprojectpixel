package com.skyblock.plugin.combat;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Static utility implementing the core Hypixel SkyBlock melee damage formula.
 *
 * <p>{@code damage = (5 + weaponDamage + ⌊strength / 5⌋) × (1 + strength / 100)},
 * and, on a critical hit, multiplied by {@code (1 + critDamage / 100)}. A hit is
 * critical with probability {@code critChance / 100}.</p>
 */
public final class DamageFormula {

    private DamageFormula() {
        // static utility class, never instantiated
    }

    /**
     * Calculates the damage of a melee hit, rolling for a critical strike.
     *
     * @param weaponDamage base weapon damage stat, clamped to &ge; 0
     * @param strength     attacker's strength stat, clamped to &ge; 0
     * @param critChance   chance to land a critical hit as a percentage, e.g. {@code 30.0} for 30 %
     * @param critDamage   crit damage bonus as a percentage, e.g. {@code 50.0} for +50 %
     * @return the final damage dealt, never negative
     */
    public static double calculate(double weaponDamage, double strength, double critChance, double critDamage) {
        double weapon = Math.max(0.0, weaponDamage);
        double str = Math.max(0.0, strength);
        double base = (5 + weapon + Math.floor(str / 5.0)) * (1 + str / 100.0);
        if (ThreadLocalRandom.current().nextDouble() < critChance / 100.0) {
            base *= (1 + critDamage / 100.0);
        }
        return base;
    }
}
