package com.skyblock.core.kuudra;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Kuudra state: key counts and completion
 * counts per tier.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class KuudraManager {

    /** Kuudra tiers, in ascending difficulty order. */
    public enum KuudraTier {
        BASIC,
        HOT,
        BURNING,
        FIERY,
        INFERNAL
    }

    private static final KuudraManager INSTANCE = new KuudraManager();

    /** Per-player Kuudra key counts indexed by tier ordinal. */
    private final Map<UUID, int[]> playerKeys = new HashMap<>();

    /** Per-player Kuudra completion counts indexed by tier ordinal. */
    private final Map<UUID, int[]> playerCompletions = new HashMap<>();

    private KuudraManager() {
    }

    /**
     * Returns the single shared {@code KuudraManager} instance.
     *
     * @return the singleton instance
     */
    public static KuudraManager getInstance() {
        return INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Keys
    // -------------------------------------------------------------------------

    /**
     * Returns the number of Kuudra keys the player has for the given tier.
     *
     * @param playerId the player to look up
     * @param tier     the Kuudra tier
     * @return the key count, {@code 0} if not set
     */
    public int getKeys(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] keys = playerKeys.get(playerId);
        return keys == null ? 0 : keys[tier.ordinal()];
    }

    /**
     * Adds Kuudra keys for the given tier (result clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param amount   the number of keys to add (may be negative)
     * @return the new key count
     */
    public int addKeys(UUID playerId, KuudraTier tier, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] keys = playerKeys.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        keys[tier.ordinal()] = Math.max(0, keys[tier.ordinal()] + amount);
        return keys[tier.ordinal()];
    }

    /**
     * Sets the Kuudra key count for the given tier.
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param count    the new count (clamped to {@code >= 0})
     */
    public void setKeys(UUID playerId, KuudraTier tier, int count) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] keys = playerKeys.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        keys[tier.ordinal()] = Math.max(0, count);
    }

    // -------------------------------------------------------------------------
    // Completions
    // -------------------------------------------------------------------------

    /**
     * Returns the number of times the player has completed the given Kuudra tier.
     *
     * @param playerId the player to look up
     * @param tier     the Kuudra tier
     * @return the completion count, {@code 0} if not set
     */
    public int getCompletions(UUID playerId, KuudraTier tier) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] completions = playerCompletions.get(playerId);
        return completions == null ? 0 : completions[tier.ordinal()];
    }

    /**
     * Adds completions for the given tier (result clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param amount   the number of completions to add (may be negative)
     * @return the new completion count
     */
    public int addCompletions(UUID playerId, KuudraTier tier, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] completions = playerCompletions.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        completions[tier.ordinal()] = Math.max(0, completions[tier.ordinal()] + amount);
        return completions[tier.ordinal()];
    }

    /**
     * Sets the completion count for the given tier.
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param count    the new count (clamped to {@code >= 0})
     */
    public void setCompletions(UUID playerId, KuudraTier tier, int count) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] completions = playerCompletions.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        completions[tier.ordinal()] = Math.max(0, count);
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Removes all Kuudra data for the given player (e.g. on quit).
     *
     * @param playerId the player to remove
     * @return {@code true} if the player had data, {@code false} otherwise
     */
    public boolean remove(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        boolean had = playerKeys.remove(playerId) != null;
        had |= playerCompletions.remove(playerId) != null;
        return had;
    }
}
