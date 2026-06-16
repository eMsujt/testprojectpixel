package com.skyblock.accessories.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages each player's accessory bag and the magical power it grants.
 *
 * <p>Accessories are identified by their accessory id (e.g. {@code "ZOMBIE_TALISMAN"})
 * and must be registered with a magical power value before players can equip them.
 * A player can hold at most one copy of each accessory. Not thread-safe;
 * synchronize externally if accessed from multiple threads.</p>
 */
public final class AccessoryManager {

    private final Map<String, Integer> magicalPowers = new HashMap<>();
    private final Map<UUID, Set<String>> playerAccessories = new HashMap<>();

    /**
     * Registers an accessory type, or updates its magical power if already registered.
     *
     * @param accessoryId  the accessory id, must not be null
     * @param magicalPower the magical power granted by the accessory, must not be negative
     * @throws IllegalArgumentException if {@code accessoryId} is null or {@code magicalPower} is negative
     */
    public void registerAccessory(String accessoryId, int magicalPower) {
        if (accessoryId == null) {
            throw new IllegalArgumentException("accessoryId must not be null");
        }
        if (magicalPower < 0) {
            throw new IllegalArgumentException("magicalPower must not be negative: " + magicalPower);
        }
        magicalPowers.put(accessoryId, magicalPower);
    }

    /**
     * Returns whether the accessory type is registered.
     *
     * @param accessoryId the accessory id
     * @return {@code true} if the accessory is registered
     */
    public boolean isRegistered(String accessoryId) {
        return magicalPowers.containsKey(accessoryId);
    }

    /**
     * Returns the magical power granted by a registered accessory.
     *
     * @param accessoryId the accessory id
     * @return the magical power of the accessory
     * @throws IllegalArgumentException if the accessory is not registered
     */
    public int getMagicalPower(String accessoryId) {
        Integer power = magicalPowers.get(accessoryId);
        if (power == null) {
            throw new IllegalArgumentException("accessory is not registered: " + accessoryId);
        }
        return power;
    }

    /**
     * Adds an accessory to a player's accessory bag.
     *
     * @param playerId    the player's unique id, must not be null
     * @param accessoryId the accessory id
     * @return {@code true} if the accessory was added, {@code false} if the player already has it
     * @throws IllegalArgumentException if {@code playerId} is null or the accessory is not registered
     */
    public boolean addAccessory(UUID playerId, String accessoryId) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
        if (!isRegistered(accessoryId)) {
            throw new IllegalArgumentException("accessory is not registered: " + accessoryId);
        }
        return playerAccessories.computeIfAbsent(playerId, id -> new LinkedHashSet<>()).add(accessoryId);
    }

    /**
     * Removes an accessory from a player's accessory bag.
     *
     * @param playerId    the player's unique id
     * @param accessoryId the accessory id
     * @return {@code true} if the player had the accessory and it has been removed
     */
    public boolean removeAccessory(UUID playerId, String accessoryId) {
        Set<String> accessories = playerAccessories.get(playerId);
        return accessories != null && accessories.remove(accessoryId);
    }

    /**
     * Returns whether the player has the given accessory in their bag.
     *
     * @param playerId    the player's unique id
     * @param accessoryId the accessory id
     * @return {@code true} if the player has the accessory
     */
    public boolean hasAccessory(UUID playerId, String accessoryId) {
        Set<String> accessories = playerAccessories.get(playerId);
        return accessories != null && accessories.contains(accessoryId);
    }

    /**
     * Returns the ids of all accessories in a player's bag.
     *
     * @param playerId the player's unique id
     * @return an unmodifiable view of the player's accessory ids, empty if none
     */
    public Set<String> getAccessories(UUID playerId) {
        Set<String> accessories = playerAccessories.get(playerId);
        return accessories == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(accessories);
    }

    /**
     * Returns the total magical power of all accessories in a player's bag.
     *
     * @param playerId the player's unique id
     * @return the sum of the magical power of the player's accessories
     */
    public int getTotalMagicalPower(UUID playerId) {
        int total = 0;
        for (String accessoryId : getAccessories(playerId)) {
            total += magicalPowers.getOrDefault(accessoryId, 0);
        }
        return total;
    }
}
