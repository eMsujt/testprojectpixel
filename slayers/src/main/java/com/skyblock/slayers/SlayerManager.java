package com.skyblock.slayers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks the active slayer quest for each player.
 *
 * <p>A player may have at most one quest at a time. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class SlayerManager {

    private final Map<UUID, SlayerQuest> activeQuests = new HashMap<>();

    /**
     * Starts a new slayer quest for the player.
     *
     * @param playerId the player's UUID
     * @param type     the slayer quest line
     * @param tier     the boss tier, from 1 to {@link SlayerType#getMaxTier()}
     * @return the newly started quest
     * @throws IllegalArgumentException if the tier is out of range
     * @throws IllegalStateException    if the player already has an active quest
     */
    public SlayerQuest startQuest(UUID playerId, SlayerType type, int tier) {
        Objects.requireNonNull(type, "type");
        if (tier < 1 || tier > type.getMaxTier()) {
            throw new IllegalArgumentException(
                    "tier must be between 1 and " + type.getMaxTier() + ": " + tier);
        }
        if (activeQuests.containsKey(playerId)) {
            throw new IllegalStateException("player already has an active slayer quest: " + playerId);
        }
        SlayerQuest quest = new SlayerQuest(type, tier);
        activeQuests.put(playerId, quest);
        return quest;
    }

    /**
     * Returns the player's active quest, or {@code null} if none is in progress.
     *
     * @param playerId the player's UUID
     * @return the active quest, or {@code null}
     */
    public SlayerQuest getActiveQuest(UUID playerId) {
        return activeQuests.get(playerId);
    }

    /**
     * Returns whether the player currently has an active slayer quest.
     *
     * @param playerId the player's UUID
     * @return {@code true} if a quest is in progress
     */
    public boolean hasActiveQuest(UUID playerId) {
        return activeQuests.containsKey(playerId);
    }

    /**
     * Adds kill progress to the player's active quest.
     *
     * @param playerId the player's UUID
     * @param amount   the number of kills to credit, must be non-negative
     * @return the updated quest
     * @throws IllegalStateException if the player has no active quest
     */
    public SlayerQuest addProgress(UUID playerId, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
        SlayerQuest quest = activeQuests.get(playerId);
        if (quest == null) {
            throw new IllegalStateException("player has no active slayer quest: " + playerId);
        }
        quest.kills += amount;
        return quest;
    }

    /**
     * Ends the player's active quest, whether completed or abandoned.
     *
     * @param playerId the player's UUID
     * @return the quest that was removed, or {@code null} if none was active
     */
    public SlayerQuest endQuest(UUID playerId) {
        return activeQuests.remove(playerId);
    }

    /**
     * A single slayer quest: the boss line, the chosen tier and the kill
     * progress accumulated toward spawning the boss.
     */
    public static final class SlayerQuest {

        private final SlayerType type;
        private final int tier;
        private int kills;

        SlayerQuest(SlayerType type, int tier) {
            this.type = type;
            this.tier = tier;
        }

        public SlayerType getType() {
            return type;
        }

        public int getTier() {
            return tier;
        }

        public int getKills() {
            return kills;
        }
    }
}
