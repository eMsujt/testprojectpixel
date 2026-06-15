package com.skyblock.plugin.managers;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @deprecated Use {@link com.skyblock.core.manager.DungeonManager} instead.
 */
@Deprecated
public final class DungeonManager {

    /** @deprecated Use {@link com.skyblock.core.manager.DungeonManager.DungeonFloor} instead. */
    @Deprecated
    public enum DungeonFloor {
        FLOOR_1, FLOOR_2, FLOOR_3, FLOOR_4, FLOOR_5, FLOOR_6, FLOOR_7
    }

    private static final com.skyblock.core.manager.DungeonManager DELEGATE =
            com.skyblock.core.manager.DungeonManager.getInstance();

    private static final DungeonManager INSTANCE = new DungeonManager();

    private DungeonManager() {}

    /** @deprecated Use {@link com.skyblock.core.manager.DungeonManager#getInstance()}. */
    @Deprecated
    public static DungeonManager getInstance() {
        return INSTANCE;
    }

    public void recordRun(UUID playerId, String summary) {
        DELEGATE.recordRun(playerId, summary);
    }

    public List<String> getRunHistory(UUID playerId) {
        return DELEGATE.getRunHistory(playerId);
    }

    public Map<UUID, List<String>> getAllRunHistory() {
        return DELEGATE.getAllRunHistory();
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

    public int getDungeonFloor(UUID playerId) {
        return DELEGATE.getDungeonFloor(playerId);
    }

    public void setDungeonFloor(UUID playerId, int floor) {
        DELEGATE.setDungeonFloor(playerId, floor);
    }

    public void addDungeonFloor(UUID playerId, int amount) {
        DELEGATE.addDungeonFloor(playerId, amount);
    }

    public Map<UUID, Integer> getDungeonFloors() {
        return DELEGATE.getDungeonFloors();
    }

    public int getHighestFloor(UUID playerId) {
        return DELEGATE.getHighestFloor(playerId);
    }

    public void setHighestFloor(UUID playerId, int floor) {
        DELEGATE.setHighestFloor(playerId, floor);
    }

    public void addHighestFloor(UUID playerId, int amount) {
        DELEGATE.addHighestFloor(playerId, amount);
    }

    public Map<UUID, Integer> getHighestFloors() {
        return DELEGATE.getHighestFloors();
    }

    public int getCompletions(UUID playerId, String floor) {
        return DELEGATE.getCompletions(playerId, floor);
    }

    public void addCompletion(UUID playerId, String floor) {
        DELEGATE.addCompletion(playerId, floor);
    }

    /** Returns String-keyed floor completions (e.g. "F1" → count). */
    public Map<String, Integer> getFloorCompletions(UUID playerId) {
        return DELEGATE.getNamedFloorCompletions(playerId);
    }

    public long getBestTime(UUID playerId, String floor) {
        return DELEGATE.getBestTime(playerId, floor);
    }

    public void setBestTime(UUID playerId, String floor, long seconds) {
        DELEGATE.setBestTime(playerId, floor, seconds);
    }

    public Map<String, Long> getFloorBestTimes(UUID playerId) {
        return DELEGATE.getNamedFloorBestTimes(playerId);
    }

    public void recordFloorCompletion(UUID playerId, int floor) {
        DELEGATE.recordFloorCompletion(playerId, floor);
    }

    public int getFloorCompletionCount(UUID playerId, int floor) {
        return DELEGATE.getFloorCompletions(playerId).getOrDefault(floor, 0);
    }

    /** Returns Integer-keyed floor completion counts. */
    public Map<Integer, Integer> getPlayerFloorCompletions(UUID playerId) {
        return DELEGATE.getFloorCompletions(playerId);
    }

    public Map<UUID, Map<Integer, Integer>> getAllFloorCompletions() {
        return DELEGATE.getAllFloorCompletions();
    }

    public String getPlayerClass(UUID playerId) {
        return DELEGATE.getPlayerClass(playerId);
    }

    public void setPlayerClass(UUID playerId, String playerClassName) {
        DELEGATE.setPlayerClass(playerId, playerClassName);
    }

    public Map<UUID, String> getPlayerClasses() {
        return DELEGATE.getPlayerClasses();
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
