package com.skyblock.core.fishing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * Singleton manager for SkyBlock trophy fishing catches.
 *
 * <p>Tracks per-player catch counts keyed by {@link TrophyFish} rarity tier.
 * Each trophy fish has a minimum fishing level and a base drop chance.
 * Call {@link #rollTrophyFish(int)} to determine whether a trophy fish
 * drops and which one.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class TrophyFishManager {

    /** Trophy fish variants obtainable through SkyBlock trophy fishing. */
    public enum TrophyFish {
        SULPHUR_SKITTER(1, 0.30),
        OBFUSCATED_FISH_1(1, 0.25),
        OBFUSCATED_FISH_2(10, 0.15),
        OBFUSCATED_FISH_3(20, 0.08),
        STEAMING_HOT_FLOUNDER(5, 0.20),
        GUSHER(5, 0.18),
        BLOBFISH(10, 0.12),
        SLUGFISH(10, 0.10),
        FLYFISH(15, 0.07),
        LAVA_HORSE(20, 0.06),
        MANA_RAY(20, 0.05),
        VOLCANIC_STONEFISH(25, 0.04),
        VANILLE(25, 0.03),
        SKELETON_FISH(30, 0.02);

        /** Minimum fishing level required for this trophy fish to drop. */
        public final int minLevel;
        /** Base drop chance (0–1) when the player meets the level requirement. */
        public final double dropChance;

        TrophyFish(int minLevel, double dropChance) {
            this.minLevel = minLevel;
            this.dropChance = dropChance;
        }
    }

    /** Overall chance (0–1) that any trophy fish attempt produces a catch. */
    public static final double BASE_TROPHY_CHANCE = 0.05;

    private static final TrophyFishManager INSTANCE = new TrophyFishManager();

    /** Per-player trophy fish catch counts. */
    private final Map<UUID, Map<TrophyFish, Integer>> catches = new HashMap<>();
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
    public void addCatch(UUID playerId, TrophyFish fish) {
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
    public Map<TrophyFish, Integer> getCatches(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<TrophyFish, Integer> playerCatches = catches.get(playerId);
        if (playerCatches == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(playerCatches);
    }

    /**
     * Removes all cached data for the player (call on quit to free memory).
     *
     * @param playerId the player whose data to remove
     */
    public void remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        catches.remove(playerId);
    }

    /**
     * Rolls whether a trophy fish drops for the given fishing level.
     * Returns the caught {@link TrophyFish}, or {@code null} if no trophy
     * fish dropped this catch.
     *
     * <p>First checks the overall {@link #BASE_TROPHY_CHANCE}, then selects
     * a random eligible trophy fish weighted by its {@code dropChance}.</p>
     *
     * @param level the player's fishing level
     * @return the caught trophy fish, or {@code null}
     */
    public TrophyFish rollTrophyFish(int level) {
        if (random.nextDouble() >= BASE_TROPHY_CHANCE) {
            return null;
        }

        double totalWeight = 0.0;
        for (TrophyFish fish : TrophyFish.values()) {
            if (fish.minLevel <= level) {
                totalWeight += fish.dropChance;
            }
        }
        if (totalWeight == 0.0) {
            return null;
        }

        double roll = random.nextDouble() * totalWeight;
        double cumulative = 0.0;
        for (TrophyFish fish : TrophyFish.values()) {
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
