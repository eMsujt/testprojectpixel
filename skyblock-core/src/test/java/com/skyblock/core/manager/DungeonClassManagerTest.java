package com.skyblock.core.manager;

import com.skyblock.core.manager.DungeonClassManager.DungeonClass;
import com.skyblock.core.model.Stat;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DungeonClassManagerTest {

    private final DungeonClassManager classes = DungeonClassManager.getInstance();

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(DungeonClassManager.getInstance(), DungeonClassManager.getInstance());
    }

    @Test
    void selectClass_RoundTrips() {
        UUID id = UUID.randomUUID();
        assertNull(classes.getSelectedClass(id));
        classes.selectClass(id, DungeonClass.MAGE);
        assertEquals(DungeonClass.MAGE, classes.getSelectedClass(id));
        classes.remove(id);
    }

    @Test
    void addClassXp_AccumulatesAndLevelsUp() {
        UUID id = UUID.randomUUID();
        assertEquals(0, classes.getClassLevel(id, DungeonClass.BERSERK));
        // level 1 needs 50 XP, level 2 needs 125 XP (cumulative).
        assertEquals(120.0, classes.addClassXp(id, DungeonClass.BERSERK, 120.0));
        assertEquals(1, classes.getClassLevel(id, DungeonClass.BERSERK));
        classes.addClassXp(id, DungeonClass.BERSERK, 5.0); // crosses 125
        assertEquals(2, classes.getClassLevel(id, DungeonClass.BERSERK));
        classes.remove(id);
    }

    @Test
    void getClassLevel_CapsAtMax() {
        UUID id = UUID.randomUUID();
        classes.addClassXp(id, DungeonClass.TANK, 1_000_000_000_000.0);
        assertEquals(DungeonClassManager.MAX_CLASS_LEVEL, classes.getClassLevel(id, DungeonClass.TANK));
        classes.remove(id);
    }

    @Test
    void getPassiveBonus_ScalesWithLevel() {
        UUID id = UUID.randomUUID();
        // MAGE bonusPerLevel = 2.0; 120 XP -> level 1 -> bonus 2.0.
        classes.addClassXp(id, DungeonClass.MAGE, 120.0);
        assertEquals(2.0, classes.getPassiveBonus(id, DungeonClass.MAGE));
        classes.remove(id);
    }

    @Test
    void getActivePassiveBonus_UsesSelectedClassStat() {
        UUID id = UUID.randomUUID();
        assertTrue(classes.getActivePassiveBonus(id).isEmpty());
        classes.selectClass(id, DungeonClass.BERSERK);
        classes.addClassXp(id, DungeonClass.BERSERK, 120.0); // level 1, bonus 1.0
        Map<Stat, Double> bonus = classes.getActivePassiveBonus(id);
        assertEquals(1.0, bonus.get(Stat.STRENGTH));
        classes.remove(id);
    }

    @Test
    void addClassXp_RejectsNegative() {
        UUID id = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class,
                () -> classes.addClassXp(id, DungeonClass.ARCHER, -1.0));
    }
}
