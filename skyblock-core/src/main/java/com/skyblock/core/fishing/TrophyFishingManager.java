package com.skyblock.core.fishing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock trophy fishing.
 *
 * <p>Tracks per-player trophy fish catches keyed by {@link TrophyFish} type.
 * Trophy fish are rare catches unlocked at specific fishing levels.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class TrophyFishingManager {

    /** All trophy fish types obtainable through SkyBlock trophy fishing. */
    public enum TrophyFish {
        MAHI_MAHI(1,  "Mahi Mahi"),
        SULPHUR_SKITTER(1,  "Sulphur Skitter"),
        OBFUSCATED_FISH_1(1,  "Obfuscated Fish 1"),
        OBFUSCATED_FISH_2(10, "Obfuscated Fish 2"),
        OBFUSCATED_FISH_3(20, "Obfuscated Fish 3"),
        STEAMING_HOT_FLOUNDER(5,  "Steaming-Hot Flounder"),
        GUSHER(5,  "Gusher"),
        BLOBFISH(10, "Blobfish"),
        SLUGFISH(10, "Slugfish"),
        FLYFISH(15, "Flyfish"),
        LAVA_HORSE(20, "Lava Horse"),
        MANA_RAY(20, "Mana Ray"),
        VOLCANIC_STONEFISH(25, "Volcanic Stonefish"),
        VANILLE(25, "Vanille"),
        SKELETON_FISH(30, "Skeleton Fish");

        /** Minimum fishing level required for this trophy fish to drop. */
        public final int minLevel;
        /** Human-readable display name. */
        public final String displayName;

        TrophyFish(int minLevel, String displayName) {
            this.minLevel = minLevel;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final TrophyFishingManager INSTANCE = new TrophyFishingManager();

    /** Per-player trophy fish catch counts, keyed by {@link TrophyFish}. */
    private final Map<UUID, Map<TrophyFish, Integer>> catches = new HashMap<>();

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
    public void recordCatch(UUID playerId, TrophyFish fish) {
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
    public int getCatchCount(UUID playerId, TrophyFish fish) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(fish, "fish");
        Map<TrophyFish, Integer> playerCatches = catches.get(playerId);
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
    public Map<TrophyFish, Integer> getAllCatches(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<TrophyFish, Integer> playerCatches = catches.get(playerId);
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
    public TrophyFish[] getAvailableTrophyFish(int level) {
        int count = 0;
        for (TrophyFish fish : TrophyFish.values()) {
            if (fish.minLevel <= level) {
                count++;
            }
        }
        TrophyFish[] result = new TrophyFish[count];
        int i = 0;
        for (TrophyFish fish : TrophyFish.values()) {
            if (fish.minLevel <= level) {
                result[i++] = fish;
            }
        }
        return result;
    }
}
