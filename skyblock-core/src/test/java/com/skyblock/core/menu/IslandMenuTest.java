package com.skyblock.core.menu;

import com.skyblock.core.manager.IslandManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IslandMenuTest {

    private UUID owner;

    @BeforeEach
    void setup() {
        owner = UUID.randomUUID();
    }

    @Test
    void title_isIsland() {
        assertEquals("§aIsland", new IslandMenu(owner).getTitle());
    }

    @Test
    void rows_isSix() {
        assertEquals(6, new IslandMenu(owner).getRows());
    }

    @Test
    void constructor_doesNotThrow() {
        assertDoesNotThrow(() -> new IslandMenu(owner));
    }

    @Test
    void beaconSlot_is22() {
        assertEquals(22, IslandMenu.BEACON_SLOT);
    }

    @Test
    void upgradeSlots_count_isEight() {
        assertEquals(8, IslandMenu.UPGRADE_SLOTS.length);
    }

    @Test
    void levelFromXp_zeroXp_levelZero() {
        assertEquals(0, IslandManager.levelFromXp(0L));
    }

    @Test
    void levelFromXp_exactLevel2_threshold() {
        // level = floor(sqrt(xp / 100)), so level 2 requires xp=400
        assertEquals(2, IslandManager.levelFromXp(400L));
    }

    @Test
    void xpToNextLevel_formula_correctAtLevelZero() {
        // level=0, nextLevelXp = 1^2 * 100 = 100; xpToNext = 100 - 0 = 100
        long xp = 0L;
        int level = IslandManager.levelFromXp(xp);
        long xpToNext = (long) (level + 1) * (level + 1) * IslandManager.XP_PER_LEVEL - xp;
        assertEquals(100L, xpToNext);
    }
}
