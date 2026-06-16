package com.skyblock.core.manager;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Canonical singleton for per-player SkyBlock quest tracking.
 */
public final class QuestManager {

    /** The quest line (island location) a quest belongs to. */
    public enum QuestLine {
        HUB("Hub"),
        ISLAND("Private Island");

        private final String displayName;

        QuestLine(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    /** All quest types available in SkyBlock. */
    public enum QuestType {
        KILL_MOBS(QuestLine.HUB, true, 20, 100, "Kill Mobs"),
        MINE_ORES(QuestLine.HUB, true, 64, 150, "Mine Ores"),
        CATCH_FISH(QuestLine.HUB, true, 30, 120, "Catch Fish"),
        EARN_COINS(QuestLine.HUB, true, 50, 50, "Earn Coins"),
        COMPLETE_DUNGEONS(QuestLine.ISLAND, false, 1, 500, "Complete Dungeons"),
        KILL_100_MOBS(QuestLine.ISLAND, false, 100, 400, "Kill 100 Mobs"),
        MINE_500_BLOCKS(QuestLine.ISLAND, false, 500, 600, "Mine 500 Blocks"),
        FISH_50_FISH(QuestLine.ISLAND, false, 50, 250, "Fish 50 Fish"),
        CRAFT_20_ITEMS(QuestLine.ISLAND, false, 20, 200, "Craft 20 Items"),
        REACH_LEVEL_25(QuestLine.ISLAND, false, 25, 1000, "Reach Level 25");

        private final QuestLine questLine;
        private final boolean daily;
        private final long goal;
        private final long coinReward;
        private final String displayName;

        QuestType(QuestLine questLine, boolean daily, long goal, long coinReward, String displayName) {
            this.questLine = questLine;
            this.daily = daily;
            this.goal = goal;
            this.coinReward = coinReward;
            this.displayName = displayName;
        }

        public QuestLine getQuestLine() { return questLine; }
        public boolean isDaily() { return daily; }
        public long getGoal() { return goal; }
        public long getCoinReward() { return coinReward; }
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

    /** Per-player set of quests whose completion reward has already been claimed. */
    private final Map<UUID, Set<QuestType>> questClaimed = new HashMap<>();

    private QuestManager() {}

    public static QuestManager getInstance() {
        return INSTANCE;
    }

    public void startQuest(UUID playerId, QuestType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        questGoals.computeIfAbsent(playerId, id -> new EnumMap<>(QuestType.class)).put(type, type.getGoal());
        questProgress.computeIfAbsent(playerId, id -> new EnumMap<>(QuestType.class)).put(type, 0L);
        questStatus.computeIfAbsent(playerId, id -> new EnumMap<>(QuestType.class))
                .put(type, QuestStatus.IN_PROGRESS);
    }

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

    public long getProgress(UUID playerId, QuestType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<QuestType, Long> progressMap = questProgress.get(playerId);
        return progressMap == null ? 0L : progressMap.getOrDefault(type, 0L);
    }

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
     * Grants the completion reward for a finished quest, crediting the player's purse.
     *
     * @return the coins granted, or {@code 0} if the quest is not completed or already claimed
     */
    public long claimReward(UUID playerId, QuestType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (getStatus(playerId, type) != QuestStatus.COMPLETED) {
            return 0L;
        }
        Set<QuestType> claimed = questClaimed.computeIfAbsent(playerId, id -> EnumSet.noneOf(QuestType.class));
        if (!claimed.add(type)) {
            return 0L;
        }
        long reward = type.getCoinReward();
        EconomyManager.getInstance().addCoins(playerId, reward);
        return reward;
    }

    public boolean isRewardClaimed(UUID playerId, QuestType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Set<QuestType> claimed = questClaimed.get(playerId);
        return claimed != null && claimed.contains(type);
    }

    /** Returns all quest types belonging to the given quest line, in declaration order. */
    public java.util.List<QuestType> questsInLine(QuestLine line) {
        Objects.requireNonNull(line, "line");
        java.util.List<QuestType> result = new java.util.ArrayList<>();
        for (QuestType type : QuestType.values()) {
            if (type.getQuestLine() == line) {
                result.add(type);
            }
        }
        return result;
    }

    /**
     * Clears all progress, goals, status, and claim records for the player's daily
     * quests so they return to {@link QuestStatus#NOT_STARTED} and can be run again.
     *
     * @return the number of daily quests that were reset for the player
     */
    public int resetDailies(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int reset = 0;
        for (QuestType type : QuestType.values()) {
            if (!type.isDaily()) {
                continue;
            }
            boolean had = false;
            Map<QuestType, Long> progressMap = questProgress.get(playerId);
            if (progressMap != null) {
                had |= progressMap.remove(type) != null;
            }
            Map<QuestType, Long> goalMap = questGoals.get(playerId);
            if (goalMap != null) {
                goalMap.remove(type);
            }
            Map<QuestType, QuestStatus> statusMap = questStatus.get(playerId);
            if (statusMap != null) {
                had |= statusMap.remove(type) != null;
            }
            Set<QuestType> claimed = questClaimed.get(playerId);
            if (claimed != null) {
                claimed.remove(type);
            }
            if (had) {
                reset++;
            }
        }
        return reset;
    }

    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = questProgress.remove(playerId) != null;
        hadData |= questGoals.remove(playerId) != null;
        hadData |= questStatus.remove(playerId) != null;
        hadData |= questClaimed.remove(playerId) != null;
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
        questClaimed.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                if (cfg.isConfigurationSection(key + ".progress")) {
                    Map<QuestType, Long> progressMap = new EnumMap<>(QuestType.class);
                    Map<QuestType, Long> goalMap = new EnumMap<>(QuestType.class);
                    Map<QuestType, QuestStatus> statusMap = new EnumMap<>(QuestType.class);
                    Set<QuestType> claimedSet = EnumSet.noneOf(QuestType.class);
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
                            if (cfg.getBoolean(key + ".claimed." + typeName, false)) {
                                claimedSet.add(type);
                            }
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown quest types
                        }
                    }
                    if (!progressMap.isEmpty()) {
                        questProgress.put(uuid, progressMap);
                        questGoals.put(uuid, goalMap);
                        questStatus.put(uuid, statusMap);
                        if (!claimedSet.isEmpty()) {
                            questClaimed.put(uuid, claimedSet);
                        }
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
            Set<QuestType> claimedSet = questClaimed.getOrDefault(entry.getKey(), EnumSet.noneOf(QuestType.class));
            for (Map.Entry<QuestType, Long> pe : entry.getValue().entrySet()) {
                QuestType type = pe.getKey();
                cfg.set(key + ".progress." + type.name(), pe.getValue());
                cfg.set(key + ".goal." + type.name(), goalMap.getOrDefault(type, type.getGoal()));
                QuestStatus status = statusMap.getOrDefault(type, QuestStatus.IN_PROGRESS);
                cfg.set(key + ".status." + type.name(), status.name());
                if (claimedSet.contains(type)) {
                    cfg.set(key + ".claimed." + type.name(), true);
                }
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save quest.yml", e);
        }
    }
}
