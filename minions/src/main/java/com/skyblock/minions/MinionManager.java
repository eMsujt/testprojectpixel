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
 * Singleton tracking the minions each player has placed on their island.
 *
 * <p>Placed minions are stored in a {@link HashMap} keyed by the owner's
 * player UUID. At most one minion may occupy a block, and a player may
 * place at most {@link #MAX_MINIONS_PER_PLAYER} minions. Access the shared
 * instance via {@link #getInstance()}. Not thread-safe; synchronize
 * externally if accessed from multiple threads.</p>
 */
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

    /**
     * A single minion placed on a player's island.
     *
     * <p>Instances are created only through
     * {@link MinionManager#placeMinion(UUID, MinionType, Location)} and
     * always start at tier 1.</p>
     *
     * @param type     the kind of minion placed, never null
     * @param location the block the minion stands on, never null
     * @param tier     the minion's upgrade tier, at least 1
     */
    public record PlacedMinion(MinionType type, Location location, int tier) {

        /**
         * Validates the components and defensively copies the location.
         *
         * @throws NullPointerException     if the type or location is null
         * @throws IllegalArgumentException if {@code tier} is less than 1
         */
        public PlacedMinion {
            Objects.requireNonNull(type, "type");
            Objects.requireNonNull(location, "location");
            if (tier < 1) {
                throw new IllegalArgumentException("tier must be at least 1, got " + tier);
            }
            location = location.clone();
        }

        /**
         * Returns a copy of the location the minion stands on.
         *
         * @return the minion's location
         */
        @Override
        public Location location() {
            return location.clone();
        }
    }

    private final Map<UUID, List<PlacedMinion>> islandMinions = new HashMap<>();

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
    public PlacedMinion placeMinion(UUID ownerId, MinionType type, Location location) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(location, "location");
        List<PlacedMinion> placed = islandMinions.computeIfAbsent(ownerId, id -> new ArrayList<>());
        if (placed.size() >= MAX_MINIONS_PER_PLAYER) {
            throw new IllegalStateException(
                    "player already has the maximum of " + MAX_MINIONS_PER_PLAYER + " minions: " + ownerId);
        }
        if (getMinionAt(ownerId, location) != null) {
            throw new IllegalStateException("a minion is already placed at " + location);
        }
        PlacedMinion minion = new PlacedMinion(type, location, 1);
        placed.add(minion);
        return minion;
    }

    /**
     * Returns the player's placed minions.
     *
     * @param ownerId the owner's player UUID
     * @return an unmodifiable view of the player's minions, possibly empty
     */
    public List<PlacedMinion> getMinions(UUID ownerId) {
        return Collections.unmodifiableList(islandMinions.getOrDefault(ownerId, Collections.emptyList()));
    }

    /**
     * Returns the player's minion at the given location, if any.
     *
     * @param ownerId  the owner's player UUID
     * @param location the block to look at
     * @return the minion at that location, or {@code null} if none
     */
    public PlacedMinion getMinionAt(UUID ownerId, Location location) {
        Objects.requireNonNull(location, "location");
        for (PlacedMinion minion : islandMinions.getOrDefault(ownerId, Collections.emptyList())) {
            if (minion.location().equals(location)) {
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
    public PlacedMinion removeMinion(UUID ownerId, Location location) {
        PlacedMinion minion = getMinionAt(ownerId, location);
        if (minion != null) {
            islandMinions.get(ownerId).remove(minion);
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
        return islandMinions.getOrDefault(ownerId, Collections.emptyList()).size();
    }
}
