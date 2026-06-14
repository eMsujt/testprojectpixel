package com.skyblock.plugin.combat;

/**
 * Static utility for resolving SkyBlock combat damage from integer stats.
 *
 * <p>Delegates to {@link DamageFormula} and rounds the result to a whole
 * number of damage points.</p>
 */
public final class CombatManager {

    private CombatManager() {
        // static utility class, never instantiated
    }

    /**
     * Calculates the melee damage of a hit from the given integer stats.
     *
     * @param weaponDamage     base weapon damage stat, clamped to &ge; 0
     * @param strength         attacker's strength stat, clamped to &ge; 0
     * @param critDamagePercent crit damage bonus as a percentage, e.g. {@code 50} for +50 %
     * @return the final damage dealt, rounded to the nearest int, never negative
     */
    public static int calculateDamage(int weaponDamage, int strength) {
        double damage = DamageFormula.calculate(weaponDamage, strength);
        return (int) Math.round(damage);
    }
}
