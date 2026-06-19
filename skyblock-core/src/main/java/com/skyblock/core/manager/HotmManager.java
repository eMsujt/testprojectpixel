package com.skyblock.core.manager;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class HotmManager {

    public enum HotmPerk {
        MINING_SPEED_BOOST(1,   0,  new int[]{20000}),
        EFFICIENT_MINER   (10,  1,  buildCosts(10,  1200, 1.8)),
        QUICK_FORGE       (20,  5,  buildCosts(20,  7500, 2.2)),
        MINING_FORTUNE    (50,  5,  buildCosts(50,  3000, 2.0)),
        DAILY_POWDER      (100, 1,  buildCosts(100, 1200, 1.8)),
        LUCK_OF_THE_CAVE  (45,  1,  buildCosts(45,  2000, 2.0)),
        POWDER_BUFF       (50,  1,  buildCosts(50,  3000, 2.0)),
        MINING_MADNESS    (1,   0,  new int[]{2000}),
        MINING_SPEED      (50,  20, buildCosts(50,  3000, 2.0)),
        SKY_MALL          (1,   0,  new int[]{2000}),
        GOBLIN_KILLER     (1,   0,  new int[]{2000}),
        MOLE              (200, 1,  buildCosts(200, 1000, 1.4)),
        PROFESSIONAL      (140, 4,  buildCosts(140, 2000, 1.7)),
        GREAT_EXPLORER    (20,  3,  buildCosts(20,  4000, 2.2)),
        PICKOBULUS        (1,   0,  new int[]{1200}),
        MANIACAL_MINER    (1,   0,  new int[]{2000});

        public final int maxLevel;
        /** Bonus stat per level (0 for toggle/ability nodes). */
        final int bonusPerLevel;
        /** Mithril Powder cost per level; index = level being purchased (0-based). */
        final int[] levelCosts;

        HotmPerk(int maxLevel, int bonusPerLevel, int[] levelCosts) {
            this.maxLevel = maxLevel;
            this.bonusPerLevel = bonusPerLevel;
            this.levelCosts = levelCosts;
        }
    }

    /** Highest HOTM tier reachable. */
    public static final int MAX_TIER = 7;

    /** Cumulative Mining XP required to reach each tier, indexed by tier-1. */
    private static final long[] TIER_XP = {0L, 3000L, 9000L, 25000L, 60000L, 100000L, 150000L};

    private static int[] buildCosts(int levels, int base, double scale) {
        int[] costs = new int[levels];
        for (int i = 0; i < levels; i++) {
            costs[i] = (int) Math.round(base * Math.pow(i + 1, scale));
        }
        return costs;
    }

    private static final HotmManager INSTANCE = new HotmManager();

    private final Map<UUID, int[]>  playerPerks    = new HashMap<>();
    private final Map<UUID, Integer> hotmTier      = new HashMap<>();
    private final Map<UUID, Long>   miningXp       = new HashMap<>();
    private final Map<UUID, Long>   mithrilPowder  = new HashMap<>();

    private HotmManager() {}

    public static HotmManager getInstance() {
        return INSTANCE;
    }

    public int getLevel(UUID playerId, HotmPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        int[] levels = playerPerks.get(playerId);
        return levels == null ? 0 : levels[perk.ordinal()];
    }

    public void setLevel(UUID playerId, HotmPerk perk, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        if (level < 0) throw new IllegalArgumentException("level must not be negative");
        int[] levels = playerPerks.computeIfAbsent(playerId, id -> new int[HotmPerk.values().length]);
        levels[perk.ordinal()] = Math.min(level, perk.maxLevel);
    }

    /** Returns the powder cost to upgrade {@code perk} from {@code currentLevel}, or {@code -1} if already maxed. */
    public int getUpgradeCost(HotmPerk perk, int currentLevel) {
        Objects.requireNonNull(perk, "perk");
        if (currentLevel < 0 || currentLevel >= perk.maxLevel) {
            return -1;
        }
        int[] costs = perk.levelCosts;
        return (costs != null && currentLevel < costs.length) ? costs[currentLevel] : -1;
    }

    /**
     * Upgrades {@code perk} by one level, deducting the required Mithril Powder.
     *
     * @return new level on success, {@code -1} if already maxed, {@code -2} if insufficient powder
     */
    public int purchaseUpgrade(UUID playerId, HotmPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        int current = getLevel(playerId, perk);
        if (current >= perk.maxLevel) {
            return -1;
        }
        int cost = getUpgradeCost(perk, current);
        if (cost > 0 && !spendMithrilPowder(playerId, cost)) {
            return -2;
        }
        setLevel(playerId, perk, current + 1);
        return current + 1;
    }

    /** Returns the cumulative stat bonus for {@code perk} at the player's current level. */
    public int getPerkBonus(UUID playerId, HotmPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        return perk.bonusPerLevel * getLevel(playerId, perk);
    }

    public long getMithrilPowder(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return mithrilPowder.getOrDefault(playerId, 0L);
    }

    public void addMithrilPowder(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        mithrilPowder.merge(playerId, amount, Long::sum);
    }

    public boolean spendMithrilPowder(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        long current = getMithrilPowder(playerId);
        if (current < amount) return false;
        mithrilPowder.put(playerId, current - amount);
        return true;
    }

    public long getMiningXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return miningXp.getOrDefault(playerId, 0L);
    }

    /**
     * Adds Mining XP, advancing the player's HOTM tier when a threshold is crossed.
     *
     * @return the player's HOTM tier after the gain
     */
    public int addMiningXp(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) throw new IllegalArgumentException("amount must not be negative");
        long total = miningXp.merge(playerId, amount, Long::sum);
        int newTier = computeTier(total);
        if (newTier > getHotmTier(playerId)) {
            hotmTier.put(playerId, newTier);
        }
        return getHotmTier(playerId);
    }

    public int getHotmTier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return hotmTier.getOrDefault(playerId, 1);
    }

    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int[] levels = playerPerks.get(playerId);
        if (levels != null) Arrays.fill(levels, 0);
        hotmTier.remove(playerId);
        miningXp.remove(playerId);
        mithrilPowder.remove(playerId);
    }

    private static int computeTier(long xp) {
        int tier = 1;
        for (int i = 1; i < TIER_XP.length; i++) {
            if (xp >= TIER_XP[i]) tier = i + 1;
            else break;
        }
        return tier;
    }
}
