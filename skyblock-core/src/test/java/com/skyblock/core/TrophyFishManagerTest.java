package com.skyblock.core;

import com.skyblock.core.manager.FishingManager.TrophyFish;
import com.skyblock.core.manager.TrophyFishManager;
import com.skyblock.core.manager.TrophyFishManager.TrophyTier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TrophyFishManagerTest {

    private TrophyFishManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = TrophyFishManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        manager.resetCatches(playerId);
    }

    // --- getInstance ---

    @Test
    void getInstance_returnsSameSingleton() {
        assertSame(manager, TrophyFishManager.getInstance());
    }

    // --- recordCatch / getCatchCount ---

    @Test
    void recordCatch_incrementsCount() {
        manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        assertEquals(1, manager.getCatchCount(playerId, TrophyFish.SULPHUR_SKITTER));
    }

    @Test
    void recordCatch_accumulatesMultipleCalls() {
        manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        assertEquals(3, manager.getCatchCount(playerId, TrophyFish.SULPHUR_SKITTER));
    }

    @Test
    void recordCatch_nullPlayer_throws() {
        assertThrows(NullPointerException.class,
                () -> manager.recordCatch(null, TrophyFish.SULPHUR_SKITTER));
    }

    @Test
    void recordCatch_nullFish_throws() {
        assertThrows(NullPointerException.class,
                () -> manager.recordCatch(playerId, null));
    }

    @Test
    void getCatchCount_freshPlayer_returnsZero() {
        assertEquals(0, manager.getCatchCount(playerId, TrophyFish.SULPHUR_SKITTER));
    }

    @Test
    void getCatchCount_uncaughtFish_returnsZero() {
        manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        assertEquals(0, manager.getCatchCount(playerId, TrophyFish.MAHI_MAHI));
    }

    // --- getAllCatches ---

    @Test
    void getAllCatches_freshPlayer_returnsEmptyMap() {
        assertTrue(manager.getAllCatches(playerId).isEmpty());
    }

    @Test
    void getAllCatches_returnsUnmodifiableView() {
        manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        Map<TrophyFish, Integer> all = manager.getAllCatches(playerId);
        assertThrows(UnsupportedOperationException.class,
                () -> all.put(TrophyFish.MAHI_MAHI, 1));
    }

    @Test
    void getAllCatches_containsAllRecordedFish() {
        manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        manager.recordCatch(playerId, TrophyFish.MAHI_MAHI);
        Map<TrophyFish, Integer> all = manager.getAllCatches(playerId);
        assertEquals(1, all.get(TrophyFish.SULPHUR_SKITTER));
        assertEquals(1, all.get(TrophyFish.MAHI_MAHI));
    }

    // --- getTier ---

    @Test
    void getTier_noCatches_returnsNull() {
        assertNull(manager.getTier(playerId, TrophyFish.SULPHUR_SKITTER));
    }

    @Test
    void getTier_oneCatch_returnsBronze() {
        manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        assertEquals(TrophyTier.BRONZE, manager.getTier(playerId, TrophyFish.SULPHUR_SKITTER));
    }

    @Test
    void getTier_silverThreshold_returnsSilver() {
        for (int i = 0; i < TrophyTier.SILVER.threshold; i++) {
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        }
        assertEquals(TrophyTier.SILVER, manager.getTier(playerId, TrophyFish.SULPHUR_SKITTER));
    }

    @Test
    void getTier_diamondThreshold_returnsDiamond() {
        for (int i = 0; i < TrophyTier.DIAMOND.threshold; i++) {
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        }
        assertEquals(TrophyTier.DIAMOND, manager.getTier(playerId, TrophyFish.SULPHUR_SKITTER));
    }

    // --- getTotalPoints ---

    @Test
    void getTotalPoints_freshPlayer_returnsZero() {
        assertEquals(0, manager.getTotalPoints(playerId));
    }

    @Test
    void getTotalPoints_oneBronzeFish_returnsBronzePoints() {
        manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        assertEquals(TrophyTier.BRONZE.points, manager.getTotalPoints(playerId));
    }

    @Test
    void getTotalPoints_sumsHighestTierAcrossFish() {
        // SULPHUR_SKITTER to SILVER, MAHI_MAHI to BRONZE
        for (int i = 0; i < TrophyTier.SILVER.threshold; i++) {
            manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        }
        manager.recordCatch(playerId, TrophyFish.MAHI_MAHI);
        assertEquals(TrophyTier.SILVER.points + TrophyTier.BRONZE.points,
                manager.getTotalPoints(playerId));
    }

    // --- getAvailableTrophyFish ---

    @Test
    void getAvailableTrophyFish_levelZero_returnsNone() {
        assertEquals(0, manager.getAvailableTrophyFish(0).length);
    }

    @Test
    void getAvailableTrophyFish_highLevel_returnsAll() {
        assertEquals(TrophyFish.values().length, manager.getAvailableTrophyFish(100).length);
    }

    @Test
    void getAvailableTrophyFish_onlyIncludesFishAtOrBelowLevel() {
        for (TrophyFish fish : manager.getAvailableTrophyFish(5)) {
            assertTrue(fish.minLevel <= 5);
        }
    }

    // --- rollTrophyFish ---

    @Test
    void rollTrophyFish_noEligibleFish_returnsNull() {
        // No trophy fish are available below level 1
        assertNull(manager.rollTrophyFish(0));
    }

    @Test
    void rollTrophyFish_neverReturnsFishAboveLevel() {
        for (int i = 0; i < 1000; i++) {
            TrophyFish fish = manager.rollTrophyFish(5);
            if (fish != null) {
                assertTrue(fish.minLevel <= 5);
            }
        }
    }

    // --- resetCatches ---

    @Test
    void resetCatches_clearsAllCatchesForPlayer() {
        manager.recordCatch(playerId, TrophyFish.SULPHUR_SKITTER);
        manager.resetCatches(playerId);
        assertEquals(0, manager.getCatchCount(playerId, TrophyFish.SULPHUR_SKITTER));
        assertTrue(manager.getAllCatches(playerId).isEmpty());
    }

    @Test
    void resetCatches_doesNotAffectOtherPlayers() {
        UUID other = UUID.randomUUID();
        manager.recordCatch(other, TrophyFish.SULPHUR_SKITTER);
        manager.resetCatches(playerId);
        assertEquals(1, manager.getCatchCount(other, TrophyFish.SULPHUR_SKITTER));
        manager.resetCatches(other);
    }

    // --- TrophyTier enum sanity ---

    @Test
    void trophyTier_thresholdsAscend() {
        assertEquals(1, TrophyTier.BRONZE.threshold);
        assertEquals(50, TrophyTier.SILVER.threshold);
        assertEquals(100, TrophyTier.GOLD.threshold);
        assertEquals(150, TrophyTier.DIAMOND.threshold);
    }

    @Test
    void trophyTier_pointsAscend() {
        assertEquals(1, TrophyTier.BRONZE.points);
        assertEquals(2, TrophyTier.SILVER.points);
        assertEquals(3, TrophyTier.GOLD.points);
        assertEquals(4, TrophyTier.DIAMOND.points);
    }
}
