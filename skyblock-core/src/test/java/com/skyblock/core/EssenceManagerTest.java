package com.skyblock.core;

import com.skyblock.core.manager.EssenceManager;
import com.skyblock.core.manager.EssenceManager.EssenceItem;
import com.skyblock.core.manager.EssenceManager.EssenceShopPerk;
import com.skyblock.core.manager.EssenceManager.EssenceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EssenceManagerTest {

    private EssenceManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = EssenceManager.getInstance();
        // Fresh id per test isolates state on the shared singleton.
        playerId = UUID.randomUUID();
    }

    // --- singleton ---

    @Test
    void getInstance_returnsSameInstance() {
        assertSame(manager, EssenceManager.getInstance());
    }

    // --- balances ---

    @Test
    void getBalance_freshPlayer_isZero() {
        assertEquals(0, manager.getBalance(playerId, EssenceType.WITHER));
    }

    @Test
    void addEssence_accumulatesAndReturnsNewBalance() {
        assertEquals(100, manager.addEssence(playerId, EssenceType.WITHER, 100));
        assertEquals(150, manager.addEssence(playerId, EssenceType.WITHER, 50));
        assertEquals(150, manager.getBalance(playerId, EssenceType.WITHER));
    }

    @Test
    void addEssence_typesAreIndependent() {
        manager.addEssence(playerId, EssenceType.WITHER, 100);
        assertEquals(0, manager.getBalance(playerId, EssenceType.DRAGON));
    }

    @Test
    void addEssence_nonPositiveAmount_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.addEssence(playerId, EssenceType.WITHER, 0));
        assertThrows(IllegalArgumentException.class,
                () -> manager.addEssence(playerId, EssenceType.WITHER, -5));
    }

    @Test
    void removeEssence_withSufficientBalance_succeeds() {
        manager.addEssence(playerId, EssenceType.GOLD, 100);
        assertTrue(manager.removeEssence(playerId, EssenceType.GOLD, 40));
        assertEquals(60, manager.getBalance(playerId, EssenceType.GOLD));
    }

    @Test
    void removeEssence_withInsufficientBalance_failsAndLeavesBalance() {
        manager.addEssence(playerId, EssenceType.GOLD, 30);
        assertFalse(manager.removeEssence(playerId, EssenceType.GOLD, 40));
        assertEquals(30, manager.getBalance(playerId, EssenceType.GOLD));
    }

    @Test
    void removeEssence_nonPositiveAmount_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.removeEssence(playerId, EssenceType.GOLD, 0));
    }

    // --- perks ---

    @Test
    void getPerkLevel_freshPlayer_isZero() {
        assertEquals(0, manager.getPerkLevel(playerId, EssenceShopPerk.HEALTH));
    }

    @Test
    void purchasePerk_withEnoughEssence_succeedsAndDeductsCost() {
        manager.addEssence(playerId, EssenceType.WITHER, 1000);
        assertTrue(manager.purchasePerk(playerId, EssenceShopPerk.HEALTH));
        assertEquals(1, manager.getPerkLevel(playerId, EssenceShopPerk.HEALTH));
        // first level costs baseCost * (0 + 1) = 100
        assertEquals(900, manager.getBalance(playerId, EssenceType.WITHER));
    }

    @Test
    void purchasePerk_withInsufficientEssence_fails() {
        manager.addEssence(playerId, EssenceType.WITHER, 50);
        assertFalse(manager.purchasePerk(playerId, EssenceShopPerk.HEALTH));
        assertEquals(0, manager.getPerkLevel(playerId, EssenceShopPerk.HEALTH));
        assertEquals(50, manager.getBalance(playerId, EssenceType.WITHER));
    }

    @Test
    void getUpgradeCost_scalesWithCurrentLevel() {
        assertEquals(100, EssenceShopPerk.HEALTH.getUpgradeCost(0));
        assertEquals(200, EssenceShopPerk.HEALTH.getUpgradeCost(1));
    }

    // --- item unlocks ---

    @Test
    void canUnlock_belowRequirement_isFalse() {
        manager.addEssence(playerId, EssenceType.WITHER, 100);
        assertFalse(manager.canUnlock(playerId, EssenceItem.WITHER_CLOAK));
    }

    @Test
    void canUnlock_atRequirement_isTrue() {
        manager.addEssence(playerId, EssenceType.WITHER, EssenceItem.WITHER_CLOAK.getRequiredEssence());
        assertTrue(manager.canUnlock(playerId, EssenceItem.WITHER_CLOAK));
    }

    // --- removal ---

    @Test
    void remove_withData_returnsTrueAndClearsState() {
        manager.addEssence(playerId, EssenceType.WITHER, 500);
        assertTrue(manager.remove(playerId));
        assertEquals(0, manager.getBalance(playerId, EssenceType.WITHER));
    }

    @Test
    void remove_freshPlayer_returnsFalse() {
        assertFalse(manager.remove(playerId));
    }
}
