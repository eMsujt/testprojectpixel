package com.skyblock.plugin.accessories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton tracking each player's equipped accessories.
 *
 * <p>Each player is associated with a {@link List} of accessory identifiers in
 * the order they were equipped.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class AccessoryBagManager {

    private static final AccessoryBagManager INSTANCE = new AccessoryBagManager();

    /** Per-player list of equipped accessory identifiers. */
    private final Map<UUID, List<String>> equipped = new HashMap<>();

    private AccessoryBagManager() {
    }

    /**
     * Returns the single shared {@code AccessoryBagManager} instance.
     *
     * @return the singleton instance
     */
    public static AccessoryBagManager getInstance() {
        return INSTANCE;
    }

    /**
     * Equips an accessory for the player.
     *
     * @param playerId  the player's UUID, must not be null
     * @param accessory the accessory identifier, must not be null
     * @return {@code true} if equipped, {@code false} if already equipped
     */
    public boolean equip(UUID playerId, String accessory) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(accessory, "accessory");
        List<String> bag = equipped.computeIfAbsent(playerId, id -> new ArrayList<>());
        if (bag.contains(accessory)) {
            return false;
        }
        return bag.add(accessory);
    }

    /**
     * Unequips an accessory from the player.
     *
     * @param playerId  the player's UUID, must not be null
     * @param accessory the accessory identifier, must not be null
     * @return {@code true} if removed, {@code false} if it was not equipped
     */
    public boolean unequip(UUID playerId, String accessory) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(accessory, "accessory");
        List<String> bag = equipped.get(playerId);
        return bag != null && bag.remove(accessory);
    }

    /**
     * Returns whether the player has the given accessory equipped.
     *
     * @param playerId  the player's UUID, must not be null
     * @param accessory the accessory identifier, must not be null
     * @return {@code true} if equipped
     */
    public boolean isEquipped(UUID playerId, String accessory) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(accessory, "accessory");
        List<String> bag = equipped.get(playerId);
        return bag != null && bag.contains(accessory);
    }

    /**
     * Returns an unmodifiable view of the player's equipped accessories.
     *
     * @param playerId the player's UUID, must not be null
     * @return an unmodifiable list of accessories, empty if none
     */
    public List<String> getEquipped(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<String> bag = equipped.get(playerId);
        return bag == null ? Collections.emptyList() : Collections.unmodifiableList(bag);
    }

    /**
     * Clears all equipped accessories for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @return {@code true} if the player had any equipped accessories, {@code false} otherwise
     */
    public boolean clear(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return equipped.remove(playerId) != null;
    }
}
