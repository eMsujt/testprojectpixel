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
 * Tracks the minions each player has placed on their island.
 *
 * <p>At most one minion may occupy a block, and a player may place at
 * most {@link #MAX_MINIONS_PER_PLAYER} minions. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class MinionManager {

    /** The maximum number of minions a single player may place. */
    public static final int MAX_MINIONS_PER_PLAYER = 25;

    private final Map<UUID, List<MinionInstance>> minions = new HashMap<>();

    /**
     * Places a new tier 1 minion for the player at the given location.
     *
     * @param ownerId  the owner's player UUID
     * @param type     the kind of minion to place
     * @param location the block to place the minion on
     * @return the newly placed minion
     * @throws IllegalStateException if the player already has
     *                               {@link #MAX_MINIONS_PER_PLAYER} minions, or a
     *                               minion already occupies the location
     */
    public MinionInstance placeMinion(UUID ownerId, MinionType type, Location location) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(location, "location");
        List<MinionInstance> placed = minions.computeIfAbsent(ownerId, id -> new ArrayList<>());
        if (placed.size() >= MAX_MINIONS_PER_PLAYER) {
            throw new IllegalStateException(
                    "player already has the maximum of " + MAX_MINIONS_PER_PLAYER + " minions: " + ownerId);
        }
        if (getMinionAt(ownerId, location) != null) {
            throw new IllegalStateException("a minion is already placed at " + location);
        }
        MinionInstance minion = new MinionInstance(type, location);
        placed.add(minion);
        return minion;
    }

    /**
     * Returns the player's placed minions.
     *
     * @param ownerId the owner's player UUID
     * @return an unmodifiable view of the player's minions, possibly empty
     */
    public List<MinionInstance> getMinions(UUID ownerId) {
        return Collections.unmodifiableList(minions.getOrDefault(ownerId, Collections.emptyList()));
    }

    /**
     * Returns the player's minion at the given location, if any.
     *
     * @param ownerId  the owner's player UUID
     * @param location the block to look at
     * @return the minion at that location, or {@code null} if none
     */
    public MinionInstance getMinionAt(UUID ownerId, Location location) {
        Objects.requireNonNull(location, "location");
        for (MinionInstance minion : minions.getOrDefault(ownerId, Collections.emptyList())) {
            if (minion.getLocation().equals(location)) {
                return minion;
            }
        }
        return null;
    }

    /**
     * Removes the player's minion at the given location.
     *
     * @param ownerId  the owner's player UUID
     * @param location the block the minion stands on
     * @return the minion that was removed, or {@code null} if none existed
     */
    public MinionInstance removeMinion(UUID ownerId, Location location) {
        MinionInstance minion = getMinionAt(ownerId, location);
        if (minion != null) {
            minions.get(ownerId).remove(minion);
        }
        return minion;
    }

    /**
     * Returns how many minions the player has placed.
     *
     * @param ownerId the owner's player UUID
     * @return the number of placed minions
     */
    public int getMinionCount(UUID ownerId) {
        return minions.getOrDefault(ownerId, Collections.emptyList()).size();
    }
}
