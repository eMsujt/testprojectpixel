package com.skyblock.core.manager;

import com.skyblock.core.manager.GardenManager.GardenCrop;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's farming skill XP, level, and per-crop harvest counts.
 *
 * <p>Farming level is capped at 60. Level {@code n} requires {@code 50 * n^2}
 * cumulative XP, matching the exponential curve used by other skill managers.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class JacobFarmingManager {

    private static final int MAX_LEVEL = 60;

    private static final JacobFarmingManager INSTANCE = new JacobFarmingManager();

    private final Map<UUID, Double> farmingXp = new HashMap<>();
    private final Map<UUID, Integer> farmingLevel = new HashMap<>();
    private final Map<UUID, Map<GardenCrop, Long>> cropCounts = new HashMap<>();

    private JacobFarmingManager() {
    }

    public static JacobFarmingManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // XP and levelling
    // -------------------------------------------------------------------------

    /**
     * Adds farming XP to the player and updates their level if a threshold is crossed.
     *
     * @param playerId the player receiving XP
     * @param amount   XP to add; must not be negative
     * @return the player's new total XP
     */
    public double addXp(UUID playerId, double amount) {
        Objects.requireNonNull(playerId, "playerId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        double total = farmingXp.merge(playerId, amount, Double::sum);
        farmingLevel.put(playerId, computeLevel(total));
        return total;
    }

    /**
     * Returns the player's current farming XP.
     *
     * @param playerId the player to look up
     * @return total XP, {@code 0} if none recorded
     */
    public double getXp(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return farmingXp.getOrDefault(playerId, 0.0);
    }

    /**
     * Returns the player's current farming level (1–{@value #MAX_LEVEL}).
     *
     * @param playerId the player to look up
     * @return farming level
     */
    public int getLevel(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return farmingLevel.getOrDefault(playerId, 1);
    }

    // -------------------------------------------------------------------------
    // Crop tracking
    // -------------------------------------------------------------------------

    /**
     * Records that the player harvested {@code count} of the given crop.
     *
     * @param playerId the harvesting player
     * @param crop     the crop harvested
     * @param count    number of items harvested; must not be negative
     */
    public void addCropHarvested(UUID playerId, GardenCrop crop, long count) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative, got " + count);
        }
        cropCounts.computeIfAbsent(playerId, k -> new EnumMap<>(GardenCrop.class))
                  .merge(crop, count, Long::sum);
    }

    /**
     * Returns the total number of the given crop the player has harvested.
     *
     * @param playerId the player to look up
     * @param crop     the crop to check
     * @return harvest count, {@code 0} if none recorded
     */
    public long getCropHarvested(UUID playerId, GardenCrop crop) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(crop, "crop");
        Map<GardenCrop, Long> counts = cropCounts.get(playerId);
        return counts == null ? 0L : counts.getOrDefault(crop, 0L);
    }

    /**
     * Returns the player's total crops harvested across all crop types.
     *
     * @param playerId the player to look up
     * @return total harvest count, {@code 0} if none recorded
     */
    public long getTotalHarvested(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<GardenCrop, Long> counts = cropCounts.get(playerId);
        if (counts == null) {
            return 0L;
        }
        long total = 0L;
        for (long v : counts.values()) {
            total += v;
        }
        return total;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

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
