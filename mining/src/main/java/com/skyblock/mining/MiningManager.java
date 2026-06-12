package com.skyblock.mining;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking each player's mining progression: per-ore mined counts,
 * earned mining experience, pickaxe enchants, and the {@link MiningLocation}
 * they are currently mining in.
 *
 * <p>Progression is stored per player as a {@link PlayerMiningData}, created
 * lazily on first access. Players start at zero blocks mined and zero
 * experience with no current location and no pickaxe enchants. Not
 * thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MiningManager {

    /**
     * A single player's mining progression.
     *
     * <p>Instances are created only through
     * {@link MiningManager#getMiningData(UUID)} and mutated only through
     * {@link MiningManager}.</p>
     */
    public static final class PlayerMiningData {

        private final Map<String, Integer> oreCounts = new HashMap<>();
        private double experience;
        private MiningLocation location;
        private int pickaxeEfficiency;

        private PlayerMiningData() {
        }

        /**
         * Returns how many blocks of the given ore the player has mined.
         *
         * @param ore the ore identifier, e.g. {@code "DIAMOND_ORE"}
         * @return the mined count for that ore, zero if never mined
         */
        public int getOreCount(String ore) {
            return oreCounts.getOrDefault(ore, 0);
        }

        /**
         * Returns the per-ore mined counts.
         *
         * @return an unmodifiable view of the ore counts, keyed by ore id
         */
        public Map<String, Integer> getOreCounts() {
            return Collections.unmodifiableMap(oreCounts);
        }

        /**
         * Returns how many blocks the player has mined across all ores.
         *
         * @return the total mined block count
         */
        public int getTotalBlocksMined() {
            return oreCounts.values().stream().mapToInt(Integer::intValue).sum();
        }

        /**
         * Returns the player's accumulated mining experience.
         *
         * @return the total mining experience
         */
        public double getExperience() {
            return experience;
        }

        /**
         * Returns the mining location the player is currently in.
         *
         * @return the current location, or {@code null} if they are not in
         *         a mining location
         */
        public MiningLocation getLocation() {
            return location;
        }

        /**
         * Returns the Efficiency enchant level on the player's pickaxe.
         *
         * @return the enchant level, zero if unenchanted
         */
        public int getPickaxeEfficiency() {
            return pickaxeEfficiency;
        }
    }

    private static final MiningManager INSTANCE = new MiningManager();

    /** Mining speed every player has with an unenchanted pickaxe. */
    private static final int BASE_MINING_SPEED = 100;

    /** Mining speed gained per level of the Efficiency enchant. */
    private static final int EFFICIENCY_SPEED_PER_LEVEL = 20;

    private final Map<UUID, PlayerMiningData> miningData = new HashMap<>();

    private MiningManager() {
    }

    /**
     * Returns the single shared {@code MiningManager} instance.
     *
     * @return the singleton instance
     */
    public static MiningManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the player's mining data, creating empty data if the player
     * has none yet.
     *
     * @param playerId the player's UUID
     * @return the player's mining data, never {@code null}
     */
    public PlayerMiningData getMiningData(UUID playerId) {
        return miningData.computeIfAbsent(playerId, id -> new PlayerMiningData());
    }

    /**
     * Records a mined ore block for the player, incrementing their count for
     * that ore and awarding the given mining experience.
     *
     * @param playerId the player's UUID
     * @param ore      the ore identifier, e.g. {@code "DIAMOND_ORE"}, must
     *                 not be null or blank
     * @param xp       the mining experience the block is worth, must not be negative
     * @return the player's mined count for that ore after this block
     * @throws IllegalArgumentException if {@code ore} is null or blank, or
     *                                  {@code xp} is negative
     */
    public int recordBlockMined(UUID playerId, String ore, double xp) {
        if (ore == null || ore.isBlank()) {
            throw new IllegalArgumentException("ore must not be null or blank");
        }
        if (xp < 0) {
            throw new IllegalArgumentException("xp must not be negative: " + xp);
        }
        PlayerMiningData data = getMiningData(playerId);
        data.experience += xp;
        return data.oreCounts.merge(ore, 1, Integer::sum);
    }

    /**
     * Sets the Efficiency enchant level on the player's pickaxe.
     *
     * @param playerId the player's UUID
     * @param level    the enchant level, zero to remove the enchant
     * @throws IllegalArgumentException if {@code level} is negative
     */
    public void setPickaxeEfficiency(UUID playerId, int level) {
        if (level < 0) {
            throw new IllegalArgumentException("level must not be negative: " + level);
        }
        getMiningData(playerId).pickaxeEfficiency = level;
    }

    /**
     * Returns the player's mining speed, derived from the enchants on their
     * pickaxe: the base speed plus a bonus per Efficiency level.
     *
     * @param playerId the player's UUID
     * @return the mining speed, the base speed if their pickaxe is unenchanted
     */
    public int getMiningSpeed(UUID playerId) {
        return BASE_MINING_SPEED
                + getMiningData(playerId).pickaxeEfficiency * EFFICIENCY_SPEED_PER_LEVEL;
    }

    /**
     * Sets the mining location the player is currently in.
     *
     * @param playerId the player's UUID
     * @param location the location the player entered, or {@code null} to clear it
     */
    public void setLocation(UUID playerId, MiningLocation location) {
        getMiningData(playerId).location = location;
    }

    /**
     * Resets the player's mining progression back to zero and clears their
     * current location and pickaxe enchants.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        miningData.remove(playerId);
    }
}
