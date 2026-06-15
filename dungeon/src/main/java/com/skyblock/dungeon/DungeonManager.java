package com.skyblock.dungeon;

import com.skyblock.core.manager.DungeonManager.FloorRecord;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.DungeonManager} instead.
 */
@Deprecated
public final class DungeonManager {

    private static final com.skyblock.core.manager.DungeonManager DELEGATE =
            com.skyblock.core.manager.DungeonManager.getInstance();

    private static final DungeonManager INSTANCE = new DungeonManager();

    private DungeonManager() {}

    /** @deprecated Use {@link com.skyblock.core.manager.DungeonManager#getInstance()}. */
    @Deprecated
    public static DungeonManager getInstance() {
        return INSTANCE;
    }

    public FloorRecord recordCompletion(UUID playerId, int floor, int score) {
        return DELEGATE.recordCompletion(playerId, floor, score);
    }

    public int getCompletions(UUID playerId, int floor) {
        return DELEGATE.getCompletions(playerId, floor);
    }

    public OptionalInt getBestScore(UUID playerId, int floor) {
        return DELEGATE.getBestScore(playerId, floor);
    }

    public OptionalInt getHighestCompletedFloor(UUID playerId) {
        return DELEGATE.getHighestCompletedFloor(playerId);
    }

    public Map<Integer, FloorRecord> getRecords(UUID playerId) {
        return DELEGATE.getRecords(playerId);
    }

    public void clearRecords(UUID playerId) {
        DELEGATE.clearRecords(playerId);
    }

    public void recordDungeonRun(UUID playerId, String summary) {
        DELEGATE.recordDungeonRun(playerId, summary);
    }

    public void recordDungeonEvent(UUID playerUuid, String summary) {
        DELEGATE.recordDungeonEvent(playerUuid, summary);
    }

    public List<String> getDungeonHistory(UUID playerUuid) {
        return DELEGATE.getDungeonHistory(playerUuid);
    }

    public Map<UUID, List<String>> getAllDungeonHistory() {
        return DELEGATE.getAllDungeonHistory();
    }

    public String getDungeonStats(UUID playerId) {
        return DELEGATE.getDungeonStats(playerId);
    }

    public void load(File dataFolder) {
        DELEGATE.load(dataFolder);
    }

    public void save(File dataFolder) {
        DELEGATE.save(dataFolder);
    }
}
