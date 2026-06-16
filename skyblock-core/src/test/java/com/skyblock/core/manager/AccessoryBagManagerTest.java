package com.skyblock.core.manager;

import com.skyblock.core.manager.AccessoryBagManager.AccessoryTier;
import com.skyblock.core.manager.AccessoryBagManager.PowerStone;
import com.skyblock.core.model.Stat;
import com.skyblock.core.talisman.manager.TalismanManager.TalismanType;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccessoryBagManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(AccessoryBagManager.getInstance(), AccessoryBagManager.getInstance());
    }

    // ------------------------------------------------------------------------
    // Magical-power totals
    // ------------------------------------------------------------------------

    @Test
    void getTotalMagicPower_ZeroForEmptyBag() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        assertEquals(0, mgr.getTotalMagicPower(UUID.randomUUID()));
    }

    @Test
    void getTotalMagicPower_SumsAcrossTiers() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addAccessory(player, TalismanType.SPEED_TALISMAN);     // COMMON -> 3
        mgr.addAccessory(player, TalismanType.STRENGTH_TALISMAN);  // COMMON -> 3
        mgr.addAccessory(player, TalismanType.STRENGTH_ARTIFACT);  // RARE   -> 8
        assertEquals(14, mgr.getTotalMagicPower(player));
    }

    @Test
    void getMagicPower_CountsOnlyMatchingTier() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addAccessory(player, TalismanType.SPEED_TALISMAN);     // COMMON
        mgr.addAccessory(player, TalismanType.STRENGTH_TALISMAN);  // COMMON
        mgr.addAccessory(player, TalismanType.STRENGTH_ARTIFACT);  // RARE
        assertEquals(2 * AccessoryTier.COMMON.magicPower, mgr.getMagicPower(player, AccessoryTier.COMMON));
        assertEquals(1 * AccessoryTier.RARE.magicPower, mgr.getMagicPower(player, AccessoryTier.RARE));
        assertEquals(0, mgr.getMagicPower(player, AccessoryTier.EPIC));
    }

    // ------------------------------------------------------------------------
    // Power-stone tuning
    // ------------------------------------------------------------------------

    @Test
    void getPowerStoneBonuses_EmptyWhenNoStoneSelected() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addAccessory(player, TalismanType.SPEED_TALISMAN);
        assertTrue(mgr.getPowerStoneBonuses(player).isEmpty());
    }

    @Test
    void getPowerStoneBonuses_EmptyWhenNoMagicPower() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.selectPowerStone(player, PowerStone.MANA_FLUX);
        assertTrue(mgr.getPowerStoneBonuses(player).isEmpty());
    }

    @Test
    void getPowerStoneBonuses_TunesTotalPowerThroughCoefficients() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addAccessory(player, TalismanType.SPEED_TALISMAN);     // COMMON -> 3
        mgr.addAccessory(player, TalismanType.STRENGTH_TALISMAN);  // COMMON -> 3
        mgr.addAccessory(player, TalismanType.STRENGTH_ARTIFACT);  // RARE   -> 8 (total 14)
        mgr.selectPowerStone(player, PowerStone.MANA_FLUX);        // INTELLIGENCE 0.6

        Map<Stat, Double> bonuses = mgr.getPowerStoneBonuses(player);
        assertEquals(14 * 0.6, bonuses.get(Stat.INTELLIGENCE), 1e-9);
    }

    @Test
    void getPowerStoneBonuses_AppliesMultipleCoefficients() {
        AccessoryBagManager mgr = AccessoryBagManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addAccessory(player, TalismanType.SPEED_TALISMAN);     // COMMON -> 3
        mgr.selectPowerStone(player, PowerStone.FORTITUDE);        // HEALTH 0.7, DEFENSE 0.3

        Map<Stat, Double> bonuses = mgr.getPowerStoneBonuses(player);
        assertEquals(3 * 0.7, bonuses.get(Stat.HEALTH), 1e-9);
        assertEquals(3 * 0.3, bonuses.get(Stat.DEFENSE), 1e-9);
    }
}
