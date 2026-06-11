package com.skyblock.fishing;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks each player's fishing progression: total catches and earned
 * fishing experience.
 *
 * <p>Players start at zero catches and zero experience. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class FishingManager {

    private final Map<UUID, Integer> catches = new HashMap<>();
    private final Map<UUID, Double> experience = new HashMap<>();

    /**
     * Records a catch for the player, incrementing their catch count and
     * awarding the given fishing experience.
     *
     * @param playerId the player's UUID
     * @param xp       the fishing experience the catch is worth, must not be negative
     * @return the player's total catch count after this catch
     * @throws IllegalArgumentException if {@code xp} is negative
     */
    public int recordCatch(UUID playerId, double xp) {
        if (xp < 0) {
            throw new IllegalArgumentException("xp must not be negative: " + xp);
        }
        experience.merge(playerId, xp, Double::sum);
        return catches.merge(playerId, 1, Integer::sum);
    }

    /**
     * Returns how many catches the player has recorded.
     *
     * @param playerId the player's UUID
     * @return the total catch count, zero if the player has never fished
     */
    public int getCatches(UUID playerId) {
        return catches.getOrDefault(playerId, 0);
    }

    /**
     * Returns the player's accumulated fishing experience.
     *
     * @param playerId the player's UUID
     * @return the total fishing experience, zero if the player has never fished
     */
    public double getExperience(UUID playerId) {
        return experience.getOrDefault(playerId, 0.0);
    }

    /**
     * Resets the player's fishing progression back to zero.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        catches.remove(playerId);
        experience.remove(playerId);
    }
}
