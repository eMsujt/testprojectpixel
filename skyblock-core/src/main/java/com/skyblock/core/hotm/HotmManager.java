package com.skyblock.core.hotm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Heart of the Mountain perk levels.
 *
 * <p>Perk levels are stored as a {@code int[]} indexed by {@link HotmPerk#ordinal()}.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class HotmManager {

    /** Every upgradeable perk in the Heart of the Mountain tree. */
    public enum HotmPerk {
        MINING_SPEED_BOOST(1),
        EFFICIENT_MINER(100),
        QUICK_FORGE(20),
        TITANIUM_INSANIUM(50),
        DAILY_POWDER(100),
        MINING_MADNESS(1),
        SKY_MALL(1),
        GOBLIN_KILLER(1),
        STAR_POWDER(1),
        MOLE(200),
        PROFESSIONAL(140),
        LONESOME_MINER(45),
        GREAT_EXPLORER(20),
        FORTUNATE(20),
        PICKOBULUS(1),
        MINING_EXPERIENCE_BOOST(100);

        /** Maximum level for this perk. */
        public final int maxLevel;

        HotmPerk(int maxLevel) {
            this.maxLevel = maxLevel;
        }
    }

    private static final HotmManager INSTANCE = new HotmManager();

    /** Per-player perk levels; absent entries default to all-zeros. */
    private final Map<UUID, int[]> playerPerks = new HashMap<>();

    private HotmManager() {
    }

    /**
     * Returns the single shared {@code HotmManager} instance.
     *
     * @return the singleton instance
     */
    public static HotmManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the current level of a perk for the given player.
     *
     * @param playerId the player to look up
     * @param perk     the perk to query
     * @return the current level, {@code 0} if not unlocked
     */
    public int getLevel(UUID playerId, HotmPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        int[] levels = playerPerks.get(playerId);
        return levels == null ? 0 : levels[perk.ordinal()];
    }

    /**
     * Sets the level of a perk for the given player.
     *
     * @param playerId the player to update
     * @param perk     the perk to set
     * @param level    the new level (clamped to {@code [0, perk.maxLevel]})
     * @throws IllegalArgumentException if {@code level} is negative
     */
    public void setLevel(UUID playerId, HotmPerk perk, int level) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        if (level < 0) {
            throw new IllegalArgumentException("level must not be negative");
        }
        int clamped = Math.min(level, perk.maxLevel);
        int[] levels = playerPerks.computeIfAbsent(playerId, id -> new int[HotmPerk.values().length]);
        levels[perk.ordinal()] = clamped;
    }

    /**
     * Upgrades a perk by one level, up to its maximum.
     *
     * @param playerId the player to upgrade
     * @param perk     the perk to upgrade
     * @return the new level after the upgrade, or {@code -1} if already at max
     */
    public int upgrade(UUID playerId, HotmPerk perk) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(perk, "perk");
        int current = getLevel(playerId, perk);
        if (current >= perk.maxLevel) {
            return -1;
        }
        int[] levels = playerPerks.computeIfAbsent(playerId, id -> new int[HotmPerk.values().length]);
        levels[perk.ordinal()] = current + 1;
        return current + 1;
    }

    /**
     * Returns a copy of all perk levels for the given player.
     *
     * @param playerId the player to look up
     * @return array of perk levels indexed by {@link HotmPerk#ordinal()}, all-zeros if no data
     */
    public int[] getAllLevels(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int[] levels = playerPerks.get(playerId);
        if (levels == null) {
            return new int[HotmPerk.values().length];
        }
        return Arrays.copyOf(levels, levels.length);
    }

    /**
     * Resets all perk levels for the given player to zero.
     *
     * @param playerId the player to reset
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        int[] levels = playerPerks.get(playerId);
        if (levels != null) {
            Arrays.fill(levels, 0);
        }
    }

    /**
     * Removes all HOTM data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return playerPerks.remove(playerId) != null;
    }
}
