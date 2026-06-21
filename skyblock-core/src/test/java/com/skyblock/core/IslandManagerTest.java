package com.skyblock.core;

import com.skyblock.core.manager.IslandManager;
import com.skyblock.core.manager.IslandManager.IslandData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IslandManagerTest {

    private IslandManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = IslandManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @Test
    void getIslandData_freshPlayer_returnsEmpty() {
        assertTrue(manager.getIslandData(playerId).isEmpty());
    }

    @Test
    void getOrCreateIslandData_createsDefaultRecord() {
        IslandData data = manager.getOrCreateIslandData(playerId);
        assertEquals(playerId, data.owner());
        assertEquals(0, data.level());
        assertEquals(0L, data.blocksPlaced());
        assertEquals(IslandData.DEFAULT_MINION_SLOTS, data.minionSlots());
        assertTrue(data.trustees().isEmpty());
    }

    @Test
    void getOrCreateIslandData_isIdempotent() {
        IslandData first = manager.getOrCreateIslandData(playerId);
        IslandData second = manager.getOrCreateIslandData(playerId);
        assertSame(first, second);
    }

    @Test
    void getIslandLevel_freshPlayer_isZero() {
        assertEquals(0, manager.getIslandLevel(playerId));
    }

    @Test
    void setLevel_updatesIslandLevel() {
        manager.setLevel(playerId, 7);
        assertEquals(7, manager.getIslandLevel(playerId));
    }

    @Test
    void setLevel_negative_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> manager.setLevel(playerId, -1));
    }

    @Test
    void levelFromXp_usesSqrtFormula() {
        // level = floor(sqrt(xp / 100))
        assertEquals(0, IslandManager.levelFromXp(0L));
        assertEquals(1, IslandManager.levelFromXp(100L));
        assertEquals(2, IslandManager.levelFromXp(400L));
    }

    @Test
    void levelFromXp_negative_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> IslandManager.levelFromXp(-1L));
    }

    @Test
    void addIslandXp_accumulatesAndDerivesLevel() {
        assertEquals(100L, manager.addIslandXp(playerId, 100L));
        assertEquals(400L, manager.addIslandXp(playerId, 300L));
        assertEquals(400L, manager.getIslandXp(playerId));
        assertEquals(2, manager.getIslandLevel(playerId));
        assertEquals(2, manager.getIslandLevelFromXp(playerId));
    }

    @Test
    void addIslandXp_negative_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> manager.addIslandXp(playerId, -1L));
    }

    @Test
    void getMinionSlots_freshPlayer_isDefault() {
        assertEquals(IslandData.DEFAULT_MINION_SLOTS, manager.getMinionSlots(playerId));
    }

    @Test
    void setMinionSlots_updatesValue() {
        manager.setMinionSlots(playerId, 12);
        assertEquals(12, manager.getMinionSlots(playerId));
    }

    @Test
    void setMinionSlots_negative_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> manager.setMinionSlots(playerId, -1));
    }

    @Test
    void addTrustee_newTrustee_returnsTrue() {
        UUID trustee = UUID.randomUUID();
        assertTrue(manager.addTrustee(playerId, trustee));
        assertTrue(manager.getIslandData(playerId).orElseThrow().trustees().contains(trustee));
    }

    @Test
    void addTrustee_duplicate_returnsFalse() {
        UUID trustee = UUID.randomUUID();
        manager.addTrustee(playerId, trustee);
        assertFalse(manager.addTrustee(playerId, trustee));
    }

    @Test
    void removeTrustee_existing_returnsTrue() {
        UUID trustee = UUID.randomUUID();
        manager.addTrustee(playerId, trustee);
        assertTrue(manager.removeTrustee(playerId, trustee));
        assertFalse(manager.getIslandData(playerId).orElseThrow().trustees().contains(trustee));
    }

    @Test
    void removeTrustee_noIsland_returnsFalse() {
        assertFalse(manager.removeTrustee(playerId, UUID.randomUUID()));
    }

    @Test
    void addBlocksPlaced_accumulates() {
        manager.addBlocksPlaced(playerId, 10L);
        manager.addBlocksPlaced(playerId, 5L);
        assertEquals(15L, manager.getIslandData(playerId).orElseThrow().blocksPlaced());
    }

    @Test
    void addBlocksPlaced_negative_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> manager.addBlocksPlaced(playerId, -1L));
    }

    @Test
    void islandData_nullOwner_throwsNullPointer() {
        assertThrows(NullPointerException.class, () -> manager.getOrCreateIslandData(null));
    }

    @Test
    void getIslandBiome_freshPlayer_defaultsToPlains() {
        assertEquals("PLAINS", manager.getIslandBiome(playerId));
    }

    @Test
    void setIslandBiome_updatesValue() {
        manager.setIslandBiome(playerId, "DESERT");
        assertEquals("DESERT", manager.getIslandBiome(playerId));
    }

    @Test
    void isIslandUnlocked_freshPlayer_isFalse() {
        assertFalse(manager.isIslandUnlocked(playerId));
    }

    @Test
    void setIslandUnlocked_updatesValue() {
        manager.setIslandUnlocked(playerId, true);
        assertTrue(manager.isIslandUnlocked(playerId));
    }

    @Test
    void addVisitor_incrementsCount() {
        manager.addVisitor(playerId);
        manager.addVisitor(playerId);
        assertEquals(2, manager.getVisitorCount(playerId));
    }

    @Test
    void hasIsland_freshPlayer_isFalse() {
        assertFalse(manager.hasIsland(playerId));
    }
}
