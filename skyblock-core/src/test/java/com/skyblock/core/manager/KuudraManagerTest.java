package com.skyblock.core.manager;

import com.skyblock.core.manager.KuudraManager.KuudraPhase;
import com.skyblock.core.manager.KuudraManager.KuudraRun;
import com.skyblock.core.manager.KuudraManager.KuudraTier;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class KuudraManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(KuudraManager.getInstance(), KuudraManager.getInstance());
    }

    @Test
    void tiers_EscalateFromBasicToInfernal() {
        assertEquals(1, KuudraTier.BASIC.getTier());
        assertEquals(2, KuudraTier.HOT.getTier());
        assertEquals(3, KuudraTier.BURNING.getTier());
        assertEquals(4, KuudraTier.FIERY.getTier());
        assertEquals(5, KuudraTier.INFERNAL.getTier());
    }

    @Test
    void tierData_EssenceCostAndRewardsScaleWithTier() {
        // {essenceCost, tokenReward, suppliesCost}
        assertArrayEquals(new int[]{0, 1, 0}, KuudraManager.TIER_DATA.get("BASIC"));
        assertArrayEquals(new int[]{2000, 10, 100}, KuudraManager.TIER_DATA.get("INFERNAL"));
        // essence cost is strictly increasing across the escalation order
        int prev = -1;
        for (KuudraTier tier : KuudraTier.values()) {
            int essence = KuudraManager.TIER_DATA.get(tier.name())[0];
            assertTrue(essence > prev, "essence cost should increase for " + tier);
            prev = essence;
        }
    }

    @Test
    void newRun_StartsInBuildPhase() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinRun(KuudraTier.HOT, List.of(id), 0L);

        KuudraRun run = mgr.getActiveRun(id);
        assertNotNull(run);
        assertEquals(KuudraTier.HOT, run.getTier());
        assertEquals(KuudraPhase.BUILD, run.getPhase());
        assertFalse(run.isFinalPhase());

        mgr.leaveRun(id);
    }

    @Test
    void advancePhase_FollowsCombatPhaseOrder() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinRun(KuudraTier.FIERY, List.of(id), 0L);

        assertEquals(KuudraPhase.SUPPLY, mgr.advancePhase(id));
        assertEquals(KuudraPhase.DPS, mgr.advancePhase(id));
        assertEquals(KuudraPhase.BURN, mgr.advancePhase(id));
        assertTrue(mgr.getActiveRun(id).isFinalPhase());

        mgr.leaveRun(id);
    }

    @Test
    void advancePhase_BeyondBurnThrows() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinRun(KuudraTier.INFERNAL, List.of(id), 0L);
        mgr.advancePhase(id);
        mgr.advancePhase(id);
        mgr.advancePhase(id); // now in BURN

        assertThrows(IllegalStateException.class, () -> mgr.advancePhase(id));

        mgr.leaveRun(id);
    }

    @Test
    void advancePhase_WhenNotInRunThrows() {
        KuudraManager mgr = KuudraManager.getInstance();
        assertThrows(IllegalStateException.class, () -> mgr.advancePhase(UUID.randomUUID()));
    }

    @Test
    void completeRun_RequiresBurnPhaseAndRecordsCompletion() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.joinRun(KuudraTier.BASIC, List.of(id), 0L);

        // cannot complete before reaching the final BURN phase
        assertThrows(IllegalStateException.class, () -> mgr.completeRun(id));
        assertEquals(0, mgr.getCompletionCount(id, KuudraTier.BASIC));

        mgr.advancePhase(id);
        mgr.advancePhase(id);
        mgr.advancePhase(id);
        mgr.completeRun(id);

        assertEquals(1, mgr.getCompletionCount(id, KuudraTier.BASIC));
        assertNull(mgr.getActiveRun(id)); // run is cleared on completion
    }

    @Test
    void completeRun_AccumulatesPerTierCounts() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID id = UUID.randomUUID();

        for (int i = 0; i < 3; i++) {
            mgr.joinRun(KuudraTier.HOT, List.of(id), 0L);
            mgr.advancePhase(id);
            mgr.advancePhase(id);
            mgr.advancePhase(id);
            mgr.completeRun(id);
        }

        assertEquals(3, mgr.getCompletionCount(id, KuudraTier.HOT));
        assertEquals(0, mgr.getCompletionCount(id, KuudraTier.FIERY));
        assertEquals(3, mgr.getAllCompletions(id).get(KuudraTier.HOT));
    }

    @Test
    void joinRun_RegistersAllParticipants() {
        KuudraManager mgr = KuudraManager.getInstance();
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        List<UUID> party = Arrays.asList(a, b);
        mgr.joinRun(KuudraTier.BURNING, party, 0L);

        KuudraRun run = mgr.getActiveRun(a);
        assertSame(run, mgr.getActiveRun(b));
        assertEquals(party, run.getParticipants());

        mgr.leaveRun(a);
        mgr.leaveRun(b);
    }

    @Test
    void getCompletionCount_UnknownPlayerIsZero() {
        KuudraManager mgr = KuudraManager.getInstance();
        assertEquals(0, mgr.getCompletionCount(UUID.randomUUID(), KuudraTier.INFERNAL));
    }
}
