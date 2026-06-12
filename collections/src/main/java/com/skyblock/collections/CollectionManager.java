package com.skyblock.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Singleton tracking each player's collection progress per collection id.
 *
 * <p>Progress is stored per player as a map of collection id, e.g.
 * {@code "COBBLESTONE"}, to the total amount collected. Players start with
 * no progress; entries are created lazily on first access. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class CollectionManager {

    private static final CollectionManager INSTANCE = new CollectionManager();

    private final Map<UUID, Map<String, Long>> playerCollections = new HashMap<>();

    private CollectionManager() {
    }

    /**
     * Returns the single shared {@code CollectionManager} instance.
     *
     * @return the singleton instance
     */
    public static CollectionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds collected items to the player's progress for a collection.
     *
     * @param playerId     the player's UUID
     * @param collectionId the collection identifier, e.g. {@code "COBBLESTONE"},
     *                     must not be null or blank
     * @param amount       the number of items collected, must be positive
     * @return the player's total for the collection after the change
     * @throws IllegalArgumentException if {@code collectionId} is null or
     *         blank, or {@code amount} is not positive
     */
    public long addProgress(UUID playerId, String collectionId, long amount) {
        if (collectionId == null || collectionId.isBlank()) {
            throw new IllegalArgumentException("collectionId must not be null or blank");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);
        }
        return playerCollections.computeIfAbsent(playerId, id -> new HashMap<>())
                .merge(collectionId, amount, Long::sum);
    }

    /**
     * Returns the player's collected total for a collection.
     *
     * @param playerId     the player's UUID
     * @param collectionId the collection identifier to look up
     * @return the collected total, or {@code 0} if never collected
     */
    public long getProgress(UUID playerId, String collectionId) {
        Map<String, Long> progress = playerCollections.get(playerId);
        if (progress == null) {
            return 0L;
        }
        return progress.getOrDefault(collectionId, 0L);
    }

    /**
     * Returns an immutable snapshot of all of the player's collection totals.
     *
     * @param playerId the player's UUID
     * @return the player's totals by collection id; empty if none
     */
    public Map<String, Long> getProgress(UUID playerId) {
        Map<String, Long> progress = playerCollections.get(playerId);
        return progress == null
                ? Map.of()
                : Collections.unmodifiableMap(new HashMap<>(progress));
    }

    /**
     * Removes all of the player's collection progress.
     *
     * @param playerId the player's UUID
     */
    public void resetProgress(UUID playerId) {
        playerCollections.remove(playerId);
    }
}
