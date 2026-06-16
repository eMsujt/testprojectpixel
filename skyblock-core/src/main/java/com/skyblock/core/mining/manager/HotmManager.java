package com.skyblock.core.mining.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock Heart of the Mountain (HOTM) progression.
 *
 * <p>Tracks per-player HOTM token count, unlocked perks, and accumulated
 * HOTM XP/level (1–{@value #MAX_LEVEL}).</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class HotmManager {

    /** Perks available in the Heart of the Mountain skill tree. */
    public enum HotmPerk {
        MINING_SPEED_BOOST("Mining Speed Boost",      1, true),
        PICKOBULUS("Pickobulus",                       1, true),
        TITANIUM_INSANIUM("Titanium Insanium",         5, false),
        DAILY_POWDER("Daily Powder",                   1, false),
        LUCK_OF_THE_CAVE("Luck of the Cave",           5, false),
        EFFICIENT_MINER("Efficient Miner",            20, false),
        MINING_MADNESS("Mining Madness",               1, false),
        SKY_MALL("Sky Mall",                           1, false),
        PRECISION_MINING("Precision Mining",           1, false),
        FRONT_LOADED("Front Loaded",                   1, false),
        STELLAR_PLATES("Stellar Plates",               5, false),
        GOBLIN_KILLER("Goblin Killer",                 1, false),
        STAR_POWDER("Star Powder",                     1, false),
        MOLE("Mole",                                 200, false),
        PROFESSIONAL("Professional",                 140, false),
        LONESOME_MINER("Lonesome Miner",              45, false),
        GREAT_EXPLORER("Great Explorer",               20, false),
        MANIAC_MINER("Maniac Miner",                   1, false),
        DAILY_GRIND("Daily Grind",                    100, false),
        MINING_FORTUNE("Mining Fortune",              50, false);

        /** Human-readable display name shown to players. */
        public final String displayName;
        /** Maximum upgrade level for this perk. */
        public final int maxLevel;
        /** Whether this perk is an active ability (vs. passive). */
        public final boolean active;

        HotmPerk(String displayName, int maxLevel, boolean active) {
            this.displayName = displayName;
            this.maxLevel = maxLevel;
            this.active = active;
        }

        public String getDisplayName() { return displayName; }
        public int getMaxLevel() { return maxLevel; }
        public boolean isActive() { return active; }
    }

    private static final int MAX_LEVEL = 10;

    private static final HotmManager INSTANCE = new HotmManager();

    /** Per-player accumulated HOTM XP. */
    private final Map<UUID, Double> hotmXp = new HashMap<>();
    /** Per-player HOTM level cache. */
    private final Map<UUID, Integer> hotmLevel = new HashMap<>();
    /** Per-player available tokens. */
    private final Map<UUID, Integer> tokens = new HashMap<>();
    /** Per-player perk upgrade levels: playerId → (perk → level). */
    private final Map<UUID, Map<HotmPerk, Integer>> perkLevels = new HashMap<>();

    private HotmManager() {}

    /**
     * Returns the single shared {@code HotmManager} instance.
     *
     * @return the singleton instance
     */
    public static HotmManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds HOTM XP to the player, updates their level, and awards tokens on level-up.
     *
     * @param playerId the player receiving XP
     * @param amount   XP to add, must not be negative
     * @return the player's new total XP
     */
    public double addXp(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        double total = hotmXp.merge(playerId, amount, Double::sum);
        int oldLevel = hotmLevel.getOrDefault(playerId, 1);
        int newLevel = computeLevel(total);
        hotmLevel.put(playerId, newLevel);
        if (newLevel > oldLevel) {
            tokens.merge(playerId, newLevel - oldLevel, Integer::sum);
        }
        return total;
    }

    /**
     * Returns the player's current HOTM XP.
     *
     * @param playerId the player to look up
     * @return total XP, {@code 0} if none recorded
     */
    public double getXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return hotmXp.getOrDefault(playerId, 0.0);
    }

    /**
     * Returns the player's current HOTM level (1–{@value #MAX_LEVEL}).
     *
     * @param playerId the player to look up
     * @return HOTM level
     */
    public int getLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return hotmLevel.getOrDefault(playerId, 1);
    }

    /**
     * Returns the number of unspent tokens the player has.
     *
     * @param playerId the player to look up
     * @return available token count
     */
    public int getTokens(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return tokens.getOrDefault(playerId, 0);
    }

    /**
     * Returns the player's current upgrade level for the given perk.
     *
     * @param playerId the player to look up
     * @param perk     the perk to query
     * @return upgrade level (0 = not unlocked)
     */
    public int getPerkLevel(UUID playerId, HotmPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        Map<HotmPerk, Integer> map = perkLevels.get(playerId);
        return map == null ? 0 : map.getOrDefault(perk, 0);
    }

    /**
     * Upgrades the given perk by one level, spending one token.
     *
     * @param playerId the player upgrading the perk
     * @param perk     the perk to upgrade
     * @throws IllegalStateException    if the player has no tokens available
     * @throws IllegalArgumentException if the perk is already at max level
     */
    public void upgradePerk(UUID playerId, HotmPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        int available = tokens.getOrDefault(playerId, 0);
        if (available <= 0) {
            throw new IllegalStateException("No tokens available");
        }
        Map<HotmPerk, Integer> map = perkLevels.computeIfAbsent(playerId, k -> new HashMap<>());
        int current = map.getOrDefault(perk, 0);
        if (current >= perk.maxLevel) {
            throw new IllegalArgumentException(perk.getDisplayName() + " is already at max level");
        }
        map.put(perk, current + 1);
        tokens.put(playerId, available - 1);
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /**
     * Computes the HOTM level for the given total XP.
     * Formula: level {@code n} requires {@code 200 * n^2} cumulative XP.
     */
    private static int computeLevel(double totalXp) {
        int level = 1;
        while (level < MAX_LEVEL) {
            double threshold = 200.0 * (level + 1) * (level + 1);
            if (totalXp < threshold) {
                break;
            }
            level++;
        }
        return level;
    }
}
