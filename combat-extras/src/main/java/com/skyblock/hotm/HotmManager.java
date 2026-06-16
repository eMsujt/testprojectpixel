package com.skyblock.hotm;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks per-player Heart of the Mountain perk levels.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class HotmManager {

    private final Map<UUID, EnumMap<HotmPerk, Integer>> perkLevels = new HashMap<>();

    /**
     * Returns the player's level in the given perk, or 0 if not unlocked.
     *
     * @param playerId the player to look up
     * @param perk     the perk to query
     * @return the current perk level
     */
    public int getPerkLevel(UUID playerId, HotmPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        Map<HotmPerk, Integer> perks = perkLevels.get(playerId);
        return perks == null ? 0 : perks.getOrDefault(perk, 0);
    }

    /**
     * Raises the player's level in the given perk by one.
     *
     * @param playerId the player to upgrade
     * @param perk     the perk to upgrade
     * @return the new perk level
     * @throws IllegalStateException if the perk is already at its maximum level
     */
    public int upgradePerk(UUID playerId, HotmPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        int current = getPerkLevel(playerId, perk);
        if (current >= perk.getMaxLevel()) {
            throw new IllegalStateException(
                    perk + " is already at max level " + perk.getMaxLevel());
        }
        int next = current + 1;
        perkLevels.computeIfAbsent(playerId, ignored -> new EnumMap<>(HotmPerk.class))
                .put(perk, next);
        return next;
    }

    /**
     * Resets all of the player's perks back to level 0.
     *
     * @param playerId the player to reset
     */
    public void resetPerks(UUID playerId) {
        perkLevels.remove(playerId);
    }

    /**
     * Returns an unmodifiable view of the player's perk levels.
     *
     * @param playerId the player to look up
     * @return the player's perk levels, empty if none unlocked
     */
    public Map<HotmPerk, Integer> getPerks(UUID playerId) {
        Map<HotmPerk, Integer> perks = perkLevels.get(playerId);
        return perks == null ? Map.of() : Collections.unmodifiableMap(perks);
    }

    /**
     * The perks available in the Heart of the Mountain tree.
     */
    public enum HotmPerk {

        MINING_SPEED("Mining Speed", 50),
        MINING_FORTUNE("Mining Fortune", 50),
        TITANIUM_INSANITY("Titanium Insanity", 50),
        LUCK_OF_THE_CAVE("Luck of the Cave", 45),
        EFFICIENT_MINER("Efficient Miner", 100),
        QUICK_FORGE("Quick Forge", 20),
        MINING_MADNESS("Mining Madness", 1),
        PICKOBULUS("Pickobulus", 3);

        private final String displayName;
        private final int maxLevel;

        HotmPerk(String displayName, int maxLevel) {
            this.displayName = displayName;
            this.maxLevel = maxLevel;
        }

        /** Returns the human-readable name of this perk. */
        public String getDisplayName() {
            return displayName;
        }

        /** Returns the maximum level this perk can be upgraded to. */
        public int getMaxLevel() {
            return maxLevel;
        }
    }
}
