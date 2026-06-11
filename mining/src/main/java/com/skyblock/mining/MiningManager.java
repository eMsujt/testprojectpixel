package com.skyblock.mining;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks each player's mining progression: blocks mined, earned mining
 * experience, and the {@link MiningLocation} they are currently mining in.
 *
 * <p>Players start at zero blocks mined and zero experience with no current
 * location. Not thread-safe; synchronize externally if accessed from
 * multiple threads.</p>
 */
public final class MiningManager {

    private final Map<UUID, Integer> blocksMined = new HashMap<>();
    private final Map<UUID, Double> experience = new HashMap<>();
    private final Map<UUID, MiningLocation> locations = new HashMap<>();

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
     * current location.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        blocksMined.remove(playerId);
        experience.remove(playerId);
        locations.remove(playerId);
    }
}
