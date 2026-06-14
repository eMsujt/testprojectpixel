package com.skyblock.core.dungeon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;

public final class DungeonManager {

    private final Map<UUID, Map<Integer, FloorRecord>> records = new HashMap<>();

    public FloorRecord recordCompletion(UUID playerId, int floor, int score) {
        Objects.requireNonNull(playerId, "playerId");
        if (floor < 1) throw new IllegalArgumentException("floor must be positive: " + floor);
        if (score < 0) throw new IllegalArgumentException("score must not be negative: " + score);
        FloorRecord record = records
                .computeIfAbsent(playerId, id -> new HashMap<>())
                .computeIfAbsent(floor, f -> new FloorRecord());
        record.completions++;
        record.bestScore = Math.max(record.bestScore, score);
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
