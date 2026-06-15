package com.skyblock.core.manager;

import com.skyblock.core.quest.QuestManager.QuestData;
import com.skyblock.core.quest.QuestManager.QuestStatus;
import com.skyblock.core.quest.QuestManager.QuestType;

import java.io.File;
import java.util.UUID;

/**
 * Canonical singleton for per-player SkyBlock quest tracking.
 *
 * <p>Delegates all state to {@link com.skyblock.core.quest.QuestManager}. Use
 * {@link QuestType} as the quest type, {@link QuestStatus} for status queries,
 * and {@link QuestData} for progress snapshots.</p>
 */
public final class QuestManager {

    private static final QuestManager INSTANCE = new QuestManager();
    private final com.skyblock.core.quest.QuestManager delegate =
            com.skyblock.core.quest.QuestManager.getInstance();

    private QuestManager() {}

    public static QuestManager getInstance() {
        return INSTANCE;
    }

    public void startQuest(UUID playerId, QuestType type) {
        delegate.startQuest(playerId, type);
    }

    public void startQuest(UUID playerId, QuestType type, long goal) {
        delegate.startQuest(playerId, type, goal);
    }

    public long addProgress(UUID playerId, QuestType type, long amount) {
        return delegate.addProgress(playerId, type, amount);
    }

    public long getProgress(UUID playerId, QuestType type) {
        return delegate.getProgress(playerId, type);
    }

    public QuestData getQuestData(UUID playerId, QuestType type) {
        return delegate.getQuestData(playerId, type);
    }

    public QuestStatus getStatus(UUID playerId, QuestType type) {
        return delegate.getStatus(playerId, type);
    }

    public boolean reset(UUID playerId) {
        return delegate.reset(playerId);
    }

    public void load(File dataFolder) {
        delegate.load(dataFolder);
    }

    public void save(File dataFolder) {
        delegate.save(dataFolder);
    }
}
