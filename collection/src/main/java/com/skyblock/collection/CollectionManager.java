package com.skyblock.collection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks per-player collection progress: how many items of each collection a
 * player has gathered and the tiers unlocked from those totals.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class CollectionManager {

    /** The highest tier a collection can reach. */
    public static final int MAX_TIER = 9;

    /** Items required to unlock each tier, indexed by tier - 1. */
    private static final long[] ITEMS_PER_TIER = {
            50, 100, 250, 1_000, 2_500, 10_000, 25_000, 50_000, 100_000
    };

    private final Map<UUID, Map<String, Long>> collected = new HashMap<>();

    /**
     * Adds items to the player's progress for the given collection, e.g. after
     * mining ore or harvesting crops.
     *
     * @param playerId     the player gaining progress
     * @param collectionId the collection being progressed, e.g. {@code "COBBLESTONE"}
     * @param amount       the number of items to add, must not be negative
     * @return the player's total for the collection after the addition
     * @throws IllegalArgumentException if {@code amount} is negative
     * @throws NullPointerException if {@code playerId} or {@code collectionId} is {@code null}
     */
    public long addItems(UUID playerId, String collectionId, long amount) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(collectionId, "collectionId");
        if (amount < 0) {
            throw new IllegalArgumentException("amount must not be negative, got " + amount);
        }
        Map<String, Long> totals = collected.computeIfAbsent(playerId, id -> new HashMap<>());
        long total = totals.getOrDefault(collectionId, 0L) + amount;
        totals.put(collectionId, total);
        return total;
    }

    /**
     * Returns how many items the player has gathered for the given collection.
     *
     * @param playerId     the player to look up
     * @param collectionId the collection to look up
     * @return the total items gathered, {@code 0} if the player has none
     */
    public long getItems(UUID playerId, String collectionId) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(collectionId, "collectionId");
        return collected.getOrDefault(playerId, Map.of()).getOrDefault(collectionId, 0L);
    }

    /**
     * Returns the tier the player has unlocked for the given collection.
     *
     * @param playerId     the player to look up
     * @param collectionId the collection to look up
     * @return the tier between {@code 0} and {@link #MAX_TIER}
     */
    public int getTier(UUID playerId, String collectionId) {
        long items = getItems(playerId, collectionId);
        int tier = 0;
        while (tier < MAX_TIER && items >= ITEMS_PER_TIER[tier]) {
            tier++;
        }
        return tier;
    }

    /**
     * Returns how many more items the player needs to unlock the next tier of
     * the given collection.
     *
     * @param playerId     the player to look up
     * @param collectionId the collection to look up
     * @return the missing items, or {@code 0} if the collection is at {@link #MAX_TIER}
     */
    public long getItemsToNextTier(UUID playerId, String collectionId) {
        int tier = getTier(playerId, collectionId);
        if (tier >= MAX_TIER) {
            return 0;
        }
        return ITEMS_PER_TIER[tier] - getItems(playerId, collectionId);
    }

    /**
     * Resets all of the player's collection progress back to zero.
     *
     * @param playerId the player to reset
     * @return {@code true} if the player had progress to reset, {@code false} otherwise
     */
    public boolean reset(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return collected.remove(playerId) != null;
    }
}
