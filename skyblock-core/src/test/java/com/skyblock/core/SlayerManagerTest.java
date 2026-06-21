package com.skyblock.core;

import com.skyblock.core.manager.SlayerManager;
import com.skyblock.core.manager.SlayerManager.BossFight;
import com.skyblock.core.manager.SlayerManager.QuestTier;
import com.skyblock.core.manager.SlayerManager.SlayerQuest;
import com.skyblock.core.manager.SlayerManager.SlayerReward;
import com.skyblock.core.manager.SlayerManager.SlayerType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SlayerManagerTest {

    private SlayerManager manager;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        manager = SlayerManager.getInstance();
        playerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        manager.reset(playerId);
    }

    // --- addExperience / getExperience ---

    @Test
    void addExperience_accumulatesAndReturnsTotal() {
        manager.addExperience(playerId, SlayerType.ZOMBIE, 10L);
        long total = manager.addExperience(playerId, SlayerType.ZOMBIE, 15L);
        assertEquals(25L, total);
        assertEquals(25L, manager.getExperience(playerId, SlayerType.ZOMBIE));
    }

    @Test
    void addExperience_negativeAmount_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.addExperience(playerId, SlayerType.ZOMBIE, -1L));
    }

    @Test
    void addExperience_nullPlayer_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.addExperience(null, SlayerType.ZOMBIE, 10L));
    }

    @Test
    void addExperience_nullType_throwsNullPointer() {
        assertThrows(NullPointerException.class,
                () -> manager.addExperience(playerId, null, 10L));
    }

    @Test
    void getExperience_unknownPlayer_returnsZero() {
        assertEquals(0L, manager.getExperience(UUID.randomUUID(), SlayerType.SPIDER));
    }

    @Test
    void getExperience_differentTypesAreIndependent() {
        manager.addExperience(playerId, SlayerType.ZOMBIE, 100L);
        assertEquals(0L, manager.getExperience(playerId, SlayerType.SPIDER));
    }

    // --- getLevel ---

    @Test
    void getLevel_freshPlayer_isZero() {
        assertEquals(0, manager.getLevel(playerId, SlayerType.ZOMBIE));
    }

    @Test
    void getLevel_afterFirstThreshold_isOne() {
        // ZOMBIE xpTable[0] = 5
        manager.addExperience(playerId, SlayerType.ZOMBIE, 5L);
        assertEquals(1, manager.getLevel(playerId, SlayerType.ZOMBIE));
    }

    @Test
    void getLevel_afterSecondThreshold_isTwo() {
        // ZOMBIE xpTable[1] = 15
        manager.addExperience(playerId, SlayerType.ZOMBIE, 15L);
        assertEquals(2, manager.getLevel(playerId, SlayerType.ZOMBIE));
    }

    @Test
    void getLevel_atMaxXp_doesNotExceedMaxLevel() {
        manager.addExperience(playerId, SlayerType.WOLF, Long.MAX_VALUE / 2);
        assertTrue(manager.getLevel(playerId, SlayerType.WOLF) <= SlayerManager.MAX_LEVEL);
    }

    // --- addKill / getKillCount ---

    @Test
    void addKill_incrementsAndReturnsTotal() {
        manager.addKill(playerId, SlayerType.SPIDER);
        int total = manager.addKill(playerId, SlayerType.SPIDER);
        assertEquals(2, total);
    }

    @Test
    void getKillCount_unknownPlayer_returnsZero() {
        assertEquals(0, manager.getKillCount(UUID.randomUUID(), SlayerType.WOLF));
    }

    @Test
    void getKillCount_differentTypesAreIndependent() {
        manager.addKill(playerId, SlayerType.ZOMBIE);
        assertEquals(0, manager.getKillCount(playerId, SlayerType.SPIDER));
    }

    // --- quest lifecycle ---

    @Test
    void startQuest_createsQuestWithCorrectTypeAndTier() {
        SlayerQuest quest = manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        assertEquals(SlayerType.ZOMBIE, quest.type);
        assertEquals(QuestTier.TIER_1, quest.tier);
        assertEquals(0, quest.getKills());
        assertFalse(quest.isBossSpawned());
        assertFalse(quest.isComplete());
    }

    @Test
    void startQuest_withExistingQuest_throwsIllegalState() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        assertThrows(IllegalStateException.class,
                () -> manager.startQuest(playerId, SlayerType.SPIDER, QuestTier.TIER_2));
    }

    @Test
    void getActiveQuest_returnsQuestAfterStart() {
        manager.startQuest(playerId, SlayerType.WOLF, QuestTier.TIER_2);
        SlayerQuest quest = manager.getActiveQuest(playerId);
        assertNotNull(quest);
        assertEquals(SlayerType.WOLF, quest.type);
    }

    @Test
    void getActiveQuest_noQuest_returnsNull() {
        assertNull(manager.getActiveQuest(playerId));
    }

    @Test
    void cancelQuest_removesActiveQuest_returnsTrue() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        assertTrue(manager.cancelQuest(playerId));
        assertNull(manager.getActiveQuest(playerId));
    }

    @Test
    void cancelQuest_noQuest_returnsFalse() {
        assertFalse(manager.cancelQuest(playerId));
    }

    // --- quest kill tracking ---

    @Test
    void addQuestKill_incrementsKillsOnActiveQuest() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        int kills = manager.addQuestKill(playerId);
        assertEquals(1, kills);
        assertEquals(1, manager.getActiveQuest(playerId).getKills());
    }

    @Test
    void addQuestKill_withoutQuest_throwsIllegalState() {
        assertThrows(IllegalStateException.class,
                () -> manager.addQuestKill(playerId));
    }

    // --- canSpawnBoss / spawnBoss ---

    @Test
    void canSpawnBoss_beforeEnoughKills_returnsFalse() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        assertFalse(manager.canSpawnBoss(playerId));
    }

    @Test
    void canSpawnBoss_afterEnoughKills_returnsTrue() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        // TIER_1 requires 10 kills
        for (int i = 0; i < 10; i++) {
            manager.addQuestKill(playerId);
        }
        assertTrue(manager.canSpawnBoss(playerId));
    }

    @Test
    void spawnBoss_returnsCorrectTypeAndTier() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        for (int i = 0; i < 10; i++) {
            manager.addQuestKill(playerId);
        }
        BossFight fight = manager.spawnBoss(playerId);
        assertEquals(SlayerType.ZOMBIE, fight.type);
        assertEquals(QuestTier.TIER_1, fight.tier);
        assertFalse(fight.isDead());
        assertTrue(fight.getHealth() > 0);
    }

    @Test
    void spawnBoss_withoutQuest_throwsIllegalState() {
        assertThrows(IllegalStateException.class,
                () -> manager.spawnBoss(playerId));
    }

    // --- damageBoss / BossFight ---

    @Test
    void damageBoss_reducesHealth() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        for (int i = 0; i < 10; i++) {
            manager.addQuestKill(playerId);
        }
        BossFight fight = manager.spawnBoss(playerId);
        int initial = fight.getHealth();
        int remaining = manager.damageBoss(playerId, 100);
        assertEquals(initial - 100, remaining);
    }

    @Test
    void damageBoss_withoutBoss_throwsIllegalState() {
        assertThrows(IllegalStateException.class,
                () -> manager.damageBoss(playerId, 100));
    }

    @Test
    void bossFight_damage_negativeAmount_throwsIllegalArgument() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        for (int i = 0; i < 10; i++) {
            manager.addQuestKill(playerId);
        }
        BossFight fight = manager.spawnBoss(playerId);
        assertThrows(IllegalArgumentException.class, () -> fight.damage(-1));
    }

    @Test
    void bossFight_isDead_afterLethalDamage() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        for (int i = 0; i < 10; i++) {
            manager.addQuestKill(playerId);
        }
        BossFight fight = manager.spawnBoss(playerId);
        fight.damage(Integer.MAX_VALUE);
        assertTrue(fight.isDead());
        assertEquals(0, fight.getHealth());
    }

    // --- killBoss / SlayerReward ---

    @Test
    void killBoss_bossNotDead_throwsIllegalState() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        for (int i = 0; i < 10; i++) {
            manager.addQuestKill(playerId);
        }
        manager.spawnBoss(playerId);
        assertThrows(IllegalStateException.class,
                () -> manager.killBoss(playerId));
    }

    @Test
    void killBoss_afterBossDead_returnsRewardAndClearsQuest() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        for (int i = 0; i < 10; i++) {
            manager.addQuestKill(playerId);
        }
        manager.spawnBoss(playerId);
        manager.damageBoss(playerId, Integer.MAX_VALUE);
        SlayerReward reward = manager.killBoss(playerId);
        assertNotNull(reward);
        assertTrue(reward.getXp() >= 0L);
        assertNotNull(reward.getDrops());
        assertNull(manager.getActiveQuest(playerId));
        assertFalse(manager.isBossActive(playerId));
    }

    @Test
    void killBoss_withoutBoss_throwsIllegalState() {
        assertThrows(IllegalStateException.class,
                () -> manager.killBoss(playerId));
    }

    // --- escalateTier ---

    @Test
    void escalateTier_upgradesQuestToNextTier() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        SlayerQuest escalated = manager.escalateTier(playerId);
        assertEquals(QuestTier.TIER_2, escalated.tier);
    }

    @Test
    void escalateTier_atMaxTier_throwsIllegalState() {
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_4);
        assertThrows(IllegalStateException.class,
                () -> manager.escalateTier(playerId));
    }

    @Test
    void escalateTier_withoutQuest_throwsIllegalState() {
        assertThrows(IllegalStateException.class,
                () -> manager.escalateTier(playerId));
    }

    // --- reset ---

    @Test
    void reset_clearsAllPlayerData_returnsTrue() {
        manager.addExperience(playerId, SlayerType.ZOMBIE, 100L);
        manager.addKill(playerId, SlayerType.ZOMBIE);
        manager.startQuest(playerId, SlayerType.ZOMBIE, QuestTier.TIER_1);
        assertTrue(manager.reset(playerId));
        assertEquals(0L, manager.getExperience(playerId, SlayerType.ZOMBIE));
        assertEquals(0, manager.getKillCount(playerId, SlayerType.ZOMBIE));
        assertNull(manager.getActiveQuest(playerId));
    }

    @Test
    void reset_unknownPlayer_returnsFalse() {
        assertFalse(manager.reset(UUID.randomUUID()));
    }

    // --- setBossActive / isBossActive ---

    @Test
    void setBossActive_andIsBossActive_roundTrips() {
        manager.setBossActive(playerId, true);
        assertTrue(manager.isBossActive(playerId));
        manager.setBossActive(playerId, false);
        assertFalse(manager.isBossActive(playerId));
    }

    @Test
    void isBossActive_unknownPlayer_returnsFalse() {
        assertFalse(manager.isBossActive(UUID.randomUUID()));
    }

    // --- getSpawnCost ---

    @Test
    void getSpawnCost_zombieTier1_matchesTable() {
        assertEquals(100, manager.getSpawnCost(SlayerType.ZOMBIE, QuestTier.TIER_1));
    }

    @Test
    void getSpawnCost_vampireAllTiers_returnsZero() {
        for (QuestTier tier : QuestTier.values()) {
            assertEquals(0, manager.getSpawnCost(SlayerType.VAMPIRE, tier));
        }
    }
}
