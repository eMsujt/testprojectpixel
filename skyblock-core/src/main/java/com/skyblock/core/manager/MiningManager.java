package com.skyblock.core.manager;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock mining skill progression and speed bonuses.
 *
 * <p>Tracks per-player mining XP and level, and exposes a {@link MiningSpeedBonus}
 * table keyed on mining skill level (1–{@value #MAX_LEVEL}).</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class MiningManager {

    /** Ore types tracked by the mining skill, each carrying XP awarded on break. */
    public enum OreType {
        COAL_ORE(Material.COAL_ORE, 5),
        DEEPSLATE_COAL_ORE(Material.DEEPSLATE_COAL_ORE, 5),
        IRON_ORE(Material.IRON_ORE, 10),
        DEEPSLATE_IRON_ORE(Material.DEEPSLATE_IRON_ORE, 10),
        GOLD_ORE(Material.GOLD_ORE, 15),
        DEEPSLATE_GOLD_ORE(Material.DEEPSLATE_GOLD_ORE, 15),
        REDSTONE_ORE(Material.REDSTONE_ORE, 15),
        DEEPSLATE_REDSTONE_ORE(Material.DEEPSLATE_REDSTONE_ORE, 15),
        LAPIS_ORE(Material.LAPIS_ORE, 20),
        DEEPSLATE_LAPIS_ORE(Material.DEEPSLATE_LAPIS_ORE, 20),
        DIAMOND_ORE(Material.DIAMOND_ORE, 50),
        DEEPSLATE_DIAMOND_ORE(Material.DEEPSLATE_DIAMOND_ORE, 50),
        EMERALD_ORE(Material.EMERALD_ORE, 65),
        DEEPSLATE_EMERALD_ORE(Material.DEEPSLATE_EMERALD_ORE, 65),
        NETHER_QUARTZ_ORE(Material.NETHER_QUARTZ_ORE, 20),
        NETHER_GOLD_ORE(Material.NETHER_GOLD_ORE, 15),
        ANCIENT_DEBRIS(Material.ANCIENT_DEBRIS, 120);

        private final Material material;
        private final int xp;

        OreType(Material material, int xp) {
            this.material = material;
            this.xp = xp;
        }

        public Material getMaterial() { return material; }
        public int getXp() { return xp; }
    }

    /** Mining zones available in SkyBlock, each requiring a minimum mining level. */
    public enum MiningZone {
        SPIDER_DEN("Spider's Den",         0),
        GOLD_MINE("Gold Mine",             1),
        DEEP_CAVERNS("Deep Caverns",       5),
        DWARVEN_MINES("Dwarven Mines",    12),
        CRYSTAL_HOLLOWS("Crystal Hollows", 20),
        MINESHAFT("Mineshaft",            25);

        private final String displayName;
        private final int minLevel;

        MiningZone(String displayName, int minLevel) {
            this.displayName = displayName;
            this.minLevel = minLevel;
        }

        public String getDisplayName() { return displayName; }
        public int getMinLevel() { return minLevel; }
    }

    /**
     * Nodes in the Heart of the Mountain perk tree. Each node has a maximum level
     * and a per-level cost in HOTM tokens.
     */
    public enum HotmNode {
        MINING_SPEED("Mining Speed",        50, 1),
        MINING_FORTUNE("Mining Fortune",    50, 1),
        QUICK_FORGE("Quick Forge",          20, 2),
        TITANIUM_INSANITY("Titanium Insanity", 50, 1),
        LONESOME_MINER("Lonesome Miner",    45, 2),
        PROFESSIONAL("Professional",       140, 1),
        MOLE("Mole",                       190, 1),
        EFFICIENT_MINER("Efficient Miner", 100, 1),
        PICKOBULUS("Pickobulus",             3, 3),
        MINING_MADNESS("Mining Madness",     1, 5);

        private final String displayName;
        private final int maxLevel;
        private final int tokenCost;

        HotmNode(String displayName, int maxLevel, int tokenCost) {
            this.displayName = displayName;
            this.maxLevel = maxLevel;
            this.tokenCost = tokenCost;
        }

        public String getDisplayName() { return displayName; }
        public int getMaxLevel() { return maxLevel; }
        public int getTokenCost() { return tokenCost; }
    }

    /** Lookup from {@link Material} to {@link OreType} for fast block-break dispatch. */
    public static final Map<Material, OreType> MATERIAL_TO_ORE;

    static {
        Map<Material, OreType> map = new EnumMap<>(Material.class);
        for (OreType ore : OreType.values()) {
            map.put(ore.getMaterial(), ore);
        }
        MATERIAL_TO_ORE = Map.copyOf(map);
    }

    /** Mining-speed bonus entry for a given skill-level range. */
    public static final class MiningSpeedBonus {
        private final int minLevel;
        private final int maxLevel;
        private final int speedBonus;

        MiningSpeedBonus(int minLevel, int maxLevel, int speedBonus) {
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.speedBonus = speedBonus;
        }

        public int getMinLevel() { return minLevel; }
        public int getMaxLevel() { return maxLevel; }
        public int getSpeedBonus() { return speedBonus; }
    }

    /**
     * Mining-speed bonus table ordered by ascending level tier.
     * Each entry covers an inclusive [minLevel, maxLevel] range.
     */
    private static final MiningSpeedBonus[] SPEED_TABLE = {
        new MiningSpeedBonus( 1,  4,   0),
        new MiningSpeedBonus( 5,  9,  10),
        new MiningSpeedBonus(10, 14,  20),
        new MiningSpeedBonus(15, 19,  35),
        new MiningSpeedBonus(20, 24,  50),
        new MiningSpeedBonus(25, 29,  70),
        new MiningSpeedBonus(30, 34,  90),
        new MiningSpeedBonus(35, 39, 120),
        new MiningSpeedBonus(40, 44, 150),
        new MiningSpeedBonus(45, 49, 190),
        new MiningSpeedBonus(50, 50, 250),
    };

    private static final int MAX_LEVEL = 50;

    private static final MiningManager INSTANCE = new MiningManager();

    /** Per-player accumulated mining XP. */
    private final Map<UUID, Double> miningXp = new HashMap<>();
    /** Per-player mining level cache. */
    private final Map<UUID, Integer> miningLevel = new HashMap<>();
    /** Per-player unspent Heart of the Mountain tokens. */
    private final Map<UUID, Integer> hotmTokens = new HashMap<>();
    /** Per-player HOTM node levels, indexed by {@link HotmNode#ordinal()}. */
    private final Map<UUID, int[]> hotmNodeLevels = new HashMap<>();

    private MiningManager() {}

    /**
     * Returns the single shared {@code MiningManager} instance.
     *
     * @return the singleton instance
     */
    public static MiningManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the full mining-speed bonus table.
     *
     * @return array of {@link MiningSpeedBonus} entries, ordered by level tier
     */
    public MiningSpeedBonus[] getSpeedTable() {
        return SPEED_TABLE.clone();
    }

    /**
     * Returns the mining-speed bonus for the given mining level.
     *
     * @param level mining skill level (1–{@value #MAX_LEVEL})
     * @return speed bonus value, or {@code 0} if the level is out of range
     */
    public int getSpeedBonus(int level) {
        for (MiningSpeedBonus entry : SPEED_TABLE) {
            if (level >= entry.minLevel && level <= entry.maxLevel) {
                return entry.speedBonus;
            }
        }
        return 0;
    }

    /**
     * Adds mining XP to the player and updates their level if thresholds are crossed.
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
        double total = miningXp.merge(playerId, amount, Double::sum);
        int newLevel = computeLevel(total);
        miningLevel.put(playerId, newLevel);
        return total;
    }

    /**
     * Returns the player's current mining XP.
     *
     * @param playerId the player to look up
     * @return total XP, {@code 0} if none recorded
     */
    public double getXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return miningXp.getOrDefault(playerId, 0.0);
    }

    /**
     * Returns the player's current mining level (1–{@value #MAX_LEVEL}).
     *
     * @param playerId the player to look up
     * @return mining level
     */
    public int getLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return miningLevel.getOrDefault(playerId, 1);
    }

    /**
     * Returns the mining-speed bonus for the given player's current level.
     *
     * @param playerId the player to look up
     * @return speed bonus value
     */
    public int getSpeedBonusForPlayer(UUID playerId) {
        return getSpeedBonus(getLevel(playerId));
    }

    // ---------------------------------------------------------------------------
    // Heart of the Mountain
    // ---------------------------------------------------------------------------

    /**
     * Returns the player's current unspent HOTM token balance.
     *
     * @param playerId the player to look up
     * @return token count, {@code 0} if none recorded
     */
    public int getHotmTokens(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return hotmTokens.getOrDefault(playerId, 0);
    }

    /**
     * Grants HOTM tokens to the player.
     *
     * @param playerId the player to credit
     * @param amount   tokens to add, must not be negative
     * @return the player's new token balance
     */
    public int addHotmTokens(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        return hotmTokens.merge(playerId, amount, Integer::sum);
    }

    /**
     * Returns the player's current level in the given HOTM node.
     *
     * @param playerId the player to look up
     * @param node     the node to query
     * @return the current level, {@code 0} if not unlocked
     */
    public int getNodeLevel(UUID playerId, HotmNode node) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(node, "node");
        int[] levels = hotmNodeLevels.get(playerId);
        return levels == null ? 0 : levels[node.ordinal()];
    }

    /**
     * Attempts to upgrade a HOTM node by one level, spending the node's per-level
     * token cost. Fails if the node is already at its maximum level or the player
     * has insufficient tokens.
     *
     * @param playerId the player to upgrade
     * @param node     the node to upgrade
     * @return the new node level after the upgrade, or {@code -1} if the upgrade failed
     */
    public int upgradeNode(UUID playerId, HotmNode node) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(node, "node");
        int current = getNodeLevel(playerId, node);
        if (current >= node.maxLevel) {
            return -1;
        }
        int tokens = getHotmTokens(playerId);
        if (tokens < node.tokenCost) {
            return -1;
        }
        hotmTokens.put(playerId, tokens - node.tokenCost);
        int[] levels = hotmNodeLevels.computeIfAbsent(playerId, id -> new int[HotmNode.values().length]);
        levels[node.ordinal()] = current + 1;
        return current + 1;
    }

    /**
     * Returns a copy of all HOTM node levels for the given player.
     *
     * @param playerId the player to look up
     * @return array of node levels indexed by {@link HotmNode#ordinal()}, all-zeros if no data
     */
    public int[] getAllNodeLevels(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int[] levels = hotmNodeLevels.get(playerId);
        if (levels == null) {
            return new int[HotmNode.values().length];
        }
        return Arrays.copyOf(levels, levels.length);
    }

    /**
     * Resets all HOTM node levels for the given player to zero and refunds the
     * total token cost of every level previously spent.
     *
     * @param playerId the player to reset
     * @return the number of tokens refunded
     */
    public int resetHotm(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int[] levels = hotmNodeLevels.get(playerId);
        if (levels == null) {
            return 0;
        }
        int refund = 0;
        for (HotmNode node : HotmNode.values()) {
            refund += levels[node.ordinal()] * node.tokenCost;
        }
        Arrays.fill(levels, 0);
        if (refund > 0) {
            hotmTokens.merge(playerId, refund, Integer::sum);
        }
        return refund;
    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    /**
     * Computes the mining level for the given total XP.
     * Formula: level {@code n} requires {@code 50 * n^2} cumulative XP.
     *
     * @param totalXp total accumulated mining XP
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
