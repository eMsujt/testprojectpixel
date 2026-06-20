package com.skyblock.core.manager;

import com.skyblock.core.model.Stat;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
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
        ENTRANCE("Entrance", "None", 0, false, 0),
        FLOOR_1("Floor 1", "Bonzo", 1, false, 0),
        FLOOR_2("Floor 2", "Scarf", 5, false, 0),
        FLOOR_3("Floor 3", "The Professor", 9, false, 0),
        FLOOR_4("Floor 4", "Thorn", 14, false, 0),
        FLOOR_5("Floor 5", "Livid", 19, false, 0),
        FLOOR_6("Floor 6", "Sadan", 24, false, 0),
        FLOOR_7("Floor 7", "Necron", 29, false, 0),
        MASTER_1("Master 1", "Bonzo", 1, true, 20),
        MASTER_2("Master 2", "Scarf", 2, true, 22),
        MASTER_3("Master 3", "The Professor", 3, true, 24),
        MASTER_4("Master 4", "Thorn", 4, true, 26),
        MASTER_5("Master 5", "Livid", 5, true, 28),
        MASTER_6("Master 6", "Sadan", 6, true, 30),
        MASTER_7("Master 7", "Necron", 7, true, 32);

        private final String displayName;
        private final String bossName;
        private final int floorNumber;
        private final boolean masterMode;
        private final int requiredCatacombsLevel;

        DungeonFloor(String displayName, String bossName, int floorNumber, boolean masterMode, int requiredCatacombsLevel) {
            this.displayName = displayName;
            this.bossName = bossName;
            this.floorNumber = floorNumber;
            this.masterMode = masterMode;
            this.requiredCatacombsLevel = requiredCatacombsLevel;
        }

        public String getDisplayName() { return displayName; }
        public String getBossName() { return bossName; }
        public int getFloorNumber() { return floorNumber; }
        public boolean isMasterMode() { return masterMode; }
        public int getRequiredCatacombsLevel() { return requiredCatacombsLevel; }
    }

    /** Playable dungeon classes, each granting a set of passive stat bonuses and a class ability. */
    public enum DungeonClass {
        HEALER("Healer", "Healing Circle",
                statMap(Stat.HEALTH, 100.0, Stat.INTELLIGENCE, 50.0, Stat.HEALTH_REGEN, 25.0)),
        MAGE("Mage", "Ender Warp",
                statMap(Stat.INTELLIGENCE, 100.0, Stat.ABILITY_DAMAGE, 25.0)),
        BERSERK("Berserk", "Berserker Rage",
                statMap(Stat.STRENGTH, 50.0, Stat.CRIT_DAMAGE, 30.0)),
        ARCHER("Archer", "Rapid Fire",
                statMap(Stat.CRIT_CHANCE, 15.0, Stat.CRIT_DAMAGE, 25.0, Stat.ATTACK_SPEED, 20.0)),
        TANK("Tank", "Impenetrable",
                statMap(Stat.HEALTH, 150.0, Stat.DEFENSE, 50.0, Stat.TRUE_DEFENSE, 10.0));

        private final String displayName;
        private final String ability;
        private final Map<Stat, Double> statBonuses;

        DungeonClass(String displayName, String ability, Map<Stat, Double> statBonuses) {
            this.displayName = displayName;
            this.ability = ability;
            this.statBonuses = statBonuses;
        }

        public String getDisplayName() { return displayName; }

        /** Returns the name of this class's passive ability. */
        public String getAbility() { return ability; }

        /** Returns this class's passive stat bonuses (immutable). */
        public Map<Stat, Double> getStatBonuses() { return statBonuses; }

        private static Map<Stat, Double> statMap(Object... pairs) {
            Map<Stat, Double> map = new EnumMap<>(Stat.class);
            for (int i = 0; i < pairs.length; i += 2) {
                map.put((Stat) pairs[i], (Double) pairs[i + 1]);
            }
            return Collections.unmodifiableMap(map);
        }
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

    /** Snapshot of a player's dungeon state: selected class, per-class XP, total completions, and run status. */
    public record DungeonData(
            String selectedClass,
            Map<String, Integer> classXp,
            int completions,
            boolean inRun) {

        public DungeonData {
            Objects.requireNonNull(selectedClass, "selectedClass");
            Objects.requireNonNull(classXp, "classXp");
        }
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
        private int roomsCleared;
        private int secretsFound;
        private int puzzlesSolved;
        private int deaths;
        private int cryptsOpened;
        private int totalRooms;
        private int skillScore;
        private int explorerScore;
        private int speedScore;
        private int completionScore;

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
        public int getRoomsCleared() { return roomsCleared; }
        public int getSecretsFound() { return secretsFound; }
        public int getPuzzlesSolved() { return puzzlesSolved; }
        public int getDeaths() { return deaths; }
        public int getCryptsOpened() { return cryptsOpened; }
        public int getTotalRooms() { return totalRooms; }
        public int getSkillScore() { return skillScore; }
        public int getExplorerScore() { return explorerScore; }
        public int getSpeedScore() { return speedScore; }
        public int getCompletionScore() { return completionScore; }

        /** Package-private factory that pre-sets sub-scores; used only in unit tests. */
        static DungeonRun withSubScores(int skill, int explorer, int speed, int completion) {
            DungeonRun r = new DungeonRun(DungeonType.CATACOMBS_F1, Collections.emptyList(), 0L);
            r.skillScore = skill; r.explorerScore = explorer;
            r.speedScore = speed; r.completionScore = completion;
            return r;
        }

        /** Letter grade for the run: S+ ≥300, S ≥270, A ≥240, B ≥175, C ≥100, D <100. */
        public String getGrade() {
            int total = skillScore + explorerScore + speedScore + completionScore;
            if (total >= 300) return "S+";
            if (total >= 270) return "S";
            if (total >= 240) return "A";
            if (total >= 175) return "B";
            if (total >= 100) return "C";
            return "D";
        }
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
        m.put("M7", new FloorMeta("Master 7", 32, "Necron"));
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
        m.put("F7",       new int[]{  0,   6000});
        m.put("M1",       new int[]{ 20,   8000});
        m.put("M2",       new int[]{ 22,  12000});
        m.put("M3",       new int[]{ 24,  18000});
        m.put("M4",       new int[]{ 26,  24000});
        m.put("M5",       new int[]{ 28,  30000});
        m.put("M6",       new int[]{ 30,  40000});
        m.put("M7",       new int[]{ 32,  60000});
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
    /** Accumulated class XP per player per DungeonClass. */
    private final Map<UUID, Map<DungeonClass, Double>> classXp = new HashMap<>();

    /** Maximum dungeon class level. */
    public static final int MAX_CLASS_LEVEL = 50;

    /** Cumulative XP required to reach each class level (index = level, 0..50). */
    private static final long[] CLASS_XP_TABLE = {
        0L, 50L, 125L, 235L, 395L, 625L, 955L, 1425L, 2095L, 3045L, 4385L,
        6275L, 8940L, 12700L, 17960L, 25340L, 35640L, 50040L, 70040L, 97640L,
        135640L, 188140L, 259640L, 356640L, 488640L, 668640L, 911640L, 1239640L,
        1684640L, 2284640L, 3084640L, 4149640L, 5559640L, 7459640L, 9959640L,
        13259640L, 17559640L, 23159640L, 30359640L, 39559640L, 51559640L,
        66559640L, 85559640L, 109559640L, 139559640L, 177559640L, 225559640L,
        285559640L, 360559640L, 453559640L, 569809640L
    };

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

    /**
     * Marks a room as cleared in the given player's active run, incrementing the
     * shared room count and run score by {@code scoreReward}.
     *
     * @return the run's new total number of cleared rooms
     */
    public int clearRoom(UUID playerId, int scoreReward) {
        Objects.requireNonNull(playerId, "playerId");
        if (scoreReward < 0) throw new IllegalArgumentException("scoreReward must not be negative: " + scoreReward);
        DungeonRun run = activeRuns.get(playerId);
        if (run == null) {
            throw new IllegalStateException("No active dungeon run for " + playerId);
        }
        run.roomsCleared++;
        run.score += scoreReward;
        return run.roomsCleared;
    }

    /**
     * Records a secret found in the given player's active run, incrementing the
     * shared secret count and run score by {@code scoreReward}.
     *
     * @return the run's new total number of secrets found
     */
    public int findSecret(UUID playerId, int scoreReward) {
        Objects.requireNonNull(playerId, "playerId");
        if (scoreReward < 0) throw new IllegalArgumentException("scoreReward must not be negative: " + scoreReward);
        DungeonRun run = activeRuns.get(playerId);
        if (run == null) {
            throw new IllegalStateException("No active dungeon run for " + playerId);
        }
        run.secretsFound++;
        run.score += scoreReward;
        return run.secretsFound;
    }

    /**
     * Records a puzzle solved in the given player's active run, incrementing the
     * shared puzzle count and run score by {@code scoreReward}.
     *
     * @return the run's new total number of puzzles solved
     */
    public int solvePuzzle(UUID playerId, int scoreReward) {
        Objects.requireNonNull(playerId, "playerId");
        if (scoreReward < 0) throw new IllegalArgumentException("scoreReward must not be negative: " + scoreReward);
        DungeonRun run = activeRuns.get(playerId);
        if (run == null) {
            throw new IllegalStateException("No active dungeon run for " + playerId);
        }
        run.puzzlesSolved++;
        run.score += scoreReward;
        return run.puzzlesSolved;
    }

    // -------------------------------------------------------------------------
    // Run scoring
    // -------------------------------------------------------------------------

    /** Skill sub-score (0–60): max 60 minus 2 per death. */
    public static int computeSkillScore(int deaths) {
        if (deaths < 0) throw new IllegalArgumentException("deaths must not be negative: " + deaths);
        return Math.max(0, 60 - deaths * 2);
    }

    /** Explorer sub-score (0–60): 40 for room completion ratio + up to 20 for crypts (2 each, max 10). */
    public static int computeExplorerScore(int roomsCompleted, int totalRooms, int cryptsOpened) {
        if (roomsCompleted < 0 || totalRooms < 0 || cryptsOpened < 0)
            throw new IllegalArgumentException("arguments must not be negative");
        if (roomsCompleted > totalRooms)
            throw new IllegalArgumentException("roomsCompleted must not exceed totalRooms");
        int roomPoints = totalRooms == 0 ? 0 : (int) Math.round(40.0 * roomsCompleted / totalRooms);
        int cryptPoints = Math.min(20, cryptsOpened * 2);
        return Math.min(60, roomPoints + cryptPoints);
    }

    /** Speed sub-score (0–100): full 100 under 5 min, 20 at 20 min, linear decay in between. */
    public static int computeSpeedScore(int elapsedSeconds) {
        if (elapsedSeconds < 0) throw new IllegalArgumentException("elapsedSeconds must not be negative: " + elapsedSeconds);
        if (elapsedSeconds <= 300) return 100;
        if (elapsedSeconds >= 1200) return 20;
        double t = (double) (elapsedSeconds - 300) / (1200 - 300);
        return (int) Math.round(100 - t * 80);
    }

    /**
     * Completes the given player's active run using the provided run-end metrics,
     * computing sub-scores and setting the run score to their sum.
     *
     * @param playerId       the player in the run
     * @param endTimeMillis  wall-clock end time (used with startTimeMillis to compute elapsed)
     * @param deaths         total party deaths
     * @param cryptsOpened   crypts opened during the run
     * @param roomsCompleted rooms fully cleared
     * @param totalRooms     total rooms on the floor
     * @return the completed run
     */
    public DungeonRun completeScoredRun(UUID playerId, long endTimeMillis,
                                        int deaths, int cryptsOpened,
                                        int roomsCompleted, int totalRooms) {
        Objects.requireNonNull(playerId, "playerId");
        DungeonRun run = activeRuns.get(playerId);
        if (run == null) throw new IllegalStateException("No active dungeon run for " + playerId);
        int elapsedSeconds = (int) Math.max(0, (endTimeMillis - run.getStartTimeMillis()) / 1000);
        int skill       = computeSkillScore(deaths);
        int explorer    = computeExplorerScore(roomsCompleted, totalRooms, cryptsOpened);
        int speed       = computeSpeedScore(elapsedSeconds);
        int completion  = 20;
        int total       = skill + explorer + speed + completion;
        run.deaths          = deaths;
        run.cryptsOpened    = cryptsOpened;
        run.totalRooms      = totalRooms;
        run.skillScore      = skill;
        run.explorerScore   = explorer;
        run.speedScore      = speed;
        run.completionScore = completion;
        run.completed       = true;
        run.score           = total;
        for (UUID id : run.getParticipants()) {
            activeRuns.remove(id);
            bestScores.computeIfAbsent(id, k -> new HashMap<>())
                    .merge(run.getType(), total, Math::max);
            completionCounts.computeIfAbsent(id, k -> new HashMap<>())
                    .merge(run.getType(), 1, Integer::sum);
            recordDungeonEvent(id, "Completed " + run.getType().name() + " with score " + total
                    + " (" + run.getGrade() + ")");
        }
        return run;
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

    /** Adds class XP to the given class and returns the player's new total XP for it. */
    public double addClassXp(UUID playerId, DungeonClass dungeonClass, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative: " + amount);
        return classXp.computeIfAbsent(playerId, k -> new HashMap<>())
                .merge(dungeonClass, amount, Double::sum);
    }

    /** Returns the player's accumulated XP in the given class. */
    public double getClassXp(UUID playerId, DungeonClass dungeonClass) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(dungeonClass, "dungeonClass");
        Map<DungeonClass, Double> xp = classXp.get(playerId);
        return xp == null ? 0.0 : xp.getOrDefault(dungeonClass, 0.0);
    }

    /** Returns the player's level (0..{@link #MAX_CLASS_LEVEL}) in the given class. */
    public int getClassLevel(UUID playerId, DungeonClass dungeonClass) {
        double xp = getClassXp(playerId, dungeonClass);
        int level = 0;
        while (level < MAX_CLASS_LEVEL && xp >= CLASS_XP_TABLE[level + 1]) {
            level++;
        }
        return level;
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
        classXp.clear();
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
                if (cfg.isConfigurationSection(key + ".classXp")) {
                    Map<DungeonClass, Double> xp = new HashMap<>();
                    for (DungeonClass dc : DungeonClass.values()) {
                        double val = cfg.getDouble(key + ".classXp." + dc.name(), 0.0);
                        if (val > 0) xp.put(dc, val);
                    }
                    if (!xp.isEmpty()) classXp.put(uuid, xp);
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
        for (Map.Entry<UUID, Map<DungeonClass, Double>> entry : classXp.entrySet()) {
            String key = entry.getKey().toString();
            for (Map.Entry<DungeonClass, Double> e : entry.getValue().entrySet()) {
                cfg.set(key + ".classXp." + e.getKey().name(), e.getValue());
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
