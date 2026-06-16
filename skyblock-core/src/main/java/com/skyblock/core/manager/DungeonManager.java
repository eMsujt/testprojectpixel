package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;

/**
 * Canonical singleton DungeonManager for SkyBlock.
 *
 * <p>Consolidates all DungeonManager/DungeonsManager duplicate implementations from
 * com.skyblock.dungeon, com.skyblock.dungeons, com.skyblock.core.dungeon,
 * com.skyblock.core.dungeons, com.skyblock.core.dungeon.manager, and
 * com.skyblock.plugin.managers packages.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class DungeonManager {

    // -------------------------------------------------------------------------
    // Enums
    // -------------------------------------------------------------------------

    /** Floor identifiers using full-word ordinal names. */
    public enum FloorType {
        ENTRANCE("Entrance", false),
        FLOOR_ONE("Floor 1", false),
        FLOOR_TWO("Floor 2", false),
        FLOOR_THREE("Floor 3", false),
        FLOOR_FOUR("Floor 4", false),
        FLOOR_FIVE("Floor 5", false),
        FLOOR_SIX("Floor 6", false),
        FLOOR_SEVEN("Floor 7", false),
        MASTER_ONE("Master 1", true),
        MASTER_TWO("Master 2", true),
        MASTER_THREE("Master 3", true),
        MASTER_FOUR("Master 4", true),
        MASTER_FIVE("Master 5", true),
        MASTER_SIX("Master 6", true),
        MASTER_SEVEN("Master 7", true);

        private final String displayName;
        private final boolean masterMode;

        FloorType(String displayName, boolean masterMode) {
            this.displayName = displayName;
            this.masterMode = masterMode;
        }

        public String getDisplayName() { return displayName; }
        public boolean isMasterMode() { return masterMode; }
    }

    /** All dungeon floors: Entrance, Catacombs FLOOR_1–FLOOR_7, and Master Mode MASTER_1–MASTER_7. */
    public enum DungeonFloor {
        ENTRANCE("Entrance", "None", 0, false),
        FLOOR_1("Floor 1", "Bonzo", 1, false),
        FLOOR_2("Floor 2", "Scarf", 5, false),
        FLOOR_3("Floor 3", "The Professor", 9, false),
        FLOOR_4("Floor 4", "Thorn", 14, false),
        FLOOR_5("Floor 5", "Livid", 19, false),
        FLOOR_6("Floor 6", "Sadan", 24, false),
        FLOOR_7("Floor 7", "Necron", 29, false),
        MASTER_1("Master 1", "Bonzo", 1, true),
        MASTER_2("Master 2", "Scarf", 2, true),
        MASTER_3("Master 3", "The Professor", 3, true),
        MASTER_4("Master 4", "Thorn", 4, true),
        MASTER_5("Master 5", "Livid", 5, true),
        MASTER_6("Master 6", "Sadan", 6, true),
        MASTER_7("Master 7", "Necron", 7, true);

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

    /** All dungeon types available in SkyBlock. */
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

    // -------------------------------------------------------------------------
    // Inner classes
    // -------------------------------------------------------------------------

    /** Immutable metadata for a single dungeon floor. */
    public static final class FloorMeta {
        private final String displayName;
        private final int minCatacombsLevel;
        private final String bossName;

        FloorMeta(String displayName, int minCatacombsLevel, String bossName) {
            this.displayName = displayName;
            this.minCatacombsLevel = minCatacombsLevel;
            this.bossName = bossName;
        }

        public String getDisplayName() { return displayName; }
        public int getMinCatacombsLevel() { return minCatacombsLevel; }
        public String getBossName() { return bossName; }
    }

    /** A player's completion statistics for a single dungeon floor (integer-keyed). */
    public static final class FloorRecord {
        private int completions;
        private int bestScore;

        private FloorRecord() {}

        public int getCompletions() { return completions; }
        public int getBestScore() { return bestScore; }
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
            this.participants = Collections.unmodifiableList(new ArrayList<>(participants));
            this.startTimeMillis = startTimeMillis;
        }

        public DungeonType getType() { return type; }
        public List<UUID> getParticipants() { return participants; }
        public long getStartTimeMillis() { return startTimeMillis; }
        public boolean isCompleted() { return completed; }
        public int getScore() { return score; }
    }

    // -------------------------------------------------------------------------
    // Static floor metadata
    // -------------------------------------------------------------------------

    /** Floor metadata keyed by short id (e.g. {@code "F1"}, {@code "M3"}). */
    public static final Map<String, FloorMeta> FLOOR_META;

    static {
        Map<String, FloorMeta> m = new LinkedHashMap<>();
        m.put("F1", new FloorMeta("Floor 1", 0, "Bonzo"));
        m.put("F2", new FloorMeta("Floor 2", 0, "Scarf"));
        m.put("F3", new FloorMeta("Floor 3", 0, "The Professor"));
        m.put("F4", new FloorMeta("Floor 4", 0, "Thorn"));
        m.put("F5", new FloorMeta("Floor 5", 0, "Livid"));
        m.put("F6", new FloorMeta("Floor 6", 0, "Sadan"));
        m.put("F7", new FloorMeta("Floor 7", 0, "Necron"));
        m.put("M1", new FloorMeta("Master 1", 20, "Bonzo"));
        m.put("M2", new FloorMeta("Master 2", 22, "Scarf"));
        m.put("M3", new FloorMeta("Master 3", 24, "The Professor"));
        m.put("M4", new FloorMeta("Master 4", 26, "Thorn"));
        m.put("M5", new FloorMeta("Master 5", 28, "Livid"));
        m.put("M6", new FloorMeta("Master 6", 30, "Sadan"));
        FLOOR_META = Collections.unmodifiableMap(m);
    }

    /** int[0] = recommended power, int[1] = secrets count. */
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

    /** int[0] = min Catacombs level required, int[1] = recommended power. */
    public static final Map<String, int[]> FLOOR_DATA;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        m.put("ENTRANCE", new int[]{  0,      0});
        m.put("F1",       new int[]{  0,    200});
        m.put("F2",       new int[]{  0,    600});
        m.put("F3",       new int[]{  0,   1200});
        m.put("F4",       new int[]{  0,   2000});
        m.put("F5",       new int[]{  0,   3000});
        m.put("F6",       new int[]{  0,   4000});
        m.put("M1",       new int[]{ 20,   8000});
        m.put("M2",       new int[]{ 22,  12000});
        m.put("M3",       new int[]{ 24,  18000});
        m.put("M4",       new int[]{ 26,  24000});
        m.put("M5",       new int[]{ 28,  30000});
        m.put("M6",       new int[]{ 30,  40000});
        FLOOR_DATA = Collections.unmodifiableMap(m);
    }

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    private static final DungeonManager INSTANCE = new DungeonManager();

    public static DungeonManager getInstance() {
        return INSTANCE;
    }

    private DungeonManager() {}

    // -------------------------------------------------------------------------
    // State: run tracking
    // -------------------------------------------------------------------------

    /** Active run keyed by each participant's UUID. */
    private final Map<UUID, DungeonRun> activeRuns = new HashMap<>();
    /** Best score per player per dungeon type. */
    private final Map<UUID, Map<DungeonType, Integer>> bestScores = new HashMap<>();
    /** Completion count per player per dungeon type. */
    private final Map<UUID, Map<DungeonType, Integer>> completionCounts = new HashMap<>();

    // -------------------------------------------------------------------------
    // State: floor-enum tracking
    // -------------------------------------------------------------------------

    /** Completion count per player per DungeonFloor enum constant. */
    private final Map<UUID, Map<DungeonFloor, Integer>> floorCompletionCounts = new HashMap<>();
    /** Best completion time (ms) per player per DungeonFloor. */
    private final Map<UUID, Map<DungeonFloor, Long>> floorBestTimes = new HashMap<>();

    // -------------------------------------------------------------------------
    // State: class selection
    // -------------------------------------------------------------------------

    /** Selected dungeon class (enum) per player. */
    private final Map<UUID, DungeonClass> playerClasses = new HashMap<>();

    // -------------------------------------------------------------------------
    // State: integer-floor tracking
    // -------------------------------------------------------------------------

    /** Simple completion count per player per floor number (1-7). */
    private final Map<UUID, Map<Integer, Integer>> floorCompletions = new HashMap<>();
    /** FloorRecord (completions + bestScore) per player per floor number. */
    private final Map<UUID, Map<Integer, FloorRecord>> records = new HashMap<>();

    // -------------------------------------------------------------------------
    // State: history
    // -------------------------------------------------------------------------

    private final Map<UUID, List<String>> dungeonHistory = new HashMap<>();
    private final Map<UUID, List<String>> runHistory = new HashMap<>();

    // -------------------------------------------------------------------------
    // State: plugin.managers-style tracking
    // -------------------------------------------------------------------------

    /** Player's current dungeon floor (1-7). */
    private final Map<UUID, Integer> dungeonFloor = new HashMap<>();
    /** Player's highest reached floor (0-7). */
    private final Map<UUID, Integer> highestFloor = new HashMap<>();
    /** String-keyed floor completions, e.g. "F1" → count. */
    private final Map<UUID, Map<String, Integer>> playerCompletions = new HashMap<>();
    /** String-keyed floor best times (seconds). */
    private final Map<UUID, Map<String, Long>> playerBestTimes = new HashMap<>();
    /** Player class as a string (for callers that use string-based class names). */
    private final Map<UUID, String> playerClass = new HashMap<>();

    private static final Set<String> VALID_CLASSES;
    static {
        Set<String> s = new LinkedHashSet<>();
        s.add("Healer"); s.add("Mage"); s.add("Berserker"); s.add("Archer"); s.add("Tank");
        VALID_CLASSES = Collections.unmodifiableSet(s);
    }

    // -------------------------------------------------------------------------
    // Run management
    // -------------------------------------------------------------------------

    /**
     * Starts a new dungeon run of the given type for the listed participants.
     * Any participant already in a run is first removed from their previous one.
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
            bestScores.computeIfAbsent(id, k -> new HashMap<>())
                    .merge(run.getType(), score, Math::max);
            completionCounts.computeIfAbsent(id, k -> new HashMap<>())
                    .merge(run.getType(), 1, Integer::sum);
            recordDungeonEvent(id, "Completed " + run.getType().name() + " with score " + score);
        }
    }

    /** Abandons the given player's active run without recording a completion. */
    public void abandonRun(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        DungeonRun run = activeRuns.remove(playerId);
        if (run == null) return;
        for (UUID id : run.getParticipants()) {
            activeRuns.remove(id);
            recordDungeonEvent(id, "Abandoned " + run.getType().name());
        }
    }

    /** Returns the player's currently active run, or {@code null} if none. */
    public DungeonRun getActiveRun(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeRuns.get(playerId);
    }

    // -------------------------------------------------------------------------
    // DungeonType-based stats
    // -------------------------------------------------------------------------

    public int getBestScore(UUID playerId, DungeonType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<DungeonType, Integer> scores = bestScores.get(playerId);
        return scores == null ? 0 : scores.getOrDefault(type, 0);
    }

    public int getCompletionCount(UUID playerId, DungeonType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<DungeonType, Integer> counts = completionCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(type, 0);
    }

    // -------------------------------------------------------------------------
    // DungeonFloor-enum-based tracking
    // -------------------------------------------------------------------------

    public int getFloorCompletionCount(UUID playerId, DungeonFloor floor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(floor, "floor");
        Map<DungeonFloor, Integer> counts = floorCompletionCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(floor, 0);
    }

    public void incrementFloorCompletion(UUID player, DungeonFloor floor) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(floor, "floor");
        floorCompletionCounts.computeIfAbsent(player, k -> new HashMap<>())
                .merge(floor, 1, Integer::sum);
    }

    public long getFloorBestTime(UUID playerId, DungeonFloor floor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(floor, "floor");
        Map<DungeonFloor, Long> times = floorBestTimes.get(playerId);
        return times == null ? Long.MAX_VALUE : times.getOrDefault(floor, Long.MAX_VALUE);
    }

    public void updateFloorBestTime(UUID playerId, DungeonFloor floor, long timeMillis) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(floor, "floor");
        if (timeMillis < 0) throw new IllegalArgumentException("timeMillis must not be negative: " + timeMillis);
        floorBestTimes.computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(floor, timeMillis, Math::min);
    }

    // -------------------------------------------------------------------------
    // DungeonClass selection (enum-based)
    // -------------------------------------------------------------------------

    public void setClass(UUID playerId, DungeonClass dungeonClass) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        playerClasses.put(playerId, dungeonClass);
    }

    public DungeonClass getClass(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerClasses.get(playerId);
    }

    // -------------------------------------------------------------------------
    // Integer-floor completion tracking (simple count)
    // -------------------------------------------------------------------------

    public void recordFloorCompletion(UUID uuid, int floor) {
        Objects.requireNonNull(uuid, "uuid");
        floorCompletions.computeIfAbsent(uuid, k -> new HashMap<>())
                .merge(floor, 1, Integer::sum);
    }

    public Map<Integer, Integer> getFloorCompletions(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        Map<Integer, Integer> counts = floorCompletions.get(uuid);
        return counts == null ? Collections.emptyMap() : Collections.unmodifiableMap(counts);
    }

    public Map<UUID, Map<Integer, Integer>> getAllFloorCompletions() {
        return Collections.unmodifiableMap(floorCompletions);
    }

    /** Alias for {@link #getFloorCompletions(UUID)} for callers expecting this name. */
    public Map<Integer, Integer> getPlayerFloorCompletions(UUID playerId) {
        return getFloorCompletions(playerId);
    }

    // -------------------------------------------------------------------------
    // FloorRecord-based tracking (completion count + best score per floor number)
    // -------------------------------------------------------------------------

    /**
     * Records a completed dungeon run, updating the player's best score if improved.
     *
     * @param playerId the player who completed the run
     * @param floor    the dungeon floor number, must be positive
     * @param score    the run score, must not be negative
     * @return the player's {@link FloorRecord} for the floor after this completion
     */
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

    /** Returns how many times the player has completed the given floor number. */
    public int getCompletions(UUID playerId, int floor) {
        FloorRecord record = getFloorRecord(playerId, floor);
        return record == null ? 0 : record.completions;
    }

    /** Returns the player's best score on the given floor number, or empty if never completed. */
    public OptionalInt getBestScore(UUID playerId, int floor) {
        FloorRecord record = getFloorRecord(playerId, floor);
        return record == null ? OptionalInt.empty() : OptionalInt.of(record.bestScore);
    }

    /** Returns the highest floor number the player has completed at least once. */
    public OptionalInt getHighestCompletedFloor(UUID playerId) {
        Map<Integer, FloorRecord> playerRecords = records.get(playerId);
        if (playerRecords == null || playerRecords.isEmpty()) return OptionalInt.empty();
        return playerRecords.keySet().stream().mapToInt(Integer::intValue).max();
    }

    /** Returns an unmodifiable view of the player's FloorRecords keyed by floor number. */
    public Map<Integer, FloorRecord> getRecords(UUID playerId) {
        return Collections.unmodifiableMap(records.getOrDefault(playerId, Collections.emptyMap()));
    }

    /** Clears all FloorRecords for a player. */
    public void clearRecords(UUID playerId) {
        records.remove(playerId);
    }

    private FloorRecord getFloorRecord(UUID playerId, int floor) {
        Map<Integer, FloorRecord> playerRecords = records.get(playerId);
        return playerRecords == null ? null : playerRecords.get(floor);
    }

    // -------------------------------------------------------------------------
    // Current/highest floor tracking (plugin.managers style)
    // -------------------------------------------------------------------------

    public int getDungeonFloor(UUID playerId) {
        return dungeonFloor.getOrDefault(playerId, 1);
    }

    public void setDungeonFloor(UUID playerId, int floor) {
        dungeonFloor.put(playerId, Math.max(1, Math.min(7, floor)));
    }

    public void addDungeonFloor(UUID playerId, int amount) {
        setDungeonFloor(playerId, getDungeonFloor(playerId) + amount);
    }

    public Map<UUID, Integer> getDungeonFloors() {
        return Collections.unmodifiableMap(dungeonFloor);
    }

    public int getHighestFloor(UUID playerId) {
        return highestFloor.getOrDefault(playerId, 0);
    }

    public void setHighestFloor(UUID playerId, int floor) {
        highestFloor.put(playerId, Math.max(0, Math.min(7, floor)));
    }

    public void addHighestFloor(UUID playerId, int amount) {
        setHighestFloor(playerId, getHighestFloor(playerId) + amount);
    }

    public Map<UUID, Integer> getHighestFloors() {
        return Collections.unmodifiableMap(highestFloor);
    }

    // -------------------------------------------------------------------------
    // String-keyed floor completions (plugin.managers style, e.g. "F1" -> count)
    // -------------------------------------------------------------------------

    public int getCompletions(UUID playerId, String floor) {
        Map<String, Integer> floors = playerCompletions.get(playerId);
        return floors == null ? 0 : floors.getOrDefault(floor, 0);
    }

    public void addCompletion(UUID playerId, String floor) {
        playerCompletions.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(floor, 1, Integer::sum);
        recordDungeonEvent(playerId, "Completed floor " + floor);
    }

    /** Returns the String-keyed floor completions map for a player. */
    public Map<String, Integer> getNamedFloorCompletions(UUID playerId) {
        return Collections.unmodifiableMap(
                playerCompletions.getOrDefault(playerId, Collections.emptyMap()));
    }

    public long getBestTime(UUID playerId, String floor) {
        Map<String, Long> times = playerBestTimes.get(playerId);
        return times == null ? 0L : times.getOrDefault(floor, 0L);
    }

    public void setBestTime(UUID playerId, String floor, long seconds) {
        playerBestTimes.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(floor, seconds, Math::min);
    }

    /** Returns the String-keyed best-time map for a player. */
    public Map<String, Long> getNamedFloorBestTimes(UUID playerId) {
        return Collections.unmodifiableMap(
                playerBestTimes.getOrDefault(playerId, Collections.emptyMap()));
    }

    // -------------------------------------------------------------------------
    // Player class (string-based, plugin.managers style)
    // -------------------------------------------------------------------------

    public String getPlayerClass(UUID playerId) {
        return playerClass.getOrDefault(playerId, "");
    }

    public void setPlayerClass(UUID playerId, String playerClassName) {
        if (!VALID_CLASSES.contains(playerClassName)) {
            throw new IllegalArgumentException(
                    "Invalid class: " + playerClassName + ". Must be one of " + VALID_CLASSES);
        }
        playerClass.put(playerId, playerClassName);
    }

    public Map<UUID, String> getPlayerClasses() {
        return Collections.unmodifiableMap(playerClass);
    }

    // -------------------------------------------------------------------------
    // History
    // -------------------------------------------------------------------------

    public void recordRun(UUID playerId, String summary) {
        Objects.requireNonNull(playerId, "playerId");
        runHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getRunHistory(UUID playerId) {
        return Collections.unmodifiableList(runHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllRunHistory() {
        return Collections.unmodifiableMap(runHistory);
    }

    public void recordDungeonRun(UUID playerId, String summary) {
        Objects.requireNonNull(playerId, "playerId");
        dungeonHistory.computeIfAbsent(playerId, k -> new ArrayList<>()).add(summary);
    }

    public void recordDungeonEvent(UUID uuid, String summary) {
        Objects.requireNonNull(uuid, "uuid");
        dungeonHistory.computeIfAbsent(uuid, k -> new ArrayList<>()).add(summary);
    }

    public List<String> getDungeonHistory(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return Collections.unmodifiableList(dungeonHistory.getOrDefault(playerId, Collections.emptyList()));
    }

    public Map<UUID, List<String>> getAllDungeonHistory() {
        return Collections.unmodifiableMap(dungeonHistory);
    }

    // -------------------------------------------------------------------------
    // Stats
    // -------------------------------------------------------------------------

    public String getDungeonStats(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        DungeonFloor[] normalFloors = {
            DungeonFloor.FLOOR_1, DungeonFloor.FLOOR_2, DungeonFloor.FLOOR_3,
            DungeonFloor.FLOOR_4, DungeonFloor.FLOOR_5, DungeonFloor.FLOOR_6,
            DungeonFloor.FLOOR_7
        };
        int total = 0;
        for (DungeonFloor floor : normalFloors) {
            total += getFloorCompletionCount(playerId, floor);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Total: ").append(total);
        for (DungeonFloor floor : normalFloors) {
            int count = getFloorCompletionCount(playerId, floor);
            long best = getFloorBestTime(playerId, floor);
            sb.append(" | ").append(floor.getDisplayName()).append(": ").append(count).append(" runs");
            if (best != Long.MAX_VALUE) sb.append(" (best: ").append(best / 1000).append("s)");
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "dungeon.yml");
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        bestScores.clear();
        completionCounts.clear();
        floorCompletionCounts.clear();
        floorBestTimes.clear();
        playerClasses.clear();
        floorCompletions.clear();
        dungeonHistory.clear();
        records.clear();
        dungeonFloor.clear();
        highestFloor.clear();
        playerCompletions.clear();
        playerBestTimes.clear();
        runHistory.clear();
        playerClass.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key + ".bestScores")) {
                    Map<DungeonType, Integer> scores = new HashMap<>();
                    for (DungeonType type : DungeonType.values()) {
                        int val = cfg.getInt(key + ".bestScores." + type.name(), 0);
                        if (val > 0) scores.put(type, val);
                    }
                    if (!scores.isEmpty()) bestScores.put(uuid, scores);
                }
                if (cfg.isConfigurationSection(key + ".completionCounts")) {
                    Map<DungeonType, Integer> counts = new HashMap<>();
                    for (DungeonType type : DungeonType.values()) {
                        int val = cfg.getInt(key + ".completionCounts." + type.name(), 0);
                        if (val > 0) counts.put(type, val);
                    }
                    if (!counts.isEmpty()) completionCounts.put(uuid, counts);
                }
                if (cfg.isConfigurationSection(key + ".floorCounts")) {
                    Map<DungeonFloor, Integer> fc = new HashMap<>();
                    for (DungeonFloor floor : DungeonFloor.values()) {
                        int val = cfg.getInt(key + ".floorCounts." + floor.name(), 0);
                        if (val > 0) fc.put(floor, val);
                    }
                    if (!fc.isEmpty()) floorCompletionCounts.put(uuid, fc);
                }
                if (cfg.isConfigurationSection(key + ".floorBestTimes")) {
                    Map<DungeonFloor, Long> times = new HashMap<>();
                    for (DungeonFloor floor : DungeonFloor.values()) {
                        long val = cfg.getLong(key + ".floorBestTimes." + floor.name(), -1L);
                        if (val >= 0) times.put(floor, val);
                    }
                    if (!times.isEmpty()) floorBestTimes.put(uuid, times);
                }
                if (cfg.isConfigurationSection(key + ".floorCompletions")) {
                    Map<Integer, Integer> fc = new HashMap<>();
                    for (String floorKey : cfg.getConfigurationSection(key + ".floorCompletions").getKeys(false)) {
                        try {
                            int floorNum = Integer.parseInt(floorKey);
                            int val = cfg.getInt(key + ".floorCompletions." + floorKey, 0);
                            if (val > 0) fc.put(floorNum, val);
                        } catch (NumberFormatException ignored) {}
                    }
                    if (!fc.isEmpty()) floorCompletions.put(uuid, fc);
                }
                if (cfg.isConfigurationSection(key + ".records")) {
                    Map<Integer, FloorRecord> pr = new HashMap<>();
                    for (String floorKey : cfg.getConfigurationSection(key + ".records").getKeys(false)) {
                        try {
                            int floor = Integer.parseInt(floorKey);
                            int completions = cfg.getInt(key + ".records." + floorKey + ".completions", 0);
                            int best = cfg.getInt(key + ".records." + floorKey + ".bestScore", 0);
                            if (completions > 0) {
                                FloorRecord fr = new FloorRecord();
                                fr.completions = completions;
                                fr.bestScore = best;
                                pr.put(floor, fr);
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                    if (!pr.isEmpty()) records.put(uuid, pr);
                }
                String cls = cfg.getString(key + ".class");
                if (cls != null) {
                    try { playerClasses.put(uuid, DungeonClass.valueOf(cls)); }
                    catch (IllegalArgumentException ignored) {}
                }
                String strCls = cfg.getString(key + ".playerClass");
                if (strCls != null && VALID_CLASSES.contains(strCls)) {
                    playerClass.put(uuid, strCls);
                }
                int dfVal = cfg.getInt(key + ".dungeonFloor", -1);
                if (dfVal >= 1) dungeonFloor.put(uuid, Math.min(7, dfVal));
                int hfVal = cfg.getInt(key + ".highestFloor", -1);
                if (hfVal >= 0) highestFloor.put(uuid, Math.min(7, hfVal));
                if (cfg.isConfigurationSection(key + ".playerCompletions")) {
                    Map<String, Integer> pc = new HashMap<>();
                    for (String floor : cfg.getConfigurationSection(key + ".playerCompletions").getKeys(false)) {
                        pc.put(floor, cfg.getInt(key + ".playerCompletions." + floor, 0));
                    }
                    if (!pc.isEmpty()) playerCompletions.put(uuid, pc);
                }
                if (cfg.isConfigurationSection(key + ".playerBestTimes")) {
                    Map<String, Long> pt = new HashMap<>();
                    for (String floor : cfg.getConfigurationSection(key + ".playerBestTimes").getKeys(false)) {
                        pt.put(floor, cfg.getLong(key + ".playerBestTimes." + floor, 0L));
                    }
                    if (!pt.isEmpty()) playerBestTimes.put(uuid, pt);
                }
                List<String> history = cfg.getStringList(key + ".dungeonHistory");
                if (!history.isEmpty()) dungeonHistory.put(uuid, new ArrayList<>(history));
                List<String> rh = cfg.getStringList(key + ".runHistory");
                if (!rh.isEmpty()) runHistory.put(uuid, new ArrayList<>(rh));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "dungeon.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<DungeonType, Integer>> entry : bestScores.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<DungeonType, Integer> e : entry.getValue().entrySet()) {
                cfg.set(key + ".bestScores." + e.getKey().name(), e.getValue());
            }
        }
        for (Map.Entry<UUID, Map<DungeonType, Integer>> entry : completionCounts.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<DungeonType, Integer> e : entry.getValue().entrySet()) {
                cfg.set(key + ".completionCounts." + e.getKey().name(), e.getValue());
            }
        }
        for (Map.Entry<UUID, Map<DungeonFloor, Integer>> entry : floorCompletionCounts.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<DungeonFloor, Integer> e : entry.getValue().entrySet()) {
                cfg.set(key + ".floorCounts." + e.getKey().name(), e.getValue());
            }
        }
        for (Map.Entry<UUID, Map<DungeonFloor, Long>> entry : floorBestTimes.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<DungeonFloor, Long> e : entry.getValue().entrySet()) {
                cfg.set(key + ".floorBestTimes." + e.getKey().name(), e.getValue());
            }
        }
        for (Map.Entry<UUID, DungeonClass> entry : playerClasses.entrySet()) {
            cfg.set(entry.getKey().toString() + ".class", entry.getValue().name());
        }
        for (Map.Entry<UUID, Map<Integer, Integer>> entry : floorCompletions.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<Integer, Integer> e : entry.getValue().entrySet()) {
                cfg.set(key + ".floorCompletions." + e.getKey(), e.getValue());
            }
        }
        for (Map.Entry<UUID, Map<Integer, FloorRecord>> playerEntry : records.entrySet()) {
            String key = playerEntry.getKey().toString();
            for (Map.Entry<Integer, FloorRecord> e : playerEntry.getValue().entrySet()) {
                cfg.set(key + ".records." + e.getKey() + ".completions", e.getValue().completions);
                cfg.set(key + ".records." + e.getKey() + ".bestScore", e.getValue().bestScore);
            }
        }
        for (Map.Entry<UUID, String> entry : playerClass.entrySet()) {
            cfg.set(entry.getKey().toString() + ".playerClass", entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : dungeonFloor.entrySet()) {
            cfg.set(entry.getKey().toString() + ".dungeonFloor", entry.getValue());
        }
        for (Map.Entry<UUID, Integer> entry : highestFloor.entrySet()) {
            cfg.set(entry.getKey().toString() + ".highestFloor", entry.getValue());
        }
        for (Map.Entry<UUID, Map<String, Integer>> playerEntry : playerCompletions.entrySet()) {
            String key = playerEntry.getKey().toString();
            for (Map.Entry<String, Integer> e : playerEntry.getValue().entrySet()) {
                cfg.set(key + ".playerCompletions." + e.getKey(), e.getValue());
            }
        }
        for (Map.Entry<UUID, Map<String, Long>> playerEntry : playerBestTimes.entrySet()) {
            String key = playerEntry.getKey().toString();
            for (Map.Entry<String, Long> e : playerEntry.getValue().entrySet()) {
                cfg.set(key + ".playerBestTimes." + e.getKey(), e.getValue());
            }
        }
        for (Map.Entry<UUID, List<String>> entry : dungeonHistory.entrySet()) {
            cfg.set(entry.getKey().toString() + ".dungeonHistory", entry.getValue());
        }
        for (Map.Entry<UUID, List<String>> entry : runHistory.entrySet()) {
            cfg.set(entry.getKey().toString() + ".runHistory", entry.getValue());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save dungeon.yml", e);
        }
    }
}
