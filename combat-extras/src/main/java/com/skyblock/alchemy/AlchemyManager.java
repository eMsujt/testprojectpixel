package com.skyblock.alchemy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks per-player alchemy skill experience and the levels derived from it.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class AlchemyManager {

    /** The highest alchemy level a player can reach. */
    public static final int MAX_LEVEL = 50;

    /** Experience required to advance from each level to the next, indexed by current level. */
    private static final long[] XP_PER_LEVEL = buildXpTable();

    private final Map<UUID, Long> experience = new HashMap<>();

    /**
     * Adds alchemy experience for the player, e.g. from brewing a potion.
     *
     * @param playerId the player gaining experience
     * @param amount   the experience to add, must not be negative
     * @return the player's total alchemy experience after the addition
     * @throws IllegalArgumentException if {@code amount} is negative
     * @throws NullPointerException if {@code playerId} is {@code null}
     */
    public long addExperience(UUID playerId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        long total = getExperience(playerId) + amount;
        experience.put(playerId, total);
        return total;
    }

    /**
     * Returns the player's total accumulated alchemy experience.
     *
     * @param playerId the player to look up
     * @return the total experience, {@code 0} if the player has none
     */
    public long getExperience(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return experience.getOrDefault(playerId, 0L);
    }

    /**
     * Returns the player's current alchemy level, derived from total experience.
     *
     * @param playerId the player to look up
     * @return the level between {@code 0} and {@link #MAX_LEVEL}
     */
    public int getLevel(UUID playerId) {
        long remaining = getExperience(playerId);
        int level = 0;
        while (level < MAX_LEVEL && remaining >= XP_PER_LEVEL[level]) {
            remaining -= XP_PER_LEVEL[level];
            level++;
        }
        return level;
    }

    /**
     * Returns the experience still needed for the player to reach the next level.
     *
     * @param playerId the player to look up
     * @return the missing experience, or {@code 0} if the player is at {@link #MAX_LEVEL}
     */
    public long getExperienceToNextLevel(UUID playerId) {
        long remaining = getExperience(playerId);
        int level = 0;
        while (level < MAX_LEVEL && remaining >= XP_PER_LEVEL[level]) {
            remaining -= XP_PER_LEVEL[level];
            level++;
        }
        return level >= MAX_LEVEL ? 0 : XP_PER_LEVEL[level] - remaining;
    }

    /**
     * Resets the player's alchemy experience back to zero.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had experience to reset, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return experience.remove(playerId) != null;
    }

    private static long[] buildXpTable() {
        long[] table = new long[MAX_LEVEL];
        for (int level = 0; level < MAX_LEVEL; level++) {
            // Cost grows quadratically: 50, 150, 300, 500, 750, ...
            table[level] = 50L * (level + 1) + 25L * (long) level * (level + 1);
        }
        return table;
    }
}
