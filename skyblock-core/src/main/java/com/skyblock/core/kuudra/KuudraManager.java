package com.skyblock.core.kuudra;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's Kuudra key counts and completion counts per tier.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class KuudraManager {

    /** Kuudra tiers, in ascending difficulty order. */
    public enum KuudraTier {
        NORMAL("Normal"),
        HOT("Hot"),
        BURNING("Burning"),
        FIERY("Fiery"),
        INFERNAL("Infernal");

        /** Human-readable display name shown to players. */
        public final String displayName;

        KuudraTier(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
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
    // Key management
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
     * Adds keys for the given tier (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param amount   the amount to add (may be negative)
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
     * Sets the key count for the given tier.
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
    // Completion management
    // -------------------------------------------------------------------------

    /**
     * Returns the number of Kuudra completions the player has for the given tier.
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
     * Adds completions for the given tier (clamped to {@code >= 0}).
     *
     * @param playerId the player to update
     * @param tier     the Kuudra tier
     * @param amount   the amount to add (may be negative)
     * @return the new completion count
     */
    public int addCompletions(UUID playerId, KuudraTier tier, int amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(tier, "tier");
        int[] completions = playerCompletions.computeIfAbsent(playerId, id -> new int[KuudraTier.values().length]);
        completions[tier.ordinal()] = Math.max(0, completions[tier.ordinal()] + amount);
        return completions[tier.ordinal()];
    }

    // -------------------------------------------------------------------------
    // Lifecycle
    // -------------------------------------------------------------------------

    /**
     * Resets all Kuudra data for the given player.
     *
     * @param playerId the player to reset
     */
    public void reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        playerKeys.remove(playerId);
        playerCompletions.remove(playerId);
    }

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
