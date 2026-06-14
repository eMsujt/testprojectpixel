package com.skyblock.plugin.islands;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of per-player private islands.
 *
 * <p>Holds island records in a {@link Map} keyed by each island's owner,
 * preserving insertion order so islands are listed in the order they were
 * created. A record is added when a player's island is generated and removed
 * if the island is deleted. Not thread-safe; access from the main server
 * thread.</p>
 */
public final class IslandManager {

    /**
     * A single player-owned island.
     *
     * @param owner     the owning player's UUID
     * @param worldName the name of the island's world
     * @param spawnX    the island spawn's x coordinate
     * @param spawnY    the island spawn's y coordinate
     * @param spawnZ    the island spawn's z coordinate
     */
    public record IslandData(UUID owner, String worldName, double spawnX, double spawnY, double spawnZ) {
        public IslandData {
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(worldName, "worldName");
        }
    }

    private static final IslandManager INSTANCE = new IslandManager();

    private final Map<UUID, IslandData> islands = new LinkedHashMap<>();

    private IslandManager() {
    }

    public static IslandManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds an island to the registry, replacing any existing record for the
     * same owner.
     *
     * @param island the island to add
     */
    public void addIsland(IslandData island) {
        Objects.requireNonNull(island, "island");
        islands.put(island.owner(), island);
    }

    /**
     * Returns the island owned by the given player, or {@code null} if absent.
     *
     * @param owner the owning player's UUID
     * @return the island, or {@code null}
     */
    public IslandData getIsland(UUID owner) {
        return islands.get(owner);
    }

    /**
     * Removes the island owned by the given player.
     *
     * @param owner the owning player's UUID
     * @return the removed island, or {@code null} if none existed
     */
    public IslandData removeIsland(UUID owner) {
        return islands.remove(owner);
    }

    /**
     * Returns an unmodifiable view of every island in creation order.
     *
     * @return the registered islands
     */
    public Collection<IslandData> getIslands() {
        return Collections.unmodifiableCollection(islands.values());
    }
}
