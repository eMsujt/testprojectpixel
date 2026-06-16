package com.skyblock.core.manager;

import com.skyblock.core.manager.FairySoulManager.FairyIsland;
import com.skyblock.core.model.Stat;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FairySoulManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(FairySoulManager.getInstance(), FairySoulManager.getInstance());
    }

    @Test
    void getTotalSouls_SumsEveryIsland() {
        int expected = 0;
        for (FairyIsland island : FairyIsland.values()) {
            expected += island.getSoulCount();
        }
        assertEquals(expected, FairySoulManager.getInstance().getTotalSouls());
    }

    // -------------------------------------------------------------------------
    // Collecting souls
    // -------------------------------------------------------------------------

    @Test
    void collectSoul_ReturnsTrueOnceThenFalse() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        assertTrue(mgr.collectSoul(player, FairyIsland.HUB, 1));
        assertFalse(mgr.collectSoul(player, FairyIsland.HUB, 1));
        assertTrue(mgr.hasCollected(player, FairyIsland.HUB, 1));
    }

    @Test
    void collectSoul_RejectsIndexOutOfRange() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> mgr.collectSoul(player, FairyIsland.THE_END, 0));
        assertThrows(IllegalArgumentException.class,
                () -> mgr.collectSoul(player, FairyIsland.THE_END, FairyIsland.THE_END.getSoulCount() + 1));
    }

    @Test
    void getFoundCount_TracksTotalAndPerIsland() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.collectSoul(player, FairyIsland.HUB, 1);
        mgr.collectSoul(player, FairyIsland.HUB, 2);
        mgr.collectSoul(player, FairyIsland.SPIDERS_DEN, 1);

        assertEquals(3, mgr.getFoundCount(player));
        assertEquals(2, mgr.getFoundCount(player, FairyIsland.HUB));
        assertEquals(1, mgr.getFoundCount(player, FairyIsland.SPIDERS_DEN));
        assertEquals(0, mgr.getFoundCount(player, FairyIsland.THE_END));
    }

    @Test
    void getFoundCount_ZeroForUnknownPlayer() {
        assertEquals(0, FairySoulManager.getInstance().getFoundCount(UUID.randomUUID()));
    }

    // -------------------------------------------------------------------------
    // Stat bonuses
    // -------------------------------------------------------------------------

    @Test
    void getStatBonuses_EmptyBelowFirstReward() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        for (int i = 1; i < FairySoulManager.SOULS_PER_REWARD; i++) {
            mgr.collectSoul(player, FairyIsland.HUB, i);
        }
        assertTrue(mgr.getStatBonuses(player).isEmpty());
    }

    @Test
    void getStatBonuses_FirstRewardGrantsHealth() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        for (int i = 1; i <= FairySoulManager.SOULS_PER_REWARD; i++) {
            mgr.collectSoul(player, FairyIsland.HUB, i);
        }
        Map<Stat, Double> bonuses = mgr.getStatBonuses(player);
        assertEquals(3.0, bonuses.get(Stat.HEALTH), 1e-9);
        assertEquals(1, bonuses.size());
    }

    @Test
    void getStatBonuses_FullCycleAggregatesEveryStat() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        // 25 souls = 5 rewards = one full reward cycle.
        for (int i = 1; i <= 25; i++) {
            mgr.collectSoul(player, FairyIsland.HUB, i);
        }
        Map<Stat, Double> bonuses = mgr.getStatBonuses(player);
        assertEquals(3.0, bonuses.get(Stat.HEALTH), 1e-9);
        assertEquals(1.0, bonuses.get(Stat.DEFENSE), 1e-9);
        assertEquals(0.5, bonuses.get(Stat.STRENGTH), 1e-9);
        assertEquals(1.0, bonuses.get(Stat.SPEED), 1e-9);
        assertEquals(1.0, bonuses.get(Stat.INTELLIGENCE), 1e-9);
    }

    @Test
    void resetPlayer_ClearsCollectedSouls() {
        FairySoulManager mgr = FairySoulManager.getInstance();
        UUID player = UUID.randomUUID();

        mgr.collectSoul(player, FairyIsland.HUB, 1);
        assertTrue(mgr.resetPlayer(player));
        assertEquals(0, mgr.getFoundCount(player));
        assertFalse(mgr.resetPlayer(player));
    }
}
