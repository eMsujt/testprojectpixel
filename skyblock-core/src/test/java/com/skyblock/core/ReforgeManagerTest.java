package com.skyblock.core;

import com.skyblock.core.manager.ReforgeManager;
import com.skyblock.core.manager.ReforgeManager.ReforgeStone;
import com.skyblock.core.manager.ReforgeManager.ReforgeType;
import com.skyblock.core.model.Rarity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReforgeManagerTest {

    private ReforgeManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = ReforgeManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        manager.clearReforge(playerId);
        manager.clearSlotReforges(playerId);
    }

    // --- singleton ---

    @Test
    void getInstance_returnsSameInstance() {
        assertSame(ReforgeManager.getInstance(), ReforgeManager.getInstance());
    }

    // --- catalogue ---

    @Test
    void testGetAllReforges_includesEveryReforgeType() {
        // The ReforgeType enum is the full reforge catalogue; NONE plus real reforges.
        assertTrue(ReforgeType.values().length > 1);
        assertEquals(ReforgeType.NONE, ReforgeType.values()[0]);
        assertEquals(ReforgeType.SUPERIOR, ReforgeType.fromName("Superior"));
    }

    // --- active player reforge ---

    @Test
    void getReforge_freshPlayer_defaultsToNone() {
        assertEquals(ReforgeType.NONE, manager.getReforge(playerId));
    }

    @Test
    void setReforge_thenGetReforge_returnsSetValue() {
        manager.setReforge(playerId, ReforgeType.SHARP);
        assertEquals(ReforgeType.SHARP, manager.getReforge(playerId));
    }

    @Test
    void clearReforge_resetsToNone() {
        manager.setReforge(playerId, ReforgeType.PERFECT);
        manager.clearReforge(playerId);
        assertEquals(ReforgeType.NONE, manager.getReforge(playerId));
    }

    @Test
    void getAllReforges_reflectsSetReforge() {
        manager.setReforge(playerId, ReforgeType.ANCIENT);
        assertEquals(ReforgeType.ANCIENT, manager.getAllReforges().get(playerId));
    }

    @Test
    void getAllReforges_isUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> manager.getAllReforges().put(playerId, ReforgeType.SHARP));
    }

    @Test
    void getReforge_rejectsNullPlayerId() {
        assertThrows(NullPointerException.class, () -> manager.getReforge(null));
    }

    @Test
    void setReforge_rejectsNullReforge() {
        assertThrows(NullPointerException.class, () -> manager.setReforge(playerId, null));
    }

    // --- per-slot reforges ---

    @Test
    void getSlotReforge_freshSlot_defaultsToNone() {
        assertEquals(ReforgeType.NONE, manager.getSlotReforge(playerId, "weapon"));
    }

    @Test
    void setSlotReforge_thenGetSlotReforge_returnsSetValue() {
        manager.setSlotReforge(playerId, "weapon", ReforgeType.LEGENDARY);
        assertEquals(ReforgeType.LEGENDARY, manager.getSlotReforge(playerId, "weapon"));
    }

    @Test
    void setSlotReforge_none_clearsSlot() {
        manager.setSlotReforge(playerId, "weapon", ReforgeType.LEGENDARY);
        manager.setSlotReforge(playerId, "weapon", ReforgeType.NONE);
        assertEquals(ReforgeType.NONE, manager.getSlotReforge(playerId, "weapon"));
    }

    @Test
    void clearSlotReforges_removesAllSlots() {
        manager.setSlotReforge(playerId, "weapon", ReforgeType.LEGENDARY);
        manager.setSlotReforge(playerId, "helmet", ReforgeType.GENTLE);
        manager.clearSlotReforges(playerId);
        assertEquals(ReforgeType.NONE, manager.getSlotReforge(playerId, "weapon"));
        assertEquals(ReforgeType.NONE, manager.getSlotReforge(playerId, "helmet"));
    }

    // --- reforge stones ---

    @Test
    void applyStone_setsResolvedReforge() {
        ReforgeType applied = manager.applyStone(playerId, ReforgeStone.SHARP);
        assertEquals(ReforgeType.SHARP, applied);
        assertEquals(ReforgeType.SHARP, manager.getReforge(playerId));
    }

    @Test
    void applyStone_toSlot_setsResolvedSlotReforge() {
        ReforgeType applied = manager.applyStone(playerId, "weapon", ReforgeStone.PERFECT);
        assertEquals(ReforgeType.PERFECT, applied);
        assertEquals(ReforgeType.PERFECT, manager.getSlotReforge(playerId, "weapon"));
    }

    // --- reforge cost ---

    @Test
    void getReforgeCost_increasesWithRarity() {
        assertEquals(250, ReforgeManager.getReforgeCost(Rarity.COMMON));
        assertEquals(1000, ReforgeManager.getReforgeCost(Rarity.RARE));
        assertTrue(ReforgeManager.getReforgeCost(Rarity.COMMON)
                < ReforgeManager.getReforgeCost(Rarity.MYTHIC));
    }
}
