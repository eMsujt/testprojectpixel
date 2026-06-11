package com.skyblock.islands;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Location;

/**
 * Registry of all loaded islands.
 *
 * <p>Islands are indexed by island id and by owner. All access goes through
 * {@code synchronized} methods, so the manager is safe to use from
 * concurrent contexts; the {@link Island} instances it hands out are not.</p>
 */
public final class IslandManager {

    private final Map<UUID, Island> islandsById = new HashMap<>();
    private final Map<UUID, UUID> islandIdByOwner = new HashMap<>();

    /**
     * Creates and registers a new island for the given owner.
     *
     * @param owner         player UUID of the island owner
     * @param spawnLocation initial spawn location of the island
     * @return the newly created island
     * @throws IllegalStateException if the owner already has an island
     */
    public synchronized Island createIsland(UUID owner, Location spawnLocation) {
        Objects.requireNonNull(owner, "owner");
        if (islandIdByOwner.containsKey(owner)) {
            throw new IllegalStateException("Player " + owner + " already owns an island");
        }
        UUID islandId = UUID.randomUUID();
        Island island = new Island(islandId, owner, spawnLocation);
        islandsById.put(islandId, island);
        islandIdByOwner.put(owner, islandId);
        return island;
    }

    /**
     * Looks up an island by its id.
     *
     * @param islandId the island id
     * @return the island, or empty if none is registered under that id
     */
    public synchronized Optional<Island> getIsland(UUID islandId) {
        return Optional.ofNullable(islandsById.get(islandId));
    }

    /**
     * Looks up the island owned by the given player.
     *
     * @param owner the owner's player UUID
     * @return the island, or empty if the player owns no island
     */
    public synchronized Optional<Island> getIslandByOwner(UUID owner) {
        return Optional.ofNullable(islandIdByOwner.get(owner)).map(islandsById::get);
    }

    /**
     * Removes an island from the registry.
     *
     * @param islandId id of the island to remove
     * @return {@code true} if an island was registered under that id
     */
    public synchronized boolean deleteIsland(UUID islandId) {
        Island removed = islandsById.remove(islandId);
        if (removed == null) {
            return false;
        }
        islandIdByOwner.remove(removed.getOwner());
        return true;
    }
}
