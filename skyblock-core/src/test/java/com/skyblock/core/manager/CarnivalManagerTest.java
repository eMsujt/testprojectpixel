package com.skyblock.core.manager;

import com.skyblock.core.manager.CarnivalManager.CarnivalData;
import com.skyblock.core.manager.CarnivalManager.CarnivalGame;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CarnivalManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(CarnivalManager.getInstance(), CarnivalManager.getInstance());
    }

    @Test
    void balances_DefaultToZero() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0L, mgr.getTickets(id));
        assertEquals(0L, mgr.getTokens(id));
        assertEquals(0, mgr.getTimesPlayed(id, CarnivalGame.BOMBS));
        assertEquals(0, mgr.getBestScore(id, CarnivalGame.BOMBS));
    }

    @Test
    void addTickets_AccumulatesAndRejectsNegative() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(3L, mgr.addTickets(id, 3));
        assertEquals(5L, mgr.addTickets(id, 2));
        assertThrows(IllegalArgumentException.class, () -> mgr.addTickets(id, -1));
    }

    @Test
    void playGame_ChargesTicketRecordsPlayAndRewardsTokens() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addTickets(id, 2);

        assertTrue(mgr.playGame(id, CarnivalGame.FRUIT_DIGGING, 40, 10));
        assertEquals(1L, mgr.getTickets(id));
        assertEquals(1, mgr.getTimesPlayed(id, CarnivalGame.FRUIT_DIGGING));
        assertEquals(40, mgr.getBestScore(id, CarnivalGame.FRUIT_DIGGING));
        assertEquals(10L, mgr.getTokens(id));
    }

    @Test
    void playGame_FailsWhenNoTickets() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        assertFalse(mgr.playGame(id, CarnivalGame.ZOMBIE_SHOOTOUT, 5, 5));
        assertEquals(0, mgr.getTimesPlayed(id, CarnivalGame.ZOMBIE_SHOOTOUT));
        assertEquals(0L, mgr.getTokens(id));
    }

    @Test
    void playGame_KeepsBestScoreAcrossRounds() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addTickets(id, 3);
        mgr.playGame(id, CarnivalGame.BOMBS, 50, 0);
        mgr.playGame(id, CarnivalGame.BOMBS, 30, 0);
        mgr.playGame(id, CarnivalGame.BOMBS, 70, 0);
        assertEquals(70, mgr.getBestScore(id, CarnivalGame.BOMBS));
        assertEquals(3, mgr.getTimesPlayed(id, CarnivalGame.BOMBS));
    }

    @Test
    void playGame_RejectsNegativeScoreOrReward() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addTickets(id, 5);
        assertThrows(IllegalArgumentException.class,
                () -> mgr.playGame(id, CarnivalGame.BOMBS, -1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> mgr.playGame(id, CarnivalGame.BOMBS, 0, -1));
    }

    @Test
    void spendTokens_ChargesOnlyWhenAffordable() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addTickets(id, 1);
        mgr.playGame(id, CarnivalGame.FRUIT_DIGGING, 0, 25);

        assertFalse(mgr.spendTokens(id, 30));
        assertEquals(25L, mgr.getTokens(id));
        assertTrue(mgr.spendTokens(id, 20));
        assertEquals(5L, mgr.getTokens(id));
        assertThrows(IllegalArgumentException.class, () -> mgr.spendTokens(id, -1));
    }

    @Test
    void getCarnivalData_SnapshotsState() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addTickets(id, 4);
        mgr.playGame(id, CarnivalGame.ZOMBIE_SHOOTOUT, 60, 15);

        CarnivalData data = mgr.getCarnivalData(id);
        assertEquals(3L, data.tickets);
        assertEquals(15L, data.tokens);
        assertEquals(60, data.bestScores.get(CarnivalGame.ZOMBIE_SHOOTOUT));
        assertEquals(1, data.timesPlayed.get(CarnivalGame.ZOMBIE_SHOOTOUT));
    }

    @Test
    void reset_ClearsAllDataAndReportsWhetherAnyExisted() {
        CarnivalManager mgr = CarnivalManager.getInstance();
        UUID id = UUID.randomUUID();
        assertFalse(mgr.reset(id));
        mgr.addTickets(id, 1);
        mgr.playGame(id, CarnivalGame.BOMBS, 10, 5);
        assertTrue(mgr.reset(id));
        assertEquals(0L, mgr.getTickets(id));
        assertEquals(0L, mgr.getTokens(id));
        assertEquals(0, mgr.getBestScore(id, CarnivalGame.BOMBS));
    }
}
