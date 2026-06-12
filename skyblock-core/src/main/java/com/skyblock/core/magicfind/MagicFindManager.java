package com.skyblock.core.magicfind;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton manager for the SkyBlock Magic Find stat.
 *
 * <p>Magic Find increases the chance of rare drops from mobs. Each point of
 * Magic Find adds {@value #BONUS_PER_POINT}% to the drop-chance bonus applied
 * by {@link MagicFindListener}.</p>
 *
 * <p>Not thread-safe; synchronize externally if needed.</p>
 */
public final class MagicFindManager {

    /** Drop-chance bonus percentage contributed by a single Magic Find point. */
    public static final double BONUS_PER_POINT = 0.1;

    /** Maximum Magic Find value a player can accumulate. */
    public static final int MAX_MAGIC_FIND = 900;

    private static final MagicFindManager INSTANCE = new MagicFindManager();

    /** Per-player Magic Find values. */
    private final Map<UUID, Integer> magicFind = new HashMap<>();

    private MagicFindManager() {}

    /**
     * Returns the single shared {@code MagicFindManager} instance.
     *
     * @return the singleton instance
     */
    public static MagicFindManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the player's current Magic Find stat.
     *
     * @param playerId the player's UUID
     * @return Magic Find value, zero if none recorded
     */
    public int getMagicFind(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return magicFind.getOrDefault(playerId, 0);
    }

    /**
     * Sets the player's Magic Find to the given value, clamped to
     * [{@code 0}, {@value #MAX_MAGIC_FIND}].
     *
     * @param playerId the player's UUID
     * @param value    the new Magic Find value
     */
    public void setMagicFind(UUID playerId, int value) {
        Objects.requireNonNull(playerId, "playerId");
        magicFind.put(playerId, Math.max(0, Math.min(MAX_MAGIC_FIND, value)));
    }

    /**
     * Adds {@code amount} to the player's Magic Find, clamped to
     * [{@code 0}, {@value #MAX_MAGIC_FIND}].
     *
     * @param playerId the player's UUID
     * @param amount   amount to add (may be negative)
     */
    public void addMagicFind(UUID playerId, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        int current = getMagicFind(playerId);
        setMagicFind(playerId, current + amount);
    }

    /**
     * Returns the bonus drop-chance multiplier for the player's Magic Find.
     *
     * <p>A value of {@code 1.0} means no bonus; {@code 1.5} means 50% more
     * loot rolls.</p>
     *
     * @param playerId the player's UUID
     * @return drop-chance multiplier ≥ 1.0
     */
    public double getDropMultiplier(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return 1.0 + getMagicFind(playerId) * BONUS_PER_POINT / 100.0;
    }

    /**
     * Removes all Magic Find data for the given player. Call on player quit to
     * avoid unbounded map growth.
     *
     * @param playerId the player's UUID
     */
    public void remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        magicFind.remove(playerId);
    }
}
