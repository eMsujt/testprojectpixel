package com.skyblock.core.manager;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.Collection;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CollectionManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        CollectionManager a = CollectionManager.getInstance();
        CollectionManager b = CollectionManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(CollectionManager.getInstance());
    }

    @Test
    void maxTier_IsNine() {
        assertEquals(9, CollectionManager.MAX_TIER);
    }

    // ------------------------------------------------------------------------
    // Tier unlock thresholds
    // ------------------------------------------------------------------------

    @Test
    void getTier_IsZero_BelowFirstThreshold() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        // WHEAT tier I threshold is 50.
        mgr.addItems(player, Collection.WHEAT, 49);
        assertEquals(0, mgr.getTier(player, Collection.WHEAT));
    }

    @Test
    void getTier_UnlocksFirstTier_AtThreshold() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItems(player, Collection.WHEAT, 50);
        assertEquals(1, mgr.getTier(player, Collection.WHEAT));
        assertTrue(mgr.hasUnlockedTier(player, Collection.WHEAT, 1));
    }

    @Test
    void getTier_AdvancesAsThresholdsAreCrossed() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        // WHEAT thresholds: 50, 100, 250, ... -> 250 items is tier III.
        mgr.addItems(player, Collection.WHEAT, 250);
        assertEquals(3, mgr.getTier(player, Collection.WHEAT));
        assertTrue(mgr.hasUnlockedTier(player, Collection.WHEAT, 3));
        assertFalse(mgr.hasUnlockedTier(player, Collection.WHEAT, 4));
    }

    @Test
    void getItemsToNextTier_ReturnsRemainingToThreshold() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItems(player, Collection.WHEAT, 60);
        // At tier I (>=50); next threshold (tier II) is 100, so 40 remain.
        assertEquals(40, mgr.getItemsToNextTier(player, Collection.WHEAT));
    }

    @Test
    void isMaxed_WhenFinalThresholdReached() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        // WHEAT final (tier IX) threshold is 100_000.
        mgr.addItems(player, Collection.WHEAT, 100_000);
        assertTrue(mgr.isMaxed(player, Collection.WHEAT));
        assertEquals(CollectionManager.MAX_TIER, mgr.getTier(player, Collection.WHEAT));
        assertEquals(0, mgr.getItemsToNextTier(player, Collection.WHEAT));
    }

    @Test
    void getTotalTiersUnlocked_SumsAcrossCollections() {
        CollectionManager mgr = CollectionManager.getInstance();
        UUID player = UUID.randomUUID();
        mgr.addItems(player, Collection.WHEAT, 50);   // tier 1
        mgr.addItems(player, Collection.PUMPKIN, 100); // thresholds 40,100,... -> tier 2
        assertEquals(3, mgr.getTotalTiersUnlocked(player));
    }
}
