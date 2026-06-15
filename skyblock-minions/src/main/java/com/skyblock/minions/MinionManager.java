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
    private final Map<Location, MinionData> placedMinions = new HashMap<>();

    /** Secondary index: owner UUID → list of their minion locations. */
    private final Map<UUID, List<Location>> ownerIndex = new HashMap<>();

    /**
     * Places a new tier-1 minion for the player at the given location.
     *
     * @param ownerId  the owner's player UUID
     * @param type     the kind of minion to place
     * @param location the block to place the minion on
     * @return the newly created {@link MinionData}
     * @throws IllegalStateException if the player already has
     *                               {@link #MAX_MINIONS_PER_PLAYER} minions, or a
     *                               minion already occupies the location
     */
    public MinionData placeMinion(UUID ownerId, MinionType type, Location location) {
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
        Location key = location.clone();
        MinionData minion = new MinionData(ownerId, type, key);
        placedMinions.put(key, minion);
        owned.add(key);
        return minion;
    }

    /**
     * Removes the minion at the given location.
     *
     * @param ownerId  the owner's player UUID
     * @param location the block the minion stands on
     * @return the minion that was removed, or {@code null} if none existed
     */
    public MinionData removeMinion(UUID ownerId, Location location) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(location, "location");
        MinionData minion = placedMinions.remove(location);
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
    public MinionData getMinion(Location location) {
        Objects.requireNonNull(location, "location");
        return placedMinions.get(location);
    }

    /**
     * Returns all minions placed by the given player.
     *
     * @param ownerId the owner's player UUID
     * @return an unmodifiable list of the player's minions, possibly empty
     */
    public List<MinionData> getMinions(UUID ownerId) {
        Objects.requireNonNull(ownerId, "ownerId");
        List<Location> locations = ownerIndex.getOrDefault(ownerId, Collections.emptyList());
        List<MinionData> result = new ArrayList<>(locations.size());
        for (Location loc : locations) {
            MinionData m = placedMinions.get(loc);
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

    // -------------------------------------------------------------------------
    // Inner types
    // -------------------------------------------------------------------------

    /**
     * The kinds of minion a player can place on their island.
     *
     * <p>Each type carries its production interval, in seconds, for every
     * upgrade tier from 1 to {@link #MAX_TIER}. Higher tiers act faster.</p>
     */
    public enum MinionType {
        COBBLESTONE("Cobblestone Minion",
                new int[] {14, 14, 12, 12, 10, 10, 9, 9, 8, 8, 7}),
        WHEAT("Wheat Minion",
                new int[] {14, 14, 12, 12, 10, 10, 9, 9, 8, 8, 7});

        /** The highest tier a minion can reach. */
        public static final int MAX_TIER = 11;

        private final String displayName;
        private final int[] productionIntervalSeconds;

        MinionType(String displayName, int[] productionIntervalSeconds) {
            this.displayName = displayName;
            this.productionIntervalSeconds = productionIntervalSeconds;
        }

        /**
         * Returns the human-readable name of this minion type.
         *
         * @return the display name, e.g. {@code "Cobblestone Minion"}
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * Returns this minion's production interval, in seconds, at the given
         * tier.
         *
         * @param tier the upgrade tier, between 1 and {@link #MAX_TIER}
         * @return the seconds between two actions at that tier
         * @throws IllegalArgumentException if {@code tier} is out of range
         */
        public int getProductionIntervalSeconds(int tier) {
            if (tier < 1 || tier > MAX_TIER) {
                throw new IllegalArgumentException(
                        "tier must be between 1 and " + MAX_TIER + ", got " + tier);
            }
            return productionIntervalSeconds[tier - 1];
        }
    }

    /**
     * Data holder for a single placed minion.
     */
    public static final class MinionData {

        private final UUID ownerId;
        private final MinionType type;
        private final Location location;
        private int tier;
        private long lastActionTime;

        private MinionData(UUID ownerId, MinionType type, Location location) {
            this.ownerId = ownerId;
            this.type = type;
            this.location = location;
            this.tier = 1;
            this.lastActionTime = 0L;
        }

        /** Returns the UUID of the player who placed this minion. */
        public UUID getOwnerId() {
            return ownerId;
        }

        /** Returns the kind of minion placed. */
        public MinionType getType() {
            return type;
        }

        /** Returns a copy of the location the minion stands on. */
        public Location getLocation() {
            return location.clone();
        }

        /** Returns the minion's upgrade tier, starting at 1. */
        public int getTier() {
            return tier;
        }

        /**
         * Sets the minion's upgrade tier.
         *
         * @param tier the new tier, must be at least 1
         * @throws IllegalArgumentException if {@code tier} is less than 1
         */
        public void setTier(int tier) {
            if (tier < 1) {
                throw new IllegalArgumentException("tier must be at least 1, got " + tier);
            }
            this.tier = tier;
        }

        /** Returns the timestamp of the minion's last action in epoch milliseconds. */
        public long getLastActionTime() {
            return lastActionTime;
        }

        /** Sets the timestamp of the minion's last action in epoch milliseconds. */
        public void setLastActionTime(long lastActionTime) {
            this.lastActionTime = lastActionTime;
        }

        @Override
        public String toString() {
            return "MinionData{ownerId=" + ownerId + ", type=" + type + ", location=" + location
                    + ", tier=" + tier + ", lastActionTime=" + lastActionTime + '}';
        }
    }
}
