package com.skyblock.backpacks;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages per-player backpacks and the items stored in their slots.
 *
 * <p>Backpacks are identified per player by a unique string id and created
 * with a fixed slot capacity. Slots are indexed from zero and hold item ids;
 * an absent slot is empty. Not thread-safe; synchronize externally if
 * accessed from multiple threads.</p>
 */
public final class BackpackManager {

    /** A single backpack: its slot capacity and slot index to item id contents. */
    private record Backpack(int size, Map<Integer, String> contents) {
    }

    private final Map<UUID, Map<String, Backpack>> backpacks = new HashMap<>();

    /**
     * Creates a backpack for a player.
     *
     * @param playerId   the unique id of the player, must not be null
     * @param backpackId the unique backpack id, must not be null or blank
     * @param size       the number of slots, must be positive
     * @throws IllegalArgumentException if the player is null, the id is null
     *                                  or blank, the size is not positive, or
     *                                  the player already has a backpack with
     *                                  that id
     */
    public void createBackpack(UUID playerId, String backpackId, int size) {
        if (playerId == null) {
            throw new IllegalArgumentException("playerId must not be null");
        }
        if (backpackId == null || backpackId.isBlank()) {
            throw new IllegalArgumentException("backpackId must not be null or blank");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive: " + size);
        }
        Map<String, Backpack> playerBackpacks =
                backpacks.computeIfAbsent(playerId, key -> new LinkedHashMap<>());
        if (playerBackpacks.containsKey(backpackId)) {
            throw new IllegalArgumentException("backpack already exists: " + backpackId);
        }
        playerBackpacks.put(backpackId, new Backpack(size, new HashMap<>()));
    }

    /**
     * Deletes a player's backpack and all items stored in it.
     *
     * @param playerId   the unique id of the player
     * @param backpackId the backpack id
     * @return {@code true} if the backpack existed and has been removed
     */
    public boolean deleteBackpack(UUID playerId, String backpackId) {
        Map<String, Backpack> playerBackpacks = backpacks.get(playerId);
        return playerBackpacks != null && playerBackpacks.remove(backpackId) != null;
    }

    /**
     * Returns whether a player has a backpack with the given id.
     *
     * @param playerId   the unique id of the player
     * @param backpackId the backpack id
     * @return {@code true} if the backpack exists
     */
    public boolean hasBackpack(UUID playerId, String backpackId) {
        Map<String, Backpack> playerBackpacks = backpacks.get(playerId);
        return playerBackpacks != null && playerBackpacks.containsKey(backpackId);
    }

    /**
     * Returns the slot capacity of a player's backpack.
     *
     * @param playerId   the unique id of the player
     * @param backpackId the backpack id
     * @return the number of slots
     * @throws IllegalArgumentException if the backpack does not exist
     */
    public int getSize(UUID playerId, String backpackId) {
        return getBackpack(playerId, backpackId).size();
    }

    /**
     * Stores an item in a slot of a player's backpack, replacing any item
     * already there.
     *
     * @param playerId   the unique id of the player
     * @param backpackId the backpack id
     * @param slot       the slot index, from 0 inclusive to the size exclusive
     * @param itemId     the item id, must not be null or blank
     * @return the item id previously in the slot, or {@code null} if it was empty
     * @throws IllegalArgumentException  if the backpack does not exist or the
     *                                   item id is null or blank
     * @throws IndexOutOfBoundsException if the slot is out of range
     */
    public String setItem(UUID playerId, String backpackId, int slot, String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId must not be null or blank");
        }
        Backpack backpack = getBackpack(playerId, backpackId);
        checkSlot(slot, backpack.size());
        return backpack.contents().put(slot, itemId);
    }

    /**
     * Removes the item from a slot of a player's backpack.
     *
     * @param playerId   the unique id of the player
     * @param backpackId the backpack id
     * @param slot       the slot index, from 0 inclusive to the size exclusive
     * @return the item id removed from the slot, or {@code null} if it was empty
     * @throws IllegalArgumentException  if the backpack does not exist
     * @throws IndexOutOfBoundsException if the slot is out of range
     */
    public String removeItem(UUID playerId, String backpackId, int slot) {
        Backpack backpack = getBackpack(playerId, backpackId);
        checkSlot(slot, backpack.size());
        return backpack.contents().remove(slot);
    }

    /**
     * Returns the item in a slot of a player's backpack.
     *
     * @param playerId   the unique id of the player
     * @param backpackId the backpack id
     * @param slot       the slot index, from 0 inclusive to the size exclusive
     * @return the item id in the slot, or {@code null} if it is empty
     * @throws IllegalArgumentException  if the backpack does not exist
     * @throws IndexOutOfBoundsException if the slot is out of range
     */
    public String getItem(UUID playerId, String backpackId, int slot) {
        Backpack backpack = getBackpack(playerId, backpackId);
        checkSlot(slot, backpack.size());
        return backpack.contents().get(slot);
    }

    /**
     * Returns the contents of a player's backpack as a slot index to item id map.
     *
     * @param playerId   the unique id of the player
     * @param backpackId the backpack id
     * @return an unmodifiable view of the occupied slots, empty if none
     * @throws IllegalArgumentException if the backpack does not exist
     */
    public Map<Integer, String> getContents(UUID playerId, String backpackId) {
        return Collections.unmodifiableMap(getBackpack(playerId, backpackId).contents());
    }

    /**
     * Returns the ids of a player's backpacks in creation order, mapped to
     * their slot capacities.
     *
     * @param playerId the unique id of the player
     * @return an unmodifiable view of backpack ids to sizes, empty if none
     */
    public Map<String, Integer> getBackpacks(UUID playerId) {
        Map<String, Backpack> playerBackpacks = backpacks.get(playerId);
        if (playerBackpacks == null) {
            return Collections.emptyMap();
        }
        Map<String, Integer> sizes = new LinkedHashMap<>();
        playerBackpacks.forEach((id, backpack) -> sizes.put(id, backpack.size()));
        return Collections.unmodifiableMap(sizes);
    }

    private Backpack getBackpack(UUID playerId, String backpackId) {
        Map<String, Backpack> playerBackpacks = backpacks.get(playerId);
        Backpack backpack = playerBackpacks == null ? null : playerBackpacks.get(backpackId);
        if (backpack == null) {
            throw new IllegalArgumentException("backpack does not exist: " + backpackId);
        }
        return backpack;
    }

    private static void checkSlot(int slot, int size) {
        if (slot < 0 || slot >= size) {
            throw new IndexOutOfBoundsException("slot " + slot + " out of range for size " + size);
        }
    }
}
