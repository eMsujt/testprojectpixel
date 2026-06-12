package com.skyblock.core.warps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Singleton registry of warp destinations and per-player unlock state.
 *
 * <p>Predefined {@link WarpPoint} values cover the standard SkyBlock
 * locations. Custom warps may be registered at runtime. Unlock state is
 * tracked per player; {@link WarpPoint#HUB} is considered always unlocked.</p>
 */
public final class WarpManager {

    /**
     * Predefined warp destinations available on the server.
     */
    public enum WarpPoint {
        HUB,
        BAZAAR,
        AUCTION_HOUSE,
        BANK,
        FARM,
        MINE,
        FOREST,
        FISHING_VILLAGE,
        DUNGEON_HUB,
        SPIDER_DEN,
        BLAZING_FORTRESS,
        END_ISLAND
    }

    /**
     * Immutable descriptor for a single warp destination.
     *
     * @param name        display name shown to players
     * @param worldName   target world name
     * @param x           target X coordinate
     * @param y           target Y coordinate
     * @param z           target Z coordinate
     * @param yaw         target yaw angle
     * @param pitch       target pitch angle
     */
    public record WarpDestination(
            String name,
            String worldName,
            double x,
            double y,
            double z,
            float yaw,
            float pitch) {

        public WarpDestination {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(worldName, "worldName");
            if (name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
            if (worldName.isBlank()) {
                throw new IllegalArgumentException("worldName must not be blank");
            }
        }
    }

    private static final WarpManager INSTANCE = new WarpManager();

    private final Map<String, WarpDestination> warps = new HashMap<>();
    private final Map<UUID, java.util.Set<String>> unlockedWarps = new java.util.concurrent.ConcurrentHashMap<>();

    private WarpManager() {
    }

    /**
     * Returns the single shared {@code WarpManager} instance.
     *
     * @return the singleton instance
     */
    public static WarpManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a warp destination under the given key.
     *
     * @param key         unique identifier for this warp (e.g. {@code WarpPoint.HUB.name()})
     * @param destination the destination to register
     * @throws IllegalStateException if a warp with the same key is already registered
     */
    public void registerWarp(String key, WarpDestination destination) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(destination, "destination");
        if (warps.containsKey(key)) {
            throw new IllegalStateException("Warp already registered for key: " + key);
        }
        warps.put(key, destination);
    }

    /**
     * Registers a predefined {@link WarpPoint} destination.
     *
     * @param point       the predefined warp point
     * @param destination the destination to associate with it
     */
    public void registerWarp(WarpPoint point, WarpDestination destination) {
        Objects.requireNonNull(point, "point");
        registerWarp(point.name(), destination);
    }

    /**
     * Returns the warp destination registered under the given key, if any.
     *
     * @param key the warp's unique key
     * @return the registered destination, or empty
     */
    public Optional<WarpDestination> getWarp(String key) {
        Objects.requireNonNull(key, "key");
        return Optional.ofNullable(warps.get(key));
    }

    /**
     * Returns the destination for a predefined {@link WarpPoint}, if registered.
     *
     * @param point the predefined warp point
     * @return the registered destination, or empty
     */
    public Optional<WarpDestination> getWarp(WarpPoint point) {
        Objects.requireNonNull(point, "point");
        return getWarp(point.name());
    }

    /**
     * Returns an unmodifiable view of all registered warps, keyed by their string key.
     *
     * @return all registered warps
     */
    public Map<String, WarpDestination> getAllWarps() {
        return Collections.unmodifiableMap(warps);
    }

    /**
     * Unlocks a warp for the given player.
     *
     * @param playerId the player's UUID
     * @param key      the warp key to unlock
     * @return {@code true} if this was the first time the player unlocked this warp
     * @throws IllegalArgumentException if no warp is registered for {@code key}
     */
    public boolean unlockWarp(UUID playerId, String key) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(key, "key");
        if (!warps.containsKey(key)) {
            throw new IllegalArgumentException("No warp registered for key: " + key);
        }
        java.util.Set<String> playerWarps =
                unlockedWarps.computeIfAbsent(playerId, k -> new java.util.HashSet<>());
        return playerWarps.add(key);
    }

    /**
     * Unlocks a predefined {@link WarpPoint} for the given player.
     *
     * @param playerId the player's UUID
     * @param point    the warp point to unlock
     * @return {@code true} if this was the first time the player unlocked this warp
     */
    public boolean unlockWarp(UUID playerId, WarpPoint point) {
        Objects.requireNonNull(point, "point");
        return unlockWarp(playerId, point.name());
    }

    /**
     * Returns whether a player has access to a specific warp.
     *
     * <p>{@link WarpPoint#HUB} is always accessible regardless of unlock state.</p>
     *
     * @param playerId the player's UUID
     * @param key      the warp key to check
     * @return {@code true} if the player may use this warp
     */
    public boolean hasWarp(UUID playerId, String key) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(key, "key");
        if (WarpPoint.HUB.name().equals(key)) {
            return true;
        }
        java.util.Set<String> playerWarps = unlockedWarps.get(playerId);
        return playerWarps != null && playerWarps.contains(key);
    }

    /**
     * Returns whether a player has access to a predefined {@link WarpPoint}.
     *
     * @param playerId the player's UUID
     * @param point    the warp point to check
     * @return {@code true} if the player may use this warp
     */
    public boolean hasWarp(UUID playerId, WarpPoint point) {
        Objects.requireNonNull(point, "point");
        return hasWarp(playerId, point.name());
    }

    /**
     * Returns an unmodifiable view of all warp keys the player has unlocked.
     *
     * @param playerId the player's UUID
     * @return the set of unlocked warp keys, empty if none
     */
    public java.util.Set<String> getUnlockedWarps(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        java.util.Set<String> playerWarps = unlockedWarps.get(playerId);
        return playerWarps == null ? java.util.Set.of() : Collections.unmodifiableSet(playerWarps);
    }

    /**
     * Clears all unlock data for the given player.
     *
     * @param playerId the player's UUID
     */
    public void resetPlayer(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        unlockedWarps.remove(playerId);
    }
}
