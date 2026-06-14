package com.skyblock.plugin.combat;

public final class DamageFormula {

    private DamageFormula() {}

    public static double calculate(double weaponDamage, double strength) {
        return (5 + weaponDamage) * (1 + strength / 100.0);
    }
}
