package com.skyblock.core.manager;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import com.skyblock.core.model.Stat;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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

    @Test
    void levelForXp_ZeroXpIsLevelZero() {
        assertEquals(0, SkillManager.levelForXp("farming", 0L));
    }

    @Test
    void levelForXp_ExactThresholdReachesNextLevel() {
        // farming deltas: 50 -> level 1, +125 (cumulative 175) -> level 2.
        assertEquals(1, SkillManager.levelForXp("farming", 50L));
        assertEquals(1, SkillManager.levelForXp("farming", 174L));
        assertEquals(2, SkillManager.levelForXp("farming", 175L));
    }

    @Test
    void levelForXp_IsCaseInsensitive() {
        assertEquals(1, SkillManager.levelForXp("FARMING", 50L));
    }

    @Test
    void levelForXp_UnknownSkillIsZero() {
        assertEquals(0, SkillManager.levelForXp("notaskill", 1_000_000L));
        assertEquals(0, SkillManager.levelForXp(null, 1_000_000L));
    }

    @Test
    void levelForXp_HugeXpClampsToMaxLevel() {
        assertEquals(60, SkillManager.levelForXp("combat", Long.MAX_VALUE));
        assertEquals(50, SkillManager.levelForXp("carpentry", Long.MAX_VALUE));
        assertEquals(25, SkillManager.levelForXp("runecrafting", Long.MAX_VALUE));
    }

    @Test
    void maxLevel_MatchesCurveLengths() {
        assertEquals(60, SkillManager.maxLevel("farming"));
        assertEquals(50, SkillManager.maxLevel("dungeoneering"));
        assertEquals(25, SkillManager.maxLevel("social"));
        assertEquals(0, SkillManager.maxLevel("notaskill"));
    }

    @Test
    void addSkillXp_AccumulatesAndResolvesLevel() {
        UUID id = UUID.randomUUID();
        SkillManager mgr = SkillManager.getInstance();
        mgr.addSkillXP(id, "farming", 50L);
        mgr.addSkillXP(id, "farming", 125L);
        assertEquals(175L, mgr.getSkillXP(id, "farming"));
        assertEquals(2, mgr.getSkillLevel(id, "farming"));
    }

    @Test
    void addXP_TypedApi_AccumulatesAndResolvesLevel() {
        UUID id = UUID.randomUUID();
        SkillManager mgr = SkillManager.getInstance();
        // farming: 50 XP → level 1; +125 XP (cumulative 175) → level 2
        mgr.addXP(id, Skill.FARMING, 50L);
        assertEquals(1, mgr.getLevel(id, Skill.FARMING));
        mgr.addXP(id, Skill.FARMING, 125L);
        assertEquals(175L, mgr.getXP(id, Skill.FARMING));
        assertEquals(2, mgr.getLevel(id, Skill.FARMING));
    }

    @Test
    void grantLevelUpRewards_FarmingLevel0To1_GrantsTwoHealth() {
        UUID id = UUID.randomUUID();
        SkillManager mgr = SkillManager.getInstance();
        // farming level 1 is tier 0 (<=14); health skill: reward = 2 + 0 = 2.0
        mgr.grantLevelUpRewards(id, Skill.FARMING, 0, 1);
        assertEquals(2.0, StatManager.getInstance().getBonus(id, Stat.HEALTH), 0.001);
    }

    @Test
    void grantLevelUpRewards_CombatLevel0To2_GrantsOneCritChance() {
        UUID id = UUID.randomUUID();
        SkillManager mgr = SkillManager.getInstance();
        // combat always awards 0.5 CRIT_CHANCE per level; 2 levels → 1.0
        mgr.grantLevelUpRewards(id, Skill.COMBAT, 0, 2);
        assertEquals(1.0, StatManager.getInstance().getBonus(id, Stat.CRIT_CHANCE), 0.001);
    }
}
