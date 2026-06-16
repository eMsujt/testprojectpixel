package com.skyblock.core.manager;

import com.skyblock.core.manager.EnchantingManager.SkyBlockEnchantment;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EnchantingManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(EnchantingManager.getInstance(), EnchantingManager.getInstance());
    }

    @Test
    void setEnchantment_AppliesAtMaxLevel() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        UUID id = UUID.randomUUID();
        int max = SkyBlockEnchantment.SHARPNESS.getMaxLevel();
        mgr.setEnchantment(id, SkyBlockEnchantment.SHARPNESS, max);
        assertEquals(max, mgr.getLevel(id, SkyBlockEnchantment.SHARPNESS));
        mgr.remove(id);
    }

    @Test
    void setEnchantment_RejectsLevelAboveMax() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        UUID id = UUID.randomUUID();
        int max = SkyBlockEnchantment.SHARPNESS.getMaxLevel();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.setEnchantment(id, SkyBlockEnchantment.SHARPNESS, max + 1));
        assertThrows(IllegalArgumentException.class,
                () -> mgr.setEnchantment(id, SkyBlockEnchantment.SHARPNESS, 0));
    }

    @Test
    void getLevel_DefaultsToZero() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        assertEquals(0, mgr.getLevel(UUID.randomUUID(), SkyBlockEnchantment.CRITICAL));
    }

    @Test
    void conflictingEnchants_AreMutuallyExclusive() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.setEnchantment(id, SkyBlockEnchantment.SILK_TOUCH, 1);
        // Fortune conflicts with Silk Touch and must be rejected.
        assertThrows(IllegalArgumentException.class,
                () -> mgr.setEnchantment(id, SkyBlockEnchantment.FORTUNE, 1));
        // Removing the conflicting enchant lets the other apply.
        assertTrue(mgr.removeEnchantment(id, SkyBlockEnchantment.SILK_TOUCH));
        mgr.setEnchantment(id, SkyBlockEnchantment.FORTUNE, 1);
        assertEquals(1, mgr.getLevel(id, SkyBlockEnchantment.FORTUNE));
        mgr.remove(id);
    }

    @Test
    void getConflicts_IsSymmetric() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        assertTrue(mgr.getConflicts(SkyBlockEnchantment.SILK_TOUCH)
                .contains(SkyBlockEnchantment.FORTUNE));
        assertTrue(mgr.getConflicts(SkyBlockEnchantment.FORTUNE)
                .contains(SkyBlockEnchantment.SILK_TOUCH));
        assertTrue(mgr.getConflicts(SkyBlockEnchantment.SHARPNESS).isEmpty());
    }

    @Test
    void reapplyingSameEnchant_IsNotAConflict() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.setEnchantment(id, SkyBlockEnchantment.SHARPNESS, 5);
        mgr.setEnchantment(id, SkyBlockEnchantment.SHARPNESS, 6);
        assertEquals(6, mgr.getLevel(id, SkyBlockEnchantment.SHARPNESS));
        mgr.remove(id);
    }

    @Test
    void ultimateEnchants_AreLimitedToOne() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        assertTrue(mgr.isUltimate(SkyBlockEnchantment.ULTIMATE_WISE));
        assertFalse(mgr.isUltimate(SkyBlockEnchantment.SHARPNESS));
    }

    @Test
    void getEnchantCost_ScalesWithLevelAndBookshelfPower() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        // Sharpness bookshelf power is 15 per ENCHANT_DATA; cost = power * level.
        assertEquals(15, mgr.getEnchantCost(SkyBlockEnchantment.SHARPNESS, 1));
        assertEquals(45, mgr.getEnchantCost(SkyBlockEnchantment.SHARPNESS, 3));
        assertThrows(IllegalArgumentException.class,
                () -> mgr.getEnchantCost(SkyBlockEnchantment.SHARPNESS,
                        SkyBlockEnchantment.SHARPNESS.getMaxLevel() + 1));
    }

    @Test
    void getMaxLevel_MatchesEnumDefinition() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        assertEquals(SkyBlockEnchantment.EXPERTISE.getMaxLevel(),
                mgr.getMaxLevel(SkyBlockEnchantment.EXPERTISE));
        assertEquals(10, mgr.getMaxLevel(SkyBlockEnchantment.EXPERTISE));
    }

    @Test
    void getEnchantTable_CoversEveryEnchantAtMaxLevel() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        var table = mgr.getEnchantTable();
        assertEquals(SkyBlockEnchantment.values().length, table.size());
        for (SkyBlockEnchantment type : SkyBlockEnchantment.values()) {
            assertEquals(type.getMaxLevel(), table.get(type));
        }
    }

    @Test
    void removeEnchantment_ReportsPresence() {
        EnchantingManager mgr = EnchantingManager.getInstance();
        UUID id = UUID.randomUUID();
        assertFalse(mgr.removeEnchantment(id, SkyBlockEnchantment.SMITE));
        mgr.setEnchantment(id, SkyBlockEnchantment.SMITE, 4);
        assertTrue(mgr.removeEnchantment(id, SkyBlockEnchantment.SMITE));
        assertEquals(0, mgr.getLevel(id, SkyBlockEnchantment.SMITE));
    }
}
