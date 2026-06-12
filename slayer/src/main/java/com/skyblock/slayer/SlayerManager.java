package com.skyblock.slayer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * Singleton tracking each player's active slayer quest and accumulated
 * slayer XP for each {@link SlayerType}.
 *
 * <p>Active quests are stored in a {@link HashMap} keyed by player UUID. A
 * player may have at most one quest at a time. Access the shared instance
 * via {@link #getInstance()}. Not thread-safe; synchronize externally if
 * accessed from multiple threads.</p>
 */
public final class SlayerManager {

    private static final SlayerManager INSTANCE = new SlayerManager();

    private SlayerManager() {
    }

    /**
     * Returns the shared manager instance.
     *
     * @return the singleton {@code SlayerManager}
     */
    public static SlayerManager getInstance() {
        return INSTANCE;
    }

    /** Required mob kills per tier (index 0 = tier 1). */
    private static final int[] REQUIRED_KILLS_BY_TIER = {5, 25, 100, 250, 500};

    /**
     * A single slayer quest: the boss line, the chosen tier and the kill
     * progress accumulated toward spawning the boss.
     *
     * <p>Instances are created only through
     * {@link SlayerManager#startQuest(Player, SlayerType, int)}.</p>
     */
    public static final class SlayerQuest {

        private final SlayerType type;
        private final int tier;
        private final int requiredKills;
        private int kills;

        private SlayerQuest(SlayerType type, int tier) {
            this.type = type;
            this.tier = tier;
            this.requiredKills = REQUIRED_KILLS_BY_TIER[tier - 1];
        }

        public SlayerType getType() {
            return type;
        }

        public int getTier() {
            return tier;
        }

        public int getRequiredKills() {
            return requiredKills;
        }

        public int getKills() {
            return kills;
        }
    }

    private final Map<UUID, SlayerQuest> activeQuests = new HashMap<>();
    private final Map<UUID, Map<SlayerType, Long>> xpMap = new HashMap<>();

    /**
     * Starts a tier-1 slayer quest for the player.
     *
     * @param player the player starting the quest, must not be null
     * @param type   the slayer quest line, must not be null
     * @return the newly started quest
     * @throws IllegalArgumentException if the player or type is null
     * @throws IllegalStateException    if the player already has an active quest
     */
    public SlayerQuest startQuest(Player player, SlayerType type) {
        return startQuest(player, type, 1);
    }

    /**
     * Starts a new slayer quest for the player.
     *
     * @param player the player starting the quest, must not be null
     * @param type   the slayer quest line, must not be null
     * @param tier   the boss tier, from 1 to {@link SlayerType#getMaxTier()}
     * @return the newly started quest
     * @throws IllegalArgumentException if the player or type is null, or the
     *                                  tier is out of range
     * @throws IllegalStateException    if the player already has an active quest
     */
    public SlayerQuest startQuest(Player player, SlayerType type, int tier) {
        if (player == null || type == null) {
            throw new IllegalArgumentException("player and type must not be null");
        }
        if (tier < 1 || tier > type.getMaxTier()) {
            throw new IllegalArgumentException(
                    "tier must be between 1 and " + type.getMaxTier() + ": " + tier);
        }
        UUID playerId = player.getUniqueId();
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
     * @param player the player to credit, must not be null
     * @param amount the number of kills to credit, must be non-negative
     * @return the updated quest
     * @throws IllegalArgumentException if the player is null or the amount is
     *                                  negative
     * @throws IllegalStateException    if the player has no active quest
     */
    public SlayerQuest addProgress(Player player, int amount) {
        if (player == null) {
            throw new IllegalArgumentException("player must not be null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
        SlayerQuest quest = activeQuests.get(player.getUniqueId());
        if (quest == null) {
            throw new IllegalStateException("player has no active slayer quest: " + player.getUniqueId());
        }
        quest.kills += amount;
        return quest;
    }

    /**
     * Ends the player's active quest, whether completed or abandoned.
     *
     * @param player the player whose quest to end, must not be null
     * @return the quest that was removed, or {@code null} if none was active
     * @throws IllegalArgumentException if the player is null
     */
    public SlayerQuest endQuest(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("player must not be null");
        }
        return activeQuests.remove(player.getUniqueId());
    }

    /**
     * Returns the player's accumulated slayer XP for the given type, or {@code 0}.
     *
     * @param playerId the player's UUID
     * @param type     the slayer quest line
     * @return current XP total
     */
    public long getXp(UUID playerId, SlayerType type) {
        Map<SlayerType, Long> entry = xpMap.get(playerId);
        if (entry == null) {
            return 0L;
        }
        return entry.getOrDefault(type, 0L);
    }

    /**
     * Adds XP to the player's slayer total for the given type.
     *
     * @param playerId the player's UUID
     * @param type     the slayer quest line
     * @param amount   the amount of XP to add, must be non-negative
     * @return the new XP total
     * @throws IllegalArgumentException if {@code amount} is negative
     * @throws ArithmeticException      if the addition would overflow
     */
    public long addXp(UUID playerId, SlayerType type, long amount) {
        requireNonNegative(amount);
        Map<SlayerType, Long> entry = xpMap.computeIfAbsent(playerId, id -> new EnumMap<>(SlayerType.class));
        long updated = Math.addExact(entry.getOrDefault(type, 0L), amount);
        entry.put(type, updated);
        return updated;
    }

    /**
     * Removes all slayer data for the given player (e.g. on profile wipe),
     * including any active quest.
     *
     * @param playerId the player's UUID
     */
    public void clear(UUID playerId) {
        activeQuests.remove(playerId);
        xpMap.remove(playerId);
    }

    private static void requireNonNegative(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
    }
}
