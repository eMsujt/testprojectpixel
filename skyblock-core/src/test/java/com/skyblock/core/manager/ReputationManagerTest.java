package com.skyblock.core.manager;

import com.skyblock.core.manager.ReputationManager.Faction;
import com.skyblock.core.manager.ReputationManager.ReputationTier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReputationManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(ReputationManager.getInstance(), ReputationManager.getInstance());
    }

    @Test
    void setFaction_StoresChosenFaction() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertNull(mgr.getFaction(id));
        mgr.setFaction(id, Faction.MAGE);
        assertEquals(Faction.MAGE, mgr.getFaction(id));
    }

    @Test
    void addReputation_AccumulatesPerFaction() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getReputation(id, Faction.MAGE));
        assertEquals(500, mgr.addReputation(id, Faction.MAGE, 500));
        assertEquals(800, mgr.addReputation(id, Faction.MAGE, 300));
        // Different faction tracked independently.
        assertEquals(0, mgr.getReputation(id, Faction.BARBARIAN));
        assertEquals(800, mgr.getReputation(id, Faction.MAGE));
    }

    @Test
    void addReputation_ClampsToBounds() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(ReputationManager.MAX_REPUTATION,
                mgr.addReputation(id, Faction.MAGE, 1_000_000));
        assertEquals(ReputationManager.MIN_REPUTATION,
                mgr.addReputation(id, Faction.BARBARIAN, -1_000_000));
    }

    @Test
    void getReputationTier_MapsReputationToTier() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(ReputationTier.NEUTRAL, mgr.getReputationTier(id, Faction.MAGE));
        mgr.addReputation(id, Faction.MAGE, 6000);
        assertEquals(ReputationTier.HONORED, mgr.getReputationTier(id, Faction.MAGE));
        mgr.addReputation(id, Faction.MAGE, 6000);   // total 12000
        assertEquals(ReputationTier.RESPECTED, mgr.getReputationTier(id, Faction.MAGE));
        mgr.addReputation(id, Faction.BARBARIAN, -6000);
        assertEquals(ReputationTier.HOSTILE, mgr.getReputationTier(id, Faction.BARBARIAN));
    }

    @Test
    void completeQuest_AwardsReputationAndTalliesQuest() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getQuestsCompleted(id, Faction.BARBARIAN));
        assertEquals(250, mgr.completeQuest(id, Faction.BARBARIAN, 250));
        assertEquals(450, mgr.completeQuest(id, Faction.BARBARIAN, 200));
        assertEquals(2, mgr.getQuestsCompleted(id, Faction.BARBARIAN));
        assertEquals(450, mgr.getReputation(id, Faction.BARBARIAN));
    }

    @Test
    void completeQuest_RejectsNegativeReward() {
        ReputationManager mgr = ReputationManager.getInstance();
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> mgr.completeQuest(id, Faction.MAGE, -10));
    }
}
