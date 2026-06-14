package com.skyblock.core.dungeon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;

public final class DungeonManager {

    /**
     * Static floor metadata keyed by short identifier (e.g. {@code "F1"}, {@code "M3"}).
     * Each int[] entry is: [0] recommended power, [1] secrets count.
     */
    public static final Map<String, int[]> FLOOR_METADATA;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        m.put("F1", new int[]{200,   40});
        m.put("F2", new int[]{600,   50});
        m.put("F3", new int[]{1200,  60});
        m.put("F4", new int[]{2000,  70});
        m.put("F5", new int[]{3000,  80});
        m.put("F6", new int[]{4000, 100});
        m.put("F7", new int[]{6000, 120});
        m.put("M1", new int[]{8000,   40});
        m.put("M2", new int[]{12000,  50});
        m.put("M3", new int[]{18000,  60});
        m.put("M4", new int[]{24000,  70});
        m.put("M5", new int[]{30000,  80});
        m.put("M6", new int[]{40000, 100});
        m.put("M7", new int[]{60000, 120});
        FLOOR_METADATA = Collections.unmodifiableMap(m);
    }

    private final Map<UUID, Map<Integer, FloorRecord>> records = new HashMap<>();
    private final Map<UUID, List<String>> dungeonHistory = new HashMap<>();

    public FloorRecord recordCompletion(UUID playerId, int floor, int score) {
        Objects.requireNonNull(playerId, "playerId");
        if (floor < 1) throw new IllegalArgumentException("floor must be positive: " + floor);
        if (score < 0) throw new IllegalArgumentException("score must not be negative: " + score);
        FloorRecord record = records
                .computeIfAbsent(playerId, id -> new HashMap<>())
                .computeIfAbsent(floor, f -> new FloorRecord());
        record.completions++;
        record.bestScore = Math.max(record.bestScore, score);
        recordDungeonEvent(playerId, "Completed floor " + floor + " with score " + score);
        return record;
    }

    public int getCompletions(UUID playerId, int floor) {
        FloorRecord record = getRecord(playerId, floor);
        return record == null ? 0 : record.completions;
    }

    public OptionalInt getBestScore(UUID playerId, int floor) {
        FloorRecord record = getRecord(playerId, floor);
        return record == null ? OptionalInt.empty() : OptionalInt.of(record.bestScore);
    }

    public OptionalInt getHighestCompletedFloor(UUID playerId) {
        Map<Integer, FloorRecord> playerRecords = records.get(playerId);
        if (playerRecords == null || playerRecords.isEmpty()) return OptionalInt.empty();
        return playerRecords.keySet().stream().mapToInt(Integer::intValue).max();
    }

    public Map<Integer, FloorRecord> getRecords(UUID playerId) {
        return Collections.unmodifiableMap(records.getOrDefault(playerId, Collections.emptyMap()));
    }

    public void clearRecords(UUID playerId) {
        records.remove(playerId);
    }

    public void recordDungeonEvent(UUID playerId, String summary) {
        dungeonHistory
                .computeIfAbsent(playerId, k -> new ArrayList<>())
                .add(summary);
    }

    public List<String> getDungeonHistory(UUID playerId) {
        return Collections.unmodifiableList(dungeonHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllDungeonHistory() {
        return Collections.unmodifiableMap(dungeonHistory);
    }

    private FloorRecord getRecord(UUID playerId, int floor) {
        Map<Integer, FloorRecord> playerRecords = records.get(playerId);
        return playerRecords == null ? null : playerRecords.get(floor);
    }

    public static final class FloorRecord {

        private int completions;
        private int bestScore;

        private FloorRecord() {}

        public int getCompletions() { return completions; }
        public int getBestScore() { return bestScore; }
    }
}
