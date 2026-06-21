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

class CollectionsManagerTest {

    private CollectionManager manager;
    private UUID player;

    @BeforeEach
    void setUp() {
        manager = CollectionManager.getInstance();
        player = UUID.randomUUID();
    }

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(CollectionManager.getInstance(), CollectionManager.getInstance());
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(CollectionManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    @Test
    void maxTier_IsNine() {
        assertEquals(9, CollectionManager.MAX_TIER);
    }

    @Test
    void tierData_ContainsAllCollectionEnumValues() {
        for (Collection c : Collection.values()) {
            assertTrue(CollectionManager.TIER_DATA.containsKey(c),
                    "TIER_DATA missing entry for " + c);
        }
    }

    @Test
    void tierData_EachEntryHasNineTiers() {
        for (Map.Entry<Collection, int[]> e : CollectionManager.TIER_DATA.entrySet()) {
            assertEquals(CollectionManager.MAX_TIER, e.getValue().length,
                    "Wrong tier count for " + e.getKey());
        }
    }

    @Test
    void tierData_ThresholdsAreStrictlyIncreasing() {
        for (Map.Entry<Collection, int[]> e : CollectionManager.TIER_DATA.entrySet()) {
            int[] t = e.getValue();
            for (int i = 1; i < t.length; i++) {
                assertTrue(t[i] > t[i - 1],
                        "Non-increasing thresholds at index " + i + " for " + e.getKey());
            }
        }
    }

    // -------------------------------------------------------------------------
    // addItems / getItems — fresh-player baseline
    // -------------------------------------------------------------------------

    @Test
    void getItems_freshPlayer_returnsZero() {
        assertEquals(0L, manager.getItems(player, Collection.COAL));
    }

    @Test
    void addItems_returnsRunningTotal() {
        manager.addItems(player, Collection.COAL, 40L);
        long total = manager.addItems(player, Collection.COAL, 60L);
        assertEquals(100L, total);
    }

    @Test
    void addItems_zeroAmount_isNoop() {
        manager.addItems(player, Collection.COAL, 50L);
        manager.addItems(player, Collection.COAL, 0L);
        assertEquals(50L, manager.getItems(player, Collection.COAL));
    }

    @Test
    void addItems_negativeAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.addItems(player, Collection.COAL, -1L));
    }

    @Test
    void addItems_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.addItems(null, Collection.COAL, 10L));
    }

    // -------------------------------------------------------------------------
    // addItems by name (String overload)
    // -------------------------------------------------------------------------

    @Test
    void addItems_byName_knownLowercase_accumulates() {
        manager.addItems(player, "coal", 80L);
        assertEquals(80L, manager.getItems(player, Collection.COAL));
    }

    @Test
    void addItems_byName_unknownName_returnsMinusOne() {
        assertEquals(-1L, manager.addItems(player, "totally_unknown", 10L));
    }

    // -------------------------------------------------------------------------
    // Tier logic
    // -------------------------------------------------------------------------

    @Test
    void getTier_freshPlayer_isZero() {
        assertEquals(0, manager.getTier(player, Collection.WHEAT));
    }

    @Test
    void getTier_justBelowFirstThreshold_isZero() {
        manager.addItems(player, Collection.COAL, 49L); // COAL tier-I = 50
        assertEquals(0, manager.getTier(player, Collection.COAL));
    }

    @Test
    void getTier_atFirstThreshold_isTierOne() {
        manager.addItems(player, Collection.COAL, 50L);
        assertEquals(1, manager.getTier(player, Collection.COAL));
    }

    @Test
    void getTier_atSecondThreshold_isTierTwo() {
        manager.addItems(player, Collection.COAL, 100L);
        assertEquals(2, manager.getTier(player, Collection.COAL));
    }

    @Test
    void hasUnlockedTier_falseBeforeThreshold() {
        manager.addItems(player, Collection.COAL, 49L);
        assertFalse(manager.hasUnlockedTier(player, Collection.COAL, 1));
    }

    @Test
    void hasUnlockedTier_trueAtThreshold() {
        manager.addItems(player, Collection.COAL, 50L);
        assertTrue(manager.hasUnlockedTier(player, Collection.COAL, 1));
    }

    @Test
    void isMaxed_falseBeforeMaxTier() {
        manager.addItems(player, Collection.COAL, 50_000L);
        assertFalse(manager.isMaxed(player, Collection.COAL));
    }

    @Test
    void isMaxed_trueAtMaxTier() {
        manager.addItems(player, Collection.COAL, 100_000L); // COAL tier-IX = 100 000
        assertTrue(manager.isMaxed(player, Collection.COAL));
        assertEquals(CollectionManager.MAX_TIER, manager.getTier(player, Collection.COAL));
    }

    @Test
    void getItemsToNextTier_freshPlayer_equalsFirstThreshold() {
        assertEquals(50L, manager.getItemsToNextTier(player, Collection.COAL));
    }

    @Test
    void getItemsToNextTier_midTier_returnsRemainder() {
        manager.addItems(player, Collection.COAL, 75L); // tier I at 50; tier II at 100
        assertEquals(25L, manager.getItemsToNextTier(player, Collection.COAL));
    }

    @Test
    void getItemsToNextTier_maxed_returnsZero() {
        manager.addItems(player, Collection.COAL, 100_000L);
        assertEquals(0L, manager.getItemsToNextTier(player, Collection.COAL));
    }

    @Test
    void getProgressToNextTier_atZero_returnsZero() {
        assertEquals(0.0, manager.getProgressToNextTier(player, Collection.COAL), 1e-9);
    }

    @Test
    void getProgressToNextTier_halfwayToFirstTier() {
        manager.addItems(player, Collection.COAL, 25L); // 25/50 = 0.5
        assertEquals(0.5, manager.getProgressToNextTier(player, Collection.COAL), 1e-9);
    }

    @Test
    void getProgressToNextTier_maxed_returnsOne() {
        manager.addItems(player, Collection.COAL, 100_000L);
        assertEquals(1.0, manager.getProgressToNextTier(player, Collection.COAL), 1e-9);
    }

    // -------------------------------------------------------------------------
    // getTotalTiersUnlocked
    // -------------------------------------------------------------------------

    @Test
    void getTotalTiersUnlocked_freshPlayer_isZero() {
        assertEquals(0, manager.getTotalTiersUnlocked(player));
    }

    @Test
    void getTotalTiersUnlocked_sumsTiersAcrossCollections() {
        manager.addItems(player, Collection.COAL, 50L);   // tier 1
        manager.addItems(player, Collection.WHEAT, 100L); // tier 2
        // at minimum tier1 + tier2 = 3
        assertTrue(manager.getTotalTiersUnlocked(player) >= 3);
    }

    // -------------------------------------------------------------------------
    // getAll / getTotalForCategory
    // -------------------------------------------------------------------------

    @Test
    void getAll_freshPlayer_returnsEmptyMap() {
        assertTrue(manager.getAll(player).isEmpty());
    }

    @Test
    void getAll_afterAdding_containsEntry() {
        manager.addItems(player, Collection.DIAMOND, 60L);
        Map<Collection, Long> all = manager.getAll(player);
        assertEquals(1, all.size());
        assertEquals(60L, all.get(Collection.DIAMOND));
    }

    @Test
    void getAll_returnsUnmodifiableView() {
        manager.addItems(player, Collection.DIAMOND, 10L);
        Map<Collection, Long> all = manager.getAll(player);
        assertThrows(UnsupportedOperationException.class,
                () -> all.put(Collection.COAL, 1L));
    }

    @Test
    void getTotalForCategory_sumsMiningCollections() {
        manager.addItems(player, Collection.COAL, 60L);
        manager.addItems(player, Collection.DIAMOND, 40L);
        assertEquals(100L, manager.getTotalForCategory(player, CollectionCategory.MINING));
    }

    @Test
    void getTotalForCategory_unrelatedCategory_returnsZero() {
        manager.addItems(player, Collection.COAL, 100L); // MINING
        assertEquals(0L, manager.getTotalForCategory(player, CollectionCategory.FARMING));
    }

    // -------------------------------------------------------------------------
    // History
    // -------------------------------------------------------------------------

    @Test
    void getCollectionsHistory_freshPlayer_returnsEmptyList() {
        assertTrue(manager.getCollectionsHistory(UUID.randomUUID()).isEmpty());
    }

    @Test
    void recordCollectionEvent_appearsInHistory() {
        manager.recordCollectionEvent(player, "test event");
        List<String> history = manager.getCollectionsHistory(player);
        assertEquals(1, history.size());
        assertEquals("test event", history.get(0));
    }

    @Test
    void addItems_crossingTier_recordsHistoryEntry() {
        manager.addItems(player, Collection.COAL, 50L);
        List<String> history = manager.getCollectionsHistory(player);
        assertFalse(history.isEmpty());
        assertTrue(history.stream().anyMatch(e -> e.contains("tier 1") || e.contains("tier I")));
    }

    @Test
    void getAllCollectionsHistory_includesAllPlayers() {
        UUID other = UUID.randomUUID();
        manager.recordCollectionEvent(player, "a");
        manager.recordCollectionEvent(other, "b");
        Map<UUID, List<String>> all = manager.getAllCollectionsHistory();
        assertTrue(all.containsKey(player));
        assertTrue(all.containsKey(other));
    }

    // -------------------------------------------------------------------------
    // reset
    // -------------------------------------------------------------------------

    @Test
    void reset_removesPlayerData_returnsTrue() {
        manager.addItems(player, Collection.COAL, 80L);
        assertTrue(manager.reset(player));
        assertEquals(0L, manager.getItems(player, Collection.COAL));
    }

    @Test
    void reset_unknownPlayer_returnsFalse() {
        assertFalse(manager.reset(UUID.randomUUID()));
    }

    @Test
    void reset_calledTwice_secondCallReturnsFalse() {
        manager.addItems(player, Collection.COAL, 10L);
        manager.reset(player);
        assertFalse(manager.reset(player));
    }

    // -------------------------------------------------------------------------
    // getCollectionStats
    // -------------------------------------------------------------------------

    @Test
    void getCollectionStats_noData_containsNone() {
        String stats = manager.getCollectionStats(player);
        assertTrue(stats.startsWith("Top Collections:"));
        assertTrue(stats.contains("none"));
    }

    @Test
    void getCollectionStats_withData_listsTopItems() {
        manager.addItems(player, Collection.COAL, 500L);
        manager.addItems(player, Collection.DIAMOND, 200L);
        String stats = manager.getCollectionStats(player);
        assertTrue(stats.contains("Coal") || stats.contains("COAL"));
    }
}
