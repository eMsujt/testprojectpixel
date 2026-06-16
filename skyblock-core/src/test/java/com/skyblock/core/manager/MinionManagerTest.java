package com.skyblock.core.manager;

import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.manager.MinionManager.MinionFuel;
import com.skyblock.core.manager.MinionManager.MinionTier;
import com.skyblock.core.manager.MinionManager.MinionType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MinionManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        MinionManager a = MinionManager.getInstance();
        MinionManager b = MinionManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(MinionManager.getInstance());
    }

    @Test
    void minionTier_Tier1IsFirstTier() {
        assertEquals(MinionTier.TIER_1, MinionTier.values()[0]);
    }

    @Test
    void tick_ProducesOneResourceAtBaseInterval() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);

        int produced = 0;
        for (int i = 0; i < MinionManager.BASE_PRODUCTION_TICKS - 1; i++) {
            produced += mgr.tick(minion);
        }
        assertEquals(0, produced);
        assertEquals(0, minion.getStoredResources());

        assertEquals(1, mgr.tick(minion));   // interval reached on this tick
        assertEquals(1, minion.getStoredResources());

        mgr.removeMinion(minion.id);
    }

    @Test
    void getProductionIntervalTicks_FasterWithFuel() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COAL, MinionTier.TIER_1);

        int baseInterval = mgr.getProductionIntervalTicks(minion);
        assertEquals(MinionManager.BASE_PRODUCTION_TICKS, baseInterval);

        assertTrue(mgr.addFuel(minion.id, MinionFuel.ENCHANTED_LAVA_BUCKET));
        int boosted = mgr.getProductionIntervalTicks(minion);
        assertTrue(boosted < baseInterval, "fuel should shorten the production interval");
        assertEquals((int) Math.round(baseInterval / MinionFuel.ENCHANTED_LAVA_BUCKET.getSpeedMultiplier()), boosted);

        mgr.removeMinion(minion.id);
    }

    @Test
    void tick_ConsumesFuelEachTickAndRevertsToNoneWhenExhausted() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COAL, MinionTier.TIER_1);

        assertTrue(mgr.addFuel(minion.id, MinionFuel.COAL));
        int duration = MinionFuel.COAL.getDurationTicks();
        assertEquals(duration, minion.getFuelTicksRemaining());

        mgr.tick(minion);
        assertEquals(duration - 1, minion.getFuelTicksRemaining());
        assertEquals(MinionFuel.COAL, minion.getFuel());

        // Burn through the rest of the fuel; it should empty and reset to NONE.
        for (int i = 1; i < duration; i++) {
            mgr.tick(minion);
        }
        assertEquals(0, minion.getFuelTicksRemaining());
        assertEquals(MinionFuel.NONE, minion.getFuel());

        mgr.removeMinion(minion.id);
    }

    @Test
    void addFuel_RejectsNoneAndUnknownMinion() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COAL, MinionTier.TIER_1);

        assertFalse(mgr.addFuel(minion.id, MinionFuel.NONE));
        assertFalse(mgr.addFuel(UUID.randomUUID(), MinionFuel.COAL));

        mgr.removeMinion(minion.id);
    }

    @Test
    void tick_StopsProducingWhenStorageFull() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);
        int capacity = mgr.getStorageCapacity(MinionTier.TIER_1);

        int produced = 0;
        // More than enough ticks to overflow storage if it were uncapped.
        for (int i = 0; i < (capacity + 5) * MinionManager.BASE_PRODUCTION_TICKS; i++) {
            produced += mgr.tick(minion);
        }
        assertEquals(capacity, produced);
        assertEquals(capacity, minion.getStoredResources());

        mgr.removeMinion(minion.id);
    }

    @Test
    void collectResources_EmptiesStorageAndReturnsAmount() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);

        for (int i = 0; i < MinionManager.BASE_PRODUCTION_TICKS; i++) {
            mgr.tick(minion);
        }
        assertEquals(1, minion.getStoredResources());

        assertEquals(1, mgr.collectResources(minion.id));
        assertEquals(0, minion.getStoredResources());
        assertEquals(0, mgr.collectResources(minion.id));   // already empty

        mgr.removeMinion(minion.id);
    }
}
