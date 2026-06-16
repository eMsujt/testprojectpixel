package com.skyblock.core.combat.calculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CombatEngineTest {

    @Test
    void applyStrength_zeroStrength_returnsSameBaseDamage() {
        assertEquals(10.0, CombatEngine.applyStrength(10.0, 0.0), 1e-9);
    }

    @Test
    void applyStrength_scales_multiplicatively() {
        // 100 strength → multiplier 2.0
        assertEquals(20.0, CombatEngine.applyStrength(10.0, 100.0), 1e-9);
    }

    @Test
    void applyCrit_scales_by_critDamage_bonus() {
        // 50% crit damage → multiplier 1.5
        assertEquals(15.0, CombatEngine.applyCrit(10.0, 50.0), 1e-9);
    }

    @Test
    void rollCrit_zeroCritChance_neverCrits() {
        for (int i = 0; i < 1000; i++) {
            assertFalse(CombatEngine.rollCrit(0.0));
        }
    }

    @Test
    void rollCrit_fullCritChance_alwaysCrits() {
        for (int i = 0; i < 1000; i++) {
            assertTrue(CombatEngine.rollCrit(100.0));
        }
    }

    @Test
    void calculateDamage_withFullCrit_returnsNonNegative() {
        double result = CombatEngine.calculateDamage(10.0, 100.0, 100.0, 50.0);
        assertTrue(result >= 0.0);
    }

    @Test
    void calculateDamage_zeroDamage_returnsZero() {
        assertEquals(0.0, CombatEngine.calculateDamage(0.0, 0.0, 0.0, 0.0), 1e-9);
    }
}
