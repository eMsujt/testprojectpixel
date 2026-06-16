package com.skyblock.core.farming.manager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock farming skill progression and yield bonuses.
 *
 * <p>Tracks per-player harvest counts per {@link CropType}, total farming XP,
 * and farming level. Level thresholds follow the same curve used by other
 * SkyBlock skills: level {@code n} requires {@code 50 * n^2} cumulative XP.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class FarmingManager {

    /** All crops that contribute to the Farming skill. */
    public enum CropType {
        WHEAT("Wheat", 6.0),
        CARROT("Carrot", 3.0),
        POTATO("Potato", 3.0),
        PUMPKIN("Pumpkin", 4.5),
        MELON("Melon", 4.0),
        SUGAR_CANE("Sugar Cane", 2.0),
        COCOA_BEANS("Cocoa Beans", 3.0),
        CACTUS("Cactus", 2.0),
        MUSHROOM("Mushroom", 6.0),
        NETHER_WART("Nether Wart", 3.0);

        private final String displayName;
        private final double baseXp;

        CropType(String displayName, double baseXp) {
            this.displayName = displayName;
            this.baseXp = baseXp;
        }

        public String getDisplayName() {
            return displayName;
        }

        public double getBaseXp() {
            return baseXp;
        }
    }

    /** Yield bonus entry for a given farming skill-level range. */
    public static final class FarmingYieldBonus {
        private final int minLevel;
        private final int maxLevel;
        private final double yieldMultiplier;

        FarmingYieldBonus(int minLevel, int maxLevel, double yieldMultiplier) {
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.yieldMultiplier = yieldMultiplier;
        }

        public int getMinLevel() { return minLevel; }
        public int getMaxLevel() { return maxLevel; }
        public double getYieldMultiplier() { return yieldMultiplier; }
    }

    /**
     * Yield-multiplier table ordered by ascending level tier.
     * Each entry covers an inclusive [minLevel, maxLevel] range.
     */
    private static final FarmingYieldBonus[] YIELD_TABLE = {
        new FarmingYieldBonus( 1,  4, 1.00),
        new FarmingYieldBonus( 5,  9, 1.10),
        new FarmingYieldBonus(10, 14, 1.20),
        new FarmingYieldBonus(15, 19, 1.30),
        new FarmingYieldBonus(20, 24, 1.40),
        new FarmingYieldBonus(25, 29, 1.55),
        new FarmingYieldBonus(30, 34, 1.70),
        new FarmingYieldBonus(35, 39, 1.90),
        new FarmingYieldBonus(40, 44, 2.10),
        new FarmingYieldBonus(45, 49, 2.35),
        new FarmingYieldBonus(50, 50, 2.60),
    };

    private static final int MAX_LEVEL = 50;

    private static final FarmingManager INSTANCE = new FarmingManager();

    /** Per-player harvest counts per crop. */
    private final Map<UUID, EnumMap<CropType, Integer>> harvests = new HashMap<>();
    /** Per-player accumulated farming XP. */
    private final Map<UUID, Double> farmingXp = new HashMap<>();
    /** Per-player farming level cache. */
    private final Map<UUID, Integer> farmingLevel = new HashMap<>();

    private FarmingManager() {}

    /**
     * Returns the single shared {@code FarmingManager} instance.
     *
     * @return the singleton instance
     */
    public static FarmingManager getInstance() {
        return INSTANCE;
    }

    /**
     * Records a crop harvest for the player, incrementing the harvest count
     * and awarding the crop's base XP multiplied by the amount.
     *
     * @param playerId the player's UUID
     * @param crop     the crop that was harvested
     * @param amount   the number of crops harvested, must be positive
     * @return the player's total harvest count for this crop after the harvest
     * @throws IllegalArgumentException if amount is not positive
     */
    public int recordHarvest(UUID playerId, CropType crop, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        double xp = crop.getBaseXp() * amount;
        double total = farmingXp.merge(playerId, xp, Double::sum);
        farmingLevel.put(playerId, computeLevel(total));
        EnumMap<CropType, Integer> playerHarvests =
                harvests.computeIfAbsent(playerId, k -> new EnumMap<>(CropType.class));
        return playerHarvests.merge(crop, amount, Integer::sum);
    }

    /**
     * Returns how many of the given crop the player has harvested.
     *
     * @param playerId the player's UUID
     * @param crop     the crop type
     * @return the harvest count, zero if none recorded
     */
    public int getHarvests(UUID playerId, CropType crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        EnumMap<CropType, Integer> playerHarvests = harvests.get(playerId);
        if (playerHarvests == null) {
            return 0;
        }
        return playerHarvests.getOrDefault(crop, 0);
    }

    /**
     * Returns the player's total accumulated farming XP across all crops.
     *
     * @param playerId the player's UUID
     * @return total farming XP, {@code 0} if none recorded
     */
    public double getXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return farmingXp.getOrDefault(playerId, 0.0);
    }

    /**
     * Returns the player's current farming level (1–{@value #MAX_LEVEL}).
     *
     * @param playerId the player's UUID
     * @return farming level
     */
    public int getLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return farmingLevel.getOrDefault(playerId, 1);
    }

    /**
     * Returns the yield multiplier for the given farming level.
     *
     * @param level farming skill level (1–{@value #MAX_LEVEL})
     * @return yield multiplier, or {@code 1.0} if level is out of range
     */
    public double getYieldMultiplier(int level) {
        for (FarmingYieldBonus entry : YIELD_TABLE) {
            if (level >= entry.minLevel && level <= entry.maxLevel) {
                return entry.yieldMultiplier;
            }
        }
        return 1.0;
    }

    /**
     * Returns the yield multiplier for the given player's current level.
     *
     * @param playerId the player to look up
     * @return yield multiplier
     */
    public double getYieldMultiplierForPlayer(UUID playerId) {
        return getYieldMultiplier(getLevel(playerId));
    }

    /**
     * Returns the full yield-bonus table.
     *
     * @return array of {@link FarmingYieldBonus} entries, ordered by level tier
     */
    public FarmingYieldBonus[] getYieldTable() {
        return YIELD_TABLE.clone();
    }

    /**
     * Resets the player's farming progression back to zero.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        harvests.remove(playerId);
        farmingXp.remove(playerId);
        farmingLevel.remove(playerId);
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /**
     * Computes the farming level for the given total XP.
     * Formula: level {@code n} requires {@code 50 * n^2} cumulative XP.
     *
     * @param totalXp total accumulated farming XP
     * @return level between 1 and {@value #MAX_LEVEL}
     */
    private static int computeLevel(double totalXp) {
        int level = 1;
        while (level < MAX_LEVEL) {
            double threshold = 50.0 * (level + 1) * (level + 1);
            if (totalXp < threshold) {
                break;
            }
            level++;
        }
        return level;
    }
}
