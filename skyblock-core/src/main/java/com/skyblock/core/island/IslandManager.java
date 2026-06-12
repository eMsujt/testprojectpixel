package com.skyblock.core.island;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Singleton registry of player SkyBlock islands.
 *
 * <p>Tracks island ownership and membership. Each player may own at most
 * one island. Island data is stored in-memory; persistence is the
 * responsibility of the caller.</p>
 */
public final class IslandManager {

    /**
     * Immutable snapshot of island data.
     *
     * @param islandId    the island's unique identifier
     * @param ownerUUID   UUID of the owning player
     * @param worldName   name of the world this island lives in
     * @param centerX     X coordinate of the island centre
     * @param centerY     Y coordinate of the island centre
     * @param centerZ     Z coordinate of the island centre
     * @param level       island upgrade level (≥ 1)
     */
    public record IslandData(
            UUID islandId,
            UUID ownerUUID,
            String worldName,
            int centerX,
            int centerY,
            int centerZ,
            int level) {

        public IslandData {
            Objects.requireNonNull(islandId, "islandId");
            Objects.requireNonNull(ownerUUID, "ownerUUID");
            Objects.requireNonNull(worldName, "worldName");
            if (worldName.isBlank()) {
                throw new IllegalArgumentException("worldName must not be blank");
            }
            if (level < 1) {
                throw new IllegalArgumentException("level must be >= 1");
            }
        }
    }

    private static final IslandManager INSTANCE = new IslandManager();

    /** islandId → IslandData */
    private final Map<UUID, IslandData> islandsById = new HashMap<>();

    /** ownerUUID → islandId (one island per player) */
    private final Map<UUID, UUID> islandByOwner = new HashMap<>();

    private IslandManager() {
    }

    /**
     * Returns the single shared {@code IslandManager} instance.
     *
     * @return the singleton instance
     */
    public static IslandManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new island for the given player.
     *
     * <p>The island is placed at {@code (0, 100, 0)} in a world named
     * {@code "island_" + player.getUniqueId()} at level 1.
     * The exact world/coordinates should be updated by the caller once the
     * world is generated.</p>
     *
     * @param player the player who will own the island
     * @return the newly created {@link IslandData}
     * @throws IllegalStateException if the player already owns an island
     */
    public IslandData createIsland(Player player) {
        Objects.requireNonNull(player, "player");
        UUID ownerId = player.getUniqueId();
        if (islandByOwner.containsKey(ownerId)) {
            throw new IllegalStateException("Player already owns an island: " + player.getName());
        }
        UUID islandId = UUID.randomUUID();
        String worldName = "island_" + ownerId;
        IslandData data = new IslandData(islandId, ownerId, worldName, 0, 100, 0, 1);
        islandsById.put(islandId, data);
        islandByOwner.put(ownerId, islandId);
        return data;
    }

    /**
     * Removes the island with the given ID and all associated ownership records.
     *
     * @param islandId the ID of the island to delete
     * @return {@code true} if an island was found and removed
     */
    public boolean deleteIsland(UUID islandId) {
        Objects.requireNonNull(islandId, "islandId");
        IslandData data = islandsById.remove(islandId);
        if (data == null) {
            return false;
        }
        islandByOwner.remove(data.ownerUUID());
        return true;
    }

    /**
     * Returns the island owned by the given player, if one exists.
     *
     * @param playerId the player's UUID
     * @return the player's island, or empty
     */
    public Optional<IslandData> getIsland(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        UUID islandId = islandByOwner.get(playerId);
        if (islandId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(islandsById.get(islandId));
    }

    /**
     * Returns the island with the given ID, if one exists.
     *
     * @param islandId the island's unique ID
     * @return the island, or empty
     */
    public Optional<IslandData> getIslandById(UUID islandId) {
        Objects.requireNonNull(islandId, "islandId");
        return Optional.ofNullable(islandsById.get(islandId));
    }

    /**
     * Returns whether the given player currently owns an island.
     *
     * @param playerId the player's UUID
     * @return {@code true} if the player has an island
     */
    public boolean hasIsland(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return islandByOwner.containsKey(playerId);
    }

    /**
     * Replaces the stored data for an existing island (e.g. after a level-up
     * or coordinate update).
     *
     * @param data the updated island data; its {@code islandId} must already
     *             be registered
     * @throws IllegalArgumentException if no island with that ID is registered
     */
    public void updateIsland(IslandData data) {
        Objects.requireNonNull(data, "data");
        if (!islandsById.containsKey(data.islandId())) {
            throw new IllegalArgumentException("No island registered with id: " + data.islandId());
        }
        islandsById.put(data.islandId(), data);
    }

    /**
     * Returns an unmodifiable view of all registered islands.
     *
     * @return all islands, keyed by island ID
     */
    public Map<UUID, IslandData> getAllIslands() {
        return Collections.unmodifiableMap(islandsById);
    }
}
