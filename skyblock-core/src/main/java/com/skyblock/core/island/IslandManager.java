package com.skyblock.core.island;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 * Singleton managing per-player SkyBlock islands.
 *
 * <p>Islands are keyed by owner UUID. Members are tracked in a secondary index
 * so that lookups by non-owner member are O(1).</p>
 *
 * <p>Each island owns a dedicated void {@link World} created via {@link WorldCreator}
 * and {@link IslandGenerator}. Worlds are stored in {@link #islandWorlds} and unloaded
 * when the island is deleted.</p>
 */
public final class IslandManager {

    public static final class SkyBlockIsland {

        private final UUID owner;
        private final List<UUID> members = new ArrayList<>();

        SkyBlockIsland(UUID owner) {
            this.owner = owner;
        }

        public UUID getOwner() {
            return owner;
        }

        public List<UUID> getMembers() {
            return Collections.unmodifiableList(members);
        }
    }

    private static final IslandManager INSTANCE = new IslandManager();

    /** owner UUID → island */
    private final Map<UUID, SkyBlockIsland> islands = new HashMap<>();
    /** member UUID → owner UUID (owner is NOT in this map) */
    private final Map<UUID, UUID> memberIndex = new HashMap<>();
    /** owner UUID → island world */
    private final Map<UUID, World> islandWorlds = new HashMap<>();

    private IslandManager() {
    }

    public static IslandManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new island owned by {@code owner}, including its void world.
     *
     * @return the new island
     * @throws IllegalStateException if the player already owns an island
     */
    public SkyBlockIsland createIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        if (islands.containsKey(owner)) {
            throw new IllegalStateException("Player already owns an island");
        }
        SkyBlockIsland island = new SkyBlockIsland(owner);
        islands.put(owner, island);

        World world = new WorldCreator("island_" + owner)
                .generator(new IslandGenerator())
                .createWorld();
        islandWorlds.put(owner, world);

        return island;
    }

    /**
     * Returns the island world for {@code owner}, if any.
     *
     * <p>Use this to obtain the spawn {@link Location} for teleportation:
     * {@code world.getSpawnLocation()}.</p>
     */
    public Optional<World> getIslandWorld(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return Optional.ofNullable(islandWorlds.get(owner));
    }

    /** Returns the island owned by {@code owner}, if any. */
    public Optional<SkyBlockIsland> getIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return Optional.ofNullable(islands.get(owner));
    }

    /** Returns the island that {@code player} belongs to (as owner or member). */
    public Optional<SkyBlockIsland> getIslandByMember(UUID player) {
        Objects.requireNonNull(player, "player");
        if (islands.containsKey(player)) {
            return Optional.of(islands.get(player));
        }
        UUID ownerUuid = memberIndex.get(player);
        return ownerUuid == null ? Optional.empty() : Optional.ofNullable(islands.get(ownerUuid));
    }

    /** Returns {@code true} if {@code owner} already has an island. */
    public boolean hasIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        return islands.containsKey(owner);
    }

    /**
     * Adds {@code invitee} as a member of {@code owner}'s island.
     *
     * @return {@code false} if the island does not exist or the player is already a member
     */
    public boolean addMember(UUID owner, UUID invitee) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(invitee, "invitee");
        SkyBlockIsland island = islands.get(owner);
        if (island == null || island.members.contains(invitee) || islands.containsKey(invitee)) {
            return false;
        }
        island.members.add(invitee);
        memberIndex.put(invitee, owner);
        return true;
    }

    /**
     * Removes {@code target} from {@code owner}'s island members.
     *
     * @return {@code false} if the island does not exist or the player is not a member
     */
    public boolean removeMember(UUID owner, UUID target) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(target, "target");
        SkyBlockIsland island = islands.get(owner);
        if (island == null) {
            return false;
        }
        boolean removed = island.members.remove(target);
        if (removed) {
            memberIndex.remove(target);
        }
        return removed;
    }

    /**
     * Removes {@code member} from whichever island they belong to.
     * Has no effect if the player owns an island (owners cannot leave their own island).
     *
     * @return {@code false} if the player is not a member of any island
     */
    public boolean leaveIsland(UUID member) {
        Objects.requireNonNull(member, "member");
        UUID ownerUuid = memberIndex.get(member);
        if (ownerUuid == null) {
            return false;
        }
        SkyBlockIsland island = islands.get(ownerUuid);
        if (island != null) {
            island.members.remove(member);
        }
        memberIndex.remove(member);
        return true;
    }

    /**
     * Deletes the island owned by {@code owner}, removes all its members, and unloads the world.
     *
     * @return {@code false} if the owner had no island
     */
    public boolean deleteIsland(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        SkyBlockIsland island = islands.remove(owner);
        if (island == null) {
            return false;
        }
        for (UUID member : island.members) {
            memberIndex.remove(member);
        }
        World world = islandWorlds.remove(owner);
        if (world != null) {
            Bukkit.unloadWorld(world, false);
        }
        return true;
    }
}
