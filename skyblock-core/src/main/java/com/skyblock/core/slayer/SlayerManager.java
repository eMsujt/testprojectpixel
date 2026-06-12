package com.skyblock.core.slayer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's slayer quest progress and experience per {@link SlayerType}.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class SlayerManager {

    /** The highest slayer level a player can reach. */
    public static final int MAX_LEVEL = 9;

    /** Cumulative XP required to reach each level, indexed by level - 1. */
    private static final long[] XP_PER_LEVEL = {
            5, 15, 200, 1000, 5000, 20000, 100000, 400000, 1000000
    };

    /** All slayer quest types available in SkyBlock. */
    public enum SlayerType {
        ZOMBIE, SPIDER, WOLF, ENDERMAN, BLAZE
    }

    /** Quest tiers that determine boss difficulty and XP rewards. */
    public enum QuestTier {
        TIER_1, TIER_2, TIER_3, TIER_4
    }

    /** Active quest state for a player. */
    public static final class SlayerQuest {
        public final SlayerType type;
        public final QuestTier tier;
        private int kills;
        private boolean bossSpawned;
        private boolean complete;

        public SlayerQuest(SlayerType type, QuestTier tier) {
            this.type = Objects.requireNonNull(type, "type");
            this.tier = Objects.requireNonNull(tier, "tier");
        }

        public int getKills() {
            return kills;
        }

        public boolean isBossSpawned() {
            return bossSpawned;
        }

        public boolean isComplete() {
            return complete;
        }

        /** Increments the kill counter and returns the new total. */
        public int incrementKills() {
            return ++kills;
        }

        public void setBossSpawned(boolean bossSpawned) {
            this.bossSpawned = bossSpawned;
        }

        public void setComplete(boolean complete) {
            this.complete = complete;
        }
    }

    private static final SlayerManager INSTANCE = new SlayerManager();

    /** Per-player XP storage keyed by slayer type. */
    private final Map<UUID, Map<SlayerType, Long>> slayerExperience = new HashMap<>();

    /** Active slayer quest per player. */
    private final Map<UUID, SlayerQuest> activeQuests = new HashMap<>();

    private SlayerManager() {
    }

    /**
     * Returns the single shared {@code SlayerManager} instance.
     *
     * @return the singleton instance
     */
    public static SlayerManager getInstance() {
        return INSTANCE;
    }

    /**
     * Starts a new slayer quest for the player.
     *
     * @param playerId the player starting the quest
     * @param type     the slayer type to start
     * @param tier     the quest tier
     * @return the newly created {@link SlayerQuest}
     * @throws IllegalStateException if the player already has an active quest
     */
    public SlayerQuest startQuest(UUID playerId, SlayerType type, QuestTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(tier, "tier");
        if (activeQuests.containsKey(playerId)) {
            throw new IllegalStateException("Player already has an active slayer quest");
        }
        SlayerQuest quest = new SlayerQuest(type, tier);
        activeQuests.put(playerId, quest);
        return quest;
    }

    /**
     * Returns the player's active slayer quest, or {@code null} if they have none.
     *
     * @param playerId the player to look up
     * @return the active {@link SlayerQuest}, or {@code null}
     */
    public SlayerQuest getActiveQuest(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeQuests.get(playerId);
    }

    /**
     * Completes and removes the player's active quest, awarding XP.
     *
     * @param playerId the player completing the quest
     * @param xpReward the amount of XP to award for this quest
     * @return the updated total XP for the slayer type, or {@code -1} if no active quest
     */
    public long completeQuest(UUID playerId, long xpReward) {
        Objects.requireNonNull(playerId, "playerId");
        SlayerQuest quest = activeQuests.remove(playerId);
        if (quest == null) {
            return -1L;
        }
        quest.setComplete(true);
        return addExperience(playerId, quest.type, xpReward);
    }

    /**
     * Cancels and removes the player's active quest without awarding XP.
     *
     * @param playerId the player cancelling the quest
     * @return {@code true} if the player had an active quest, {@code false} otherwise
     */
    public boolean cancelQuest(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeQuests.remove(playerId) != null;
    }

    /**
     * Adds experience to the given slayer type for a player.
     *
     * @param playerId the player gaining experience
     * @param type     the slayer type receiving XP
     * @param amount   the amount of XP to add, must not be negative
     * @return the player's total XP for the slayer type after the addition
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public long addExperience(UUID playerId, SlayerType type, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<SlayerType, Long> xpMap = slayerExperience.computeIfAbsent(
                playerId, id -> new EnumMap<>(SlayerType.class));
        long total = xpMap.getOrDefault(type, 0L) + amount;
        xpMap.put(type, total);
        return total;
    }

    /**
     * Returns the total experience the player has for the given slayer type.
     *
     * @param playerId the player to look up
     * @param type     the slayer type to look up
     * @return the total XP, {@code 0} if the player has none
     */
    public long getExperience(UUID playerId, SlayerType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SlayerType, Long> xpMap = slayerExperience.get(playerId);
        return xpMap == null ? 0L : xpMap.getOrDefault(type, 0L);
    }

    /**
     * Returns the current slayer level for the player and slayer type.
     *
     * @param playerId the player to look up
     * @param type     the slayer type to look up
     * @return the level between {@code 0} and {@link #MAX_LEVEL}
     */
    public int getLevel(UUID playerId, SlayerType type) {
        long xp = getExperience(playerId, type);
        int level = 0;
        while (level < MAX_LEVEL && xp >= XP_PER_LEVEL[level]) {
            level++;
        }
        return level;
    }

    /**
     * Removes all slayer data for the given player, including XP and any active quest.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had any data, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean hadData = slayerExperience.remove(playerId) != null;
        hadData |= activeQuests.remove(playerId) != null;
        return hadData;
    }
}
