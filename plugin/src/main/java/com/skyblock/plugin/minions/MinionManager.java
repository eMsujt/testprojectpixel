package com.skyblock.plugin.minions;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * In-memory registry of every minion players have placed.
 *
 * <p>Holds placed minions in a {@link Map} keyed by the owning player's UUID,
 * each mapping to the list of that player's minions in placement order. A
 * minion is tracked when it is placed and removed when it is picked up. Not
 * thread-safe; access from the main server thread.</p>
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

    private final Map<UUID, List<MinionData>> minions = new HashMap<>();

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
        minions.computeIfAbsent(minion.owner(), k -> new ArrayList<>()).add(minion);
    }

    /**
     * Stops tracking a placed minion.
     *
     * @param minion the minion to remove
     * @return {@code true} if the minion was tracked and removed
     */
    public boolean removeMinion(MinionData minion) {
        Objects.requireNonNull(minion, "minion");
        List<MinionData> list = minions.get(minion.owner());
        if (list == null) {
            return false;
        }
        boolean removed = list.remove(minion);
        if (list.isEmpty()) {
            minions.remove(minion.owner());
        }
        return removed;
    }

    /**
     * Returns an unmodifiable view of the minions placed by the given player.
     *
     * @param playerId the owning player's UUID
     * @return the player's minions in placement order, empty if they have none
     */
    public List<MinionData> getMinions(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<MinionData> list = minions.get(playerId);
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }
}
