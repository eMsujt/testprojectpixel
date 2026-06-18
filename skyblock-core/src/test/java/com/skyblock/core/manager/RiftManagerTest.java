package com.skyblock.core.manager;

import com.skyblock.core.manager.RiftManager.RiftArea;
import com.skyblock.core.manager.RiftManager.RiftData;
import com.skyblock.core.manager.RiftManager.RiftMobType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RiftManagerTest {

    private final RiftManager mgr = RiftManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(RiftManager.getInstance(), RiftManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Entering / exiting / area progression
    // -------------------------------------------------------------------------

    @Test
    void enterRift_SetsZoneAndDefaultTime() {
        UUID player = UUID.randomUUID();
        mgr.enterRift(player, RiftArea.WYLD_WOODS);

        RiftData data = mgr.getRiftData(player);
        assertTrue(data.inRift);
        assertEquals(RiftArea.WYLD_WOODS, data.zone);
        assertEquals(480L, data.timeRemainingSeconds);
    }

    @Test
    void exitRift_ReturnsWhetherPlayerWasInside() {
        UUID player = UUID.randomUUID();
        assertFalse(mgr.exitRift(player));

        mgr.enterRift(player, RiftArea.LAGOON);
        assertTrue(mgr.exitRift(player));
        assertFalse(mgr.getRiftData(player).inRift);
    }

    @Test
    void exitRift_PreservesKillsForReEntry() {
        UUID player = UUID.randomUUID();
        mgr.enterRift(player, RiftArea.DREADFARM);
        mgr.addKill(player, RiftMobType.BACTE, 0);
        mgr.exitRift(player);

        assertEquals(1, mgr.getRiftData(player).kills.getOrDefault(RiftMobType.BACTE, 0));
    }

    // -------------------------------------------------------------------------
    // Kills / time
    // -------------------------------------------------------------------------

    @Test
    void addKill_IncrementsCountAndDeductsTime() {
        UUID player = UUID.randomUUID();
        mgr.enterRift(player, RiftArea.MIRRORVERSE);

        assertEquals(1, mgr.addKill(player, RiftMobType.CRUX, 30));
        assertEquals(2, mgr.addKill(player, RiftMobType.CRUX, 30));
        assertEquals(420L, mgr.getTimeRemaining(player));
    }

    @Test
    void addKill_TimeNeverGoesNegative() {
        UUID player = UUID.randomUUID();
        mgr.enterRift(player, RiftArea.COLOSSEUM);

        mgr.addKill(player, RiftMobType.VOLT, 10_000);
        assertEquals(0L, mgr.getTimeRemaining(player));
    }

    // -------------------------------------------------------------------------
    // Motes currency
    // -------------------------------------------------------------------------

    @Test
    void addAndSpendMotes_TracksBalance() {
        UUID player = UUID.randomUUID();
        assertEquals(0L, mgr.getMotes(player));

        assertEquals(100L, mgr.addMotes(player, 100));
        assertTrue(mgr.spendMotes(player, 40));
        assertEquals(60L, mgr.getMotes(player));
    }

    @Test
    void addMotes_AccruesAcrossMultipleCreditsUntilCap() {
        UUID player = UUID.randomUUID();
        // Successive credits accumulate...
        assertEquals(1000L, mgr.addMotes(player, 1000));
        assertEquals(3000L, mgr.addMotes(player, 2000));
        // ...but the purse never exceeds its cap; the overflow decays away.
        assertEquals(RiftManager.MOTES_PURSE_CAP, mgr.addMotes(player, 5000));
        assertEquals(RiftManager.MOTES_PURSE_CAP, mgr.getMotes(player));
        // Spending below the cap frees room for further accrual.
        assertTrue(mgr.spendMotes(player, 1000));
        assertEquals(RiftManager.MOTES_PURSE_CAP, mgr.addMotes(player, 1000));
    }

    @Test
    void spendMotes_FailsWhenInsufficient() {
        UUID player = UUID.randomUUID();
        mgr.addMotes(player, 10);

        assertFalse(mgr.spendMotes(player, 11));
        assertEquals(10L, mgr.getMotes(player));
    }

    @Test
    void motes_RejectNegativeAmounts() {
        UUID player = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> mgr.addMotes(player, -1));
        assertThrows(IllegalArgumentException.class, () -> mgr.spendMotes(player, -1));
    }

    // -------------------------------------------------------------------------
    // Timecharms / souls
    // -------------------------------------------------------------------------

    @Test
    void collectTimecharm_DeduplicatesById() {
        UUID player = UUID.randomUUID();
        assertTrue(mgr.collectTimecharm(player, "spider"));
        assertFalse(mgr.collectTimecharm(player, "spider"));
        assertTrue(mgr.collectTimecharm(player, "wyld"));
        assertEquals(2, mgr.getTimecharmCount(player));
    }

    @Test
    void collectRiftSoul_DeduplicatesById() {
        UUID player = UUID.randomUUID();
        assertTrue(mgr.collectRiftSoul(player, "a"));
        assertFalse(mgr.collectRiftSoul(player, "a"));
        assertEquals(1, mgr.getRiftSoulCount(player));
    }

    @Test
    void collectEnigmaSoul_DeduplicatesAndValidatesRange() {
        UUID player = UUID.randomUUID();
        assertTrue(mgr.collectEnigmaSoul(player, 1));
        assertFalse(mgr.collectEnigmaSoul(player, 1));
        assertEquals(1, mgr.getEnigmaSoulCount(player));

        assertThrows(IllegalArgumentException.class, () -> mgr.collectEnigmaSoul(player, 0));
        assertThrows(IllegalArgumentException.class,
                () -> mgr.collectEnigmaSoul(player, RiftManager.ENIGMA_SOUL_TOTAL + 1));
    }

    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------

    @Test
    void reset_ClearsAllStateAndReportsPriorData() {
        UUID player = UUID.randomUUID();
        mgr.enterRift(player, RiftArea.STILLGORE_CHATEAU);
        mgr.addMotes(player, 50);
        mgr.collectTimecharm(player, "x");

        assertTrue(mgr.reset(player));
        assertFalse(mgr.reset(player));

        RiftData data = mgr.getRiftData(player);
        assertFalse(data.inRift);
        assertEquals(0L, data.motes);
        assertEquals(0, data.timecharms);
    }
}
