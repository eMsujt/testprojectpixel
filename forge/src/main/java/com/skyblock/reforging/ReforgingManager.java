package com.skyblock.reforging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks the reforge applied to each item instance.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class ReforgingManager {

    private final Map<UUID, AppliedReforge> appliedReforges = new HashMap<>();

    /**
     * Applies a reforge to the given item, replacing any existing reforge on it.
     *
     * @param itemId    the item instance to reforge
     * @param reforgeId the identifier of the reforge to apply
     * @param cost      the coins paid for the reforge, must not be negative
     * @return the reforge previously applied to the item, or {@code null} if it had none
     * @throws IllegalArgumentException if {@code cost} is negative
     * @throws NullPointerException if {@code itemId} or {@code reforgeId} is {@code null}
     */
    public AppliedReforge applyReforge(UUID itemId, String reforgeId, long cost) {
        Objects.requireNonNull(itemId, "itemId");
        Objects.requireNonNull(reforgeId, "reforgeId");
        if (cost < 0) {
            throw new IllegalArgumentException("cost must not be negative, got " + cost);
        }
        return appliedReforges.put(itemId, new AppliedReforge(reforgeId, cost));
    }

    /**
     * Returns the reforge applied to the given item, or {@code null} if it has none.
     *
     * @param itemId the item instance to look up
     * @return the applied reforge entry, or {@code null} if the item is not reforged
     */
    public AppliedReforge getReforge(UUID itemId) {
        Objects.requireNonNull(itemId, "itemId");
        return appliedReforges.get(itemId);
    }

    /**
     * Returns whether the given item currently has a reforge applied.
     *
     * @param itemId the item instance to check
     * @return {@code true} if the item is reforged
     */
    public boolean isReforged(UUID itemId) {
        Objects.requireNonNull(itemId, "itemId");
        return appliedReforges.containsKey(itemId);
    }

    /**
     * Removes the reforge from the given item.
     *
     * @param itemId the item instance to strip
     * @return the removed reforge entry, or {@code null} if the item had none
     */
    public AppliedReforge removeReforge(UUID itemId) {
        Objects.requireNonNull(itemId, "itemId");
        return appliedReforges.remove(itemId);
    }

    /**
     * Returns an unmodifiable view of all reforged items, keyed by item instance.
     *
     * @return the applied reforges, empty if no item is reforged
     */
    public Map<UUID, AppliedReforge> getReforges() {
        return Collections.unmodifiableMap(appliedReforges);
    }

    /**
     * A reforge applied to a single item instance.
     */
    public static final class AppliedReforge {

        private final String reforgeId;
        private final long cost;

        AppliedReforge(String reforgeId, long cost) {
            this.reforgeId = reforgeId;
            this.cost = cost;
        }

        /** Returns the identifier of the applied reforge. */
        public String getReforgeId() {
            return reforgeId;
        }

        /** Returns the coins paid for this reforge. */
        public long getCost() {
            return cost;
        }
    }
}
