package com.skyblock.core.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock trophy fishing catches.
 *
 * <p>Tracks per-player catch counts keyed by {@link FishingManager.TrophyFish}
 * type. Each trophy fish progresses through Bronze, Silver, Gold and Diamond
 * tiers based on how many of it the player has caught, and contributes to the
 * player's overall trophy point total. Call {@link #rollTrophyFish(int)} to
 * determine whether a trophy fish drops and which one.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class TrophyFishManager {

    /**
     * Trophy tier earned for a given fish, determined by how many of that fish
     * the player has caught. Tiers ascend by catch-count threshold and award
     * an increasing number of trophy points.
     */
    public enum TrophyTier {
        BRONZE(1, 1),
        SILVER(50, 2),
        GOLD(100, 3),
        DIAMOND(150, 4);

        /** Minimum catch count required to reach this tier. */
        public final int threshold;
        /** Trophy points awarded for reaching this tier. */
        public final int points;

        TrophyTier(int threshold, int points) {
            this.threshold = threshold;
            this.points = points;
        }
    }

    /** Overall chance (0–1) that any fishing catch triggers a trophy fish roll. */
    public static final double BASE_TROPHY_CHANCE = 0.05;

    private static final TrophyFishManager INSTANCE = new TrophyFishManager();

    /** Per-player trophy fish catch counts. */
    private final Map<UUID, Map<FishingManager.TrophyFish, Integer>> catches = new HashMap<>();
    private final Random random = new Random();

    private TrophyFishManager() {
    }

    /**
     * Returns the single shared {@code TrophyFishManager} instance.
     *
     * @return the singleton instance
     */
    public static TrophyFishManager getInstance() {
        return INSTANCE;
    }

    /**
     * Records one catch of the given trophy fish for the player.
     *
     * @param playerId the player who caught it, must not be null
     * @param fish     the trophy fish type, must not be null
     */
    public void recordCatch(UUID playerId, FishingManager.TrophyFish fish) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(fish, "fish");
        catches.computeIfAbsent(playerId, k -> new HashMap<>())
               .merge(fish, 1, Integer::sum);
    }

    /**
     * Returns how many times the player has caught the given trophy fish.
     *
     * @param playerId the player to look up
     * @param fish     the trophy fish type
     * @return catch count, {@code 0} if none recorded
     */
    public int getCatchCount(UUID playerId, FishingManager.TrophyFish fish) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(fish, "fish");
        Map<FishingManager.TrophyFish, Integer> playerCatches = catches.get(playerId);
        if (playerCatches == null) {
            return 0;
        }
        return playerCatches.getOrDefault(fish, 0);
    }

    /**
     * Returns an unmodifiable view of all trophy fish catches for the player.
     *
     * @param playerId the player to look up
     * @return map of trophy fish to catch counts, empty if none recorded
     */
    public Map<FishingManager.TrophyFish, Integer> getAllCatches(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<FishingManager.TrophyFish, Integer> playerCatches = catches.get(playerId);
        if (playerCatches == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(playerCatches);
    }

    /**
     * Returns the trophy tier the player has earned for the given fish, based on
     * how many of it they have caught, or {@code null} if none caught yet.
     *
     * @param playerId the player to look up
     * @param fish     the trophy fish type
     * @return the highest {@link TrophyTier} reached, or {@code null} if uncaught
     */
    public TrophyTier getTier(UUID playerId, FishingManager.TrophyFish fish) {
        int count = getCatchCount(playerId, fish);
        TrophyTier tier = null;
        for (TrophyTier candidate : TrophyTier.values()) {
            if (count >= candidate.threshold) {
                tier = candidate;
            }
        }
        return tier;
    }

    /**
     * Returns the player's total trophy points, summed across every trophy fish
     * by the points awarded for the highest tier reached on each.
     *
     * @param playerId the player to look up
     * @return total trophy points, {@code 0} if none caught
     */
    public int getTotalPoints(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<FishingManager.TrophyFish, Integer> playerCatches = catches.get(playerId);
        if (playerCatches == null) {
            return 0;
        }
        int total = 0;
        for (FishingManager.TrophyFish fish : playerCatches.keySet()) {
            TrophyTier tier = getTier(playerId, fish);
            if (tier != null) {
                total += tier.points;
            }
        }
        return total;
    }

    /**
     * Returns all trophy fish types available at or below the given fishing level.
     *
     * @param level the player's fishing level
     * @return array of available trophy fish
     */
    public FishingManager.TrophyFish[] getAvailableTrophyFish(int level) {
        int count = 0;
        for (FishingManager.TrophyFish fish : FishingManager.TrophyFish.values()) {
            if (fish.minLevel <= level) {
                count++;
            }
        }
        FishingManager.TrophyFish[] result = new FishingManager.TrophyFish[count];
        int i = 0;
        for (FishingManager.TrophyFish fish : FishingManager.TrophyFish.values()) {
            if (fish.minLevel <= level) {
                result[i++] = fish;
            }
        }
        return result;
    }

    /**
     * Resets all trophy fish catches for the given player (call on quit to free
     * memory).
     *
     * @param playerId the player whose catches to reset
     */
    public void resetCatches(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        catches.remove(playerId);
    }

    /**
     * Rolls whether a trophy fish drops for the given fishing level.
     * Returns the caught {@link FishingManager.TrophyFish}, or {@code null} if no
     * trophy fish dropped this catch.
     *
     * <p>First checks the overall {@link #BASE_TROPHY_CHANCE}, then selects
     * a random eligible trophy fish weighted by its {@code dropChance}.</p>
     *
     * @param level the player's fishing level
     * @return the caught trophy fish, or {@code null}
     */
    public FishingManager.TrophyFish rollTrophyFish(int level) {
        if (random.nextDouble() >= BASE_TROPHY_CHANCE) {
            return null;
        }

        double totalWeight = 0.0;
        for (FishingManager.TrophyFish fish : FishingManager.TrophyFish.values()) {
            if (fish.minLevel <= level) {
                totalWeight += fish.dropChance;
            }
        }
        if (totalWeight == 0.0) {
            return null;
        }

        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (FishingManager.TrophyFish fish : FishingManager.TrophyFish.values()) {
            if (fish.minLevel > level) {
                continue;
            }
            cumulative += fish.dropChance;
            if (roll < cumulative) {
                return fish;
            }
        }
        return null;
    }
}
