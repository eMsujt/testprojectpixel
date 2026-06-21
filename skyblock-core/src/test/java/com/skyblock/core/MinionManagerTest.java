package com.skyblock.core;

import com.skyblock.core.manager.MinionManager;
import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.manager.MinionManager.MinionTier;
import com.skyblock.core.manager.MinionManager.MinionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MinionManagerTest {

    private MinionManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = MinionManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        manager.clearMinions(playerId);
    }

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(MinionManager.getInstance(), MinionManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // placeMinion
    // -------------------------------------------------------------------------

    @Test
    void placeMinion_ReturnsDataWithCorrectOwnerAndType() {
        MinionData data = manager.placeMinion(playerId, MinionType.COBBLESTONE, MinionTier.TIER_1);

        assertEquals(playerId, data.owner);
        assertEquals(MinionType.COBBLESTONE, data.type);
        assertEquals(MinionTier.TIER_1, data.getTier());
        assertNotNull(data.id);
    }

    @Test
    void placeMinion_AppearsInGetMinions() {
        MinionData data = manager.placeMinion(playerId, MinionType.WHEAT, MinionTier.TIER_1);

        List<UUID> minions = manager.getMinions(playerId);
        assertEquals(1, minions.size());
        assertEquals(data.id, minions.get(0));
    }

    @Test
    void placeMinion_MultipleMinionsTrackedInOrder() {
        MinionData first  = manager.placeMinion(playerId, MinionType.WHEAT, MinionTier.TIER_1);
        MinionData second = manager.placeMinion(playerId, MinionType.COAL,  MinionTier.TIER_2);

        List<UUID> minions = manager.getMinions(playerId);
        assertEquals(2, minions.size());
        assertEquals(first.id,  minions.get(0));
        assertEquals(second.id, minions.get(1));
    }

    @Test
    void placeMinion_ThrowsWhenSlotCapReached() {
        manager.setMaxSlots(playerId, MinionManager.BASE_SLOTS);
        for (int i = 0; i < MinionManager.BASE_SLOTS; i++) {
            manager.placeMinion(playerId, MinionType.COBBLESTONE, MinionTier.TIER_1);
        }

        assertThrows(IllegalStateException.class,
                () -> manager.placeMinion(playerId, MinionType.WHEAT, MinionTier.TIER_1));
    }

    // -------------------------------------------------------------------------
    // removeMinion
    // -------------------------------------------------------------------------

    @Test
    void removeMinion_ReturnsTrueAndRemovesFromGetMinions() {
        MinionData data = manager.placeMinion(playerId, MinionType.WHEAT, MinionTier.TIER_1);

        assertTrue(manager.removeMinion(data.id));
        assertTrue(manager.getMinions(playerId).isEmpty());
        assertNull(manager.getMinion(data.id));
    }

    @Test
    void removeMinion_ReturnsFalseForUnknownId() {
        assertFalse(manager.removeMinion(UUID.randomUUID()));
    }

    // -------------------------------------------------------------------------
    // getMinions
    // -------------------------------------------------------------------------

    @Test
    void getMinions_EmptyForFreshPlayer() {
        assertTrue(manager.getMinions(playerId).isEmpty());
    }

    // -------------------------------------------------------------------------
    // clearMinions
    // -------------------------------------------------------------------------

    @Test
    void clearMinions_RemovesAllMinionsAndReturnsCount() {
        manager.placeMinion(playerId, MinionType.WHEAT,       MinionTier.TIER_1);
        manager.placeMinion(playerId, MinionType.COBBLESTONE, MinionTier.TIER_1);

        int removed = manager.clearMinions(playerId);

        assertEquals(2, removed);
        assertTrue(manager.getMinions(playerId).isEmpty());
    }

    @Test
    void clearMinions_ReturnsZeroForFreshPlayer() {
        assertEquals(0, manager.clearMinions(playerId));
    }

    // -------------------------------------------------------------------------
    // setMaxSlots / getMaxSlots
    // -------------------------------------------------------------------------

    @Test
    void setMaxSlots_ThrowsWhenBelowBaseSlots() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.setMaxSlots(playerId, MinionManager.BASE_SLOTS - 1));
    }

    @Test
    void setMaxSlots_AllowsPlacingUpToNewCap() {
        int cap = MinionManager.BASE_SLOTS + 2;
        manager.setMaxSlots(playerId, cap);
        assertEquals(cap, manager.getMaxSlots(playerId));

        for (int i = 0; i < cap; i++) {
            manager.placeMinion(playerId, MinionType.WHEAT, MinionTier.TIER_1);
        }
        assertEquals(cap, manager.getMinions(playerId).size());
    }
}
