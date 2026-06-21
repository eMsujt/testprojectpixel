package com.skyblock.core;

import com.skyblock.core.manager.DungeonsManager;
import com.skyblock.core.manager.DungeonsManager.DungeonClass;
import com.skyblock.core.manager.DungeonsManager.Floor;
import com.skyblock.core.manager.DungeonsManager.FloorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DungeonsManagerTest {

    private DungeonsManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = DungeonsManager.getInstance();
        // Fresh id per test isolates state on the shared singleton delegate.
        playerId = UUID.randomUUID();
    }

    // --- singleton ---

    @Test
    void getInstance_returnsSameInstance() {
        assertSame(manager, DungeonsManager.getInstance());
    }

    // --- FloorType enum ---

    @Test
    void floorType_normalFloors_areNotMasterMode() {
        assertFalse(FloorType.ENTRANCE.isMasterMode());
        assertFalse(FloorType.FLOOR_1.isMasterMode());
        assertFalse(FloorType.FLOOR_7.isMasterMode());
    }

    @Test
    void floorType_masterFloors_areMasterMode() {
        assertTrue(FloorType.MASTER_1.isMasterMode());
        assertTrue(FloorType.MASTER_7.isMasterMode());
    }

    @Test
    void floorType_hasFifteenConstants() {
        assertEquals(15, FloorType.values().length);
    }

    // --- Floor enum ---

    @Test
    void floor_normalFloorRequiredSecrets_matchTable() {
        assertFalse(Floor.F1.isMasterMode());
        assertEquals(30, Floor.F1.getRequiredSecrets());
        assertEquals(120, Floor.F7.getRequiredSecrets());
    }

    @Test
    void floor_masterFloorsAreMasterMode() {
        assertTrue(Floor.M1.isMasterMode());
        assertEquals(300, Floor.M7.getRequiredSecrets());
    }

    // --- dungeon floor tracking ---

    @Test
    void getDungeonFloor_freshPlayer_defaultsToOne() {
        assertEquals(1, manager.getDungeonFloor(playerId));
    }

    @Test
    void setDungeonFloor_roundTrips() {
        manager.setDungeonFloor(playerId, 5);
        assertEquals(5, manager.getDungeonFloor(playerId));
    }

    @Test
    void setDungeonFloor_clampsToValidRange() {
        manager.setDungeonFloor(playerId, 99);
        assertEquals(7, manager.getDungeonFloor(playerId));
        manager.setDungeonFloor(playerId, 0);
        assertEquals(1, manager.getDungeonFloor(playerId));
    }

    // --- highest floor tracking ---

    @Test
    void getHighestFloor_freshPlayer_defaultsToZero() {
        assertEquals(0, manager.getHighestFloor(playerId));
    }

    @Test
    void setHighestFloor_roundTrips() {
        manager.setHighestFloor(playerId, 4);
        assertEquals(4, manager.getHighestFloor(playerId));
    }

    @Test
    void setHighestFloor_clampsToValidRange() {
        manager.setHighestFloor(playerId, 99);
        assertEquals(7, manager.getHighestFloor(playerId));
    }

    // --- class selection ---

    @Test
    void getPlayerClass_freshPlayer_isNull() {
        assertNull(manager.getPlayerClass(playerId));
    }

    @Test
    void setPlayerClass_roundTrips() {
        manager.setPlayerClass(playerId, DungeonClass.MAGE);
        assertEquals(DungeonClass.MAGE, manager.getPlayerClass(playerId));
    }

    // --- class XP / level ---

    @Test
    void getClassXp_freshPlayer_isZero() {
        assertEquals(0.0, manager.getClassXp(playerId, DungeonClass.ARCHER));
    }

    @Test
    void addClassXp_accumulatesAndReturnsTotal() {
        manager.addClassXp(playerId, DungeonClass.BERSERK, 30.0);
        double total = manager.addClassXp(playerId, DungeonClass.BERSERK, 20.0);
        assertEquals(50.0, total);
        assertEquals(50.0, manager.getClassXp(playerId, DungeonClass.BERSERK));
    }

    @Test
    void addClassXp_differentClassesAreIndependent() {
        manager.addClassXp(playerId, DungeonClass.TANK, 100.0);
        assertEquals(0.0, manager.getClassXp(playerId, DungeonClass.HEALER));
    }

    @Test
    void getClassLevel_freshPlayer_isZero() {
        assertEquals(0, manager.getClassLevel(playerId, DungeonClass.HEALER));
    }

    @Test
    void getClassLevel_atFirstThreshold_isOne() {
        // CLASS_XP_TABLE[1] = 50
        manager.addClassXp(playerId, DungeonClass.MAGE, 50.0);
        assertEquals(1, manager.getClassLevel(playerId, DungeonClass.MAGE));
    }

    // --- recordMob ---

    @Test
    void recordMob_withSelectedClass_awardsMobClassXp() {
        manager.setPlayerClass(playerId, DungeonClass.ARCHER);
        manager.recordMob(playerId);
        assertEquals(DungeonsManager.MOB_CLASS_XP,
                manager.getClassXp(playerId, DungeonClass.ARCHER));
    }

    @Test
    void recordMob_withoutSelectedClass_isNoOp() {
        manager.recordMob(playerId);
        for (DungeonClass cls : DungeonClass.values()) {
            assertEquals(0.0, manager.getClassXp(playerId, cls));
        }
    }
}
