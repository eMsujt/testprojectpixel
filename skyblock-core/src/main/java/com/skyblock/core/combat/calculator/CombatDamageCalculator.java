package com.skyblock.core.combat.calculator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Static utility implementing the core Hypixel SkyBlock melee damage formula.
 *
 * <p>{@code damage = (5 + weaponDamage + ⌊strength / 5⌋) × (1 + strength / 100)},
 * multiplied by {@code (1 + critDamage / 100)} for a critical strike.</p>
 */
public final class CombatDamageCalculator {

    private CombatDamageCalculator() {
        // static utility class, never instantiated
    }

    private static boolean rollCrit(double critChancePercent) {
        return ThreadLocalRandom.current().nextDouble(100.0) < critChancePercent;
    }

    public static double calculateDamage(double weaponDamage, double strength, double critDamage) {
        double weapon = Math.max(0.0, weaponDamage);
        double str = Math.max(0.0, strength);
        double base = (5.0 + weapon + Math.floor(str / 5.0)) * (1.0 + str / 100.0);
        return base * (1.0 + critDamage / 100.0);
    }
}
