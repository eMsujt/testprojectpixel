package com.skyblock.dungeons;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Tracks and computes Hypixel SkyBlock-style scores for completed dungeon runs.
 *
 * <p>Each completed run is stored as a {@link DungeonRun} record that captures
 * the raw inputs (deaths, crypts, rooms, elapsed time) and exposes the four
 * sub-scores and the final letter grade. Not thread-safe; synchronize
 * externally if accessed from multiple threads.</p>
 */
public final class DungeonScoreManager {

    /** Maximum score achievable per sub-category. */
    private static final int MAX_SKILL_SCORE       = 60;
    private static final int MAX_EXPLORER_SCORE    = 60;
    private static final int MAX_SPEED_SCORE       = 100;
    private static final int MAX_COMPLETION_SCORE  = 20;

    private final Map<UUID, DungeonRun> completedRuns = new HashMap<>();

    /**
     * Records a completed run and computes its score.
     *
     * @param runId          unique id for this run
     * @param floorId        the {@link DungeonFloor} ordinal used as a floor UUID
     * @param playerIds      players who participated
     * @param floor          the floor that was completed
     * @param elapsedSeconds wall-clock seconds taken to complete the run
     * @param deaths         total party deaths during the run
     * @param cryptsOpened   number of crypts opened
     * @param roomsCompleted rooms fully cleared
     * @param totalRooms     total rooms on the floor
     * @return the stored {@link DungeonRun}
     * @throws IllegalArgumentException if any numeric argument is negative, or if
     *                                  roomsCompleted exceeds totalRooms
     */
    public DungeonRun recordRun(UUID runId, UUID floorId, List<UUID> playerIds,
                                DungeonFloor floor, int elapsedSeconds,
                                int deaths, int cryptsOpened,
                                int roomsCompleted, int totalRooms) {
        if (runId == null) {
            throw new IllegalArgumentException("runId must not be null");
        }
        if (floorId == null) {
            throw new IllegalArgumentException("floorId must not be null");
        }
        if (playerIds == null || playerIds.isEmpty()) {
            throw new IllegalArgumentException("playerIds must not be null or empty");
        }
        if (floor == null) {
            throw new IllegalArgumentException("floor must not be null");
        }
        if (elapsedSeconds < 0) {
            throw new IllegalArgumentException("elapsedSeconds must not be negative");
        }
        if (deaths < 0) {
            throw new IllegalArgumentException("deaths must not be negative");
        }
        if (cryptsOpened < 0) {
            throw new IllegalArgumentException("cryptsOpened must not be negative");
        }
        if (roomsCompleted < 0 || roomsCompleted > totalRooms) {
            throw new IllegalArgumentException(
                    "roomsCompleted must be between 0 and totalRooms");
        }

        int skillScore      = computeSkillScore(deaths);
        int explorerScore   = computeExplorerScore(roomsCompleted, totalRooms, cryptsOpened);
        int speedScore      = computeSpeedScore(elapsedSeconds);
        int completionScore = MAX_COMPLETION_SCORE;

        DungeonRun run = new DungeonRun(runId, floorId, Collections.unmodifiableList(playerIds),
                floor, elapsedSeconds, deaths, cryptsOpened,
                roomsCompleted, totalRooms,
                skillScore, explorerScore, speedScore, completionScore);
        completedRuns.put(runId, run);
        return run;
    }

    /**
     * Returns a previously recorded run, or empty if none exists.
     *
     * @param runId the run to look up
     * @return the run wrapped in an Optional
     */
    public Optional<DungeonRun> getRun(UUID runId) {
        return Optional.ofNullable(completedRuns.get(runId));
    }

    /**
     * Returns an unmodifiable view of all recorded runs keyed by run ID.
     *
     * @return all completed runs
     */
    public Map<UUID, DungeonRun> getCompletedRuns() {
        return Collections.unmodifiableMap(completedRuns);
    }

    // -------------------------------------------------------------------------
    // Score sub-calculations (Hypixel SkyBlock approximations)
    // -------------------------------------------------------------------------

    private int computeSkillScore(int deaths) {
        // Each death deducts 2 points from the max of 60.
        return Math.max(0, MAX_SKILL_SCORE - (deaths * 2));
    }

