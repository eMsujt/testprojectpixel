package com.skyblock.core.achievement;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking per-player achievement progress and completion state.
 *
 * <p>Progress is stored per player as an {@link EnumMap} of achievement type to
 * current progress value. Not thread-safe; synchronize externally if accessed
 * from multiple threads.</p>
 */
public final class AchievementManager {

    /** All achievement types available in SkyBlock. */
    public enum AchievementType {
        KILL_1000_MONSTERS,
        MINE_10000_BLOCKS,
        REACH_SKILL_LEVEL_50,
        COMPLETE_100_DUNGEONS,
        EARN_1M_COINS,
        FISH_500_ITEMS,
        CRAFT_200_ITEMS,
        TRADE_IN_BAZAAR,
        EXPLORE_ALL_ZONES,
        MAX_PET_LEVEL
    }

    /** Completion status for a single achievement. */
    public enum AchievementStatus {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }

    /** Immutable snapshot of an achievement instance. */
    public static final class AchievementData {
        public final AchievementType type;
        public final long goal;
        public final long progress;
        public final AchievementStatus status;

        public AchievementData(AchievementType type, long goal, long progress, AchievementStatus status) {
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

    private static final AchievementManager INSTANCE = new AchievementManager();

    /** Per-player achievement progress, keyed by achievement type. */
    private final Map<UUID, Map<AchievementType, Long>> achievementProgress = new HashMap<>();

    /** Per-player achievement goals, keyed by achievement type. */
    private final Map<UUID, Map<AchievementType, Long>> achievementGoals = new HashMap<>();

    /** Per-player achievement status, keyed by achievement type. */
    private final Map<UUID, Map<AchievementType, AchievementStatus>> achievementStatus = new HashMap<>();

    private AchievementManager() {
    }

    /**
     * Returns the single shared {@code AchievementManager} instance.
     *
     * @return the singleton instance
     */
    public static AchievementManager getInstance() {
        return INSTANCE;
    }

    /**
     * Starts tracking an achievement for the given player, setting a target goal.
     *
     * @param playerId the player starting the achievement
     * @param type     the achievement type to start
     * @param goal     the progress amount required to complete the achievement
     * @throws IllegalArgumentException if {@code goal} is not positive
     */
    public void startAchievement(UUID playerId, AchievementType type, long goal) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (goal <= 0) {
            throw new IllegalArgumentException("goal must be positive, got " + goal);
        }
        achievementGoals.computeIfAbsent(playerId, id -> new EnumMap<>(AchievementType.class)).put(type, goal);
        achievementProgress.computeIfAbsent(playerId, id -> new EnumMap<>(AchievementType.class)).put(type, 0L);
        achievementStatus.computeIfAbsent(playerId, id -> new EnumMap<>(AchievementType.class))
                .put(type, AchievementStatus.IN_PROGRESS);
    }

    /**
     * Adds progress to the given achievement for a player.
     *
     * @param playerId the player making progress
     * @param type     the achievement type being progressed
     * @param amount   the amount of progress to add, must not be negative
     * @return the player's total progress for the achievement after the addition
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addProgress(UUID playerId, AchievementType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<AchievementType, Long> progressMap = achievementProgress.computeIfAbsent(
                playerId, id -> new EnumMap<>(AchievementType.class));
        Map<AchievementType, Long> goalMap = achievementGoals.getOrDefault(
                playerId, new EnumMap<>(AchievementType.class));
        long total = progressMap.getOrDefault(type, 0L) + amount;
        progressMap.put(type, total);

        long goal = goalMap.getOrDefault(type, Long.MAX_VALUE);
        if (total >= goal) {
            achievementStatus.computeIfAbsent(playerId, id -> new EnumMap<>(AchievementType.class))
                    .put(type, AchievementStatus.COMPLETED);
        } else {
            achievementStatus.computeIfAbsent(playerId, id -> new EnumMap<>(AchievementType.class))
                    .putIfAbsent(type, AchievementStatus.IN_PROGRESS);
        }
        return total;
    }

    /**
     * Returns the current progress the player has for the given achievement type.
     *
     * @param playerId the player to look up
     * @param type     the achievement type to look up
     * @return the current progress, {@code 0} if the player has none
     */
    public long getProgress(UUID playerId, AchievementType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<AchievementType, Long> progressMap = achievementProgress.get(playerId);
        return progressMap == null ? 0L : progressMap.getOrDefault(type, 0L);
    }

    /**
     * Returns a snapshot of the player's achievement data for the given type.
     *
     * @param playerId the player to look up
     * @param type     the achievement type to look up
     * @return an {@link AchievementData} snapshot, or {@code null} if the achievement was never started
     */
    public AchievementData getAchievementData(UUID playerId, AchievementType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<AchievementType, AchievementStatus> statusMap = achievementStatus.get(playerId);
        if (statusMap == null || !statusMap.containsKey(type)) {
            return null;
        }
        long progress = getProgress(playerId, type);
        Map<AchievementType, Long> goalMap = achievementGoals.getOrDefault(
                playerId, new EnumMap<>(AchievementType.class));
        long goal = goalMap.getOrDefault(type, 0L);
        return new AchievementData(type, goal, progress, statusMap.get(type));
    }

    /**
     * Returns the status of the given achievement for a player.
     *
     * @param playerId the player to look up
     * @param type     the achievement type to look up
     * @return the {@link AchievementStatus}, or {@link AchievementStatus#NOT_STARTED} if never started
     */
    public AchievementStatus getStatus(UUID playerId, AchievementType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<AchievementType, AchievementStatus> statusMap = achievementStatus.get(playerId);
        if (statusMap == null) {
            return AchievementStatus.NOT_STARTED;
        }
        return statusMap.getOrDefault(type, AchievementStatus.NOT_STARTED);
    }

    /**
     * Resets all achievement data for the given player.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any achievement data, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = achievementProgress.remove(playerId) != null;
        hadData |= achievementGoals.remove(playerId) != null;
        hadData |= achievementStatus.remove(playerId) != null;
        return hadData;
    }
}
