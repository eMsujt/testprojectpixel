package com.skyblock.slayer;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking each player's active slayer quest and accumulated
 * slayer XP per {@link SlayerType}.
 *
 * <p>Access the shared instance via {@link #getInstance()}. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class SlayerManager {

    /** The five slayer quest lines available on SkyBlock. */
    public enum SlayerType {

        ZOMBIE("Revenant Horror", 4),
        SPIDER("Tarantula Broodfather", 4),
        WOLF("Sven Packmaster", 4),
        ENDERMAN("Voidgloom Seraph", 4),
        BLAZE("Inferno Demonlord", 4);

        private final String displayName;
        private final int maxTier;

        SlayerType(String displayName, int maxTier) {
            this.displayName = displayName;
            this.maxTier = maxTier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMaxTier() {
            return maxTier;
        }
    }

    private static final SlayerManager INSTANCE = new SlayerManager();

    private SlayerManager() {
    }

    public static SlayerManager getInstance() {
        return INSTANCE;
    }

    /** Summary data per slayer type: {maxLevel, coinsToActivate}. */
    public static final Map<String, int[]> SLAYER_BOSS_DATA;

    static {
        Map<String, int[]> m = new LinkedHashMap<>();
        m.put("ZOMBIE",   new int[]{9,  100});
        m.put("SPIDER",   new int[]{9,  2_000});
        m.put("WOLF",     new int[]{9,  10_000});
        m.put("ENDERMAN", new int[]{9,  50_000});
        m.put("BLAZE",    new int[]{9,  100_000});
        m.put("VAMPIRE",  new int[]{5,  0});
        SLAYER_BOSS_DATA = Collections.unmodifiableMap(m);
    }

    /**
     * XP required to reach each slayer level (index 0 = level 1, index 8 = level 9)
     * for the three original boss lines.  Values match the standard SkyBlock XP table.
     */
    public static final Map<String, long[]> SLAYER_XP_TABLE;

    static {
        Map<String, long[]> m = new LinkedHashMap<>();
        m.put("ZOMBIE", new long[]{5, 15, 200, 1_000, 5_000, 20_000, 100_000, 400_000, 1_000_000});
        m.put("SPIDER", new long[]{5, 25, 200, 1_000, 5_000, 20_000, 100_000, 400_000, 1_000_000});
        m.put("WOLF",   new long[]{10, 30, 250, 1_500, 7_000, 30_000, 150_000, 500_000, 1_000_000});
        SLAYER_XP_TABLE = Collections.unmodifiableMap(m);
    }

    /** Required mob kills per tier (index 0 = tier 1). */
    private static final int[] REQUIRED_KILLS_BY_TIER = {5, 25, 100, 250, 500};

    /**
     * A single slayer quest: the boss line, the chosen tier, and kill progress
     * accumulated toward spawning the boss.
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

        public boolean isComplete() {
            return kills >= requiredKills;
        }
    }

    private final Map<UUID, SlayerQuest> activeQuests = new HashMap<>();
    private final Map<UUID, Map<SlayerType, Long>> xpMap = new HashMap<>();

    /**
     * Starts a tier-1 slayer quest for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the slayer quest line, must not be null
     * @return the newly started quest
     * @throws IllegalArgumentException if playerId or type is null
     * @throws IllegalStateException    if the player already has an active quest
     */
    public SlayerQuest startQuest(UUID playerId, SlayerType type) {
        return startQuest(playerId, type, 1);
    }

    /**
     * Starts a new slayer quest for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @param type     the slayer quest line, must not be null
     * @param tier     the boss tier, from 1 to {@link SlayerType#getMaxTier()}
     * @return the newly started quest
     * @throws IllegalArgumentException if playerId or type is null, or tier is out of range
     * @throws IllegalStateException    if the player already has an active quest
     */
    public SlayerQuest startQuest(UUID playerId, SlayerType type, int tier) {
        if (playerId == null || type == null) {
            throw new IllegalArgumentException("playerId and type must not be null");
        }
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
     * @param playerId the player's UUID, must not be null
     * @param amount   the number of kills to credit, must be non-negative
     * @return the updated quest
     * @throws IllegalArgumentException if playerId is null or amount is negative
     * @throws IllegalStateException    if the player has no active quest
     */
    public SlayerQuest addProgress(UUID playerId, int amount) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
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
     * @param playerId the player's UUID, must not be null
     * @return the quest that was removed, or {@code null} if none was active
     * @throws IllegalArgumentException if playerId is null
     */
    public SlayerQuest endQuest(UUID playerId) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
        return activeQuests.remove(playerId);
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
     * @throws IllegalArgumentException if amount is negative
     * @throws ArithmeticException      if the addition would overflow
     */
    public long addXp(UUID playerId, SlayerType type, long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
        Map<SlayerType, Long> entry = xpMap.computeIfAbsent(playerId, id -> new EnumMap<>(SlayerType.class));
        long updated = Math.addExact(entry.getOrDefault(type, 0L), amount);
        entry.put(type, updated);
        return updated;
    }

    /**
     * Removes all slayer data for the given player, including any active quest.
     *
     * @param playerId the player's UUID
     */
    public void clear(UUID playerId) {
        activeQuests.remove(playerId);
        xpMap.remove(playerId);
    }
}
