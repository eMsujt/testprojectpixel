package com.skyblock.dungeon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * Tracks per-player dungeon floor completions and best scores.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class DungeonManager {

    private final Map<UUID, Map<Integer, FloorRecord>> records = new HashMap<>();
    private final Map<UUID, List<String>> dungeonHistory = new HashMap<>();

    /**
     * Records a completed dungeon run, updating the player's best score if improved.
     *
     * @param playerId the player who completed the run
     * @param floor    the dungeon floor number, must be positive
     * @param score    the run score, must not be negative
     * @return the player's {@link FloorRecord} for the floor after this completion
     * @throws IllegalArgumentException if floor is not positive or score is negative
     */
    public FloorRecord recordCompletion(UUID playerId, int floor, int score) {
        Objects.requireNonNull(playerId, "playerId");
        if (floor < 1) {
            throw new IllegalArgumentException("floor must be positive: " + floor);
        }
        if (score < 0) {
            throw new IllegalArgumentException("score must not be negative: " + score);
        }
        FloorRecord record = records
                .computeIfAbsent(playerId, id -> new HashMap<>())
                .computeIfAbsent(floor, f -> new FloorRecord());
        record.completions++;
        record.bestScore = Math.max(record.bestScore, score);
        recordDungeonEvent(playerId, "Completed floor " + floor + " with score " + score);
        return record;
    }

    /**
     * Returns how many times the player has completed the given floor.
     *
     * @param playerId the player to look up
     * @param floor    the dungeon floor number
     * @return the completion count, 0 if the player has never completed the floor
     */
    public int getCompletions(UUID playerId, int floor) {
        FloorRecord record = getRecord(playerId, floor);
        return record == null ? 0 : record.completions;
    }

    /**
     * Returns the player's best score on the given floor, or empty if never completed.
     *
     * @param playerId the player to look up
     * @param floor    the dungeon floor number
     * @return the best score wrapped in an OptionalInt
     */
    public OptionalInt getBestScore(UUID playerId, int floor) {
        FloorRecord record = getRecord(playerId, floor);
        return record == null ? OptionalInt.empty() : OptionalInt.of(record.bestScore);
    }

    /**
     * Returns the highest floor the player has completed at least once.
     *
     * @param playerId the player to look up
     * @return the highest completed floor, or empty if the player has no completions
     */
    public OptionalInt getHighestCompletedFloor(UUID playerId) {
        Map<Integer, FloorRecord> playerRecords = records.get(playerId);
        if (playerRecords == null || playerRecords.isEmpty()) {
            return OptionalInt.empty();
        }
        return playerRecords.keySet().stream().mapToInt(Integer::intValue).max();
    }

    /**
     * Returns an unmodifiable view of the player's records keyed by floor number.
     *
     * @param playerId the player to look up
     * @return the player's floor records, empty if none exist
     */
    public Map<Integer, FloorRecord> getRecords(UUID playerId) {
        return Collections.unmodifiableMap(records.getOrDefault(playerId, Collections.emptyMap()));
    }

    /**
     * Clears all records for a player.
     *
     * @param playerId the player whose records to remove
     */
    public void clearRecords(UUID playerId) {
        records.remove(playerId);
    }

    public void recordDungeonEvent(UUID playerUuid, String summary) {
        dungeonHistory
                .computeIfAbsent(playerUuid, k -> new ArrayList<>())
                .add(summary);
    }

    public List<String> getDungeonHistory(UUID playerUuid) {
        return Collections.unmodifiableList(dungeonHistory.getOrDefault(playerUuid, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllDungeonHistory() {
        return Collections.unmodifiableMap(dungeonHistory);
    }

    private FloorRecord getRecord(UUID playerId, int floor) {
        Map<Integer, FloorRecord> playerRecords = records.get(playerId);
        return playerRecords == null ? null : playerRecords.get(floor);
    }

    /**
     * A player's completion statistics for a single dungeon floor.
     */
    public static final class FloorRecord {

        private int completions;
        private int bestScore;

        private FloorRecord() {
        }

        /** Returns how many times the floor has been completed. */
        public int getCompletions() {
            return completions;
        }

        /** Returns the best score achieved on the floor. */
        public int getBestScore() {
            return bestScore;
        }
    }
}
