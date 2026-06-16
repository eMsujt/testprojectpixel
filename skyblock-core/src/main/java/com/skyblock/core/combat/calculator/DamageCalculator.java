package com.skyblock.core.combat.calculator;

import com.skyblock.core.combat.model.DamageType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Full Hypixel SkyBlock damage formula with attacker and defender stats.
 *
 * <p>Formula:
 * <ol>
 *   <li>Raw damage = (5 + weaponDamage + ⌊strength / 5⌋) × (1 + strength / 100)</li>
 *   <li>Crit (if rolled): × (1 + critDamage / 100)</li>
 *   <li>Defense reduction (if {@link DamageType#isReducedByDefense()}):
 *       × 100 / (100 + defense)</li>
 * </ol>
 * </p>
 */
public final class DamageCalculator {

    /**
     * An immutable snapshot of all player stats relevant to damage calculation.
     *
     * @param health      maximum health, never negative
     * @param defense     defense stat, never negative
     * @param strength    strength stat, never negative
     * @param speed       speed stat, never negative
     * @param weaponDamage base weapon damage, never negative
     * @param critChance  crit chance as a percentage in {@code [0, 100]}
     * @param critDamage  crit damage bonus as a percentage, e.g. {@code 50.0} for +50 %
     * @param ferocity    ferocity stat, never negative (multi-hit chance percent)
     */
    public record PlayerStats(
            int health,
            int defense,
            int strength,
            int speed,
            int weaponDamage,
            double critChance,
            double critDamage,
            double ferocity) {

        public PlayerStats {
            if (health < 0) throw new IllegalArgumentException("health must not be negative: " + health);
            if (defense < 0) throw new IllegalArgumentException("defense must not be negative: " + defense);
            if (strength < 0) throw new IllegalArgumentException("strength must not be negative: " + strength);
            if (speed < 0) throw new IllegalArgumentException("speed must not be negative: " + speed);
            if (weaponDamage < 0) throw new IllegalArgumentException("weaponDamage must not be negative: " + weaponDamage);
            if (ferocity < 0) throw new IllegalArgumentException("ferocity must not be negative: " + ferocity);
        }
    }

    private DamageCalculator() {
        // static utility class, never instantiated
    }

    /**
     * Calculates final damage dealt to a defender given attacker stats and damage type.
     *
     * @param attacker   the attacking player's stats
     * @param defenderDefense the defender's defense stat, clamped to &ge; 0
     * @param type       the {@link DamageType}; controls whether defense reduces damage
     * @return the final damage dealt, never negative
     */
    public static double calculate(PlayerStats attacker, int defenderDefense, DamageType type) {
        double damage = rawDamage(attacker.weaponDamage(), attacker.strength());
        if (rollCrit(attacker.critChance())) {
            damage = applyCrit(damage, attacker.critDamage());
        }
        if (type.isReducedByDefense()) {
            damage = applyDefense(damage, Math.max(0, defenderDefense));
        }
        return Math.max(0.0, damage);
    }

    /**
     * Computes the raw (pre-crit, pre-defense) damage value.
     *
     * <p>{@code (5 + weaponDamage + ⌊strength / 5⌋) × (1 + strength / 100)}</p>
     *
     * @param weaponDamage base weapon damage stat
     * @param strength     attacker's strength stat
     * @return raw damage before crit and defense modifiers
     */
    public static double rawDamage(int weaponDamage, int strength) {
        double base = 5 + weaponDamage + Math.floor(strength / 5.0);
        return base * (1 + strength / 100.0);
    }

    /**
     * Applies the critical-hit multiplier.
     *
     * @param damage     damage before crit
     * @param critDamage crit damage bonus as a percentage
     * @return {@code damage * (1 + critDamage / 100)}
     */
    public static double applyCrit(double damage, double critDamage) {
        return damage * (1 + critDamage / 100.0);
    }

    /**
     * Reduces damage by the defender's defense using the SkyBlock formula.
     *
     * <p>{@code damage × 100 / (100 + defense)}</p>
     *
     * @param damage  incoming damage
     * @param defense defender's defense stat, must be &ge; 0
     * @return damage after defense reduction
     */
    public static double applyDefense(double damage, int defense) {
        return damage * 100.0 / (100.0 + defense);
    }

    /**
     * Rolls whether a hit is a critical strike.
     *
     * @param critChance crit chance as a percentage; clamped to {@code [0, 100]}
     * @return {@code true} if the hit crits
     */
    public static boolean rollCrit(double critChance) {
        double clamped = Math.min(100.0, Math.max(0.0, critChance));
        return ThreadLocalRandom.current().nextDouble(100.0) < clamped;
    }
}
