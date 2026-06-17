package com.skyblock.core.manager;

import com.skyblock.core.manager.GardenManager.GardenCrop;
import com.skyblock.core.manager.PestManager.PestType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PestManagerTest {

    private final PestManager mgr = PestManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(PestManager.getInstance(), PestManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Pest <-> crop mapping
    // -------------------------------------------------------------------------

    @Test
    void forCrop_ReturnsInfestingPest() {
        assertEquals(PestType.FLY, PestType.forCrop(GardenCrop.WHEAT));
        assertEquals(PestType.MITE, PestType.forCrop(GardenCrop.CACTUS));
        assertNull(PestType.forCrop(GardenCrop.COARSE_POTATO));
    }

    // -------------------------------------------------------------------------
    // Farming activity -> spawn chance
    // -------------------------------------------------------------------------

    @Test
    void spawnChance_ScalesWithActivityAndCaps() {
        UUID player = UUID.randomUUID();
        assertEquals(0.0D, mgr.getSpawnChance(player), 1e-9);

        mgr.recordFarmingActivity(player, 100);
        assertEquals(0.01D, mgr.getSpawnChance(player), 1e-9);

        mgr.recordFarmingActivity(player, 1_000_000);
        assertEquals(0.05D, mgr.getSpawnChance(player), 1e-9);

        mgr.reset(player);
    }

    @Test
    void recordFarmingActivity_NeverGoesNegative() {
        UUID player = UUID.randomUUID();
        mgr.recordFarmingActivity(player, 50);
        assertEquals(0L, mgr.recordFarmingActivity(player, -100));
        mgr.reset(player);
    }

    // -------------------------------------------------------------------------
    // Spawning pests
    // -------------------------------------------------------------------------

    @Test
    void spawnPest_AddsPestAndResetsActivity() {
        UUID player = UUID.randomUUID();
        mgr.recordFarmingActivity(player, 500);

        assertTrue(mgr.spawnPest(player, PestType.RAT));
        assertEquals(1, mgr.getPestCount(player, PestType.RAT));
        assertEquals(1, mgr.getTotalPests(player));
        assertEquals(0L, mgr.getFarmingActivity(player));

        mgr.reset(player);
    }

    @Test
    void spawnPest_StopsAtMax() {
        UUID player = UUID.randomUUID();
        for (int i = 0; i < PestManager.MAX_PESTS; i++) {
            assertTrue(mgr.spawnPest(player, PestType.SLUG));
        }
        assertEquals(PestManager.MAX_PESTS, mgr.getTotalPests(player));
        assertFalse(mgr.spawnPest(player, PestType.SLUG));
        assertEquals(PestManager.MAX_PESTS, mgr.getTotalPests(player));

        mgr.reset(player);
    }

    // -------------------------------------------------------------------------
    // SkyMart pesticide
    // -------------------------------------------------------------------------

    @Test
    void usePesticide_KillsPestAndConsumesStock() {
        UUID player = UUID.randomUUID();
        mgr.spawnPest(player, PestType.MOTH);
        mgr.addPesticides(player, 2);

        assertTrue(mgr.usePesticide(player, PestType.MOTH));
        assertEquals(0, mgr.getPestCount(player, PestType.MOTH));
        assertEquals(1, mgr.getPesticides(player));
        assertEquals(1L, mgr.getPestsKilled(player));

        mgr.reset(player);
    }

    @Test
    void usePesticide_FailsWithoutStockOrPest() {
        UUID player = UUID.randomUUID();
        // No pesticide held.
        mgr.spawnPest(player, PestType.BEETLE);
        assertFalse(mgr.usePesticide(player, PestType.BEETLE));

        // Pesticide held but no pest of that type.
        mgr.addPesticides(player, 1);
        assertFalse(mgr.usePesticide(player, PestType.MOUSE));
        assertEquals(1, mgr.getPesticides(player));
        assertEquals(0L, mgr.getPestsKilled(player));

        mgr.reset(player);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    @Test
    void remove_ReportsPriorData() {
        UUID player = UUID.randomUUID();
        assertFalse(mgr.remove(player));

        mgr.spawnPest(player, PestType.CRICKET);
        assertTrue(mgr.remove(player));
        assertEquals(0, mgr.getTotalPests(player));
    }
}
