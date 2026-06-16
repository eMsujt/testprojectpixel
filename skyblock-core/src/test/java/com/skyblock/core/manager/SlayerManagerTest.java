package com.skyblock.core.manager;

import com.skyblock.core.manager.SlayerManager.BossFight;
import com.skyblock.core.manager.SlayerManager.QuestTier;
import com.skyblock.core.manager.SlayerManager.SlayerQuest;
import com.skyblock.core.manager.SlayerManager.SlayerType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SlayerManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(SlayerManager.getInstance(), SlayerManager.getInstance());
    }

    @Test
    void getLevel_FollowsCumulativeXpThresholds() {
        SlayerManager mgr = SlayerManager.getInstance();
        UUID id = UUID.randomUUID();
        assertEquals(0, mgr.getLevel(id, SlayerType.ZOMBIE));
        mgr.addExperience(id, SlayerType.ZOMBIE, 5L);     // first threshold
        assertEquals(1, mgr.getLevel(id, SlayerType.ZOMBIE));
        mgr.addExperience(id, SlayerType.ZOMBIE, 9L);     // total 14, still level 1
        assertEquals(1, mgr.getLevel(id, SlayerType.ZOMBIE));
        mgr.addExperience(id, SlayerType.ZOMBIE, 1L);     // total 15, level 2
        assertEquals(2, mgr.getLevel(id, SlayerType.ZOMBIE));
    }

    @Test
    void getLevel_HugeXpClampsToMaxLevel() {
        SlayerManager mgr = SlayerManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.addExperience(id, SlayerType.ENDERMAN, Long.MAX_VALUE);
        assertEquals(SlayerManager.MAX_LEVEL, mgr.getLevel(id, SlayerType.ENDERMAN));
    }

    @Test
    void escalateTier_AdvancesThroughTiersThenRejectsBeyondTier4() {
        SlayerManager mgr = SlayerManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, SlayerType.ZOMBIE, QuestTier.TIER_1);
        assertEquals(QuestTier.TIER_2, mgr.escalateTier(id).tier);
        assertEquals(QuestTier.TIER_3, mgr.escalateTier(id).tier);
        assertEquals(QuestTier.TIER_4, mgr.escalateTier(id).tier);
        assertEquals(QuestTier.TIER_4, mgr.getActiveQuest(id).tier);
        assertThrows(IllegalStateException.class, () -> mgr.escalateTier(id));
        mgr.cancelQuest(id);
    }

    @Test
    void escalateTier_ResetsQuestKills() {
        SlayerManager mgr = SlayerManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, SlayerType.WOLF, QuestTier.TIER_1);
        mgr.addQuestKill(id);
        mgr.addQuestKill(id);
        assertEquals(2, mgr.getActiveQuest(id).getKills());
        mgr.escalateTier(id);
        assertEquals(0, mgr.getActiveQuest(id).getKills());
        mgr.cancelQuest(id);
    }

    @Test
    void escalateTier_RejectedAfterBossSpawned() {
        SlayerManager mgr = SlayerManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, SlayerType.ZOMBIE, QuestTier.TIER_1);
        int needed = SlayerManager.KILLS_TO_SPAWN_BOSS.get(QuestTier.TIER_1);
        for (int i = 0; i < needed; i++) {
            mgr.addQuestKill(id);
        }
        mgr.spawnBoss(id);
        assertThrows(IllegalStateException.class, () -> mgr.escalateTier(id));
        mgr.cancelQuest(id);
    }

    @Test
    void canSpawnBoss_OnlyAfterReachingKillRequirement() {
        SlayerManager mgr = SlayerManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, SlayerType.ZOMBIE, QuestTier.TIER_1);
        int needed = SlayerManager.KILLS_TO_SPAWN_BOSS.get(QuestTier.TIER_1);
        for (int i = 0; i < needed - 1; i++) {
            mgr.addQuestKill(id);
        }
        assertFalse(mgr.canSpawnBoss(id));
        mgr.addQuestKill(id);
        assertTrue(mgr.canSpawnBoss(id));
        mgr.cancelQuest(id);
    }

    @Test
    void spawnBoss_ScalesHealthAndPhasesWithTier() {
        SlayerManager mgr = SlayerManager.getInstance();
        UUID id = UUID.randomUUID();
        // Escalate to TIER_4 before any boss spawns, then meet its kill requirement.
        mgr.startQuest(id, SlayerType.ZOMBIE, QuestTier.TIER_1);
        mgr.escalateTier(id);
        mgr.escalateTier(id);
        mgr.escalateTier(id);
        int needed = SlayerManager.KILLS_TO_SPAWN_BOSS.get(QuestTier.TIER_4);
        for (int i = 0; i < needed; i++) {
            mgr.addQuestKill(id);
        }
        BossFight fight = mgr.spawnBoss(id);
        int[] zombieHealth = SlayerManager.BOSS_HEALTH.get("Zombie");
        assertEquals(zombieHealth[QuestTier.TIER_4.ordinal()], fight.getMaxHealth());
        assertEquals(QuestTier.TIER_4.ordinal() + 1, fight.getTotalPhases());
        assertEquals(1, fight.getPhase());
        mgr.cancelQuest(id);
    }

    @Test
    void damageBoss_EscalatesPhaseAndKillsBoss() {
        SlayerManager mgr = SlayerManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, SlayerType.ZOMBIE, QuestTier.TIER_2);
        int needed = SlayerManager.KILLS_TO_SPAWN_BOSS.get(QuestTier.TIER_2);
        for (int i = 0; i < needed; i++) {
            mgr.addQuestKill(id);
        }
        BossFight fight = mgr.spawnBoss(id);
        int max = fight.getMaxHealth();      // 2 total phases at TIER_2
        assertEquals(2, fight.getTotalPhases());
        mgr.damageBoss(id, max / 2);
        assertEquals(2, fight.getPhase());
        assertFalse(fight.isDead());
        mgr.damageBoss(id, max);             // overkill clamps to zero
        assertTrue(fight.isDead());
        assertEquals(0, fight.getHealth());
        mgr.cancelQuest(id);
    }

    @Test
    void damage_RejectsNegativeAmount() {
        SlayerManager mgr = SlayerManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, SlayerType.SPIDER, QuestTier.TIER_1);
        int needed = SlayerManager.KILLS_TO_SPAWN_BOSS.get(QuestTier.TIER_1);
        for (int i = 0; i < needed; i++) {
            mgr.addQuestKill(id);
        }
        mgr.spawnBoss(id);
        assertThrows(IllegalArgumentException.class, () -> mgr.damageBoss(id, -1));
        mgr.cancelQuest(id);
    }

    @Test
    void startQuest_RejectsSecondConcurrentQuest() {
        SlayerManager mgr = SlayerManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, SlayerType.WOLF, QuestTier.TIER_1);
        assertThrows(IllegalStateException.class,
                () -> mgr.startQuest(id, SlayerType.WOLF, QuestTier.TIER_2));
        mgr.cancelQuest(id);
    }
}
