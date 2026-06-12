package com.skyblock.slayer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks per-player slayer XP for each {@link SlayerType}.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class SlayerManager {

    private final Map<UUID, Map<SlayerType, Long>> xpMap = new HashMap<>();

    /**
     * Returns the player's accumulated slayer XP for the given type, or {@code 0}.
     *
     * @param playerId the player's UUID
     * @param type     the slayer quest line
     * @return current XP total
     */
    public long getXp(UUID playerId, SlayerType type) {
        Map<SlayerType, Long> entry = xpMap.get(playerId);
        if (entry == null) {
            return 0L;
        }
        return entry.getOrDefault(type, 0L);
    }

    /**
     * Adds XP to the player's slayer total for the given type.
     *
     * @param playerId the player's UUID
     * @param type     the slayer quest line
     * @param amount   the amount of XP to add, must be non-negative
     * @return the new XP total
     * @throws IllegalArgumentException if {@code amount} is negative
     * @throws ArithmeticException      if the addition would overflow
     */
    public long addXp(UUID playerId, SlayerType type, long amount) {
        requireNonNegative(amount);
        Map<SlayerType, Long> entry = xpMap.computeIfAbsent(playerId, id -> new EnumMap<>(SlayerType.class));
        long updated = Math.addExact(entry.getOrDefault(type, 0L), amount);
        entry.put(type, updated);
        return updated;
    }

    /**
     * Removes all slayer data for the given player (e.g. on profile wipe).
     *
     * @param playerId the player's UUID
     */
    public void clear(UUID playerId) {
        xpMap.remove(playerId);
    }

    private static void requireNonNegative(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
    }
}
