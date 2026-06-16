package com.skyblock.core.combat.calculator;

import com.skyblock.core.combat.model.DamageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DamageCalculatorTest {

    @Test
    void rawDamage_zeroStats_returnsBaseFive() {
        // (5 + 0 + floor(0/5)) * (1 + 0/100) = 5
        assertEquals(5.0, DamageCalculator.rawDamage(0, 0), 1e-9);
    }

    @Test
    void rawDamage_includesStrengthFloorBonus() {
        // strength=50: floor(50/5)=10, base = (5 + 10 + 10) * (1 + 50/100) = 25 * 1.5 = 37.5
        assertEquals(37.5, DamageCalculator.rawDamage(10, 50), 1e-9);
    }

    @Test
    void applyCrit_zeroCritDamage_returnsUnchanged() {
        assertEquals(20.0, DamageCalculator.applyCrit(20.0, 0.0), 1e-9);
    }

    @Test
    void applyCrit_100PercentBonus_doublesDamage() {
        assertEquals(40.0, DamageCalculator.applyCrit(20.0, 100.0), 1e-9);
    }

    @Test
    void applyDefense_zeroDefense_returnsUnchanged() {
        assertEquals(100.0, DamageCalculator.applyDefense(100.0, 0), 1e-9);
    }

    @Test
    void applyDefense_100Defense_halvesIncomingDamage() {
        assertEquals(50.0, DamageCalculator.applyDefense(100.0, 100), 1e-9);
    }

    @Test
    void rollCrit_zeroCritChance_neverCrits() {
        for (int i = 0; i < 1000; i++) {
            assertFalse(DamageCalculator.rollCrit(0.0));
        }
    }

    @Test
    void rollCrit_fullCritChance_alwaysCrits() {
        for (int i = 0; i < 1000; i++) {
            assertTrue(DamageCalculator.rollCrit(100.0));
        }
    }

    @Test
    void calculate_trueType_skipsDefense() {
        DamageCalculator.PlayerStats attacker = new DamageCalculator.PlayerStats(
                100, 0, 0, 100, 10, 0.0, 0.0, 0.0);
        double withDefense    = DamageCalculator.calculate(attacker, 1000, DamageType.MELEE);
        double withoutDefense = DamageCalculator.calculate(attacker, 1000, DamageType.TRUE);
        assertTrue(withoutDefense > withDefense);
    }

    @Test
    void calculate_returnsNonNegative() {
        DamageCalculator.PlayerStats attacker = new DamageCalculator.PlayerStats(
                100, 0, 0, 100, 0, 100.0, 200.0, 0.0);
        double result = DamageCalculator.calculate(attacker, 0, DamageType.MELEE);
        assertTrue(result >= 0.0);
    }

    @Test
    void playerStats_negativeHealth_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> new DamageCalculator.PlayerStats(-1, 0, 0, 100, 0, 0.0, 0.0, 0.0));
    }
}
