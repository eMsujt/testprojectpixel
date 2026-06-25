package com.skyblock.core.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Canonical singleton tracking each player's SkyBlock XP and deriving their SkyBlock level.
 *
 * <p>XP can be credited from several {@link Category categories} (skills, slayers,
 * dungeons, events, museum, …); the per-category breakdown is tracked alongside the
 * cumulative total. Level formula mirrors the standard Hypixel skill curve defined in
 * {@link SkillManager}: max level is {@value #MAX_LEVEL}.</p>
 *
 * <p>Each level grants a {@link LevelReward} (coins plus a small permanent health
 * bonus); {@link #rewardsForLevelRange(int, int)} returns the rewards earned when
 * advancing across a span of levels.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class SkyblockLevelManager {

    /** Hypixel's SkyBlock Level: 1 level per 100 SkyBlock XP, with rewards documented to Level 500. */
    public static final int MAX_LEVEL = 500;

    /** SkyBlock XP required per level (flat). */
    private static final long XP_PER_LEVEL = 100L;

    /** SkyBlock Level task categories, matching Hypixel's breakdown. */
    public enum Category {
        CORE, SKILL, DUNGEON, EVENT, SLAYING, ESSENCE_SHOP, MISC
    }

    /** Rewards granted for reaching a single SkyBlock level. */
    public record LevelReward(int level, long coins, double healthBonus) {}

    private static final SkyblockLevelManager INSTANCE = new SkyblockLevelManager();

    /** Per-player cumulative SkyBlock XP; absent entries default to zero. */
    private final Map<UUID, Long> skyblockXP = new HashMap<>();

    /** Per-player XP broken down by the category it was earned from. */
    private final Map<UUID, EnumMap<Category, Long>> categoryXP = new HashMap<>();

    private SkyblockLevelManager() {}

    /**
     * Returns the single shared {@code SkyblockLevelManager} instance.
     *
     * @return the singleton instance
     */
    public static SkyblockLevelManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the total SkyBlock XP for the given player.
     *
     * @param playerId the player to look up
     * @return cumulative XP, {@code 0} if the player has none
     */
    public long getXP(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return skyblockXP.getOrDefault(playerId, 0L);
    }

    /**
     * Adds SkyBlock XP to the player's total, attributing it to {@link Category#MISC}.
     *
     * @param playerId the player to update
     * @param amount   the amount of XP to add (must be positive)
     * @return the new total XP after the addition
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public long addXP(UUID playerId, long amount) {
        return addXP(playerId, Category.MISC, amount);
    }

    /**
     * Adds SkyBlock XP to the player's total, attributing it to the given source category.
     *
     * @param playerId the player to update
     * @param category the source the XP was earned from
     * @param amount   the amount of XP to add (must be positive)
     * @return the new total XP after the addition
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public long addXP(UUID playerId, Category category, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(category, "category");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        long newXP = skyblockXP.getOrDefault(playerId, 0L) + amount;
        skyblockXP.put(playerId, newXP);
        categoryXP.computeIfAbsent(playerId, id -> new EnumMap<>(Category.class))
                .merge(category, amount, Long::sum);
        return newXP;
    }

    /**
     * Sets the player's SkyBlock XP to an explicit value (op use only). The per-category
     * breakdown is reset and the new value attributed to {@link Category#MISC}.
     *
     * @param playerId the player to update
     * @param xp       the new XP value (must not be negative)
     * @throws IllegalArgumentException if {@code xp} is negative
     */
    public void setXP(UUID playerId, long xp) {
        Objects.requireNonNull(playerId, "playerId");
        if (xp < 0) {
            throw new IllegalArgumentException("xp must not be negative");
        }
        skyblockXP.put(playerId, xp);
        EnumMap<Category, Long> breakdown = new EnumMap<>(Category.class);
        if (xp > 0) {
            breakdown.put(Category.MISC, xp);
        }
        categoryXP.put(playerId, breakdown);
    }

    /**
     * Returns the XP the player has earned from the given source category.
     *
     * @param playerId the player to look up
     * @param category the source category
     * @return XP earned from that category, {@code 0} if none
     */
    public long getCategoryXP(UUID playerId, Category category) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(category, "category");
        EnumMap<Category, Long> breakdown = categoryXP.get(playerId);
        return breakdown == null ? 0L : breakdown.getOrDefault(category, 0L);
    }

    /**
     * Returns an unmodifiable view of the player's XP broken down by source category.
     *
     * @param playerId the player to look up
     * @return category → XP map, empty if the player has none
     */
    public Map<Category, Long> getCategoryBreakdown(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        EnumMap<Category, Long> breakdown = categoryXP.get(playerId);
        if (breakdown == null || breakdown.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new EnumMap<>(breakdown));
    }

    /**
     * Returns the SkyBlock level (1–{@value #MAX_LEVEL}) for the given player.
     *
     * @param playerId the player to look up
     * @return current SkyBlock level
     */
    public int getLevel(UUID playerId) {
        return levelForXP(getXP(playerId));
    }

    /**
     * Returns the SkyBlock level corresponding to the given cumulative XP.
     *
     * @param totalXP total accumulated XP
     * @return level in range [1, {@value #MAX_LEVEL}]
     */
    public int levelForXP(long totalXP) {
        if (totalXP < 0) {
            return 0;
        }
        return (int) Math.min(MAX_LEVEL, totalXP / XP_PER_LEVEL);
    }

    /**
     * Returns the cumulative XP required to reach the given level.
     *
     * @param level target level, clamped to [1, {@value #MAX_LEVEL}]
     * @return cumulative XP threshold
     */
    public long xpForLevel(int level) {
        int clamped = Math.max(0, Math.min(level, MAX_LEVEL));
        return clamped * XP_PER_LEVEL;
    }

    /**
     * Returns the XP still needed to advance to the next level, or {@code 0}
     * if the player is already at max level.
     *
     * @param playerId the player to look up
     * @return XP remaining until next level, or 0 at max level
     */
    public long xpToNextLevel(UUID playerId) {
        long totalXP = getXP(playerId);
        int level = levelForXP(totalXP);
        if (level >= MAX_LEVEL) {
            return 0L;
        }
        return (long) (level + 1) * XP_PER_LEVEL - totalXP;
    }

    /**
     * Returns the reward granted for reaching the given level. Each level grants
     * {@code level * 100} coins and a small permanent health bonus (a larger one on
     * every fifth, milestone level).
     *
     * @param level target level, clamped to [1, {@value #MAX_LEVEL}]
     * @return the reward for that level
     */
    public LevelReward rewardForLevel(int level) {
        int clamped = Math.max(1, Math.min(level, MAX_LEVEL));
        long coins = clamped * 100L;
        double health = clamped % 5 == 0 ? 5.0 : 2.0;
        return new LevelReward(clamped, coins, health);
    }

    /**
     * Returns the rewards earned when advancing from {@code fromLevel} (exclusive) to
     * {@code toLevel} (inclusive), in ascending level order. Returns an empty list if
     * no levels were gained.
     *
     * @param fromLevel the level before advancing
     * @param toLevel   the level after advancing
     * @return rewards for each newly gained level
     */
    public List<LevelReward> rewardsForLevelRange(int fromLevel, int toLevel) {
        List<LevelReward> rewards = new ArrayList<>();
        for (int level = Math.max(1, fromLevel + 1); level <= Math.min(toLevel, MAX_LEVEL); level++) {
            rewards.add(rewardForLevel(level));
        }
        return rewards;
    }

    /**
     * Returns an unmodifiable view of all player UUIDs with tracked XP.
     *
     * @return set of tracked player UUIDs
     */
    public Set<UUID> getTrackedPlayers() {
        return Collections.unmodifiableSet(skyblockXP.keySet());
    }

    /**
     * Removes all XP data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        categoryXP.remove(playerId);
        return skyblockXP.remove(playerId) != null;
    }
}
