package com.skyblock.core.dungeon;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock dungeon runs.
 *
 * <p>Tracks active {@link DungeonRun} instances per player and records each
 * player's best score and total completion count per {@link DungeonType}.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class DungeonManager {

    /** All dungeon floors: Entrance, Catacombs F1–F7, and Master Mode M1–M7. */
    public enum DungeonFloor {
        ENTRANCE("Entrance", "None", 0, false),
        F1("Floor 1", "Bonzo", 1, false),
        F2("Floor 2", "Scarf", 5, false),
        F3("Floor 3", "The Professor", 9, false),
        F4("Floor 4", "Thorn", 14, false),
        F5("Floor 5", "Livid", 19, false),
        F6("Floor 6", "Sadan", 24, false),
        F7("Floor 7", "Necron", 29, false),
        M1("Master 1", "Bonzo", 1, true),
        M2("Master 2", "Scarf", 2, true),
        M3("Master 3", "The Professor", 3, true),
        M4("Master 4", "Thorn", 4, true),
        M5("Master 5", "Livid", 5, true),
        M6("Master 6", "Sadan", 6, true),
        M7("Master 7", "Necron", 7, true);

        private final String displayName;
        private final String bossName;
        private final int floorNumber;
        private final boolean masterMode;

        DungeonFloor(String displayName, String bossName, int floorNumber, boolean masterMode) {
            this.displayName = displayName;
            this.bossName = bossName;
            this.floorNumber = floorNumber;
            this.masterMode = masterMode;
        }

        public String getDisplayName() { return displayName; }
        public String getBossName() { return bossName; }
        public int getFloorNumber() { return floorNumber; }
        public boolean isMasterMode() { return masterMode; }
    }

    /** Playable dungeon classes. */
    public enum DungeonClass {
        HEALER("Healer"),
        MAGE("Mage"),
        BERSERK("Berserk"),
        ARCHER("Archer"),
        TANK("Tank");

        private final String displayName;

        DungeonClass(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /** All dungeon floors available in SkyBlock. */
    public enum DungeonType {
        ENTRANCE,
        CATACOMBS_F1,
        CATACOMBS_F2,
        CATACOMBS_F3,
        CATACOMBS_F4,
        CATACOMBS_F5,
        CATACOMBS_F6,
        CATACOMBS_F7,
        MASTER_M1,
        MASTER_M2,
        MASTER_M3,
        MASTER_M4,
        MASTER_M5,
        MASTER_M6,
        MASTER_M7
    }

    /** A single active dungeon run. */
    public static final class DungeonRun {
        private final DungeonType type;
        private final List<UUID> participants;
        private final long startTimeMillis;
        private boolean completed;
        private int score;

        DungeonRun(DungeonType type, List<UUID> participants, long startTimeMillis) {
            this.type = type;
            this.participants = Collections.unmodifiableList(participants);
            this.startTimeMillis = startTimeMillis;
        }

        public DungeonType getType() { return type; }
        public List<UUID> getParticipants() { return participants; }
        public long getStartTimeMillis() { return startTimeMillis; }
        public boolean isCompleted() { return completed; }
        public int getScore() { return score; }
    }

    private static final DungeonManager INSTANCE = new DungeonManager();

    /** Active run keyed by each participant's UUID. */
    private final Map<UUID, DungeonRun> activeRuns = new HashMap<>();
    /** Best score per player per dungeon type. */
    private final Map<UUID, Map<DungeonType, Integer>> bestScores = new HashMap<>();
    /** Completion count per player per dungeon type. */
    private final Map<UUID, Map<DungeonType, Integer>> completionCounts = new HashMap<>();
    /** Selected dungeon class per player. */
    private final Map<UUID, DungeonClass> playerClasses = new HashMap<>();

    private DungeonManager() {}

    /**
     * Returns the single shared {@code DungeonManager} instance.
     *
     * @return the singleton instance
     */
    public static DungeonManager getInstance() {
        return INSTANCE;
    }

    /**
     * Starts a new dungeon run of the given type for the listed participants.
     * Any participant already in a run is first removed from their previous one.
     *
     * @param type         the dungeon floor to run
     * @param participants UUIDs of all players joining the run
     * @param startTimeMillis wall-clock start time in milliseconds
     * @return the newly created {@link DungeonRun}
     */
    public DungeonRun startRun(DungeonType type, List<UUID> participants, long startTimeMillis) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(participants, "participants");
        if (participants.isEmpty()) {
            throw new IllegalArgumentException("participants must not be empty");
        }

        DungeonRun run = new DungeonRun(type, participants, startTimeMillis);
        for (UUID id : participants) {
            activeRuns.put(id, run);
        }
        return run;
    }

    /**
     * Marks the given player's active run as completed with the provided score,
     * updates best-score and completion-count records for all participants,
     * then removes the run from the active map.
     *
     * @param playerId UUID of any participant in the run
     * @param score    final score achieved
     * @throws IllegalStateException if the player has no active run
     */
    public void completeRun(UUID playerId, int score) {
        Objects.requireNonNull(playerId, "playerId");
        DungeonRun run = activeRuns.get(playerId);
        if (run == null) {
            throw new IllegalStateException("No active dungeon run for " + playerId);
        }

        run.completed = true;
        run.score = score;

        for (UUID id : run.getParticipants()) {
            activeRuns.remove(id);

            bestScores
                .computeIfAbsent(id, k -> new HashMap<>())
                .merge(run.getType(), score, Math::max);

            completionCounts
                .computeIfAbsent(id, k -> new HashMap<>())
                .merge(run.getType(), 1, Integer::sum);
        }
    }

    /**
     * Abandons the given player's active run without recording a completion.
     * All participants are removed from the active map.
     *
     * @param playerId UUID of any participant in the run
     */
    public void abandonRun(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        DungeonRun run = activeRuns.remove(playerId);
        if (run == null) {
            return;
        }
        for (UUID id : run.getParticipants()) {
            activeRuns.remove(id);
        }
    }

    /**
     * Returns the player's currently active run, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the active {@link DungeonRun}, or {@code null}
     */
    public DungeonRun getActiveRun(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeRuns.get(playerId);
    }

    /**
     * Returns the player's best score for the given dungeon type, or {@code 0}
     * if they have never completed it.
     *
     * @param playerId the player to look up
     * @param type     the dungeon floor
     * @return best score, {@code 0} if none
     */
    public int getBestScore(UUID playerId, DungeonType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<DungeonType, Integer> scores = bestScores.get(playerId);
        return scores == null ? 0 : scores.getOrDefault(type, 0);
    }

    /**
     * Returns how many times the player has completed the given dungeon type.
     *
     * @param playerId the player to look up
     * @param type     the dungeon floor
     * @return completion count, {@code 0} if none
     */
    public int getCompletionCount(UUID playerId, DungeonType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<DungeonType, Integer> counts = completionCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(type, 0);
    }

    /**
     * Sets the dungeon class for the given player.
     *
     * @param playerId     the player
     * @param dungeonClass the chosen class
     */
    public void setClass(UUID playerId, DungeonClass dungeonClass) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        playerClasses.put(playerId, dungeonClass);
    }

    /**
     * Returns the player's selected dungeon class, or {@code null} if none chosen.
     *
     * @param playerId the player to look up
     * @return the selected {@link DungeonClass}, or {@code null}
     */
    public DungeonClass getClass(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerClasses.get(playerId);
    }
}
