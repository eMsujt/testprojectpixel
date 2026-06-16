package com.skyblock.core.manager;

import com.skyblock.core.manager.MayorManager.MayorCandidate;
import com.skyblock.core.model.Stat;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MayorManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(MayorManager.getInstance(), MayorManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Votes
    // -------------------------------------------------------------------------

    @Test
    void vote_RecordedAndCleared() {
        MayorManager mgr = MayorManager.getInstance();
        UUID id = UUID.randomUUID();

        assertNull(mgr.getVote(id));
        mgr.vote(id, MayorCandidate.PAUL);
        assertEquals(MayorCandidate.PAUL, mgr.getVote(id));
        assertTrue(mgr.clearVote(id));
        assertNull(mgr.getVote(id));
        assertFalse(mgr.clearVote(id));
    }

    @Test
    void tallyVotes_CountsVotesPerCandidate() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.runElection(); // clear any leftover votes from other tests

        mgr.vote(UUID.randomUUID(), MayorCandidate.JERRY);
        mgr.vote(UUID.randomUUID(), MayorCandidate.JERRY);
        mgr.vote(UUID.randomUUID(), MayorCandidate.SCORPIUS);

        Map<MayorCandidate, Integer> tally = mgr.tallyVotes();
        assertEquals(2, tally.get(MayorCandidate.JERRY));
        assertEquals(1, tally.get(MayorCandidate.SCORPIUS));
        assertNull(tally.get(MayorCandidate.PAUL));
    }

    // -------------------------------------------------------------------------
    // Election cycle
    // -------------------------------------------------------------------------

    @Test
    void runElection_ElectsMostVotedAdvancesCycleAndClearsVotes() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.runElection(); // clean slate
        int cycleBefore = mgr.getElectionCycle();

        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        UUID c = UUID.randomUUID();
        mgr.vote(a, MayorCandidate.MARINA);
        mgr.vote(b, MayorCandidate.MARINA);
        mgr.vote(c, MayorCandidate.COLE);

        MayorCandidate winner = mgr.runElection();
        assertEquals(MayorCandidate.MARINA, winner);
        assertEquals(MayorCandidate.MARINA, mgr.getCurrentMayor());
        assertEquals(cycleBefore + 1, mgr.getElectionCycle());
        assertNull(mgr.getVote(a));
        assertNull(mgr.getVote(b));
        assertNull(mgr.getVote(c));
    }

    @Test
    void runElection_BreaksTiesByDeclarationOrder() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.runElection(); // clean slate

        // PAUL is declared before DIANA, so it wins a 1-1 tie.
        mgr.vote(UUID.randomUUID(), MayorCandidate.DIANA);
        mgr.vote(UUID.randomUUID(), MayorCandidate.PAUL);

        assertEquals(MayorCandidate.PAUL, mgr.runElection());
    }

    @Test
    void runElection_WithNoVotesReturnsNull() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.runElection(); // clear leftovers
        assertNull(mgr.runElection());
    }

    // -------------------------------------------------------------------------
    // Active-mayor perks
    // -------------------------------------------------------------------------

    @Test
    void getActiveStatBonuses_EmptyWhenNoMayor() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.setCurrentMayor(null);
        assertTrue(mgr.getActiveStatBonuses().isEmpty());
    }

    @Test
    void getActiveStatBonuses_MatchesActiveMayorPerks() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.setCurrentMayor(MayorCandidate.COLE);
        Map<Stat, Double> bonuses = mgr.getActiveStatBonuses();
        assertEquals(100.0, bonuses.get(Stat.MINING_SPEED));
        assertEquals(50.0, bonuses.get(Stat.MINING_FORTUNE));
    }

    @Test
    void applyPerks_AddsActiveMayorBonusesOntoBaseStats() {
        MayorManager mgr = MayorManager.getInstance();
        mgr.setCurrentMayor(MayorCandidate.COLE);

        Map<Stat, Double> base = new EnumMap<>(Stat.class);
        base.put(Stat.MINING_SPEED, 10.0);
        base.put(Stat.HEALTH, 100.0);

        Map<Stat, Double> result = mgr.applyPerks(base);
        assertEquals(110.0, result.get(Stat.MINING_SPEED)); // 10 base + 100 perk
        assertEquals(50.0, result.get(Stat.MINING_FORTUNE)); // perk only
        assertEquals(100.0, result.get(Stat.HEALTH));        // untouched
        assertEquals(10.0, base.get(Stat.MINING_SPEED));     // input not mutated
    }

    @Test
    void setCurrentMayor_RecordsElectionEvent() {
        MayorManager mgr = MayorManager.getInstance();
        int before = mgr.getElectionHistory().size();
        mgr.setCurrentMayor(MayorCandidate.AATROX);
        assertEquals(before + 1, mgr.getElectionHistory().size());
    }
}
