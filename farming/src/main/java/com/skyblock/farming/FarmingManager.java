package com.skyblock.farming;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages per-player farming progression: crop harvests and earned farming
 * experience, keyed by {@link CropType}.
 *
 * <p>Harvest counts and experience are stored per-player per-crop.
 * Not thread-safe; synchronize externally if accessed from multiple
 * threads.</p>
 */
public final class FarmingManager {

    private final Map<UUID, EnumMap<CropType, Integer>> harvests = new HashMap<>();
    private final Map<UUID, Double> experience = new HashMap<>();

    /**
     * Records a crop harvest for the player, incrementing the harvest count
     * for the given crop and awarding the crop's base XP.
     *
     * @param playerId the player's UUID
     * @param crop     the crop that was harvested, must not be null
     * @param amount   the number of crops harvested, must be positive
     * @return the player's total harvest count for this crop after the harvest
     * @throws IllegalArgumentException if crop is null or amount is not positive
     */
    public int recordHarvest(UUID playerId, CropType crop, int amount) {
        if (crop == null) {
            throw new IllegalArgumentException("crop must not be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        double xp = crop.getBaseXp() * amount;
        experience.merge(playerId, xp, Double::sum);
        EnumMap<CropType, Integer> playerHarvests =
                harvests.computeIfAbsent(playerId, k -> new EnumMap<>(CropType.class));
        return playerHarvests.merge(crop, amount, Integer::sum);
    }

    /**
     * Returns how many of the given crop the player has harvested.
     *
     * @param playerId the player's UUID
     * @param crop     the crop type, must not be null
     * @return the harvest count, zero if the player has never harvested this crop
     * @throws IllegalArgumentException if crop is null
     */
    public int getHarvests(UUID playerId, CropType crop) {
        if (crop == null) {
            throw new IllegalArgumentException("crop must not be null");
        }
        EnumMap<CropType, Integer> playerHarvests = harvests.get(playerId);
        if (playerHarvests == null) {
            return 0;
        }
        return playerHarvests.getOrDefault(crop, 0);
    }

    /**
     * Returns the player's total accumulated farming experience across all crops.
     *
     * @param playerId the player's UUID
     * @return the total farming experience, zero if the player has no farming data
     */
    public double getExperience(UUID playerId) {
        return experience.getOrDefault(playerId, 0.0);
    }

    /**
     * Resets the player's farming progression back to zero.
     *
     * @param playerId the player's UUID
     */
    public void reset(UUID playerId) {
        harvests.remove(playerId);
        experience.remove(playerId);
    }
}
