package com.skyblock.core.magic;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton registry of fairy soul locations and per-player collection state.
 *
 * <p>All known {@link FairySoul} spawn locations are registered up front;
 * collected souls are tracked per player and keyed by
 * {@link FairySoul#locationKey()}. Not thread-safe beyond the
 * {@link ConcurrentHashMap} used for the per-player sets; mutate from the
 * server main thread only.</p>
 */
public final class FairySoulManager {

    /**
     * Immutable descriptor for a single fairy soul spawn point.
     *
     * @param locationKey unique string identifier for this soul's position
     * @param x           world X coordinate
     * @param y           world Y coordinate
     * @param z           world Z coordinate
     */
    public record FairySoul(String locationKey, double x, double y, double z) {

        /** Validates that {@code locationKey} is non-null and non-blank. */
        public FairySoul {
            Objects.requireNonNull(locationKey, "locationKey");
            if (locationKey.isBlank()) {
                throw new IllegalArgumentException("locationKey must not be blank");
            }
        }
    }

    private static final FairySoulManager INSTANCE = new FairySoulManager();

    private final Map<String, FairySoul> souls = new HashMap<>();
    private final ConcurrentHashMap<UUID, Set<String>> collected = new ConcurrentHashMap<>();

    private FairySoulManager() {
    }

    /**
     * Returns the single shared {@code FairySoulManager} instance.
     *
     * @return the singleton instance
     */
    public static FairySoulManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a fairy soul spawn location.
     *
     * @param soul the soul to register
     * @throws IllegalStateException if a soul with the same key is already registered
     */
    public void registerSoul(FairySoul soul) {
        Objects.requireNonNull(soul, "soul");
        if (souls.containsKey(soul.locationKey())) {
            throw new IllegalStateException(
                    "FairySoul already registered for key: " + soul.locationKey());
        }
        souls.put(soul.locationKey(), soul);
    }

    /**
     * Returns the fairy soul registered under the given key, if any.
     *
     * @param locationKey the soul's unique key
     * @return the registered soul, or empty
     */
    public Optional<FairySoul> getSoul(String locationKey) {
        Objects.requireNonNull(locationKey, "locationKey");
        return Optional.ofNullable(souls.get(locationKey));
    }

    /**
     * Returns an unmodifiable view of all registered fairy souls, keyed by
     * location key.
     *
     * @return all registered souls
     */
    public Map<String, FairySoul> getAllSouls() {
        return Collections.unmodifiableMap(souls);
    }

    /**
     * Marks a fairy soul as collected for the given player.
     *
     * @param playerId    the player's UUID
     * @param locationKey the soul's unique key
     * @return {@code true} if this was the first time the player collected this soul
     * @throws IllegalArgumentException if no soul is registered for {@code locationKey}
     */
    public boolean collectSoul(UUID playerId, String locationKey) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(locationKey, "locationKey");
        if (!souls.containsKey(locationKey)) {
            throw new IllegalArgumentException("No FairySoul registered for key: " + locationKey);
        }
        Set<String> playerCollected =
                collected.computeIfAbsent(playerId, k -> new HashSet<>());
        return playerCollected.add(locationKey);
    }

    /**
     * Returns whether a player has already collected a specific fairy soul.
     *
     * @param playerId    the player's UUID
     * @param locationKey the soul's unique key
     * @return {@code true} if the player collected this soul
     */
    public boolean hasCollected(UUID playerId, String locationKey) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(locationKey, "locationKey");
        Set<String> playerCollected = collected.get(playerId);
        return playerCollected != null && playerCollected.contains(locationKey);
    }

    /**
     * Returns the number of fairy souls the given player has collected.
     *
     * @param playerId the player's UUID
     * @return the collected count, 0 if the player has collected none
     */
    public int getCollectedCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<String> playerCollected = collected.get(playerId);
        return playerCollected == null ? 0 : playerCollected.size();
    }

    /**
     * Returns an unmodifiable view of the location keys the player has collected.
     *
     * @param playerId the player's UUID
     * @return the set of collected soul keys, empty if none
     */
    public Set<String> getCollected(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Set<String> playerCollected = collected.get(playerId);
        return playerCollected == null ? Set.of() : Collections.unmodifiableSet(playerCollected);
    }

    /**
     * Clears all collection data for the given player.
     *
     * @param playerId the player's UUID
     */
    public void resetPlayer(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        collected.remove(playerId);
    }
}
