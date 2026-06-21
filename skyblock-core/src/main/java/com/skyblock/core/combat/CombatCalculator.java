package com.skyblock.core.combat;

/**
 * Deterministic SkyBlock damage calculations using integer player stats.
 *
 * <p>Unlike the randomised engine in the {@code calculator} subpackage, this
 * utility computes the damage of a guaranteed critical hit, which makes it
 * suitable for tooltips, previews and theoretical max-damage displays.</p>
 *
 * <p>Formula:
 * {@code (5 + weaponDmg + ⌊strength / 5⌋) × (1 + strength / 100) × (1 + critDamage / 100)}.</p>
 */
public final class CombatCalculator {

    private CombatCalculator() {
        // static utility class, never instantiated
    }

    /**
     * Calculates the damage of a critical hit for the given stats.
     *
     * @param weaponDmg  base weapon damage, clamped to &ge; 0
     * @param strength   the attacker's strength stat, clamped to &ge; 0
     * @param critDamage the crit damage bonus as a percentage, e.g. {@code 50} for +50%
     * @return the final critical damage dealt, never negative
     */
    public static double calculateDamage(int weaponDmg, int strength, int critDamage) {
        int weapon = Math.max(0, weaponDmg);
        int str = Math.max(0, strength);
        double base = (5 + weapon + Math.floor(str / 5.0)) * (1 + str / 100.0);
        return base * (1 + Math.max(0, critDamage) / 100.0);
    }
}
