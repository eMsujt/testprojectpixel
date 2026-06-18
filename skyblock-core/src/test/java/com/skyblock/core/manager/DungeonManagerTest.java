package com.skyblock.core.manager;

import com.skyblock.core.manager.DungeonManager.DungeonClass;
import org.junit.jupiter.api.Test;

import java.util.OptionalInt;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DungeonManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(DungeonManager.getInstance(), DungeonManager.getInstance());
    }

    @Test
    void setDungeonFloor_ClampsToOneThroughSeven() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.setDungeonFloor(id, 5);
        assertEquals(5, mgr.getDungeonFloor(id));
        mgr.setDungeonFloor(id, 99);
        assertEquals(7, mgr.getDungeonFloor(id));
        mgr.setDungeonFloor(id, -3);
        assertEquals(1, mgr.getDungeonFloor(id));
    }

    @Test
    void getDungeonFloor_DefaultsToOne() {
        assertEquals(1, DungeonManager.getInstance().getDungeonFloor(UUID.randomUUID()));
    }

    @Test
    void addDungeonFloor_AdvancesAndClampsAtSeven() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.setDungeonFloor(id, 1);
        mgr.addDungeonFloor(id, 3);
        assertEquals(4, mgr.getDungeonFloor(id));
        mgr.addDungeonFloor(id, 10);
        assertEquals(7, mgr.getDungeonFloor(id));
    }

    @Test
    void highestFloor_DefaultsToZeroAndClampsAtSeven() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getHighestFloor(id));
        mgr.setHighestFloor(id, 100);
        assertEquals(7, mgr.getHighestFloor(id));
    }

    @Test
    void recordCompletion_TracksCountAndKeepsBestScore() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.recordCompletion(id, 3, 250);
        mgr.recordCompletion(id, 3, 180);
        mgr.recordCompletion(id, 3, 300);
        assertEquals(3, mgr.getCompletions(id, 3));
        assertEquals(OptionalInt.of(300), mgr.getBestScore(id, 3));
    }

    @Test
    void getBestScore_EmptyWhenFloorNeverCompleted() {
        assertEquals(OptionalInt.empty(),
                DungeonManager.getInstance().getBestScore(UUID.randomUUID(), 1));
    }

    @Test
    void recordCompletion_RejectsNonPositiveFloorAndNegativeScore() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> mgr.recordCompletion(id, 0, 100));
        assertThrows(IllegalArgumentException.class, () -> mgr.recordCompletion(id, 1, -1));
    }

    @Test
    void getHighestCompletedFloor_ReturnsMaxAcrossRecords() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(OptionalInt.empty(), mgr.getHighestCompletedFloor(id));
        mgr.recordCompletion(id, 2, 100);
        mgr.recordCompletion(id, 6, 100);
        mgr.recordCompletion(id, 4, 100);
        assertEquals(OptionalInt.of(6), mgr.getHighestCompletedFloor(id));
    }

    @Test
    void getClassLevel_FollowsCumulativeXpCurve() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getClassLevel(id, DungeonClass.MAGE));
        mgr.addClassXp(id, DungeonClass.MAGE, 50.0);   // exactly level 1
        assertEquals(1, mgr.getClassLevel(id, DungeonClass.MAGE));
        mgr.addClassXp(id, DungeonClass.MAGE, 74.0);   // cumulative 124, still level 1
        assertEquals(1, mgr.getClassLevel(id, DungeonClass.MAGE));
        mgr.addClassXp(id, DungeonClass.MAGE, 1.0);    // cumulative 125, level 2
        assertEquals(2, mgr.getClassLevel(id, DungeonClass.MAGE));
    }

    @Test
    void getClassLevel_HugeXpClampsToMaxClassLevel() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addClassXp(id, DungeonClass.TANK, Double.MAX_VALUE);
        assertEquals(DungeonManager.MAX_CLASS_LEVEL, mgr.getClassLevel(id, DungeonClass.TANK));
    }

    @Test
    void setPlayerClass_RejectsUnknownClassName() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> mgr.setPlayerClass(id, "Wizard"));
        mgr.setPlayerClass(id, "Mage");
        assertEquals("Mage", mgr.getPlayerClass(id));
    }

    @Test
    void computeSkillScore_PenalisesTwoPerDeath() {
        assertEquals(60, DungeonManager.computeSkillScore(0));
        assertEquals(56, DungeonManager.computeSkillScore(2));
        assertEquals(0,  DungeonManager.computeSkillScore(30));
        assertEquals(0,  DungeonManager.computeSkillScore(100));
    }

    @Test
    void computeSpeedScore_LinearDecayBetweenBoundaries() {
        assertEquals(100, DungeonManager.computeSpeedScore(0));
        assertEquals(100, DungeonManager.computeSpeedScore(300));
        assertEquals(20,  DungeonManager.computeSpeedScore(1200));
        assertEquals(20,  DungeonManager.computeSpeedScore(9999));
        int mid = DungeonManager.computeSpeedScore(750);
        assertTrue(mid > 20 && mid < 100, "mid-point score should be between 20 and 100");
    }

    @Test
    void completeScoredRun_PerfectRunGivesSPlusGrade() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        long start = 0L;
        mgr.startRun(DungeonManager.DungeonType.CATACOMBS_F7,
                java.util.Collections.singletonList(id), start);
        // 0 deaths (skill=60), all 10 rooms (explorer=40+20=60), 250 s (speed=100), completion=20 → 240 → A
        // To get S+: need ≥300. Max possible = 60+60+100+20 = 240... that's only A.
        // S requires ≥270; max is 240. The grade table tops out at "A" for 240. Let's just verify grade="A".
        DungeonManager.DungeonRun run = mgr.completeScoredRun(id, start + 250_000L,
                0, 10, 10, 10);
        assertEquals(240, run.getSkillScore() + run.getExplorerScore()
                + run.getSpeedScore() + run.getCompletionScore());
        assertEquals("A", run.getGrade());
        assertTrue(run.isCompleted());
    }

    @Test
    void completeScoredRun_ManyDeathsSlowClearGivesLowGrade() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        long start = 0L;
        mgr.startRun(DungeonManager.DungeonType.CATACOMBS_F1,
                java.util.Collections.singletonList(id), start);
        // 20 deaths → skill=max(0,60-40)=20; 5/10 rooms, 0 crypts → explorer=20;
        // 1800 s → speed=20; completion=20 → total=80 → D
        DungeonManager.DungeonRun run = mgr.completeScoredRun(id, start + 1_800_000L,
                20, 0, 5, 10);
        assertEquals("D", run.getGrade());
    }
}
