package com.skyblock.core.manager;

import com.skyblock.core.manager.FishingManager.SeaCreature;
import com.skyblock.core.manager.FishingManager.WaterType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FishingManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(FishingManager.getInstance(), FishingManager.getInstance());
    }

    @Test
    void getLevel_FollowsExponentialXpCurve() {
        FishingManager mgr = FishingManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(1, mgr.getLevel(id));            // no XP -> level 1
        mgr.addXp(id, 199.0);                          // below 50 * 2^2 = 200
        assertEquals(1, mgr.getLevel(id));
        mgr.addXp(id, 1.0);                            // total 200 -> level 2
        assertEquals(2, mgr.getLevel(id));
        mgr.addXp(id, 250.0);                          // total 450 = 50 * 3^2 -> level 3
        assertEquals(3, mgr.getLevel(id));
    }

    @Test
    void addXp_AccumulatesAndReturnsTotal() {
        FishingManager mgr = FishingManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(10.0, mgr.addXp(id, 10.0));
        assertEquals(25.0, mgr.addXp(id, 15.0));
        assertEquals(25.0, mgr.getXp(id));
    }

    @Test
    void getLevel_HugeXpClampsToMaxLevel() {
        FishingManager mgr = FishingManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addXp(id, Double.MAX_VALUE);
        assertEquals(50, mgr.getLevel(id));
    }

    @Test
    void addXp_RejectsNegativeAmount() {
        FishingManager mgr = FishingManager.getInstance();
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> mgr.addXp(id, -1.0));
    }

    @Test
    void rollSeaCreature_ReturnsNullWhenNoCreatureUnlocked() {
        FishingManager mgr = FishingManager.getInstance();
        // Level 0 unlocks nothing in WATER (lowest minLevel is 1), even with a
        // guaranteed spawn-chance roll, so the weighted pick has nothing to return.
        assertNull(mgr.rollSeaCreature(0, WaterType.WATER, 100.0));
    }

    @Test
    void rollSeaCreature_OnlyReturnsCreaturesUnlockedAtLevel() {
        FishingManager mgr = FishingManager.getInstance();
        // Level 1 WATER unlocks only SEA_WALKER; high luck guarantees the spawn.
        for (int i = 0; i < 50; i++) {
            assertEquals(SeaCreature.SEA_WALKER, mgr.rollSeaCreature(1, WaterType.WATER, 100.0));
        }
    }

    @Test
    void rollSeaCreature_RespectsWaterType() {
        FishingManager mgr = FishingManager.getInstance();
        for (int i = 0; i < 50; i++) {
            SeaCreature creature = mgr.rollSeaCreature(50, WaterType.LAVA, 100.0);
            assertNotNull(creature);
            assertEquals(WaterType.LAVA, creature.waterType);
        }
    }

    @Test
    void rollSeaCreature_RejectsNegativeLuck() {
        FishingManager mgr = FishingManager.getInstance();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.rollSeaCreature(20, WaterType.WATER, -0.5));
    }

    @Test
    void rollSeaCreature_RejectsNullWaterType() {
        FishingManager mgr = FishingManager.getInstance();
        assertThrows(NullPointerException.class,
                () -> mgr.rollSeaCreature(20, null, 0.0));
    }
}
