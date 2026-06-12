package com.skyblock.core.foraging;

import org.bukkit.Material;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock foraging skill progression and speed bonuses.
 *
 * <p>Tracks per-player chop counts, total foraging XP, and foraging level.
 * Level thresholds follow the same curve used by other SkyBlock skills:
 * level {@code n} requires {@code 50 * n^2} cumulative XP.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class ForagingManager {

    /** All wood types that contribute to the Foraging skill. */
    public enum TreeType {
        OAK(Material.OAK_LOG, "Oak", 6),
        SPRUCE(Material.SPRUCE_LOG, "Spruce", 6),
        BIRCH(Material.BIRCH_LOG, "Birch", 6),
        JUNGLE(Material.JUNGLE_LOG, "Jungle", 8),
        ACACIA(Material.ACACIA_LOG, "Acacia", 8),
        DARK_OAK(Material.DARK_OAK_LOG, "Dark Oak", 8),
        MANGROVE(Material.MANGROVE_LOG, "Mangrove", 10),
        CHERRY(Material.CHERRY_LOG, "Cherry", 10),
        CRIMSON(Material.CRIMSON_STEM, "Crimson Stem", 12),
        WARPED(Material.WARPED_STEM, "Warped Stem", 12),
        MUSHROOM(Material.MUSHROOM_STEM, "Mushroom", 15);

        private final Material material;
        private final String displayName;
        private final int baseXp;

        TreeType(Material material, String displayName, int baseXp) {
            this.material = material;
            this.displayName = displayName;
            this.baseXp = baseXp;
        }

        public Material getMaterial() { return material; }
        public String getDisplayName() { return displayName; }
        public int getBaseXp() { return baseXp; }
    }

    /**
     * Static map from {@link Material} to foraging XP award, used by the
     * shared {@code SkyBlockEventListener} to check whether a broken block
     * qualifies for foraging XP before calling {@link #recordChop}.
     */
    public static final Map<Material, Integer> WOOD_XP_MAP;

    static {
        Map<Material, Integer> map = new EnumMap<>(Material.class);
        for (TreeType tree : TreeType.values()) {
            map.put(tree.getMaterial(), tree.getBaseXp());
        }
        WOOD_XP_MAP = Map.copyOf(map);
    }

    /** Speed bonus entry for a given foraging skill-level range. */
    public static final class ForagingSpeedBonus {
        private final int minLevel;
        private final int maxLevel;
        private final double speedMultiplier;

        ForagingSpeedBonus(int minLevel, int maxLevel, double speedMultiplier) {
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.speedMultiplier = speedMultiplier;
        }

        public int getMinLevel() { return minLevel; }
        public int getMaxLevel() { return maxLevel; }
        public double getSpeedMultiplier() { return speedMultiplier; }
    }

    /**
     * Speed-multiplier table ordered by ascending level tier.
     * Each entry covers an inclusive [minLevel, maxLevel] range.
     */
    private static final ForagingSpeedBonus[] SPEED_TABLE = {
        new ForagingSpeedBonus( 1,  4, 1.00),
        new ForagingSpeedBonus( 5,  9, 1.10),
        new ForagingSpeedBonus(10, 14, 1.20),
        new ForagingSpeedBonus(15, 19, 1.30),
        new ForagingSpeedBonus(20, 24, 1.40),
        new ForagingSpeedBonus(25, 29, 1.55),
        new ForagingSpeedBonus(30, 34, 1.70),
        new ForagingSpeedBonus(35, 39, 1.90),
        new ForagingSpeedBonus(40, 44, 2.10),
        new ForagingSpeedBonus(45, 49, 2.35),
        new ForagingSpeedBonus(50, 50, 2.60),
    };

    private static final int MAX_LEVEL = 50;

    private static final ForagingManager INSTANCE = new ForagingManager();

    /** Per-player chop counts per tree type. */
    private final Map<UUID, EnumMap<TreeType, Integer>> chops = new HashMap<>();
    /** Per-player accumulated foraging XP. */
    private final Map<UUID, Double> foragingXp = new HashMap<>();
    /** Per-player foraging level cache. */
    private final Map<UUID, Integer> foragingLevel = new HashMap<>();

    private ForagingManager() {}

    /**
     * Returns the single shared {@code ForagingManager} instance.
     *
     * @return the singleton instance
     */
    public static ForagingManager getInstance() {
        return INSTANCE;
    }

    /**
     * Records a log chop for the player, awarding the given XP amount and
     * incrementing the chop count for the matching tree type (if any).
     *
     * @param playerId the player's UUID
     * @param xp       the XP to award, must be positive
     * @throws IllegalArgumentException if xp is not positive
     */
    public void recordChop(UUID playerId, int xp) {
        Objects.requireNonNull(playerId, "playerId");
        if (xp <= 0) {
            throw new IllegalArgumentException("xp must be positive: " + xp);
        }
        double total = foragingXp.merge(playerId, (double) xp, Double::sum);
        foragingLevel.put(playerId, computeLevel(total));
    }

    /**
     * Records a log chop for a specific tree type, awarding base XP and
     * incrementing the per-type chop count.
     *
     * @param playerId the player's UUID
     * @param tree     the tree type that was chopped
     * @param amount   the number of logs chopped, must be positive
     * @return the player's total chop count for this tree after the chop
     * @throws IllegalArgumentException if amount is not positive
     */
    public int recordChop(UUID playerId, TreeType tree, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tree, "tree");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        recordChop(playerId, tree.getBaseXp() * amount);
        EnumMap<TreeType, Integer> playerChops =
                chops.computeIfAbsent(playerId, k -> new EnumMap<>(TreeType.class));
        return playerChops.merge(tree, amount, Integer::sum);
    }

    /**
     * Returns how many logs of the given tree type the player has chopped.
     *
     * @param playerId the player's UUID
     * @param tree     the tree type
     * @return the chop count, zero if none recorded
     */
    public int getChops(UUID playerId, TreeType tree) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tree, "tree");
        EnumMap<TreeType, Integer> playerChops = chops.get(playerId);
        if (playerChops == null) {
            return 0;
        }
        return playerChops.getOrDefault(tree, 0);
    }

    /**
     * Returns the player's total accumulated foraging XP across all tree types.
     *
     * @param playerId the player's UUID
     * @return total foraging XP, {@code 0} if none recorded
     */
    public double getXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return foragingXp.getOrDefault(playerId, 0.0);
    }

    /**
     * Returns the player's current foraging level (1–{@value #MAX_LEVEL}).
     *
     * @param playerId the player's UUID
     * @return foraging level
     */
    public int getLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return foragingLevel.getOrDefault(playerId, 1);
    }

    /**
     * Returns the speed multiplier for the given foraging level.
     *
     * @param level foraging skill level (1–{@value #MAX_LEVEL})
     * @return speed multiplier, or {@code 1.0} if level is out of range
     */
    public double getSpeedMultiplier(int level) {
        for (ForagingSpeedBonus entry : SPEED_TABLE) {
            if (level >= entry.minLevel && level <= entry.maxLevel) {
                return entry.speedMultiplier;
            }
        }
        return 1.0;
    }

    /**
     * Returns the speed multiplier for the given player's current level.
     *
     * @param playerId the player to look up
     * @return speed multiplier
     */
    public double getSpeedMultiplierForPlayer(UUID playerId) {
        return getSpeedMultiplier(getLevel(playerId));
    }

    /**
     * Returns the full speed-bonus table.
     *
     * @return array of {@link ForagingSpeedBonus} entries, ordered by level tier
     */
    public ForagingSpeedBonus[] getSpeedTable() {
        return SPEED_TABLE.clone();
    }

    /**
     * Resets the player's foraging progression back to zero.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        chops.remove(playerId);
        foragingXp.remove(playerId);
        foragingLevel.remove(playerId);
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /**
     * Computes the foraging level for the given total XP.
     * Formula: level {@code n} requires {@code 50 * n^2} cumulative XP.
     *
     * @param totalXp total accumulated foraging XP
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
