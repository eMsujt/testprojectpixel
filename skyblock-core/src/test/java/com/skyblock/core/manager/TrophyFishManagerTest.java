package com.skyblock.core.manager;

import com.skyblock.core.manager.FishingManager.TrophyFish;
import com.skyblock.core.manager.TrophyFishManager.TrophyTier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TrophyFishManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(TrophyFishManager.getInstance(), TrophyFishManager.getInstance());
    }

    @Test
    void recordCatch_IncrementsCount() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getCatchCount(id, TrophyFish.GUSHER));
        mgr.recordCatch(id, TrophyFish.GUSHER);
        mgr.recordCatch(id, TrophyFish.GUSHER);
        assertEquals(2, mgr.getCatchCount(id, TrophyFish.GUSHER));
    }

    @Test
    void getTier_NullUntilFirstCatchThenBronze() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        assertNull(mgr.getTier(id, TrophyFish.BLOBFISH));
        mgr.recordCatch(id, TrophyFish.BLOBFISH);
        assertEquals(TrophyTier.BRONZE, mgr.getTier(id, TrophyFish.BLOBFISH));
    }

    @Test
    void getTier_EscalatesBronzeSilverGoldDiamondByThreshold() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        TrophyFish fish = TrophyFish.MANA_RAY;

        recordCatches(mgr, id, fish, 49);
        assertEquals(TrophyTier.BRONZE, mgr.getTier(id, fish)); // 1..49 -> bronze

        mgr.recordCatch(id, fish); // 50
        assertEquals(TrophyTier.SILVER, mgr.getTier(id, fish));

        recordCatches(mgr, id, fish, 50); // 100
        assertEquals(TrophyTier.GOLD, mgr.getTier(id, fish));

        recordCatches(mgr, id, fish, 50); // 150
        assertEquals(TrophyTier.DIAMOND, mgr.getTier(id, fish));

        assertEquals(150, mgr.getCatchCount(id, fish));
    }

    @Test
    void trophyTierThresholds_AreOrdered() {
        assertEquals(1, TrophyTier.BRONZE.threshold);
        assertEquals(50, TrophyTier.SILVER.threshold);
        assertEquals(100, TrophyTier.GOLD.threshold);
        assertEquals(150, TrophyTier.DIAMOND.threshold);
    }

    @Test
    void getTotalPoints_SumsHighestTierPointsAcrossFish() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getTotalPoints(id));

        mgr.recordCatch(id, TrophyFish.FLYFISH); // bronze -> 1 point
        assertEquals(1, mgr.getTotalPoints(id));

        recordCatches(mgr, id, TrophyFish.VANILLE, 50); // silver -> 2 points
        assertEquals(3, mgr.getTotalPoints(id));
    }

    @Test
    void getAllCatches_ReturnsUnmodifiableView() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        assertTrue(mgr.getAllCatches(id).isEmpty());
        mgr.recordCatch(id, TrophyFish.FLYFISH);
        assertEquals(1, mgr.getAllCatches(id).get(TrophyFish.FLYFISH));
        assertThrows(UnsupportedOperationException.class,
                () -> mgr.getAllCatches(id).put(TrophyFish.GUSHER, 5));
    }

    @Test
    void resetCatches_ClearsPlayerData() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.recordCatch(id, TrophyFish.VANILLE);
        mgr.resetCatches(id);
        assertEquals(0, mgr.getCatchCount(id, TrophyFish.VANILLE));
        assertNull(mgr.getTier(id, TrophyFish.VANILLE));
        assertEquals(0, mgr.getTotalPoints(id));
    }

    @Test
    void getAvailableTrophyFish_OnlyReturnsLevelEligibleFish() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        for (TrophyFish fish : mgr.getAvailableTrophyFish(1)) {
            assertTrue(fish.minLevel <= 1, fish + " should not be available at fishing level 1");
        }
    }

    @Test
    void rollTrophyFish_BelowAllLevelRequirementsNeverDrops() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        // every trophy fish requires at least level 1, so level 0 can never roll one
        for (int i = 0; i < 1000; i++) {
            assertNull(mgr.rollTrophyFish(0));
        }
    }

    @Test
    void rollTrophyFish_OnlyReturnsLevelEligibleFish() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        // at level 1, only fish with minLevel <= 1 may drop
        for (int i = 0; i < 2000; i++) {
            TrophyFish fish = mgr.rollTrophyFish(1);
            if (fish != null) {
                assertTrue(fish.minLevel <= 1,
                        fish + " should not drop at fishing level 1");
            }
        }
    }

    private static void recordCatches(TrophyFishManager mgr, UUID id, TrophyFish fish, int times) {
        for (int i = 0; i < times; i++) {
            mgr.recordCatch(id, fish);
        }
    }
}
