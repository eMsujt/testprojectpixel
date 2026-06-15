package com.skyblock.minions.model;

import java.util.Objects;
import org.bukkit.Location;

/**
 * Data holder for a single placed minion.
 *
 * <p>Tracks the minion's type, the block it stands on, its upgrade tier
 * and the timestamp of its last action. Instances are not thread-safe;
 * access them from the server main thread or guard them externally.</p>
 */
public final class MinionInstance {

    private final MinionType type;
    private final Location location;
    private int tier;
    private long lastActionTime;

    /**
     * Creates a new minion at tier 1 that has never acted.
     *
     * @param type     the kind of minion placed
     * @param location the block the minion stands on
     */
    public MinionInstance(MinionType type, Location location) {
        this.type = Objects.requireNonNull(type, "type");
        this.location = Objects.requireNonNull(location, "location").clone();
        this.tier = 1;
        this.lastActionTime = 0L;
    }

    /**
     * Returns the kind of minion placed.
     *
     * @return the minion type
     */
    public MinionType getType() {
        return type;
    }

    /**
     * Returns a copy of the location the minion stands on.
     *
     * @return the minion's location
     */
    public Location getLocation() {
        return location.clone();
    }

    /**
     * Returns the minion's upgrade tier.
     *
     * @return the current tier, at least 1
     */
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

    /**
     * Returns the timestamp of the minion's last action.
     *
     * @return the last action time in epoch milliseconds, or {@code 0}
     *         if the minion has never acted
     */
    public long getLastActionTime() {
        return lastActionTime;
    }

    /**
     * Sets the timestamp of the minion's last action.
     *
     * @param lastActionTime the last action time in epoch milliseconds
     */
    public void setLastActionTime(long lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    @Override
    public String toString() {
        return "MinionInstance{type=" + type + ", location=" + location
                + ", tier=" + tier + ", lastActionTime=" + lastActionTime + '}';
    }
}
