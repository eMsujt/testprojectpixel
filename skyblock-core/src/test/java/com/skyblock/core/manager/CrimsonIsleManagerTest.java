package com.skyblock.core.manager;

import com.skyblock.core.kuudra.KuudraManager;
import com.skyblock.core.kuudra.KuudraManager.KuudraTier;
import com.skyblock.core.manager.ReputationManager.Faction;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CrimsonIsleManagerTest {

    private static void completeRun(KuudraManager mgr, UUID id, KuudraTier tier) {
        mgr.joinRun(tier, List.of(id), 0L);
        mgr.advancePhase(id); // SUPPLY
        mgr.advancePhase(id); // DPS
        mgr.advancePhase(id); // BURN
        mgr.completeRun(id);
    }

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(CrimsonIsleManager.getInstance(), CrimsonIsleManager.getInstance());
    }

    @Test
    void newPlayer_OnlyHasBasicUnlocked() {
        CrimsonIsleManager mgr = CrimsonIsleManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(KuudraTier.BASIC, mgr.getHighestUnlockedTier(id));
        assertTrue(mgr.canJoinTier(id, KuudraTier.BASIC));
        assertFalse(mgr.canJoinTier(id, KuudraTier.HOT));
    }

    @Test
    void completingTier_UnlocksTheNextTier() {
        CrimsonIsleManager mgr = CrimsonIsleManager.getInstance();
        UUID id = UUID.randomUUID();

        completeRun(mgr.kuudra(), id, KuudraTier.BASIC);
        assertEquals(KuudraTier.HOT, mgr.getHighestUnlockedTier(id));
        assertTrue(mgr.canJoinTier(id, KuudraTier.HOT));
        assertFalse(mgr.canJoinTier(id, KuudraTier.BURNING));

        completeRun(mgr.kuudra(), id, KuudraTier.HOT);
        assertEquals(KuudraTier.BURNING, mgr.getHighestUnlockedTier(id));
    }

    @Test
    void summary_ReportsFactionAndHighestTier() {
        CrimsonIsleManager mgr = CrimsonIsleManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.reputation().setFaction(id, Faction.BARBARIAN);

        String summary = mgr.getSummary(id);
        assertTrue(summary.contains("Barbarians"), summary);
        assertTrue(summary.contains("Basic"), summary);
    }

    @Test
    void nullArguments_Rejected() {
        CrimsonIsleManager mgr = CrimsonIsleManager.getInstance();
        assertThrows(NullPointerException.class, () -> mgr.getHighestUnlockedTier(null));
        assertThrows(NullPointerException.class, () -> mgr.canJoinTier(null, KuudraTier.BASIC));
        assertThrows(NullPointerException.class, () -> mgr.canJoinTier(UUID.randomUUID(), null));
    }
}
