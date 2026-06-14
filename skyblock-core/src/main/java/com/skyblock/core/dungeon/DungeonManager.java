package com.skyblock.core.dungeon;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    /** Floor identifiers using full-word ordinal names (ENTRANCE, FLOOR_ONE … FLOOR_SEVEN, MASTER_ONE … MASTER_SEVEN). */
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

    /**
     * Static floor metadata keyed by short identifier (e.g. {@code "F1"}, {@code "M3"}).
     * Covers normal floors F1–F7 and Master Mode floors M1–M6.
     */
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

    /**
     * Static floor data keyed by short identifier (e.g. {@code "ENTRANCE"}, {@code "F1"}, {@code "M3"}).
     * Each int[] entry is: [0] minimum Catacombs level required, [1] recommended power.
     * Covers Catacombs floors ENTRANCE and F1–F6 and Master Mode floors M1–M6.
     */
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
    /** Completion count per player per dungeon floor. */
    private final Map<UUID, Map<DungeonFloor, Integer>> floorCompletionCounts = new HashMap<>();
    /** Best completion time (ms) per player per dungeon floor. */
    private final Map<UUID, Map<DungeonFloor, Long>> floorBestTimes = new HashMap<>();
    /** Selected dungeon class per player. */
    private final Map<UUID, DungeonClass> playerClasses = new HashMap<>();
    /** Completion count per player per floor number. */
    private final Map<UUID, Map<Integer, Integer>> floorCompletions = new HashMap<>();
    /** Run history summaries per player. */
    private final Map<UUID, List<String>> dungeonHistory = new HashMap<>();

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
     * Returns how many times the player has completed the given dungeon floor.
     *
     * @param playerId the player to look up
     * @param floor    the dungeon floor
     * @return completion count, {@code 0} if none
     */
    public int getFloorCompletionCount(UUID playerId, DungeonFloor floor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(floor, "floor");
        Map<DungeonFloor, Integer> counts = floorCompletionCounts.get(playerId);
        return counts == null ? 0 : counts.getOrDefault(floor, 0);
    }

    /**
     * Increments the per-player completion count for the given dungeon floor by 1.
     *
     * @param player the player's UUID
     * @param floor  the floor that was completed
     */
    public void incrementFloorCompletion(UUID player, DungeonFloor floor) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(floor, "floor");
        floorCompletionCounts
            .computeIfAbsent(player, k -> new HashMap<>())
            .merge(floor, 1, Integer::sum);
    }

    /**
     * Returns the player's best completion time in milliseconds for the given floor,
     * or {@code Long.MAX_VALUE} if they have never completed it.
     *
     * @param playerId the player to look up
     * @param floor    the dungeon floor
     * @return best time in ms, or {@code Long.MAX_VALUE} if none
     */
    public long getFloorBestTime(UUID playerId, DungeonFloor floor) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(floor, "floor");
        Map<DungeonFloor, Long> times = floorBestTimes.get(playerId);
        return times == null ? Long.MAX_VALUE : times.getOrDefault(floor, Long.MAX_VALUE);
    }

    /**
     * Updates the player's best completion time for the given floor if the provided
     * time is strictly better (lower) than the stored value.
     *
     * @param playerId  the player's UUID
     * @param floor     the floor that was completed
     * @param timeMillis elapsed time in milliseconds
     */
    public void updateFloorBestTime(UUID playerId, DungeonFloor floor, long timeMillis) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(floor, "floor");
        if (timeMillis < 0) {
            throw new IllegalArgumentException("timeMillis must not be negative, got " + timeMillis);
        }
        floorBestTimes
            .computeIfAbsent(playerId, k -> new HashMap<>())
            .merge(floor, timeMillis, Math::min);
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

    /**
     * Records a completion for the given floor number, incrementing the count by 1.
     *
     * @param uuid  the player's UUID
     * @param floor the floor number that was completed
     */
    public void recordFloorCompletion(UUID uuid, int floor) {
        Objects.requireNonNull(uuid, "uuid");
        floorCompletions
            .computeIfAbsent(uuid, k -> new HashMap<>())
            .merge(floor, 1, Integer::sum);
    }

    /**
     * Returns the floor completion counts for the given player, or an empty map if none.
     *
     * @param uuid the player to look up
     * @return unmodifiable map of floor number to completion count
     */
    public Map<Integer, Integer> getFloorCompletions(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        Map<Integer, Integer> counts = floorCompletions.get(uuid);
        return counts == null ? Collections.emptyMap() : Collections.unmodifiableMap(counts);
    }

    /**
     * Returns all per-player floor completion counts.
     *
     * @return unmodifiable map of UUID to floor-number completion counts
     */
    public Map<UUID, Map<Integer, Integer>> getAllFloorCompletions() {
        return Collections.unmodifiableMap(floorCompletions);
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
    // Persistence
    // -------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "dungeon.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        bestScores.clear();
        completionCounts.clear();
        floorCompletionCounts.clear();
        floorBestTimes.clear();
        playerClasses.clear();
        floorCompletions.clear();
        dungeonHistory.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key + ".bestScores")) {
                    Map<DungeonType, Integer> scores = new HashMap<>();
                    for (DungeonType type : DungeonType.values()) {
                        int val = cfg.getInt(key + ".bestScores." + type.name(), 0);
                        if (val > 0) {
                            scores.put(type, val);
                        }
                    }
                    if (!scores.isEmpty()) {
                        bestScores.put(uuid, scores);
                    }
                }
                if (cfg.isConfigurationSection(key + ".completionCounts")) {
                    Map<DungeonType, Integer> counts = new HashMap<>();
                    for (DungeonType type : DungeonType.values()) {
                        int val = cfg.getInt(key + ".completionCounts." + type.name(), 0);
                        if (val > 0) {
                            counts.put(type, val);
                        }
                    }
                    if (!counts.isEmpty()) {
                        completionCounts.put(uuid, counts);
                    }
                }
                if (cfg.isConfigurationSection(key + ".floorCounts")) {
                    Map<DungeonFloor, Integer> floorCounts = new HashMap<>();
                    for (DungeonFloor floor : DungeonFloor.values()) {
                        int val = cfg.getInt(key + ".floorCounts." + floor.name(), 0);
                        if (val > 0) {
                            floorCounts.put(floor, val);
                        }
                    }
                    if (!floorCounts.isEmpty()) {
                        floorCompletionCounts.put(uuid, floorCounts);
                    }
                }
                if (cfg.isConfigurationSection(key + ".floorBestTimes")) {
                    Map<DungeonFloor, Long> times = new HashMap<>();
                    for (DungeonFloor floor : DungeonFloor.values()) {
                        long val = cfg.getLong(key + ".floorBestTimes." + floor.name(), -1L);
                        if (val >= 0) {
                            times.put(floor, val);
                        }
                    }
                    if (!times.isEmpty()) {
                        floorBestTimes.put(uuid, times);
                    }
                }
                if (cfg.isConfigurationSection(key + ".floorCompletions")) {
                    Map<Integer, Integer> fc = new HashMap<>();
                    for (String floorKey : cfg.getConfigurationSection(key + ".floorCompletions").getKeys(false)) {
                        try {
                            int floorNum = Integer.parseInt(floorKey);
                            int val = cfg.getInt(key + ".floorCompletions." + floorKey, 0);
                            if (val > 0) {
                                fc.put(floorNum, val);
                            }
                        } catch (NumberFormatException ignored) {
                            // skip malformed floor keys
                        }
                    }
                    if (!fc.isEmpty()) {
                        floorCompletions.put(uuid, fc);
                    }
                }
                String cls = cfg.getString(key + ".class");
                if (cls != null) {
                    try {
                        playerClasses.put(uuid, DungeonClass.valueOf(cls));
                    } catch (IllegalArgumentException ignored) {
                        // skip unknown class
                    }
                }
                List<String> history = cfg.getStringList(key + ".dungeonHistory");
                if (!history.isEmpty()) {
                    dungeonHistory.put(uuid, new ArrayList<>(history));
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
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
        for (Map.Entry<UUID, List<String>> entry : dungeonHistory.entrySet()) {
            cfg.set(entry.getKey().toString() + ".dungeonHistory", entry.getValue());
        }
        for (Map.Entry<UUID, Map<Integer, Integer>> entry : floorCompletions.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<Integer, Integer> e : entry.getValue().entrySet()) {
                cfg.set(key + ".floorCompletions." + e.getKey(), e.getValue());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save dungeon.yml", e);
        }
    }
}
