package com.skyblock.core.island;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton manager that owns all {@link SkyBlockIsland} instances, keyed by
 * the island owner's UUID.
 *
 * <p>The registry is a {@link ConcurrentHashMap}; individual {@code SkyBlockIsland}
 * objects are not thread-safe and should only be mutated from the server main
 * thread.</p>
 */
public final class IslandManager {

    /**
     * Immutable-by-contract record representing a single SkyBlock island.
     *
     * <p>The {@code members} list is mutable so callers may add or remove
     * members after creation, but the list reference itself is fixed.</p>
     */
    public static final class SkyBlockIsland {

        private final UUID owner;
        private final List<UUID> members;

        /**
         * Creates a new island owned by {@code owner} with no members.
         *
         * @param owner the island owner's UUID
         */
        public SkyBlockIsland(UUID owner) {
            this.owner = Objects.requireNonNull(owner, "owner");
            this.members = new ArrayList<>();
        }

        /**
         * Returns the UUID of the island owner.
         *
         * @return the owner UUID
         */
        public UUID getOwner() {
            return owner;
        }

        /**
         * Returns an unmodifiable view of the current member list.
         *
         * @return the member UUIDs
         */
        public List<UUID> getMembers() {
            return Collections.unmodifiableList(members);
        }

        /**
         * Adds a member to this island.
         *
         * @param player the UUID of the player to add
         * @return {@code true} if the list changed
         */
        public boolean addMember(UUID player) {
            Objects.requireNonNull(player, "player");
            if (members.contains(player)) {
                return false;
            }
            return members.add(player);
        }

        /**
         * Removes a member from this island.
         *
         * @param player the UUID of the player to remove
         * @return {@code true} if the member was present and removed
         */
        public boolean removeMember(UUID player) {
            Objects.requireNonNull(player, "player");
            return members.remove(player);
        }

        /**
         * Returns whether {@code player} is the owner or a member of this island.
         *
         * @param player the UUID to test
         * @return {@code true} if the player belongs to this island
         */
        public boolean isMember(UUID player) {
            Objects.requireNonNull(player, "player");
            return owner.equals(player) || members.contains(player);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SkyBlockIsland other && owner.equals(other.owner);
        }

        @Override
        public int hashCode() {
            return owner.hashCode();
        }

        @Override
        public String toString() {
            return "SkyBlockIsland{owner=" + owner + ", members=" + members.size() + '}';
        }
    }

    private static final IslandManager INSTANCE = new IslandManager();

    private final Map<UUID, SkyBlockIsland> islands = new ConcurrentHashMap<>();

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
     * Creates a new island owned by {@code owner} and registers it.
     *
     * @param owner the owner's UUID
     * @return the newly created island
     * @throws IllegalStateException if an island already exists for {@code owner}
     */
    public SkyBlockIsland createIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        SkyBlockIsland island = new SkyBlockIsland(owner);
        if (islands.putIfAbsent(owner, island) != null) {
            throw new IllegalStateException("Island already exists for owner " + owner);
        }
        return island;
    }

    /**
     * Returns the island owned by {@code owner}, if one exists.
     *
     * @param owner the owner's UUID
     * @return the island, or empty if none is registered
     */
    public Optional<SkyBlockIsland> getIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return Optional.ofNullable(islands.get(owner));
    }

    /**
     * Returns whether an island is registered for {@code owner}.
     *
     * @param owner the owner's UUID
     * @return {@code true} if an island exists
     */
    public boolean hasIsland(UUID owner) {
        return islands.containsKey(owner);
    }

    /**
     * Removes the island owned by {@code owner} from the registry.
     *
     * @param owner the owner's UUID
     * @return {@code true} if an island was removed
     */
    public boolean deleteIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return islands.remove(owner) != null;
    }
}
