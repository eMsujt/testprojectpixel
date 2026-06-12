package com.skyblock.core.slayer;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock slayer quests.
 *
 * <p>Tracks per-player slayer XP across all {@link SlayerType}s and each
 * player's currently active {@link SlayerQuest}.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class SlayerManager {

    /** The six SkyBlock slayer boss types. */
    public enum SlayerType {
        ZOMBIE, SPIDER, WOLF, ENDERMAN, BLAZE, VAMPIRE
    }

    /** A tier definition for a slayer boss. */
    public static final class SlayerTier {
        private final int tier;
        private final long goldCost;
        private final int bossesRequired;
        private final long xpReward;
        /** Total accumulated XP needed to unlock this tier. */
        private final long xpRequired;

        SlayerTier(int tier, long goldCost, int bossesRequired, long xpReward, long xpRequired) {
            this.tier = tier;
            this.goldCost = goldCost;
            this.bossesRequired = bossesRequired;
            this.xpReward = xpReward;
            this.xpRequired = xpRequired;
        }

        public int getTier() { return tier; }
        public long getGoldCost() { return goldCost; }
        public int getBossesRequired() { return bossesRequired; }
        public long getXpReward() { return xpReward; }
        public long getXpRequired() { return xpRequired; }
    }

    /** An active slayer quest for a player. */
    public static final class SlayerQuest {
        private final SlayerType type;
        private final SlayerTier tier;
        private int bossesKilled;

        SlayerQuest(SlayerType type, SlayerTier tier) {
            this.type = Objects.requireNonNull(type, "type");
            this.tier = Objects.requireNonNull(tier, "tier");
            this.bossesKilled = 0;
        }

        public SlayerType getType() { return type; }
        public SlayerTier getTier() { return tier; }
        public int getBossesKilled() { return bossesKilled; }

        /** Returns {@code true} if the required boss kills have been reached. */
        public boolean isComplete() {
            return bossesKilled >= tier.getBossesRequired();
        }

        void incrementBossKill() {
            bossesKilled++;
        }
    }

    // ---------------------------------------------------------------------------
    // Tier catalogue
    // ---------------------------------------------------------------------------

    /** Tiers I–V for each slayer type. Index 0 = tier I. */
    private static final Map<SlayerType, SlayerTier[]> TIERS;

    static {
        Map<SlayerType, SlayerTier[]> t = new EnumMap<>(SlayerType.class);

        // tier(tier, goldCost, bossesRequired, xpReward, xpRequired)
        t.put(SlayerType.ZOMBIE, new SlayerTier[]{
                new SlayerTier(1,    0,  1,    5,       0),
                new SlayerTier(2,  100,  2,   25,     100),
                new SlayerTier(3,  500,  3,  200,    1000),
                new SlayerTier(4, 1500,  5, 1000,    5000),
                new SlayerTier(5, 5000,  8, 5000,   20000),
        });

        t.put(SlayerType.SPIDER, new SlayerTier[]{
                new SlayerTier(1,    0,  1,    5,       0),
                new SlayerTier(2,  100,  2,   25,     100),
                new SlayerTier(3,  500,  3,  200,    1000),
                new SlayerTier(4, 1500,  5, 1000,    5000),
                new SlayerTier(5, 5000,  8, 5000,   20000),
        });

        t.put(SlayerType.WOLF, new SlayerTier[]{
                new SlayerTier(1,    0,  1,    5,       0),
                new SlayerTier(2,  100,  2,   25,     100),
                new SlayerTier(3,  500,  3,  200,    1000),
                new SlayerTier(4, 1500,  5, 1000,    5000),
                new SlayerTier(5, 5000,  8, 5000,   20000),
        });

        t.put(SlayerType.ENDERMAN, new SlayerTier[]{
                new SlayerTier(1,    0,  1,    5,       0),
                new SlayerTier(2,  100,  2,   25,     100),
                new SlayerTier(3,  500,  3,  200,    1000),
                new SlayerTier(4, 1500,  5, 1000,    5000),
                new SlayerTier(5, 5000,  8, 5000,   20000),
        });

        t.put(SlayerType.BLAZE, new SlayerTier[]{
                new SlayerTier(1,    0,  1,    5,       0),
                new SlayerTier(2,  100,  2,   25,     100),
                new SlayerTier(3,  500,  3,  200,    1000),
                new SlayerTier(4, 1500,  5, 1000,    5000),
                new SlayerTier(5, 5000,  8, 5000,   20000),
        });

        t.put(SlayerType.VAMPIRE, new SlayerTier[]{
                new SlayerTier(1,    0,  1,    5,       0),
                new SlayerTier(2,  100,  2,   25,     100),
                new SlayerTier(3,  500,  3,  200,    1000),
                new SlayerTier(4, 1500,  5, 1000,    5000),
                new SlayerTier(5, 5000,  8, 5000,   20000),
        });

        TIERS = Collections.unmodifiableMap(t);
    }

    // ---------------------------------------------------------------------------
    // Singleton + state
    // ---------------------------------------------------------------------------

    private static final SlayerManager INSTANCE = new SlayerManager();

    /** Active quest per player. */
    private final Map<UUID, SlayerQuest> activeQuests = new HashMap<>();
    /** Accumulated slayer XP per player per type. */
    private final Map<UUID, Map<SlayerType, Long>> slayerXp = new HashMap<>();

    private SlayerManager() {}

    public static SlayerManager getInstance() {
        return INSTANCE;
    }

    // ---------------------------------------------------------------------------
    // Tier access
    // ---------------------------------------------------------------------------

    /**
     * Returns all five tiers for the given slayer type.
     *
     * @param type the slayer type
     * @return array of tiers, index 0 = tier I
     */
    public SlayerTier[] getTiers(SlayerType type) {
        return TIERS.get(Objects.requireNonNull(type, "type"));
    }

    /**
     * Returns the tier definition for the given type and 1-based tier number.
     *
     * @param type      the slayer type
     * @param tierNumber 1-based tier number (1–5)
     * @return the {@link SlayerTier}
     * @throws IllegalArgumentException if the tier number is out of range
     */
    public SlayerTier getTier(SlayerType type, int tierNumber) {
        if (tierNumber < 1 || tierNumber > 5) {
            throw new IllegalArgumentException("Tier must be 1–5, got: " + tierNumber);
        }
        return TIERS.get(Objects.requireNonNull(type, "type"))[tierNumber - 1];
    }

    // ---------------------------------------------------------------------------
    // XP
    // ---------------------------------------------------------------------------

    /**
     * Returns the accumulated slayer XP for the given player and type.
     *
     * @param playerId the player's UUID
     * @param type     the slayer type
     * @return accumulated XP (0 if none recorded)
     */
    public long getXp(UUID playerId, SlayerType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        Map<SlayerType, Long> xpMap = slayerXp.get(playerId);
        if (xpMap == null) return 0L;
        return xpMap.getOrDefault(type, 0L);
    }

    private void addXp(UUID playerId, SlayerType type, long amount) {
        slayerXp
                .computeIfAbsent(playerId, k -> new EnumMap<>(SlayerType.class))
                .merge(type, amount, Long::sum);
    }

    // ---------------------------------------------------------------------------
    // Quest lifecycle
    // ---------------------------------------------------------------------------

    /**
     * Starts a new slayer quest for the player.
     *
     * @param playerId   the player starting the quest
     * @param type       the slayer boss type
     * @param tierNumber 1-based tier number (1–5)
     * @throws IllegalArgumentException if the tier number is out of range
     * @throws IllegalStateException    if the player already has an active quest
     */
    public void startQuest(UUID playerId, SlayerType type, int tierNumber) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        if (activeQuests.containsKey(playerId)) {
            throw new IllegalStateException("Player already has an active slayer quest");
        }
        SlayerTier tier = getTier(type, tierNumber);
        activeQuests.put(playerId, new SlayerQuest(type, tier));
    }

    /**
     * Returns the player's active slayer quest, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the {@link SlayerQuest}, or {@code null}
     */
    public SlayerQuest getActiveQuest(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeQuests.get(playerId);
    }

    /**
     * Records a boss kill for the player's active quest.
     *
     * @param playerId the player who killed the boss
     * @throws IllegalStateException if the player has no active quest
     */
    public void recordBossKill(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        SlayerQuest quest = activeQuests.get(playerId);
        if (quest == null) {
            throw new IllegalStateException("No active slayer quest for this player");
        }
        quest.incrementBossKill();
    }

    /**
     * Completes and removes the player's active quest, awarding slayer XP.
     *
     * @param playerId the player collecting the quest reward
     * @return the completed {@link SlayerQuest}
     * @throws IllegalStateException if there is no active quest, or it is not yet complete
     */
    public SlayerQuest collectQuest(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        SlayerQuest quest = activeQuests.get(playerId);
        if (quest == null) {
            throw new IllegalStateException("No active slayer quest for this player");
        }
        if (!quest.isComplete()) {
            throw new IllegalStateException("Slayer quest is not yet complete");
        }
        activeQuests.remove(playerId);
        addXp(playerId, quest.getType(), quest.getTier().getXpReward());
        return quest;
    }

    /**
     * Cancels and removes the player's active quest without awarding XP.
     *
     * @param playerId the player whose quest to cancel
     * @return {@code true} if a quest was cancelled, {@code false} if there was none
     */
    public boolean cancelQuest(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return activeQuests.remove(playerId) != null;
    }
}
