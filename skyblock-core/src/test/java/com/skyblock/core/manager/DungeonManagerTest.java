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
}
