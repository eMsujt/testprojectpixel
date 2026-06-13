package com.skyblock.core.quest;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking per-player quest progress and completion state.
 *
 * <p>Progress is stored per player as an {@link EnumMap} of quest type to
 * current progress value. Not thread-safe; synchronize externally if accessed
 * from multiple threads.</p>
 */
public final class QuestManager {

    /** All quest types available in SkyBlock. */
    public enum QuestType {
        KILL_MOBS(20, "Kill Mobs"),
        MINE_ORES(64, "Mine Ores"),
        CATCH_FISH(30, "Catch Fish"),
        EARN_COINS(50, "Earn Coins"),
        COMPLETE_DUNGEONS(1, "Complete Dungeons");

        private final long goal;
        private final String displayName;

        QuestType(long goal, String displayName) {
            this.goal = goal;
            this.displayName = displayName;
        }

        public long getGoal() { return goal; }
        public String getDisplayName() { return displayName; }
    }

    /** Completion status for a single quest instance. */
    public enum QuestStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }

    /** Immutable snapshot of a quest instance. */
    public static final class QuestData {
        public final QuestType type;
        public final long goal;
        public final long progress;
        public final QuestStatus status;

        public QuestData(QuestType type, long goal, long progress, QuestStatus status) {
            this.type = Objects.requireNonNull(type, "type");
            this.goal = goal;
            this.progress = progress;
            this.status = Objects.requireNonNull(status, "status");
        }

        /** Returns {@code true} if progress has reached or exceeded the goal. */
        public boolean isComplete() {
            return progress >= goal;
        }
    }

    private static final QuestManager INSTANCE = new QuestManager();

    /** Per-player quest progress, keyed by quest type. */
    private final Map<UUID, Map<QuestType, Long>> questProgress = new HashMap<>();

    /** Per-player quest goals, keyed by quest type. */
    private final Map<UUID, Map<QuestType, Long>> questGoals = new HashMap<>();

    /** Per-player quest status, keyed by quest type. */
    private final Map<UUID, Map<QuestType, QuestStatus>> questStatus = new HashMap<>();

    private QuestManager() {
    }

    /**
     * Returns the single shared {@code QuestManager} instance.
     *
     * @return the singleton instance
     */
    public static QuestManager getInstance() {
        return INSTANCE;
    }

    /**
     * Starts a quest for the given player using the type's built-in goal.
     *
     * @param playerId  the player starting the quest
     * @param type      the quest type to start
     */
    public void startQuest(UUID playerId, QuestType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        questGoals.computeIfAbsent(playerId, id -> new EnumMap<>(QuestType.class)).put(type, type.getGoal());
        questProgress.computeIfAbsent(playerId, id -> new EnumMap<>(QuestType.class)).put(type, 0L);
        questStatus.computeIfAbsent(playerId, id -> new EnumMap<>(QuestType.class))
                .put(type, QuestStatus.IN_PROGRESS);
    }

    /**
     * Starts a quest for the given player with a custom goal.
     *
     * @param playerId  the player starting the quest
     * @param type      the quest type to start
     * @param goal      the custom target goal; must be positive
     */
    public void startQuest(UUID playerId, QuestType type, long goal) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (goal <= 0) {
            throw new IllegalArgumentException("goal must be positive, got " + goal);
        }
        questGoals.computeIfAbsent(playerId, id -> new EnumMap<>(QuestType.class)).put(type, goal);
        questProgress.computeIfAbsent(playerId, id -> new EnumMap<>(QuestType.class)).put(type, 0L);
        questStatus.computeIfAbsent(playerId, id -> new EnumMap<>(QuestType.class))
                .put(type, QuestStatus.IN_PROGRESS);
    }

    /**
     * Adds progress to the given quest for a player.
     *
     * @param playerId the player making progress
     * @param type     the quest type being progressed
     * @param amount   the amount of progress to add, must not be negative
     * @return the player's total progress for the quest after the addition
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addProgress(UUID playerId, QuestType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<QuestType, Long> progressMap = questProgress.computeIfAbsent(
                playerId, id -> new EnumMap<>(QuestType.class));
        Map<QuestType, Long> goalMap = questGoals.getOrDefault(playerId, new EnumMap<>(QuestType.class));
        long total = progressMap.getOrDefault(type, 0L) + amount;
        progressMap.put(type, total);

        long goal = goalMap.getOrDefault(type, Long.MAX_VALUE);
        if (total >= goal) {
            questStatus.computeIfAbsent(playerId, id -> new EnumMap<>(QuestType.class))
                    .put(type, QuestStatus.COMPLETED);
        } else {
            questStatus.computeIfAbsent(playerId, id -> new EnumMap<>(QuestType.class))
                    .putIfAbsent(type, QuestStatus.IN_PROGRESS);
        }
        return total;
    }

    /**
     * Returns the current progress the player has for the given quest type.
     *
     * @param playerId the player to look up
     * @param type     the quest type to look up
     * @return the current progress, {@code 0} if the player has none
     */
    public long getProgress(UUID playerId, QuestType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<QuestType, Long> progressMap = questProgress.get(playerId);
        return progressMap == null ? 0L : progressMap.getOrDefault(type, 0L);
    }

    /**
     * Returns a snapshot of the player's quest data for the given type.
     *
     * @param playerId the player to look up
     * @param type     the quest type to look up
     * @return a {@link QuestData} snapshot, or {@code null} if the quest was never started
     */
    public QuestData getQuestData(UUID playerId, QuestType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<QuestType, QuestStatus> statusMap = questStatus.get(playerId);
        if (statusMap == null || !statusMap.containsKey(type)) {
            return null;
        }
        long progress = getProgress(playerId, type);
        Map<QuestType, Long> goalMap = questGoals.getOrDefault(playerId, new EnumMap<>(QuestType.class));
        long goal = goalMap.getOrDefault(type, 0L);
        return new QuestData(type, goal, progress, statusMap.get(type));
    }

    /**
     * Returns the status of the given quest for a player.
     *
     * @param playerId the player to look up
     * @param type     the quest type to look up
     * @return the {@link QuestStatus}, or {@link QuestStatus#NOT_STARTED} if never started
     */
    public QuestStatus getStatus(UUID playerId, QuestType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<QuestType, QuestStatus> statusMap = questStatus.get(playerId);
        if (statusMap == null) {
            return QuestStatus.NOT_STARTED;
        }
        return statusMap.getOrDefault(type, QuestStatus.NOT_STARTED);
    }

    /**
     * Resets all quest data for the given player.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any quest data, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = questProgress.remove(playerId) != null;
        hadData |= questGoals.remove(playerId) != null;
        hadData |= questStatus.remove(playerId) != null;
        return hadData;
    }

    public void load(File dataFolder) {
        File file = new File(dataFolder, "quest.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        questProgress.clear();
        questGoals.clear();
        questStatus.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key + ".progress")) {
                    Map<QuestType, Long> progressMap = new EnumMap<>(QuestType.class);
                    Map<QuestType, Long> goalMap = new EnumMap<>(QuestType.class);
                    Map<QuestType, QuestStatus> statusMap = new EnumMap<>(QuestType.class);
                    for (String typeName : cfg.getConfigurationSection(key + ".progress").getKeys(false)) {
                        try {
                            QuestType type = QuestType.valueOf(typeName);
                            progressMap.put(type, cfg.getLong(key + ".progress." + typeName, 0L));
                            goalMap.put(type, cfg.getLong(key + ".goal." + typeName, type.getGoal()));
                            String statusStr = cfg.getString(key + ".status." + typeName, QuestStatus.IN_PROGRESS.name());
                            try {
                                statusMap.put(type, QuestStatus.valueOf(statusStr));
                            } catch (IllegalArgumentException ignored) {
                                statusMap.put(type, QuestStatus.IN_PROGRESS);
                            }
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown quest types
                        }
                    }
                    if (!progressMap.isEmpty()) {
                        questProgress.put(uuid, progressMap);
                        questGoals.put(uuid, goalMap);
                        questStatus.put(uuid, statusMap);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed entries
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "quest.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<QuestType, Long>> entry : questProgress.entrySet()) {
            String key = entry.getKey().toString();
            Map<QuestType, Long> goalMap = questGoals.getOrDefault(entry.getKey(), new EnumMap<>(QuestType.class));
            Map<QuestType, QuestStatus> statusMap = questStatus.getOrDefault(entry.getKey(), new EnumMap<>(QuestType.class));
            for (Map.Entry<QuestType, Long> pe : entry.getValue().entrySet()) {
                QuestType type = pe.getKey();
                cfg.set(key + ".progress." + type.name(), pe.getValue());
                cfg.set(key + ".goal." + type.name(), goalMap.getOrDefault(type, type.getGoal()));
                QuestStatus status = statusMap.getOrDefault(type, QuestStatus.IN_PROGRESS);
                cfg.set(key + ".status." + type.name(), status.name());
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save quest.yml", e);
        }
    }
}
