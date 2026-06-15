package com.skyblock.core.manager;

import com.skyblock.core.island.manager.IslandManager.IslandUpgrade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IslandManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        IslandManager a = IslandManager.getInstance();
        IslandManager b = IslandManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(IslandManager.getInstance());
    }

    @Test
    void islandUpgrade_MinionSlotsMaxLevelIsPositive() {
        assertTrue(IslandUpgrade.MINION_SLOTS.getMaxLevel() > 0);
    }
}
