package com.skyblock.core.manager;

import com.skyblock.core.manager.MinionManager.MinionData;
import com.skyblock.core.manager.MinionManager.MinionFuel;
import com.skyblock.core.manager.MinionManager.MinionTier;
import com.skyblock.core.manager.MinionManager.MinionType;
import com.skyblock.core.manager.MinionManager.MinionUpgrade;
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

    @Test
    void setUpgrade_InstallsUpgradeInSlot() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);

        assertEquals(MinionUpgrade.NONE, minion.getUpgrade(0));
        assertTrue(mgr.setUpgrade(minion.id, 0, MinionUpgrade.SUPER_COMPACTOR_3000));
        assertEquals(MinionUpgrade.SUPER_COMPACTOR_3000, minion.getUpgrade(0));
        assertEquals(MinionUpgrade.NONE, minion.getUpgrade(1));

        assertFalse(mgr.setUpgrade(UUID.randomUUID(), 0, MinionUpgrade.COMPACTOR));

        mgr.removeMinion(minion.id);
    }

    @Test
    void getHopperSellRate_ReturnsBestInstalledHopper() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);

        assertEquals(0.0, mgr.getHopperSellRate(minion));
        mgr.setUpgrade(minion.id, 0, MinionUpgrade.BUDGET_HOPPER);
        assertEquals(0.50, mgr.getHopperSellRate(minion));
        mgr.setUpgrade(minion.id, 1, MinionUpgrade.ENCHANTED_HOPPER);
        assertEquals(0.90, mgr.getHopperSellRate(minion));

        mgr.removeMinion(minion.id);
    }

    @Test
    void autoSell_SellsStoredResourcesViaHopperAndEmptiesStorage() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);

        // Fill some storage by ticking, then install an enchanted hopper.
        for (int i = 0; i < MinionManager.BASE_PRODUCTION_TICKS * 4; i++) {
            mgr.tick(minion);
        }
        int stored = minion.getStoredResources();
        assertEquals(4, stored);

        mgr.setUpgrade(minion.id, 0, MinionUpgrade.ENCHANTED_HOPPER);
        long coins = mgr.autoSell(minion.id, 10);
        assertEquals((long) Math.floor(stored * 10 * 0.90), coins);
        assertEquals(0, minion.getStoredResources());

        mgr.removeMinion(minion.id);
    }

    @Test
    void autoSell_ReturnsZeroWithoutHopper() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_1);

        for (int i = 0; i < MinionManager.BASE_PRODUCTION_TICKS; i++) {
            mgr.tick(minion);
        }
        assertEquals(1, minion.getStoredResources());
        assertEquals(0L, mgr.autoSell(minion.id, 10));   // no hopper installed
        assertEquals(1, minion.getStoredResources());    // storage untouched

        mgr.removeMinion(minion.id);
    }

    @Test
    void upgradeMinion_AdvancesTierByOne() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.WHEAT, MinionTier.TIER_1);

        assertTrue(mgr.upgradeMinion(minion.id));
        assertEquals(MinionTier.TIER_2, minion.getTier());

        mgr.removeMinion(minion.id);
    }

    @Test
    void upgradeMinion_ReturnsFalseAtMaxTier() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.WHEAT, MinionTier.TIER_12);

        assertFalse(mgr.upgradeMinion(minion.id));
        assertEquals(MinionTier.TIER_12, minion.getTier());

        mgr.removeMinion(minion.id);
    }

    @Test
    void getProductionIntervalTicks_DecreasesByTierOrdinal() {
        MinionManager mgr = MinionManager.getInstance();
        // TIER_5 ordinal = 4; expected interval = BASE_PRODUCTION_TICKS - 4
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_5);
        int expected = MinionManager.BASE_PRODUCTION_TICKS - MinionTier.TIER_5.ordinal();
        assertEquals(expected, mgr.getProductionIntervalTicks(minion));

        mgr.removeMinion(minion.id);
    }

    @Test
    void tick_ProducesResourceAtTierFiveInterval() {
        MinionManager mgr = MinionManager.getInstance();
        MinionData minion = mgr.placeMinion(UUID.randomUUID(), MinionType.COBBLESTONE, MinionTier.TIER_5);
        int interval = mgr.getProductionIntervalTicks(minion);

        int produced = 0;
        for (int i = 0; i < interval - 1; i++) {
            produced += mgr.tick(minion);
        }
        assertEquals(0, produced);
        assertEquals(1, mgr.tick(minion));

        mgr.removeMinion(minion.id);
    }
}
