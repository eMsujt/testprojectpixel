package com.skyblock.core;

import com.skyblock.core.manager.CollectionManager;
import com.skyblock.core.model.Collection;
import com.skyblock.core.model.CollectionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CollectionManagerTest {

    private CollectionManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = CollectionManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @Test
    void addItems_accumulates_and_returnsTotal() {
        manager.addItems(playerId, Collection.WHEAT, 30L);
        long total = manager.addItems(playerId, Collection.WHEAT, 25L);
        assertEquals(55L, total);
        assertEquals(55L, manager.getItems(playerId, Collection.WHEAT));
    }

    @Test
    void addItems_negative_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.addItems(playerId, Collection.WHEAT, -1L));
    }

    @Test
    void addItems_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.addItems(null, Collection.WHEAT, 10L));
    }

    @Test
    void getItems_unknownPlayer_returnsZero() {
        assertEquals(0L, manager.getItems(UUID.randomUUID(), Collection.COAL));
    }

    @Test
    void getTier_freshPlayer_isZero() {
        assertEquals(0, manager.getTier(playerId, Collection.DIAMOND));
    }

    @Test
    void getTier_atFirstThreshold_isTierOne() {
        // WHEAT tier 1 threshold = 50
        manager.addItems(playerId, Collection.WHEAT, 50L);
        assertEquals(1, manager.getTier(playerId, Collection.WHEAT));
    }

    @Test
    void getTier_belowThreshold_isStillZero() {
        manager.addItems(playerId, Collection.WHEAT, 49L);
        assertEquals(0, manager.getTier(playerId, Collection.WHEAT));
    }

    @Test
    void getTier_atSecondThreshold_isTierTwo() {
        // WHEAT tier 2 threshold = 100
        manager.addItems(playerId, Collection.WHEAT, 100L);
        assertEquals(2, manager.getTier(playerId, Collection.WHEAT));
    }

    @Test
    void getItemsToNextTier_atTierZero_equalsFirstThreshold() {
        // WHEAT first threshold = 50; player has 0
        long needed = manager.getItemsToNextTier(playerId, Collection.WHEAT);
        assertEquals(50L, needed);
    }

    @Test
    void getItemsToNextTier_partialProgress_returnsRemainder() {
        manager.addItems(playerId, Collection.WHEAT, 30L);
        // next tier threshold = 50; 50 - 30 = 20
        assertEquals(20L, manager.getItemsToNextTier(playerId, Collection.WHEAT));
    }

    @Test
    void getItemsToNextTier_maxed_returnsZero() {
        manager.addItems(playerId, Collection.WHEAT, 100_000L);
        assertEquals(0L, manager.getItemsToNextTier(playerId, Collection.WHEAT));
    }

    @Test
    void hasUnlockedTier_falseBeforeThreshold() {
        manager.addItems(playerId, Collection.WHEAT, 49L);
        assertFalse(manager.hasUnlockedTier(playerId, Collection.WHEAT, 1));
    }

    @Test
    void hasUnlockedTier_trueAtThreshold() {
        manager.addItems(playerId, Collection.WHEAT, 50L);
        assertTrue(manager.hasUnlockedTier(playerId, Collection.WHEAT, 1));
    }

    @Test
    void isMaxed_falseWhenNotDone() {
        manager.addItems(playerId, Collection.WHEAT, 50_000L);
        assertFalse(manager.isMaxed(playerId, Collection.WHEAT));
    }

    @Test
    void isMaxed_trueAtMaxTier() {
        manager.addItems(playerId, Collection.WHEAT, 100_000L);
        assertTrue(manager.isMaxed(playerId, Collection.WHEAT));
    }

    @Test
    void getProgressToNextTier_halfwayToTierOne() {
        manager.addItems(playerId, Collection.WHEAT, 25L);
        assertEquals(0.5, manager.getProgressToNextTier(playerId, Collection.WHEAT), 1e-9);
    }

    @Test
    void getProgressToNextTier_maxed_returnsOne() {
        manager.addItems(playerId, Collection.WHEAT, 100_000L);
        assertEquals(1.0, manager.getProgressToNextTier(playerId, Collection.WHEAT), 1e-9);
    }

    @Test
    void addItems_byName_unknownName_returnsMinusOne() {
        long result = manager.addItems(playerId, "notacollection", 10L);
        assertEquals(-1L, result);
    }

    @Test
    void addItems_byName_knownName_accumulates() {
        manager.addItems(playerId, "wheat", 60L);
        assertEquals(60L, manager.getItems(playerId, Collection.WHEAT));
    }

    @Test
    void reset_removesPlayerData() {
        manager.addItems(playerId, Collection.WHEAT, 100L);
        assertTrue(manager.reset(playerId));
        assertEquals(0L, manager.getItems(playerId, Collection.WHEAT));
    }

    @Test
    void reset_unknownPlayer_returnsFalse() {
        assertFalse(manager.reset(UUID.randomUUID()));
    }

    @Test
    void getAll_freshPlayer_returnsEmptyMap() {
        assertTrue(manager.getAll(playerId).isEmpty());
    }

    @Test
    void getAll_afterAdding_containsEntry() {
        manager.addItems(playerId, Collection.COAL, 75L);
        Map<Collection, Long> all = manager.getAll(playerId);
        assertEquals(1, all.size());
        assertEquals(75L, all.get(Collection.COAL));
    }

    @Test
    void getTotalForCategory_sumsFarmingCollections() {
        manager.addItems(playerId, Collection.WHEAT, 30L);
        manager.addItems(playerId, Collection.CARROT, 20L);
        assertEquals(50L, manager.getTotalForCategory(playerId, CollectionCategory.FARMING));
    }

    @Test
    void getTotalForCategory_unrelatedCategory_returnsZero() {
        manager.addItems(playerId, Collection.WHEAT, 100L);
        assertEquals(0L, manager.getTotalForCategory(playerId, CollectionCategory.COMBAT));
    }

    @Test
    void getTotalTiersUnlocked_freshPlayer_isZero() {
        assertEquals(0, manager.getTotalTiersUnlocked(playerId));
    }

    @Test
    void getTotalTiersUnlocked_incrementsWithProgress() {
        manager.addItems(playerId, Collection.WHEAT, 50L);   // tier 1
        manager.addItems(playerId, Collection.COAL, 100L);  // tier 2
        assertTrue(manager.getTotalTiersUnlocked(playerId) >= 3);
    }

    @Test
    void recordCollectionEvent_andGetCollectionsHistory_accumulates() {
        manager.recordCollectionEvent(playerId, "event one");
        manager.recordCollectionEvent(playerId, "event two");
        List<String> history = manager.getCollectionsHistory(playerId);
        assertEquals(2, history.size());
        assertTrue(history.contains("event one"));
        assertTrue(history.contains("event two"));
    }

    @Test
    void getCollectionsHistory_unknownPlayer_returnsEmptyList() {
        assertTrue(manager.getCollectionsHistory(UUID.randomUUID()).isEmpty());
    }

    @Test
    void getCollectionStats_noCollections_returnsNoneMessage() {
        String stats = manager.getCollectionStats(playerId);
        assertTrue(stats.startsWith("Top Collections:"));
        assertTrue(stats.contains("none"));
    }

    @Test
    void getCollectionStats_withEntries_listsTopCollections() {
        manager.addItems(playerId, Collection.WHEAT, 500L);
        manager.addItems(playerId, Collection.COAL, 200L);
        String stats = manager.getCollectionStats(playerId);
        assertTrue(stats.contains("Wheat"));
        assertTrue(stats.contains("Coal"));
    }
}
