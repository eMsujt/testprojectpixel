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
}
