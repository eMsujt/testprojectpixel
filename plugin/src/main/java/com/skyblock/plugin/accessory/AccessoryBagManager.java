package com.skyblock.plugin.accessory;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton in-memory registry of the accessories each player owns, keyed by
 * player UUID.
 *
 * <p>Each player maps to a {@link Set} of accessory ids, so an accessory is only
 * ever stored once per player and ownership tests are constant time.</p>
 *
 * <p>The backing map is mutated only on the server main thread; access it from
 * the main thread or guard it externally.</p>
 */
public final class AccessoryBagManager {

    private static final AccessoryBagManager INSTANCE = new AccessoryBagManager();

    private final Map<UUID, Set<String>> accessories = new HashMap<>();

    private AccessoryBagManager() {}

    public static AccessoryBagManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds the given accessory to the player's bag.
     *
     * @param uuid unique identifier of the player
     * @param accessoryId id of the accessory to add
     * @return {@code true} if it was added, {@code false} if already owned
     */
    public boolean add(UUID uuid, String accessoryId) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(accessoryId, "accessoryId");
        return accessories.computeIfAbsent(uuid, k -> new HashSet<>()).add(accessoryId);
    }

    /**
     * Removes the given accessory from the player's bag.
     *
     * @param uuid unique identifier of the player
     * @param accessoryId id of the accessory to remove
     * @return {@code true} if it was removed, {@code false} if it was not owned
     */
    public boolean remove(UUID uuid, String accessoryId) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(accessoryId, "accessoryId");
        Set<String> owned = accessories.get(uuid);
        return owned != null && owned.remove(accessoryId);
    }

    /**
     * Returns whether the player owns the given accessory.
     *
     * @param uuid unique identifier of the player
     * @param accessoryId id of the accessory to test
     * @return {@code true} if the player owns it
     */
    public boolean has(UUID uuid, String accessoryId) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(accessoryId, "accessoryId");
        Set<String> owned = accessories.get(uuid);
        return owned != null && owned.contains(accessoryId);
    }

    /**
     * Returns an immutable snapshot of the accessories the player owns.
     *
     * @param uuid unique identifier of the player
     * @return the player's accessory ids, never {@code null}
     */
    public Set<String> getAccessories(UUID uuid) {
        Objects.requireNonNull(uuid, "uuid");
        Set<String> owned = accessories.get(uuid);
        if (owned == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet<>(owned));
    }
}
