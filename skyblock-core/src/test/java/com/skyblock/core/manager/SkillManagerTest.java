package com.skyblock.core.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SkillManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        SkillManager a = SkillManager.getInstance();
        SkillManager b = SkillManager.getInstance();
        assertSame(a, b);
    }

    @Test
    void getInstance_ReturnsNonNull() {
        assertNotNull(SkillManager.getInstance());
    }

    @Test
    void skillXpTable_IsNonEmpty() {
        assertFalse(SkillManager.SKILL_XP_TABLE.isEmpty());
    }

    @Test
    void skillXpTable_StoresPerLevelDeltas_NotCumulative() {
        // SKILL_XP_TABLE must store per-level deltas, not cumulative totals.
        // Entry[0] = 50 XP to reach level 1; entry[1] = 125 XP to advance to level 2.
        // If the table stored cumulative values, entry[1] would be 175 (50 + 125).
        long[] farming = SkillManager.SKILL_XP_TABLE.get("farming");
        assertNotNull(farming, "farming skill must be present in SKILL_XP_TABLE");
        assertEquals(50L, farming[0], "farming level-1 delta should be 50");
        assertEquals(125L, farming[1], "farming level-2 delta should be 125 (not 175 cumulative)");
    }
}
