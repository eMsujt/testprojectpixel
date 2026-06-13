package com.skyblock.core.talisman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton managing each player's Talisman Bag — the inventory used to store
 * accessories that are not currently equipped.
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class TalismanBagManager {

    /** Default maximum number of accessory slots in a player's talisman bag. */
    public static final int DEFAULT_CAPACITY = 45;

    private static final TalismanBagManager INSTANCE = new TalismanBagManager();

    /** Per-player ordered list of accessories stored in the bag. */
    private final Map<UUID, List<TalismanManager.TalismanType>> bags = new HashMap<>();

    private TalismanBagManager() {
    }

    /**
     * Returns the single shared {@code TalismanBagManager} instance.
     *
     * @return the singleton instance
     */
    public static TalismanBagManager getInstance() {
        return INSTANCE;
    }

    /**
     * Adds an accessory to the player's talisman bag.
     *
     * @param playerId the player
     * @param type     the accessory to add
     * @return {@code true} if added, {@code false} if the bag is full
     */
    public boolean addToBag(UUID playerId, TalismanManager.TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        List<TalismanManager.TalismanType> bag = bags.computeIfAbsent(playerId, id -> new ArrayList<>());
        if (bag.size() >= DEFAULT_CAPACITY) {
            return false;
        }
        bag.add(type);
        return true;
    }

    /**
     * Removes the first occurrence of the given accessory from the player's bag.
     *
     * @param playerId the player
     * @param type     the accessory to remove
     * @return {@code true} if the accessory was present and removed
     */
    public boolean removeFromBag(UUID playerId, TalismanManager.TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        List<TalismanManager.TalismanType> bag = bags.get(playerId);
        return bag != null && bag.remove(type);
    }

    /**
     * Returns whether the player's bag contains the given accessory.
     *
     * @param playerId the player
     * @param type     the accessory to check
     * @return {@code true} if the bag contains at least one of this type
     */
    public boolean contains(UUID playerId, TalismanManager.TalismanType type) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(type, "type");
        List<TalismanManager.TalismanType> bag = bags.get(playerId);
        return bag != null && bag.contains(type);
    }

    /**
     * Returns an unmodifiable view of the player's bag contents.
     *
     * @param playerId the player to look up
     * @return an unmodifiable list, empty if the bag has no accessories
     */
    public List<TalismanManager.TalismanType> getContents(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<TalismanManager.TalismanType> bag = bags.get(playerId);
        return bag == null ? Collections.emptyList() : Collections.unmodifiableList(bag);
    }

    /**
     * Returns the number of accessories currently in the player's bag.
     *
     * @param playerId the player to look up
     * @return the count, {@code 0} if the bag is empty
     */
    public int getCount(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        List<TalismanManager.TalismanType> bag = bags.get(playerId);
        return bag == null ? 0 : bag.size();
    }

    /**
     * Removes all accessories from the player's bag.
     *
     * @param playerId the player to clear
     * @return {@code true} if the player had any accessories in the bag
     */
    public boolean clearBag(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return bags.remove(playerId) != null;
    }
}
