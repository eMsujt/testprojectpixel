package com.skyblock.islands;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;

/**
 * Data holder for a single player island.
 *
 * <p>Tracks the island's identity, ownership, member roster, upgrade level
 * and spawn point. Instances are not thread-safe; access them from the
 * server main thread or guard them externally (e.g. via
 * {@link IslandManager}).</p>
 */
public final class Island {

    private final UUID islandId;
    private final UUID owner;
    private final Set<UUID> members = new HashSet<>();
    private int level;
    private Location spawnLocation;

    /**
     * Creates a new island at level 1 with no members.
     *
     * @param islandId      unique identifier of the island
     * @param owner         player UUID of the island owner
     * @param spawnLocation location players are sent to when visiting
     */
    public Island(UUID islandId, UUID owner, Location spawnLocation) {
        this.islandId = Objects.requireNonNull(islandId, "islandId");
        this.owner = Objects.requireNonNull(owner, "owner");
        this.spawnLocation = Objects.requireNonNull(spawnLocation, "spawnLocation").clone();
        this.level = 1;
    }

    /**
     * Returns the unique identifier of this island.
     *
     * @return the island id
     */
    public UUID getIslandId() {
        return islandId;
    }

    /**
     * Returns the player UUID of the island owner.
     *
     * @return the owner's UUID
     */
    public UUID getOwner() {
        return owner;
    }

    /**
     * Returns an immutable snapshot of the island members. The owner is not
     * included in this set.
     *
     * @return the member UUIDs
     */
    public Set<UUID> getMembers() {
        return Set.copyOf(members);
    }

    /**
     * Adds a player to the member roster.
     *
     * @param playerId the player to add
     * @return {@code true} if the player was not already a member
     */
    public boolean addMember(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return members.add(playerId);
    }

    /**
     * Removes a player from the member roster.
     *
     * @param playerId the player to remove
     * @return {@code true} if the player was a member
     */
    public boolean removeMember(UUID playerId) {
        return members.remove(playerId);
    }

    /**
     * Checks whether a player is the owner or a member of this island.
     *
     * @param playerId the player to check
     * @return {@code true} if the player belongs to this island
     */
    public boolean isMember(UUID playerId) {
        return owner.equals(playerId) || members.contains(playerId);
    }

    /**
     * Returns the island's upgrade level.
     *
     * @return the current level, at least 1
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets the island's upgrade level.
     *
     * @param level the new level, must be at least 1
     * @throws IllegalArgumentException if {@code level} is less than 1
     */
    public void setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("level must be at least 1, got " + level);
        }
        this.level = level;
    }

    /**
     * Returns a copy of the island spawn location.
     *
     * @return the spawn location
     */
    public Location getSpawnLocation() {
        return spawnLocation.clone();
    }

    /**
     * Sets the island spawn location.
     *
     * @param spawnLocation the new spawn location
     */
    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = Objects.requireNonNull(spawnLocation, "spawnLocation").clone();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Island other && islandId.equals(other.islandId);
    }

    @Override
    public int hashCode() {
        return islandId.hashCode();
    }

    @Override
    public String toString() {
        return "Island{islandId=" + islandId + ", owner=" + owner
                + ", members=" + members.size() + ", level=" + level + '}';
    }
}
