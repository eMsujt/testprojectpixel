package com.skyblock.core.dungeon;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.DungeonManager} instead.
 *             All inner types (DungeonClass, DungeonFloor, DungeonType, DungeonRun,
 *             FloorRecord, FloorMeta, FloorType) have moved to the canonical class.
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

    public static final java.util.Map<String, com.skyblock.core.manager.DungeonManager.FloorMeta> FLOOR_META =
            com.skyblock.core.manager.DungeonManager.FLOOR_META;
    public static final java.util.Map<String, int[]> FLOOR_METADATA =
            com.skyblock.core.manager.DungeonManager.FLOOR_METADATA;
    public static final java.util.Map<String, int[]> FLOOR_DATA =
            com.skyblock.core.manager.DungeonManager.FLOOR_DATA;

    public com.skyblock.core.manager.DungeonManager.DungeonRun startRun(
            com.skyblock.core.manager.DungeonManager.DungeonType type,
            List<UUID> participants, long startTimeMillis) {
        return DELEGATE.startRun(type, participants, startTimeMillis);
    }

    public void completeRun(UUID playerId, int score) {
        DELEGATE.completeRun(playerId, score);
    }

    public void abandonRun(UUID playerId) {
        DELEGATE.abandonRun(playerId);
    }

    public com.skyblock.core.manager.DungeonManager.DungeonRun getActiveRun(UUID playerId) {
        return DELEGATE.getActiveRun(playerId);
    }

    public int getBestScore(UUID playerId, com.skyblock.core.manager.DungeonManager.DungeonType type) {
        return DELEGATE.getBestScore(playerId, type);
    }

    public int getCompletionCount(UUID playerId, com.skyblock.core.manager.DungeonManager.DungeonType type) {
        return DELEGATE.getCompletionCount(playerId, type);
    }

    public int getFloorCompletionCount(UUID playerId, com.skyblock.core.manager.DungeonManager.DungeonFloor floor) {
        return DELEGATE.getFloorCompletionCount(playerId, floor);
    }

    public void incrementFloorCompletion(UUID player, com.skyblock.core.manager.DungeonManager.DungeonFloor floor) {
        DELEGATE.incrementFloorCompletion(player, floor);
    }

    public long getFloorBestTime(UUID playerId, com.skyblock.core.manager.DungeonManager.DungeonFloor floor) {
        return DELEGATE.getFloorBestTime(playerId, floor);
    }

    public void updateFloorBestTime(UUID playerId, com.skyblock.core.manager.DungeonManager.DungeonFloor floor, long timeMillis) {
        DELEGATE.updateFloorBestTime(playerId, floor, timeMillis);
    }

    public void setClass(UUID playerId, com.skyblock.core.manager.DungeonManager.DungeonClass dungeonClass) {
        DELEGATE.setClass(playerId, dungeonClass);
    }

    public com.skyblock.core.manager.DungeonManager.DungeonClass getClass(UUID playerId) {
        return DELEGATE.getClass(playerId);
    }

    public void recordFloorCompletion(UUID uuid, int floor) {
        DELEGATE.recordFloorCompletion(uuid, floor);
    }

    public Map<Integer, Integer> getFloorCompletions(UUID uuid) {
        return DELEGATE.getFloorCompletions(uuid);
    }

    public Map<UUID, Map<Integer, Integer>> getAllFloorCompletions() {
        return DELEGATE.getAllFloorCompletions();
    }

    public com.skyblock.core.manager.DungeonManager.FloorRecord recordCompletion(UUID playerId, int floor, int score) {
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

    public Map<Integer, com.skyblock.core.manager.DungeonManager.FloorRecord> getRecords(UUID playerId) {
        return DELEGATE.getRecords(playerId);
    }

    public void clearRecords(UUID playerId) {
        DELEGATE.clearRecords(playerId);
    }

    public void recordDungeonRun(UUID playerId, String summary) {
        DELEGATE.recordDungeonRun(playerId, summary);
    }

    public void recordDungeonEvent(UUID uuid, String summary) {
        DELEGATE.recordDungeonEvent(uuid, summary);
    }

    public List<String> getDungeonHistory(UUID playerId) {
        return DELEGATE.getDungeonHistory(playerId);
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
