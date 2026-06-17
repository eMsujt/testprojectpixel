package com.skyblock.core.manager;

import com.skyblock.core.manager.EssenceManager.EssenceItem;
import com.skyblock.core.manager.EssenceManager.EssenceShopPerk;
import com.skyblock.core.manager.EssenceManager.EssenceType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EssenceManagerTest {

    private final EssenceManager manager = EssenceManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(EssenceManager.getInstance(), EssenceManager.getInstance());
    }

    @Test
    void allEightEssenceCurrencies_ArePresent() {
        assertEquals(8, EssenceType.values().length);
        for (String name : new String[]{"WITHER", "SPIDER", "UNDEAD", "DRAGON",
                "GOLD", "DIAMOND", "ICE", "CRIMSON"}) {
            assertDoesNotThrow(() -> EssenceType.valueOf(name));
        }
    }

    @Test
    void balance_DefaultsToZero() {
        UUID player = UUID.randomUUID();
        assertEquals(0, manager.getBalance(player, EssenceType.WITHER));
    }

    @Test
    void addEssence_IsTrackedPerType() {
        UUID player = UUID.randomUUID();
        assertEquals(500, manager.addEssence(player, EssenceType.WITHER, 500));
        assertEquals(800, manager.addEssence(player, EssenceType.WITHER, 300));
        assertEquals(800, manager.getBalance(player, EssenceType.WITHER));
        // a different type is independent
        assertEquals(0, manager.getBalance(player, EssenceType.DRAGON));
    }

    @Test
    void addEssence_RejectsNonPositiveAmount() {
        UUID player = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> manager.addEssence(player, EssenceType.GOLD, 0));
        assertThrows(IllegalArgumentException.class,
                () -> manager.addEssence(player, EssenceType.GOLD, -5));
    }

    @Test
    void removeEssence_SucceedsWhenSufficient_FailsWhenNot() {
        UUID player = UUID.randomUUID();
        manager.addEssence(player, EssenceType.SPIDER, 100);
        assertFalse(manager.removeEssence(player, EssenceType.SPIDER, 200));
        assertEquals(100, manager.getBalance(player, EssenceType.SPIDER));
        assertTrue(manager.removeEssence(player, EssenceType.SPIDER, 60));
        assertEquals(40, manager.getBalance(player, EssenceType.SPIDER));
    }

    @Test
    void purchasePerk_DeductsCost_AndIncrementsLevel() {
        UUID player = UUID.randomUUID();
        EssenceShopPerk perk = EssenceShopPerk.HEALTH;
        // insufficient essence -> no purchase
        assertFalse(manager.purchasePerk(player, perk));
        assertEquals(0, manager.getPerkLevel(player, perk));

        manager.addEssence(player, perk.getEssenceType(), perk.getUpgradeCost(0));
        assertTrue(manager.purchasePerk(player, perk));
        assertEquals(1, manager.getPerkLevel(player, perk));
        assertEquals(0, manager.getBalance(player, perk.getEssenceType()));
    }

    @Test
    void purchasePerk_CannotExceedMaxLevel() {
        UUID player = UUID.randomUUID();
        EssenceShopPerk perk = EssenceShopPerk.CRIT_DAMAGE; // maxLevel 10
        // fund generously and buy until maxed
        manager.addEssence(player, perk.getEssenceType(), 1_000_000);
        for (int i = 0; i < perk.getMaxLevel(); i++) {
            assertTrue(manager.purchasePerk(player, perk));
        }
        assertEquals(perk.getMaxLevel(), manager.getPerkLevel(player, perk));
        assertFalse(manager.purchasePerk(player, perk));
    }

    @Test
    void accrual_IsTrackedIndependentlyPerEssenceType() {
        UUID player = UUID.randomUUID();
        manager.addEssence(player, EssenceType.WITHER, 700);
        manager.addEssence(player, EssenceType.DRAGON, 250);
        manager.addEssence(player, EssenceType.CRIMSON, 90);
        assertEquals(700, manager.getBalance(player, EssenceType.WITHER));
        assertEquals(250, manager.getBalance(player, EssenceType.DRAGON));
        assertEquals(90, manager.getBalance(player, EssenceType.CRIMSON));
        // an untouched type is unaffected
        assertEquals(0, manager.getBalance(player, EssenceType.ICE));
    }

    @Test
    void purchasePerk_DeductsEscalatingCostPerLevel() {
        UUID player = UUID.randomUUID();
        EssenceShopPerk perk = EssenceShopPerk.HEALTH; // baseCost 100
        // cost of level n is baseCost * (n + 1): 100, then 200
        manager.addEssence(player, perk.getEssenceType(), 300);
        assertTrue(manager.purchasePerk(player, perk));
        assertEquals(200, manager.getBalance(player, perk.getEssenceType()));
        assertTrue(manager.purchasePerk(player, perk));
        assertEquals(0, manager.getBalance(player, perk.getEssenceType()));
        assertEquals(2, manager.getPerkLevel(player, perk));
        // next level would cost 300 (100 * 3) which the player can no longer afford
        assertFalse(manager.purchasePerk(player, perk));
        assertEquals(2, manager.getPerkLevel(player, perk));
    }

    @Test
    void canUnlock_GatesItemBehindEssenceBalance() {
        UUID player = UUID.randomUUID();
        EssenceItem item = EssenceItem.HYPERION; // 20000 wither
        assertFalse(manager.canUnlock(player, item));
        manager.addEssence(player, item.getEssenceType(), item.getRequiredEssence());
        assertTrue(manager.canUnlock(player, item));
    }

    @Test
    void remove_ClearsAllPlayerData() {
        UUID player = UUID.randomUUID();
        manager.addEssence(player, EssenceType.ICE, 50);
        assertTrue(manager.remove(player));
        assertEquals(0, manager.getBalance(player, EssenceType.ICE));
        assertFalse(manager.remove(player));
    }
}
