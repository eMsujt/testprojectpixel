package com.skyblock.mining;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking each player's mining progression: blocks mined, earned
 * mining experience, pickaxe enchants, and the {@link MiningLocation} they
 * are currently mining in.
 *
 * <p>Players start at zero blocks mined and zero experience with no current
 * location and no pickaxe enchants. Not thread-safe; synchronize externally
 * if accessed from multiple threads.</p>
 */
public final class MiningManager {

    private static final MiningManager INSTANCE = new MiningManager();

    /** Mining speed every player has with an unenchanted pickaxe. */
    private static final int BASE_MINING_SPEED = 100;

    /** Mining speed gained per level of the Efficiency enchant. */
    private static final int EFFICIENCY_SPEED_PER_LEVEL = 20;

    private final Map<UUID, Integer> blocksMined = new HashMap<>();
    private final Map<UUID, Double> experience = new HashMap<>();
    private final Map<UUID, MiningLocation> locations = new HashMap<>();
    private final Map<UUID, Integer> pickaxeEfficiency = new HashMap<>();

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
     * Records a mined block for the player, incrementing their block count
     * and awarding the given mining experience.
     *
     * @param playerId the player's UUID
     * @param xp       the mining experience the block is worth, must not be negative
     * @return the player's total mined block count after this block
     * @throws IllegalArgumentException if {@code xp} is negative
     */
    public int recordBlockMined(UUID playerId, double xp) {
        if (xp < 0) {
            throw new IllegalArgumentException("xp must not be negative: " + xp);
        }
        experience.merge(playerId, xp, Double::sum);
        return blocksMined.merge(playerId, 1, Integer::sum);
    }

    /**
     * Returns how many blocks the player has mined.
     *
     * @param playerId the player's UUID
     * @return the total mined block count, zero if the player has never mined
     */
    public int getBlocksMined(UUID playerId) {
        return blocksMined.getOrDefault(playerId, 0);
    }

    /**
     * Returns the player's accumulated mining experience.
     *
     * @param playerId the player's UUID
     * @return the total mining experience, zero if the player has never mined
     */
    public double getExperience(UUID playerId) {
        return experience.getOrDefault(playerId, 0.0);
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
        if (level == 0) {
            pickaxeEfficiency.remove(playerId);
        } else {
            pickaxeEfficiency.put(playerId, level);
        }
    }

    /**
     * Returns the player's mining speed, derived from the enchants on their
     * pickaxe: the base speed plus a bonus per Efficiency level.
     *
     * @param playerId the player's UUID
     * @return the mining speed, the base speed if their pickaxe is unenchanted
     */
    public int getMiningSpeed(UUID playerId) {
        int efficiency = pickaxeEfficiency.getOrDefault(playerId, 0);
        return BASE_MINING_SPEED + efficiency * EFFICIENCY_SPEED_PER_LEVEL;
    }

    /**
     * Sets the mining location the player is currently in.
     *
     * @param playerId the player's UUID
     * @param location the location the player entered, or {@code null} to clear it
     */
    public void setLocation(UUID playerId, MiningLocation location) {
        if (location == null) {
            locations.remove(playerId);
        } else {
            locations.put(playerId, location);
        }
    }

    /**
     * Returns the mining location the player is currently in.
     *
     * @param playerId the player's UUID
     * @return the player's current location, or {@code null} if they are not
     *         in a mining location
     */
    public MiningLocation getLocation(UUID playerId) {
        return locations.get(playerId);
    }

    /**
     * Resets the player's mining progression back to zero and clears their
     * current location and pickaxe enchants.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        blocksMined.remove(playerId);
        experience.remove(playerId);
        locations.remove(playerId);
        pickaxeEfficiency.remove(playerId);
    }
}
