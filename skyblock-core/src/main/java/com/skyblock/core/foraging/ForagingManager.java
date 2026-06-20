package com.skyblock.core.foraging;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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

    /** Wood types that contribute to the Foraging skill, used for per-player tracking. */
    public enum ForagingType {
        OAK,
        BIRCH,
        SPRUCE,
        JUNGLE,
        ACACIA,
        DARK_OAK,
        MANGROVE,
        CHERRY
    }

    /** Foraging areas in The Park and other SkyBlock foraging zones. */
    public enum ForagingArea {
        DARK_THICKET("Dark Thicket", TreeType.DARK_OAK),
        SPRUCE_WOODS("Spruce Woods", TreeType.SPRUCE),
        BIRCH_PARK("Birch Park", TreeType.BIRCH),
        SAVANNA_WOODLAND("Savanna Woodland", TreeType.ACACIA),
        JUNGLE_ISLAND("Jungle Island", TreeType.JUNGLE);

        private final String displayName;
        private final TreeType primaryTree;

        ForagingArea(String displayName, TreeType primaryTree) {
            this.displayName = displayName;
            this.primaryTree = primaryTree;
        }

        public String getDisplayName() { return displayName; }
        public TreeType getPrimaryTree() { return primaryTree; }
    }

    /** @deprecated Use {@link ForagingArea} instead. */
    @Deprecated
    public enum ForagingZone {
        DARK_THICKET("Dark Thicket", TreeType.DARK_OAK),
        BIRCH_PARK("Birch Park", TreeType.BIRCH),
        SPRUCE_WOODS("Spruce Woods", TreeType.SPRUCE),
        SAVANNA_WOODLAND("Savanna Woodland", TreeType.ACACIA),
        JUNGLE_ISLAND("Jungle Island", TreeType.JUNGLE);

        private final String displayName;
        private final TreeType primaryTree;

        ForagingZone(String displayName, TreeType primaryTree) {
            this.displayName = displayName;
            this.primaryTree = primaryTree;
        }

        public String getDisplayName() { return displayName; }
        public TreeType getPrimaryTree() { return primaryTree; }
    }

    /** Simple log-type identifiers used for per-player chop tracking. */
    public enum LogType {
        OAK("Oak"),
        SPRUCE("Spruce"),
        BIRCH("Birch"),
        JUNGLE("Jungle"),
        ACACIA("Acacia"),
        DARK_OAK("Dark Oak"),
        MANGROVE("Mangrove"),
        CHERRY("Cherry"),
        CRIMSON("Crimson Stem"),
        WARPED("Warped Stem"),
        MUSHROOM("Mushroom");

        private final String displayName;

        LogType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

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
    /** Per-player current foraging area. */
    private final Map<UUID, ForagingArea> playerAreas = new HashMap<>();

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
     * Returns the foraging area the player is currently in, or {@code null} if none.
     *
     * @param playerId the player to look up
     * @return the player's current area, or {@code null}
     */
    public ForagingArea getArea(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerAreas.get(playerId);
    }

    /**
     * Sets the player's current foraging area.
     *
     * @param playerId the player to update
     * @param area     the area the player is entering, must not be null
     */
    public void setArea(UUID playerId, ForagingArea area) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(area, "area");
        playerAreas.put(playerId, area);
    }

    /**
     * Removes the player's current area assignment (e.g. on quit or hub travel).
     *
     * @param playerId the player to clear
     */
    public void clearArea(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerAreas.remove(playerId);
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
        playerAreas.remove(playerId);
    }

    // ---------------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------------

    public void load(File dataFolder) {
        File file = new File(dataFolder, "foraging.yml");
        if (!file.exists()) {
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        foragingXp.clear();
        foragingLevel.clear();
        chops.clear();
        for (String key : cfg.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                double xp = cfg.getDouble(key + ".xp", 0.0);
                foragingXp.put(id, xp);
                foragingLevel.put(id, computeLevel(xp));
                if (cfg.isConfigurationSection(key + ".chops")) {
                    EnumMap<TreeType, Integer> playerChops = new EnumMap<>(TreeType.class);
                    for (String treeName : cfg.getConfigurationSection(key + ".chops").getKeys(false)) {
                        try {
                            TreeType tree = TreeType.valueOf(treeName);
                            playerChops.put(tree, cfg.getInt(key + ".chops." + treeName, 0));
                        } catch (IllegalArgumentException ignored) {
                            // skip unknown tree types
                        }
                    }
                    if (!playerChops.isEmpty()) {
                        chops.put(id, playerChops);
                    }
                }
            } catch (IllegalArgumentException ignored) {
                // skip malformed UUIDs
            }
        }
    }

    public void save(File dataFolder) {
        File file = new File(dataFolder, "foraging.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : foragingXp.entrySet()) {
            String key = entry.getKey().toString();
            cfg.set(key + ".xp", entry.getValue());
            EnumMap<TreeType, Integer> playerChops = chops.get(entry.getKey());
            if (playerChops != null) {
                for (Map.Entry<TreeType, Integer> chop : playerChops.entrySet()) {
                    cfg.set(key + ".chops." + chop.getKey().name(), chop.getValue());
                }
            }
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save foraging.yml", e);
        }
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
