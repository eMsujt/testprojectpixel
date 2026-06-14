package com.skyblock.plugin.damage;

/**
 * Static utility implementing the core Hypixel SkyBlock melee damage formula.
 *
 * <p>{@code damage = (5 + weaponDamage + ⌊strength / 5⌋) × (1 + strength / 100) × (1 + critDamage / 100)}</p>
 */
public final class DamageFormula {

    private DamageFormula() {
        // static utility class, never instantiated
    }

    /**
     * Calculates the damage of a melee hit.
     *
     * @param weaponDamage base weapon damage stat, clamped to &ge; 0
     * @param strength     attacker's strength stat, clamped to &ge; 0
     * @param critDamage   crit damage bonus as a percentage, e.g. {@code 50.0} for +50 %
     * @return the final damage dealt, never negative
     */
    public static double calculate(double weaponDamage, double strength, double critDamage) {
        double weapon = Math.max(0.0, weaponDamage);
        double str = Math.max(0.0, strength);
        double base = 5 + weapon + Math.floor(str / 5.0);
        return base * (1 + str / 100.0) * (1 + critDamage / 100.0);
    }
}
