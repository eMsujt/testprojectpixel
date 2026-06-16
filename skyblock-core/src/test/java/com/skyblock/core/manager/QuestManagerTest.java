package com.skyblock.core.manager;

import com.skyblock.core.manager.QuestManager.QuestData;
import com.skyblock.core.manager.QuestManager.QuestLine;
import com.skyblock.core.manager.QuestManager.QuestStatus;
import com.skyblock.core.manager.QuestManager.QuestType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuestManagerTest {

    @Test
    void getInstance_ReturnsSameInstance() {
        assertSame(QuestManager.getInstance(), QuestManager.getInstance());
    }

    // -------------------------------------------------------------------------
    // Objective progress
    // -------------------------------------------------------------------------

    @Test
    void startQuest_BeginsInProgressWithZeroProgress() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();

        mgr.startQuest(id, QuestType.KILL_MOBS);
        assertEquals(QuestStatus.IN_PROGRESS, mgr.getStatus(id, QuestType.KILL_MOBS));
        assertEquals(0L, mgr.getProgress(id, QuestType.KILL_MOBS));

        QuestData data = mgr.getQuestData(id, QuestType.KILL_MOBS);
        assertNotNull(data);
        assertEquals(QuestType.KILL_MOBS.getGoal(), data.goal);
        assertFalse(data.isComplete());
    }

    @Test
    void addProgress_AccumulatesAndReturnsRunningTotal() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.KILL_MOBS); // goal 20

        assertEquals(5L, mgr.addProgress(id, QuestType.KILL_MOBS, 5));
        assertEquals(12L, mgr.addProgress(id, QuestType.KILL_MOBS, 7));
        assertEquals(12L, mgr.getProgress(id, QuestType.KILL_MOBS));
        assertEquals(QuestStatus.IN_PROGRESS, mgr.getStatus(id, QuestType.KILL_MOBS));
    }

    @Test
    void addProgress_CompletesWhenGoalReached() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.KILL_MOBS); // goal 20

        mgr.addProgress(id, QuestType.KILL_MOBS, 25);
        assertEquals(QuestStatus.COMPLETED, mgr.getStatus(id, QuestType.KILL_MOBS));
        assertTrue(mgr.getQuestData(id, QuestType.KILL_MOBS).isComplete());
    }

    @Test
    void addProgress_RejectsNegativeAmount() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.KILL_MOBS);

        assertThrows(IllegalArgumentException.class,
                () -> mgr.addProgress(id, QuestType.KILL_MOBS, -1));
    }

    @Test
    void startQuest_WithCustomGoalOverridesDefault() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();

        mgr.startQuest(id, QuestType.MINE_ORES, 3);
        assertEquals(3L, mgr.getQuestData(id, QuestType.MINE_ORES).goal);
        mgr.addProgress(id, QuestType.MINE_ORES, 3);
        assertEquals(QuestStatus.COMPLETED, mgr.getStatus(id, QuestType.MINE_ORES));
    }

    @Test
    void startQuest_RejectsNonPositiveCustomGoal() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class,
                () -> mgr.startQuest(id, QuestType.MINE_ORES, 0));
    }

    @Test
    void getStatus_UnstartedQuestIsNotStarted() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();

        assertEquals(QuestStatus.NOT_STARTED, mgr.getStatus(id, QuestType.CATCH_FISH));
        assertEquals(0L, mgr.getProgress(id, QuestType.CATCH_FISH));
        assertNull(mgr.getQuestData(id, QuestType.CATCH_FISH));
    }

    // -------------------------------------------------------------------------
    // Rewards
    // -------------------------------------------------------------------------

    @Test
    void claimReward_GrantsCoinsOnceForCompletedQuest() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.EARN_COINS); // goal 50, reward 50
        mgr.addProgress(id, QuestType.EARN_COINS, 50);

        assertEquals(50L, mgr.claimReward(id, QuestType.EARN_COINS));
        assertTrue(mgr.isRewardClaimed(id, QuestType.EARN_COINS));
        // A second claim grants nothing.
        assertEquals(0L, mgr.claimReward(id, QuestType.EARN_COINS));
    }

    @Test
    void claimReward_IncompleteQuestGrantsNothing() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.EARN_COINS);
        mgr.addProgress(id, QuestType.EARN_COINS, 10);

        assertEquals(0L, mgr.claimReward(id, QuestType.EARN_COINS));
        assertFalse(mgr.isRewardClaimed(id, QuestType.EARN_COINS));
    }

    // -------------------------------------------------------------------------
    // Quest lines & resets
    // -------------------------------------------------------------------------

    @Test
    void questsInLine_GroupsByQuestLine() {
        QuestManager mgr = QuestManager.getInstance();
        List<QuestType> hub = mgr.questsInLine(QuestLine.HUB);
        assertTrue(hub.stream().allMatch(t -> t.getQuestLine() == QuestLine.HUB));
        assertTrue(hub.contains(QuestType.KILL_MOBS));
        assertFalse(hub.contains(QuestType.COMPLETE_DUNGEONS));
    }

    @Test
    void resetDailies_ClearsOnlyDailyQuests() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.KILL_MOBS);            // daily
        mgr.startQuest(id, QuestType.COMPLETE_DUNGEONS);    // not daily
        mgr.addProgress(id, QuestType.KILL_MOBS, 5);

        int reset = mgr.resetDailies(id);
        assertEquals(1, reset);
        assertEquals(QuestStatus.NOT_STARTED, mgr.getStatus(id, QuestType.KILL_MOBS));
        assertEquals(QuestStatus.IN_PROGRESS, mgr.getStatus(id, QuestType.COMPLETE_DUNGEONS));
    }

    @Test
    void reset_RemovesAllPlayerData() {
        QuestManager mgr = QuestManager.getInstance();
        UUID id = UUID.randomUUID();
        mgr.startQuest(id, QuestType.KILL_MOBS);

        assertTrue(mgr.reset(id));
        assertEquals(QuestStatus.NOT_STARTED, mgr.getStatus(id, QuestType.KILL_MOBS));
        assertFalse(mgr.reset(id));
    }
}
