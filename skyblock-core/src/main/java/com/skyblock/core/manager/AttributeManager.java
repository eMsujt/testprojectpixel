package com.skyblock.core.manager;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Canonical Crimson Isle attribute manager.
 *
 * <p>Crimson Isle gear carries <em>attributes</em> that a player levels up by
 * fusing <em>attribute shards</em>. This manager tracks, per player and per
 * {@link Attribute}: the pool of un-fused shards collected, the attribute's
 * current level (clamped to {@code [0, MAX_LEVEL]}), and the combined stat
 * bonus those levels grant.</p>
 *
 * <p>Each level follows a doubling shard cost curve (see {@link #LEVEL_UP_COST}),
 * and each level grants {@link Attribute#getBonusPerLevel()} of the attribute's
 * stat. The <em>combined attribute bonus</em> is the sum across all of a
 * player's attributes.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class AttributeManager {

    /**
     * Crimson Isle attributes. Each grants {@code bonusPerLevel} of its stat per
     * level reached.
     */
    public enum Attribute {
        LIFELINE("Lifeline", 1.0),
        MANA_POOL("Mana Pool", 2.0),
        BLAZING_FORTUNE("Blazing Fortune", 5.0),
        FISHING_EXPERIENCE("Fishing Experience", 1.0),
        DOMINANCE("Dominance", 1.0),
        MENDING("Mending", 2.0),
        VITALITY("Vitality", 2.0),
        SPEED("Speed", 0.5),
        MAGIC_FIND("Magic Find", 0.5),
        VETERAN("Veteran", 1.0);

        private final String displayName;
        private final double bonusPerLevel;

        Attribute(String displayName, double bonusPerLevel) {
            this.displayName = displayName;
            this.bonusPerLevel = bonusPerLevel;
        }

        public String getDisplayName() {
            return displayName;
        }

        public double getBonusPerLevel() {
            return bonusPerLevel;
        }
    }

    /** The highest level any attribute can reach. */
    public static final int MAX_LEVEL = 10;

    /**
     * Shards needed to advance from level {@code i} to level {@code i + 1}.
     * The curve doubles each level, so {@code LEVEL_UP_COST[0]} raises an
     * attribute from level 0 to 1.
     */
    private static final int[] LEVEL_UP_COST =
            {5, 10, 20, 40, 80, 160, 320, 640, 1280, 2560};

    private static final AttributeManager INSTANCE = new AttributeManager();

    private final Map<UUID, Map<Attribute, Integer>> shards = new HashMap<>();
    private final Map<UUID, Map<Attribute, Integer>> levels = new HashMap<>();

    private AttributeManager() {}

    public static AttributeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds attribute shards to the player's un-fused pool for the attribute.
     *
     * @param amount the number of shards to add; must not be negative
     * @return the new shard total
     */
    public int addShards(UUID playerId, Attribute attribute, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(attribute, "attribute");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<Attribute, Integer> shardMap = shards.computeIfAbsent(
                playerId, id -> new EnumMap<>(Attribute.class));
        int total = shardMap.getOrDefault(attribute, 0) + amount;
        shardMap.put(attribute, total);
        return total;
    }

    public int getShards(UUID playerId, Attribute attribute) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(attribute, "attribute");
        Map<Attribute, Integer> shardMap = shards.get(playerId);
        return shardMap == null ? 0 : shardMap.getOrDefault(attribute, 0);
    }

    public int getLevel(UUID playerId, Attribute attribute) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(attribute, "attribute");
        Map<Attribute, Integer> levelMap = levels.get(playerId);
        return levelMap == null ? 0 : levelMap.getOrDefault(attribute, 0);
    }

    /**
     * The shard cost to advance an attribute from {@code level} to the next
     * level, or {@code -1} if {@code level} is already at {@link #MAX_LEVEL}.
     */
    public int getShardCostForNextLevel(int level) {
        if (level < 0 || level >= MAX_LEVEL) {
            return -1;
        }
        return LEVEL_UP_COST[level];
    }

    /**
     * Fuses one level into the attribute if the player has enough shards and the
     * attribute is below {@link #MAX_LEVEL}, consuming the level's shard cost.
     *
     * @return the new attribute level (unchanged if the level-up could not happen)
     */
    public int tryLevelUp(UUID playerId, Attribute attribute) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(attribute, "attribute");
        int level = getLevel(playerId, attribute);
        int cost = getShardCostForNextLevel(level);
        if (cost < 0 || getShards(playerId, attribute) < cost) {
            return level;
        }
        shards.get(playerId).merge(attribute, -cost, Integer::sum);
        int newLevel = level + 1;
        levels.computeIfAbsent(playerId, id -> new EnumMap<>(Attribute.class))
                .put(attribute, newLevel);
        return newLevel;
    }

    /** The stat bonus a single attribute grants the player at its current level. */
    public double getAttributeBonus(UUID playerId, Attribute attribute) {
        Objects.requireNonNull(attribute, "attribute");
        return getLevel(playerId, attribute) * attribute.getBonusPerLevel();
    }

    /** The summed stat bonus across all of the player's attributes. */
    public double getCombinedBonus(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        double total = 0.0;
        for (Attribute attribute : Attribute.values()) {
            total += getAttributeBonus(playerId, attribute);
        }
        return total;
    }
}
