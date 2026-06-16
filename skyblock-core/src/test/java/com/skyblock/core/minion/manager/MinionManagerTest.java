package com.skyblock.core.minion.manager;

import com.skyblock.core.minion.manager.MinionManager.MinionTier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MinionManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        MinionManager a = MinionManager.getInstance();
        MinionManager b = MinionManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(MinionManager.getInstance());
    }

    @Test
    void minionTier_Tier1IsFirstTier() {
        assertEquals(MinionTier.TIER_1, MinionTier.values()[0]);
    }
}
