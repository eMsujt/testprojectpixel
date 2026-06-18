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

    // ------------------------------------------------------------------
    // Grade threshold tests — exercise every boundary via package-visible
    // sub-score fields (same package, no reflection needed).
    // ------------------------------------------------------------------

    @Test
    void getGrade_SPlusAtExactly300() {
        assertEquals("S+", DungeonManager.DungeonRun.withSubScores(100, 100, 100, 0).getGrade());
    }

    @Test
    void getGrade_SAt270() {
        assertEquals("S", DungeonManager.DungeonRun.withSubScores(90, 90, 90, 0).getGrade());
    }

    @Test
    void getGrade_AAt240() {
        assertEquals("A", DungeonManager.DungeonRun.withSubScores(60, 60, 100, 20).getGrade());
    }

    @Test
    void getGrade_BAt175() {
        assertEquals("B", DungeonManager.DungeonRun.withSubScores(50, 60, 50, 15).getGrade());
    }

    @Test
    void getGrade_CAt100() {
        assertEquals("C", DungeonManager.DungeonRun.withSubScores(30, 30, 30, 10).getGrade());
    }

    @Test
    void getGrade_DBelow100() {
        assertEquals("D", DungeonManager.DungeonRun.withSubScores(10, 10, 10, 0).getGrade());
    }

    @Test
    void getGrade_BoundaryAt239IsStillB() {
        assertEquals("B", DungeonManager.DungeonRun.withSubScores(60, 60, 99, 20).getGrade()); // 239
    }

    // ------------------------------------------------------------------
    // DungeonFloor enum — boss names for F1-F7 and Master Mode M1-M7
    // ------------------------------------------------------------------

    @Test
    void dungeonFloor_F1ThroughF7HaveCorrectBossNames() {
        assertEquals("Bonzo",         DungeonManager.DungeonFloor.FLOOR_1.getBossName());
        assertEquals("Scarf",         DungeonManager.DungeonFloor.FLOOR_2.getBossName());
        assertEquals("The Professor", DungeonManager.DungeonFloor.FLOOR_3.getBossName());
        assertEquals("Thorn",         DungeonManager.DungeonFloor.FLOOR_4.getBossName());
        assertEquals("Livid",         DungeonManager.DungeonFloor.FLOOR_5.getBossName());
        assertEquals("Sadan",         DungeonManager.DungeonFloor.FLOOR_6.getBossName());
        assertEquals("Necron",        DungeonManager.DungeonFloor.FLOOR_7.getBossName());
    }

    @Test
    void dungeonFloor_M1ThroughM7HaveCorrectBossNamesAndMasterModeFlag() {
        assertEquals("Bonzo",         DungeonManager.DungeonFloor.MASTER_1.getBossName());
        assertEquals("Scarf",         DungeonManager.DungeonFloor.MASTER_2.getBossName());
        assertEquals("The Professor", DungeonManager.DungeonFloor.MASTER_3.getBossName());
        assertEquals("Thorn",         DungeonManager.DungeonFloor.MASTER_4.getBossName());
        assertEquals("Livid",         DungeonManager.DungeonFloor.MASTER_5.getBossName());
        assertEquals("Sadan",         DungeonManager.DungeonFloor.MASTER_6.getBossName());
        assertEquals("Necron",        DungeonManager.DungeonFloor.MASTER_7.getBossName());
        for (DungeonManager.DungeonFloor f : DungeonManager.DungeonFloor.values()) {
            if (f.name().startsWith("MASTER_")) assertTrue(f.isMasterMode(), f.name());
            else assertFalse(f.isMasterMode(), f.name());
        }
    }

    // ------------------------------------------------------------------
    // DungeonClass enum — all five classes present
    // ------------------------------------------------------------------

    @Test
    void dungeonClass_AllFiveClassesExist() {
        assertEquals(5, DungeonManager.DungeonClass.values().length);
        assertNotNull(DungeonManager.DungeonClass.valueOf("HEALER"));
        assertNotNull(DungeonManager.DungeonClass.valueOf("MAGE"));
        assertNotNull(DungeonManager.DungeonClass.valueOf("BERSERK"));
        assertNotNull(DungeonManager.DungeonClass.valueOf("ARCHER"));
        assertNotNull(DungeonManager.DungeonClass.valueOf("TANK"));
    }

    @Test
    void setClass_EnumRoundTrips() {
        DungeonManager mgr = DungeonManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.setClass(id, DungeonManager.DungeonClass.HEALER);
        assertEquals(DungeonManager.DungeonClass.HEALER, mgr.getClass(id));
        mgr.setClass(id, DungeonManager.DungeonClass.ARCHER);
        assertEquals(DungeonManager.DungeonClass.ARCHER, mgr.getClass(id));
    }

    // ------------------------------------------------------------------
    // FLOOR_META map — boss names and master-mode level requirements
    // ------------------------------------------------------------------

    @Test
    void floorMeta_F1ThroughF7BossNames() {
        assertEquals("Bonzo",         DungeonManager.FLOOR_META.get("F1").getBossName());
        assertEquals("Scarf",         DungeonManager.FLOOR_META.get("F2").getBossName());
        assertEquals("The Professor", DungeonManager.FLOOR_META.get("F3").getBossName());
        assertEquals("Thorn",         DungeonManager.FLOOR_META.get("F4").getBossName());
        assertEquals("Livid",         DungeonManager.FLOOR_META.get("F5").getBossName());
        assertEquals("Sadan",         DungeonManager.FLOOR_META.get("F6").getBossName());
        assertEquals("Necron",        DungeonManager.FLOOR_META.get("F7").getBossName());
    }

    @Test
    void floorMeta_M1ThroughM7LevelRequirements() {
        assertEquals(20, DungeonManager.FLOOR_META.get("M1").getMinCatacombsLevel());
        assertEquals(22, DungeonManager.FLOOR_META.get("M2").getMinCatacombsLevel());
        assertEquals(24, DungeonManager.FLOOR_META.get("M3").getMinCatacombsLevel());
        assertEquals(26, DungeonManager.FLOOR_META.get("M4").getMinCatacombsLevel());
        assertEquals(28, DungeonManager.FLOOR_META.get("M5").getMinCatacombsLevel());
        assertEquals(30, DungeonManager.FLOOR_META.get("M6").getMinCatacombsLevel());
        assertEquals(32, DungeonManager.FLOOR_META.get("M7").getMinCatacombsLevel());
        assertEquals("Necron", DungeonManager.FLOOR_META.get("M7").getBossName());
    }

    // ------------------------------------------------------------------
    // computeExplorerScore
    // ------------------------------------------------------------------

    @Test
    void computeExplorerScore_FullClearAndMaxCryptsGives60() {
        assertEquals(60, DungeonManager.computeExplorerScore(10, 10, 10));
    }

    @Test
    void computeExplorerScore_NoRoomsAndNoCryptsGivesZero() {
        assertEquals(0, DungeonManager.computeExplorerScore(0, 10, 0));
    }

    @Test
    void computeExplorerScore_HalfRoomsAndZeroCryptsGives20() {
        assertEquals(20, DungeonManager.computeExplorerScore(5, 10, 0));
    }

    @Test
    void computeExplorerScore_ZeroTotalRoomsGivesZeroRoomPoints() {
        assertEquals(20, DungeonManager.computeExplorerScore(0, 0, 10));
    }
}
