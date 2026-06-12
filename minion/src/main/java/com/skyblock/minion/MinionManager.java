package com.skyblock.minion;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Tracks the minions each player has placed and their tiers.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class MinionManager {

    /** Maximum tier a minion can be upgraded to. */
    public static final int MAX_TIER = 12;

    /** Default number of minion slots available to a player. */
    public static final int DEFAULT_SLOT_LIMIT = 5;

    private final Map<UUID, Map<UUID, Minion>> minionsByOwner = new HashMap<>();
    private final Map<UUID, Integer> slotLimits = new HashMap<>();

    /**
     * Places a new tier-1 minion of the given type for the player.
     *
     * @param ownerId    the player placing the minion
     * @param minionType the minion type, e.g. {@code "COBBLESTONE"}, must not be blank
     * @return the newly placed {@link Minion}
     * @throws IllegalArgumentException if minionType is blank
     * @throws IllegalStateException    if the player has no free minion slots
     */
    public Minion placeMinion(UUID ownerId, String minionType) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(minionType, "minionType");
        if (minionType.isBlank()) {
            throw new IllegalArgumentException("minionType must not be blank");
        }
        Map<UUID, Minion> owned = minionsByOwner.computeIfAbsent(ownerId, id -> new LinkedHashMap<>());
        if (owned.size() >= getSlotLimit(ownerId)) {
            throw new IllegalStateException("no free minion slots for player " + ownerId);
        }
        Minion minion = new Minion(UUID.randomUUID(), minionType);
        owned.put(minion.getId(), minion);
        return minion;
    }

    /**
     * Upgrades the given minion to the next tier.
     *
     * @param ownerId  the player who owns the minion
     * @param minionId the minion to upgrade
     * @return the minion's tier after the upgrade
     * @throws IllegalArgumentException if the player does not own such a minion
     * @throws IllegalStateException    if the minion is already at {@link #MAX_TIER}
     */
    public int upgradeMinion(UUID ownerId, UUID minionId) {
        Minion minion = getOwnedMinion(ownerId, minionId);
        if (minion.tier >= MAX_TIER) {
            throw new IllegalStateException("minion is already at max tier: " + MAX_TIER);
        }
        minion.tier++;
        return minion.tier;
    }

    /**
     * Removes a placed minion, freeing its slot.
     *
     * @param ownerId  the player who owns the minion
     * @param minionId the minion to remove
     * @return the removed {@link Minion}
     * @throws IllegalArgumentException if the player does not own such a minion
     */
    public Minion removeMinion(UUID ownerId, UUID minionId) {
        Minion minion = getOwnedMinion(ownerId, minionId);
        minionsByOwner.get(ownerId).remove(minionId);
        return minion;
    }

    /**
     * Returns an unmodifiable view of the player's placed minions keyed by minion id.
     *
     * @param ownerId the player to look up
     * @return the player's minions, empty if none are placed
     */
    public Map<UUID, Minion> getMinions(UUID ownerId) {
        return Collections.unmodifiableMap(minionsByOwner.getOrDefault(ownerId, Collections.emptyMap()));
    }

    /**
     * Returns the number of minion slots available to the player.
     *
     * @param ownerId the player to look up
     * @return the slot limit, {@link #DEFAULT_SLOT_LIMIT} if never raised
     */
    public int getSlotLimit(UUID ownerId) {
        return slotLimits.getOrDefault(ownerId, DEFAULT_SLOT_LIMIT);
    }

    /**
     * Sets the number of minion slots available to the player.
     *
     * @param ownerId   the player whose limit to set
     * @param slotLimit the new slot limit, must be positive
     * @throws IllegalArgumentException if slotLimit is not positive
     */
    public void setSlotLimit(UUID ownerId, int slotLimit) {
        Objects.requireNonNull(ownerId, "ownerId");
        if (slotLimit < 1) {
            throw new IllegalArgumentException("slotLimit must be positive: " + slotLimit);
        }
        slotLimits.put(ownerId, slotLimit);
    }

    private Minion getOwnedMinion(UUID ownerId, UUID minionId) {
        Objects.requireNonNull(ownerId, "ownerId");
        Objects.requireNonNull(minionId, "minionId");
        Minion minion = minionsByOwner.getOrDefault(ownerId, Collections.emptyMap()).get(minionId);
        if (minion == null) {
            throw new IllegalArgumentException("player " + ownerId + " owns no minion " + minionId);
        }
        return minion;
    }

    /**
     * A single placed minion.
     */
    public static final class Minion {

        private final UUID id;
        private final String type;
        private int tier = 1;

        private Minion(UUID id, String type) {
            this.id = id;
            this.type = type;
        }

        /** Returns the unique id of this placed minion. */
        public UUID getId() {
            return id;
        }

        /** Returns the minion type, e.g. {@code "COBBLESTONE"}. */
        public String getType() {
            return type;
        }

        /** Returns the minion's current tier, starting at 1. */
        public int getTier() {
            return tier;
        }
    }
}
