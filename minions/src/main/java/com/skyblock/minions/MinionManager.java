package com.skyblock.minions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Location;

/**
 * Singleton tracking minions placed on islands, keyed by location.
 *
 * <p>At most one minion may occupy a block, and a player may place at most
 * {@link #MAX_MINIONS_PER_PLAYER} minions. Access the shared instance via
 * {@link #getInstance()}. Not thread-safe; synchronize externally if accessed
 * from multiple threads.</p>
 *
 * @deprecated Use {@link com.skyblock.core.manager.MinionManager} instead.
 */
@Deprecated
public final class MinionManager {

    /** The maximum number of minions a single player may place. */
    public static final int MAX_MINIONS_PER_PLAYER = 25;

    private static final MinionManager INSTANCE = new MinionManager();

    private MinionManager() {
    }

    /**
     * Returns the shared manager instance.
     *
     * @return the singleton {@code MinionManager}
     */
    public static MinionManager getInstance() {
        return INSTANCE;
    }

    /** Primary store: location → minion. */
    private final Map<Location, MinionInstance> placedMinions = new HashMap<>();

    /** Secondary index: owner UUID → list of their minion locations. */
    private final Map<UUID, List<Location>> ownerIndex = new HashMap<>();

    /**
     * Places a new tier 1 minion for the player at the given location.
     *
     * @param ownerId  the owner's player UUID
     * @param type     the kind of minion to place
     * @param location the block to place the minion on
     * @return the newly created {@link MinionInstance}
     * @throws IllegalStateException if the player already has
     *                               {@link #MAX_MINIONS_PER_PLAYER} minions, or a
     *                               minion already occupies the location
     */
    public MinionInstance placeMinion(UUID ownerId, MinionType type, Location location) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(location, "location");
        if (placedMinions.containsKey(location)) {
            throw new IllegalStateException("a minion is already placed at " + location);
        }
        List<Location> owned = ownerIndex.computeIfAbsent(ownerId, id -> new ArrayList<>());
        if (owned.size() >= MAX_MINIONS_PER_PLAYER) {
            throw new IllegalStateException(
                    "player already has the maximum of " + MAX_MINIONS_PER_PLAYER + " minions: " + ownerId);
        }
        MinionInstance minion = new MinionInstance(type, location);
        placedMinions.put(location.clone(), minion);
        owned.add(location.clone());
        return minion;
    }

    /**
     * Removes the minion at the given location.
     *
     * @param ownerId  the owner's player UUID
     * @param location the block the minion stands on
     * @return the minion that was removed, or {@code null} if none existed
     */
    public MinionInstance removeMinion(UUID ownerId, Location location) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(location, "location");
        MinionInstance minion = placedMinions.remove(location);
        if (minion != null) {
            List<Location> owned = ownerIndex.get(ownerId);
            if (owned != null) {
                owned.remove(location);
            }
        }
        return minion;
    }

    /**
     * Returns the minion at the given location, if any.
     *
     * @param location the block to look at
     * @return the minion at that location, or {@code null} if none
     */
    public MinionInstance getMinion(Location location) {
        Objects.requireNonNull(location, "location");
        return placedMinions.get(location);
    }

    /**
     * Returns all minions placed by the given player.
     *
     * @param ownerId the owner's player UUID
     * @return an unmodifiable list of the player's minions, possibly empty
     */
    public List<MinionInstance> getMinions(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        List<Location> locations = ownerIndex.getOrDefault(ownerId, Collections.emptyList());
        List<MinionInstance> result = new ArrayList<>(locations.size());
        for (Location loc : locations) {
            MinionInstance m = placedMinions.get(loc);
            if (m != null) {
                result.add(m);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns how many minions the player has placed.
     *
     * @param ownerId the owner's player UUID
     * @return the number of placed minions
     */
    public int getMinionCount(UUID ownerId) {
        return ownerIndex.getOrDefault(ownerId, Collections.emptyList()).size();
    }
}
