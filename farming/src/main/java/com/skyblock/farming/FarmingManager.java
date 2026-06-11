package com.skyblock.farming;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks each player's farming XP per crop type and derives their farming level.
 *
 * <p>XP defaults to {@code 0} until modified. Not thread-safe; synchronize
 * externally if accessed from multiple threads.</p>
 */
public final class FarmingManager {

    private static final int MAX_LEVEL = 60;
    private static final long XP_PER_LEVEL = 200L;

    private final Map<UUID, Map<CropType, Long>> cropXp = new HashMap<>();

    /**
     * Returns the total XP a player has earned for a given crop type.
     *
     * @param playerId the player's UUID
     * @param crop     the crop type to query
     * @return accumulated XP, or {@code 0} if none has been added
     */
    public long getXp(UUID playerId, CropType crop) {
        Objects.requireNonNull(crop, "crop");
        Map<CropType, Long> xpMap = cropXp.get(playerId);
        if (xpMap == null) {
            return 0L;
        }
        return xpMap.getOrDefault(crop, 0L);
    }

    /**
     * Adds XP to a player's total for the given crop type.
     *
     * @param playerId the player's UUID
     * @param crop     the crop type to credit
     * @param amount   the amount of XP to add, must be positive
     * @throws IllegalArgumentException if {@code amount} is not positive
     */
    public void addXp(UUID playerId, CropType crop, long amount) {
        Objects.requireNonNull(crop, "crop");
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        cropXp.computeIfAbsent(playerId, id -> new EnumMap<>(CropType.class))
                .merge(crop, amount, Long::sum);
    }

    /**
     * Returns the farming level for a player on a given crop type.
     *
     * <p>Levels run from {@code 0} to {@link #MAX_LEVEL}. Each level requires
     * {@link #XP_PER_LEVEL} XP.</p>
     *
     * @param playerId the player's UUID
     * @param crop     the crop type to check
     * @return the level, between {@code 0} and {@value #MAX_LEVEL} inclusive
     */
    public int getLevel(UUID playerId, CropType crop) {
        return (int) Math.min(MAX_LEVEL, getXp(playerId, crop) / XP_PER_LEVEL);
    }

    /**
     * Resets all farming XP for the given player.
     *
     * @param playerId the player's UUID
     */
    public void resetProgress(UUID playerId) {
        cropXp.remove(playerId);
    }
}
