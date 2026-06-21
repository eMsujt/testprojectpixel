package com.skyblock.core;

import com.skyblock.core.manager.SackManager;
import com.skyblock.core.manager.SackManager.CapacityTier;
import com.skyblock.core.manager.SackManager.SackType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SackManagerTest {

    private SackManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = SackManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        manager.reset(playerId);
    }

    // --- getSackContents / getItemCount on fresh player ---

    @Test
    void getSackContents_freshPlayer_returnsEmptyMap() {
        Map<String, Integer> contents = manager.getSackContents(playerId, SackType.MINING);
        assertTrue(contents.isEmpty());
    }

    @Test
    void getItemCount_freshPlayer_returnsZero() {
        assertEquals(0, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
    }

    @Test
    void getTotalItemCount_freshPlayer_returnsZero() {
        assertEquals(0, manager.getTotalItemCount(playerId, "COBBLESTONE"));
    }

    // --- addItem ---

    @Test
    void addItem_storesItemAndReturnsZeroOverflow() {
        int overflow = manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 100);
        assertEquals(0, overflow);
        assertEquals(100, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
    }

    @Test
    void addItem_accumulates() {
        manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 50);
        manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 30);
        assertEquals(80, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
    }

    @Test
    void addItem_exceedsCapacity_returnsOverflow() {
        int cap = CapacityTier.SMALL.getCapacity(); // 4000
        int overflow = manager.addItem(playerId, SackType.MINING, "COBBLESTONE", cap + 500);
        assertEquals(500, overflow);
        assertEquals(cap, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
    }

    @Test
    void addItem_alreadyFull_returnsFullAmountAsOverflow() {
        int cap = CapacityTier.SMALL.getCapacity();
        manager.addItem(playerId, SackType.MINING, "COBBLESTONE", cap);
        int overflow = manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 10);
        assertEquals(10, overflow);
        assertEquals(cap, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
    }

    @Test
    void addItem_zeroAmount_returnsZeroAndChangesNothing() {
        int overflow = manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 0);
        assertEquals(0, overflow);
        assertEquals(0, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
    }

    @Test
    void addItem_negativeAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.addItem(playerId, SackType.MINING, "COBBLESTONE", -1));
    }

    @Test
    void addItem_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.addItem(null, SackType.MINING, "COBBLESTONE", 1));
    }

    @Test
    void addItem_nullSackType_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.addItem(playerId, null, "COBBLESTONE", 1));
    }

    @Test
    void addItem_nullItemId_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.addItem(playerId, SackType.MINING, null, 1));
    }

    // --- removeItem ---

    @Test
    void removeItem_reducesCount() {
        manager.addItem(playerId, SackType.FARMING, "WHEAT", 100);
        int remaining = manager.removeItem(playerId, SackType.FARMING, "WHEAT", 40);
        assertEquals(60, remaining);
        assertEquals(60, manager.getItemCount(playerId, SackType.FARMING, "WHEAT"));
    }

    @Test
    void removeItem_moreThanPresent_clampsToZero() {
        manager.addItem(playerId, SackType.FARMING, "WHEAT", 10);
        int remaining = manager.removeItem(playerId, SackType.FARMING, "WHEAT", 50);
        assertEquals(0, remaining);
        assertEquals(0, manager.getItemCount(playerId, SackType.FARMING, "WHEAT"));
    }

    @Test
    void removeItem_fromEmptySack_returnsZero() {
        int remaining = manager.removeItem(playerId, SackType.FARMING, "WHEAT", 10);
        assertEquals(0, remaining);
    }

    @Test
    void removeItem_negativeAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.removeItem(playerId, SackType.FARMING, "WHEAT", -1));
    }

    @Test
    void removeItem_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.removeItem(null, SackType.FARMING, "WHEAT", 1));
    }

    // --- getSackContents ---

    @Test
    void getSackContents_reflectsAddedItems() {
        manager.addItem(playerId, SackType.COMBAT, "BONE", 200);
        manager.addItem(playerId, SackType.COMBAT, "ROTTEN_FLESH", 50);
        Map<String, Integer> contents = manager.getSackContents(playerId, SackType.COMBAT);
        assertEquals(200, contents.get("BONE"));
        assertEquals(50, contents.get("ROTTEN_FLESH"));
    }

    @Test
    void getSackContents_isUnmodifiable() {
        manager.addItem(playerId, SackType.COMBAT, "BONE", 10);
        Map<String, Integer> contents = manager.getSackContents(playerId, SackType.COMBAT);
        assertThrows(UnsupportedOperationException.class, () -> contents.put("BONE", 999));
    }

    @Test
    void getSackContents_differentSackTypesAreIsolated() {
        manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 100);
        assertTrue(manager.getSackContents(playerId, SackType.FARMING).isEmpty());
    }

    // --- getTotalItemCount (Sack of Sacks) ---

    @Test
    void getTotalItemCount_aggregatesAcrossSackTypes() {
        manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 100);
        manager.addItem(playerId, SackType.COMBAT, "COBBLESTONE", 200);
        assertEquals(300, manager.getTotalItemCount(playerId, "COBBLESTONE"));
    }

    @Test
    void getTotalItemCount_onlyCountsRequestedItem() {
        manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 100);
        manager.addItem(playerId, SackType.MINING, "STONE", 50);
        assertEquals(100, manager.getTotalItemCount(playerId, "COBBLESTONE"));
    }

    @Test
    void getTotalItemCount_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.getTotalItemCount(null, "COBBLESTONE"));
    }

    @Test
    void getTotalItemCount_nullItemId_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.getTotalItemCount(playerId, null));
    }

    // --- setItemTier / getItemTier ---

    @Test
    void getItemTier_unregisteredItem_returnsDefaultTier() {
        assertEquals(SackManager.DEFAULT_TIER, manager.getItemTier("UNKNOWN_ITEM"));
    }

    @Test
    void setItemTier_changesEffectiveCapacity() {
        manager.setItemTier("DIAMOND", CapacityTier.JUMBO);
        assertEquals(CapacityTier.JUMBO, manager.getItemTier("DIAMOND"));
        int overflow = manager.addItem(playerId, SackType.MINING, "DIAMOND", CapacityTier.JUMBO.getCapacity());
        assertEquals(0, overflow);
        assertEquals(CapacityTier.JUMBO.getCapacity(), manager.getItemCount(playerId, SackType.MINING, "DIAMOND"));
    }

    @Test
    void setItemTier_nullItemId_throwsNullPointer() {
        assertThrows(NullPointerException.class, () -> manager.setItemTier(null, CapacityTier.LARGE));
    }

    @Test
    void setItemTier_nullTier_throwsNullPointer() {
        assertThrows(NullPointerException.class, () -> manager.setItemTier("DIAMOND", null));
    }

    // --- CapacityTier values ---

    @Test
    void capacityTier_valuesAreOrdered() {
        assertTrue(CapacityTier.SMALL.getCapacity() < CapacityTier.MEDIUM.getCapacity());
        assertTrue(CapacityTier.MEDIUM.getCapacity() < CapacityTier.LARGE.getCapacity());
        assertTrue(CapacityTier.LARGE.getCapacity() < CapacityTier.JUMBO.getCapacity());
    }

    // --- reset ---

    @Test
    void reset_existingPlayer_returnsTrueAndClearsData() {
        manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 10);
        assertTrue(manager.reset(playerId));
        assertEquals(0, manager.getItemCount(playerId, SackType.MINING, "COBBLESTONE"));
    }

    @Test
    void reset_unknownPlayer_returnsFalse() {
        assertFalse(manager.reset(UUID.randomUUID()));
    }

    @Test
    void reset_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class, () -> manager.reset(null));
    }

    // --- multiple players are isolated ---

    @Test
    void multiplePlayersAreIsolated() {
        UUID other = UUID.randomUUID();
        try {
            manager.addItem(playerId, SackType.MINING, "COBBLESTONE", 100);
            assertEquals(0, manager.getItemCount(other, SackType.MINING, "COBBLESTONE"));
        } finally {
            manager.reset(other);
        }
    }
}
