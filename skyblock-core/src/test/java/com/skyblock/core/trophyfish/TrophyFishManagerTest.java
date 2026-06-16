package com.skyblock.core.trophyfish;

import com.skyblock.core.trophyfish.TrophyFishManager.TrophyFish;
import com.skyblock.core.trophyfish.TrophyFishManager.TrophyTier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TrophyFishManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(TrophyFishManager.getInstance(), TrophyFishManager.getInstance());
    }

    @Test
    void addCatch_IncrementsCount() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getCatchCount(id, TrophyFish.GUSHER));
        mgr.addCatch(id, TrophyFish.GUSHER);
        mgr.addCatch(id, TrophyFish.GUSHER);
        assertEquals(2, mgr.getCatchCount(id, TrophyFish.GUSHER));
    }

    @Test
    void getTier_NullUntilFirstCatchThenBronze() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        assertNull(mgr.getTier(id, TrophyFish.BLOBFISH));
        mgr.addCatch(id, TrophyFish.BLOBFISH);
        assertEquals(TrophyTier.BRONZE, mgr.getTier(id, TrophyFish.BLOBFISH));
    }

    @Test
    void getTier_EscalatesBronzeSilverGoldDiamondByThreshold() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        TrophyFish fish = TrophyFish.MANA_RAY;

        addCatches(mgr, id, fish, 49);
        assertEquals(TrophyTier.BRONZE, mgr.getTier(id, fish)); // 1..49 -> bronze

        mgr.addCatch(id, fish); // 50
        assertEquals(TrophyTier.SILVER, mgr.getTier(id, fish));

        addCatches(mgr, id, fish, 50); // 100
        assertEquals(TrophyTier.GOLD, mgr.getTier(id, fish));

        addCatches(mgr, id, fish, 50); // 150
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
    void getCatches_ReturnsUnmodifiableView() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        assertTrue(mgr.getCatches(id).isEmpty());
        mgr.addCatch(id, TrophyFish.FLYFISH);
        assertEquals(1, mgr.getCatches(id).get(TrophyFish.FLYFISH));
        assertThrows(UnsupportedOperationException.class,
                () -> mgr.getCatches(id).put(TrophyFish.GUSHER, 5));
    }

    @Test
    void remove_ClearsPlayerData() {
        TrophyFishManager mgr = TrophyFishManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addCatch(id, TrophyFish.VANILLE);
        mgr.remove(id);
        assertEquals(0, mgr.getCatchCount(id, TrophyFish.VANILLE));
        assertNull(mgr.getTier(id, TrophyFish.VANILLE));
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

    private static void addCatches(TrophyFishManager mgr, UUID id, TrophyFish fish, int times) {
        for (int i = 0; i < times; i++) {
            mgr.addCatch(id, fish);
        }
    }
}
