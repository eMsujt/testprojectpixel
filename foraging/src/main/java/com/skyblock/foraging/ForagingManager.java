package com.skyblock.foraging;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks each player's foraging progression: total logs chopped and earned
 * foraging experience.
 *
 * <p>Players start at zero logs and zero experience. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class ForagingManager {

    private final Map<UUID, Integer> logsChopped = new HashMap<>();
    private final Map<UUID, Double> experience = new HashMap<>();

    /**
     * Records a chopped log for the player, incrementing their log count and
     * awarding the given foraging experience.
     *
     * @param playerId the player's UUID
     * @param xp       the foraging experience the log is worth, must not be negative
     * @return the player's total log count after this chop
     * @throws IllegalArgumentException if {@code xp} is negative
     */
    public int recordChop(UUID playerId, double xp) {
        if (xp < 0) {
            throw new IllegalArgumentException("xp must not be negative: " + xp);
        }
        experience.merge(playerId, xp, Double::sum);
        return logsChopped.merge(playerId, 1, Integer::sum);
    }

    /**
     * Returns how many logs the player has chopped.
     *
     * @param playerId the player's UUID
     * @return the total log count, zero if the player has never foraged
     */
    public int getLogsChopped(UUID playerId) {
        return logsChopped.getOrDefault(playerId, 0);
    }

    /**
     * Returns the player's accumulated foraging experience.
     *
     * @param playerId the player's UUID
     * @return the total foraging experience, zero if the player has never foraged
     */
    public double getExperience(UUID playerId) {
        return experience.getOrDefault(playerId, 0.0);
    }

    /**
     * Resets the player's foraging progression back to zero.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        logsChopped.remove(playerId);
        experience.remove(playerId);
    }
}
