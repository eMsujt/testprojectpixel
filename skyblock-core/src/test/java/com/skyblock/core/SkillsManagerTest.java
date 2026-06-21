package com.skyblock.core;

import com.skyblock.core.manager.SkillManager;
import com.skyblock.core.model.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SkillsManagerTest {

    private SkillManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = SkillManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @Test
    void addXp_doubleVariant_accumulatesAndConvertsToLong() {
        manager.addXp(playerId, Skill.MINING, 50.9);
        assertEquals(50L, manager.getXP(playerId, Skill.MINING));
    }

    @Test
    void addXP_returnsNewTotal() {
        manager.addXP(playerId, Skill.COMBAT, 100L);
        long total = manager.addXP(playerId, Skill.COMBAT, 200L);
        assertEquals(300L, total);
    }

    @Test
    void addXP_negativeAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.addXP(playerId, Skill.FARMING, -1L));
    }

    @Test
    void addXP_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.addXP(null, Skill.FARMING, 50L));
    }

    @Test
    void getXP_unknownPlayer_returnsZero() {
        assertEquals(0L, manager.getXP(UUID.randomUUID(), Skill.FISHING));
    }

    @Test
    void getLevel_freshPlayer_isZero() {
        assertEquals(0, manager.getLevel(playerId, Skill.FORAGING));
    }

    @Test
    void getLevel_afterEnoughXp_incrementsCorrectly() {
        // farming: 50 XP → level 1, 175 XP → level 2
        manager.addXP(playerId, Skill.FARMING, 175L);
        assertEquals(2, manager.getLevel(playerId, Skill.FARMING));
    }

    @Test
    void setSkillXP_overwritesPreviousValue() {
        manager.addXP(playerId, Skill.ALCHEMY, 1000L);
        manager.setSkillXP(playerId, "alchemy", 50L);
        assertEquals(50L, manager.getSkillXP(playerId, "alchemy"));
        assertEquals(1, manager.getSkillLevel(playerId, "alchemy"));
    }

    @Test
    void setSkillXP_unknownSkill_isNoOp() {
        assertDoesNotThrow(() -> manager.setSkillXP(playerId, "notaskill", 999L));
        assertEquals(0L, manager.getSkillXP(playerId, "notaskill"));
    }

    @Test
    void getSkillXPs_returnsOnlyEnteredSkills() {
        manager.addXP(playerId, Skill.TAMING, 50L);
        manager.addXP(playerId, Skill.ENCHANTING, 175L);
        Map<String, Long> xps = manager.getSkillXPs(playerId);
        assertEquals(2, xps.size());
        assertEquals(50L, xps.get("taming"));
        assertEquals(175L, xps.get("enchanting"));
    }

    @Test
    void getSkillXPs_unknownPlayer_returnsEmptyMap() {
        assertTrue(manager.getSkillXPs(UUID.randomUUID()).isEmpty());
    }

    @Test
    void xpToNextLevel_atLevelZero_equalsFirstThreshold() {
        long next = manager.xpToNextLevel(playerId, Skill.FARMING);
        assertEquals(SkillManager.xpForLevel("farming", 1), next);
    }

    @Test
    void xpToNextLevel_atMaxLevel_returnsZero() {
        // Dump enough XP to hit max level (farming = 60)
        manager.addXP(playerId, Skill.FARMING, Long.MAX_VALUE / 2);
        assertEquals(0L, manager.xpToNextLevel(playerId, Skill.FARMING));
    }

    @Test
    void getSkillsStats_containsAllSkillNames() {
        String stats = manager.getSkillsStats(playerId);
        assertTrue(stats.startsWith("Skills Stats:"));
        for (Skill skill : Skill.values()) {
            assertTrue(stats.contains(skill.displayName),
                    "Expected stats to mention " + skill.displayName);
        }
    }

    @Test
    void addCollection_andGetCollectionCount_accumulates() {
        manager.addCollection(playerId, "wheat", 10);
        manager.addCollection(playerId, "wheat", 5);
        assertEquals(15, manager.getCollectionCount(playerId, "wheat"));
    }

    @Test
    void getCollectionCount_unknownCollection_returnsZero() {
        assertEquals(0, manager.getCollectionCount(playerId, "diamond"));
    }

    @Test
    void getAllSkillXP_includesRegisteredPlayer() {
        manager.addXP(playerId, Skill.COMBAT, 300L);
        Map<UUID, Long> all = manager.getAllSkillXP("combat");
        assertTrue(all.containsKey(playerId));
        assertEquals(300L, all.get(playerId));
    }

    @Test
    void xpForLevel_level0_returnsZero() {
        assertEquals(0L, SkillManager.xpForLevel("farming", 0));
    }

    @Test
    void xpForLevel_unknownSkill_returnsMinusOne() {
        assertEquals(-1L, SkillManager.xpForLevel("notaskill", 1));
    }

    @Test
    void xpForLevel_beyondMax_returnsMinusOne() {
        int max = SkillManager.maxLevel("farming");
        assertEquals(-1L, SkillManager.xpForLevel("farming", max + 1));
    }
}
