package com.skyblock.plugin.manager;

import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of every active minion players have placed.
 *
 * <p>Holds placed minions in a nested {@link Map} keyed first by the owning
 * player's UUID and then by the world location the minion occupies, so a placed
 * minion can be resolved from its owner and position. A minion is tracked when
 * it is placed and removed when it is picked up. Not thread-safe; access from
 * the main server thread.</p>
 */
public final class MinionManager {

    /**
     * A single placed minion.
     *
     * @param owner the owning player's UUID
     * @param loc   the world location the minion is placed at
     * @param type  the minion type (e.g. {@code COBBLESTONE}, {@code WHEAT})
     * @param tier  the minion's upgrade tier
     */
    public record MinionData(UUID owner, Location loc, String type, int tier) {
        public MinionData {
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(loc, "loc");
            Objects.requireNonNull(type, "type");
        }
    }

    private static final MinionManager INSTANCE = new MinionManager();

    private final Map<UUID, Map<Location, MinionData>> minions = new HashMap<>();

    private MinionManager() {
    }

    public static MinionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Tracks a newly placed minion for its owner.
     *
     * @param minion the minion to add
     */
    public void addMinion(MinionData minion) {
        Objects.requireNonNull(minion, "minion");
        minions.computeIfAbsent(minion.owner(), k -> new HashMap<>()).put(minion.loc(), minion);
    }

    /**
     * Stops tracking the minion placed at the given location by the given owner.
     *
     * @param owner the owning player's UUID
     * @param loc   the location the minion occupies
     * @return the removed minion, or {@code null} if none was tracked there
     */
    public MinionData removeMinion(UUID owner, Location loc) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(loc, "loc");
        Map<Location, MinionData> owned = minions.get(owner);
        if (owned == null) {
            return null;
        }
        MinionData removed = owned.remove(loc);
        if (owned.isEmpty()) {
            minions.remove(owner);
        }
        return removed;
    }

    /**
     * Returns the owner's minion placed at the given location, or {@code null}.
     *
     * @param owner the owning player's UUID
     * @param loc   the location to look up
     * @return the tracked minion, or {@code null} if none is there
     */
    public MinionData getMinion(UUID owner, Location loc) {
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(loc, "loc");
        Map<Location, MinionData> owned = minions.get(owner);
        return owned == null ? null : owned.get(loc);
    }

    /**
     * Returns an unmodifiable view of the minions placed by the given player,
     * keyed by location.
     *
     * @param owner the owning player's UUID
     * @return the player's minions, empty if they have none
     */
    public Map<Location, MinionData> getMinions(UUID owner) {
        Objects.requireNonNull(owner, "owner");
        Map<Location, MinionData> owned = minions.get(owner);
        return owned == null ? Collections.emptyMap() : Collections.unmodifiableMap(owned);
    }
}
