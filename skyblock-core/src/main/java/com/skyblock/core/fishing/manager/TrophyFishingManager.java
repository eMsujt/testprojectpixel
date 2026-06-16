package com.skyblock.core.fishing.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock trophy fishing.
 *
 * <p>Tracks per-player trophy fish catches keyed by {@link FishingManager.TrophyFish} type.
 * Trophy fish are rare catches unlocked at specific fishing levels.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class TrophyFishingManager {

    private static final TrophyFishingManager INSTANCE = new TrophyFishingManager();

    /** Per-player trophy fish catch counts, keyed by {@link FishingManager.TrophyFish}. */
    private final Map<UUID, Map<FishingManager.TrophyFish, Integer>> catches = new HashMap<>();

    private TrophyFishingManager() {
    }

    /**
     * Returns the single shared {@code TrophyFishingManager} instance.
     *
     * @return the singleton instance
     */
    public static TrophyFishingManager getInstance() {
        return INSTANCE;
    }

    /**
     * Records a trophy fish catch for the given player.
     *
     * @param playerId the player who caught the fish, must not be null
     * @param fish     the trophy fish type caught, must not be null
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
     * Resets all trophy fish catches for the given player.
     *
     * @param playerId the player whose catches to reset
     */
    public void resetCatches(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        catches.remove(playerId);
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
}