    private int computeExplorerScore(int roomsCompleted, int totalRooms, int cryptsOpened) {
        // 40 points for room completion + up to 20 bonus points for crypts (2 each, max 10).
        int roomPoints  = totalRooms == 0 ? 0
                : (int) Math.round(40.0 * roomsCompleted / totalRooms);
        int cryptPoints = Math.min(20, cryptsOpened * 2);
        return Math.min(MAX_EXPLORER_SCORE, roomPoints + cryptPoints);
    }

    private int computeSpeedScore(int elapsedSeconds) {
        // Full 100 points under 5 min; 20 points at 20 min; linearly interpolated.
        if (elapsedSeconds <= 300) {
            return MAX_SPEED_SCORE;
        }
        if (elapsedSeconds >= 1200) {
            return 20;
        }
        // Linear decay from 100 → 20 between 300 s and 1200 s.
        double t = (double) (elapsedSeconds - 300) / (1200 - 300);
        return (int) Math.round(100 - t * 80);
    }

    // -------------------------------------------------------------------------
    // DungeonRun record
    // -------------------------------------------------------------------------

    /**
     * Immutable record of a single completed dungeon run and its scores.
     */
    public static final class DungeonRun {

        private final UUID runId;
        private final UUID floorId;
        private final List<UUID> playerIds;
        private final DungeonFloor floor;
        private final int elapsedSeconds;
        private final int deaths;
        private final int cryptsOpened;
        private final int roomsCompleted;
        private final int totalRooms;
        private final int skillScore;
        private final int explorerScore;
        private final int speedScore;
        private final int completionScore;

        private DungeonRun(UUID runId, UUID floorId, List<UUID> playerIds,
                           DungeonFloor floor, int elapsedSeconds,
                           int deaths, int cryptsOpened,
                           int roomsCompleted, int totalRooms,
                           int skillScore, int explorerScore,
                           int speedScore, int completionScore) {
            this.runId           = runId;
            this.floorId         = floorId;
            this.playerIds       = playerIds;
            this.floor           = floor;
            this.elapsedSeconds  = elapsedSeconds;
            this.deaths          = deaths;
            this.cryptsOpened    = cryptsOpened;
            this.roomsCompleted  = roomsCompleted;
            this.totalRooms      = totalRooms;
            this.skillScore      = skillScore;
            this.explorerScore   = explorerScore;
            this.speedScore      = speedScore;
            this.completionScore = completionScore;
        }

        /** Unique ID for this run. */
        public UUID getRunId() { return runId; }

        /** UUID identifying the floor instance (mirrors DungeonFloor as a UUID). */
        public UUID getFloorId() { return floorId; }

        /** Unmodifiable list of player UUIDs who participated. */
        public List<UUID> getPlayerIds() { return playerIds; }

        /** The floor that was completed. */
        public DungeonFloor getFloor() { return floor; }

        /** Wall-clock seconds taken to complete the run. */
        public int getElapsedSeconds() { return elapsedSeconds; }

        /** Total party deaths during the run. */
        public int getDeaths() { return deaths; }

        /** Number of crypts opened. */
        public int getCryptsOpened() { return cryptsOpened; }

        /** Rooms fully cleared. */
        public int getRoomsCompleted() { return roomsCompleted; }

        /** Total rooms on the floor. */
        public int getTotalRooms() { return totalRooms; }

        /** Skill sub-score (0–60), penalised per death. */
        public int getSkillScore() { return skillScore; }

        /** Explorer sub-score (0–60), based on rooms and crypts. */
        public int getExplorerScore() { return explorerScore; }

        /** Speed sub-score (0–100), based on elapsed time. */
        public int getSpeedScore() { return speedScore; }

        /** Bonus sub-score (0–20) awarded for completing the run. */
        public int getCompletionScore() { return completionScore; }

        /** Total score across all sub-categories. */
        public int getTotalScore() {
            return skillScore + explorerScore + speedScore + completionScore;
        }

        /**
         * Letter grade derived from the total score.
         *
         * <ul>
         *   <li>S+ : 300+</li>
         *   <li>S  : 270–299</li>
         *   <li>A  : 240–269</li>
         *   <li>B  : 175–239</li>
         *   <li>C  : 100–174</li>
         *   <li>D  : below 100</li>
         * </ul>
         *
         * @return the letter grade as a String
         */
        public String getGrade() {
            int total = getTotalScore();
            if (total >= 300) return "S+";
            if (total >= 270) return "S";
            if (total >= 240) return "A";
            if (total >= 175) return "B";
            if (total >= 100) return "C";
            return "D";
        }
    }
}
