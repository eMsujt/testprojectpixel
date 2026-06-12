package com.skyblock.core.backpack;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Singleton managing per-player backpacks (named item storage bags).
 *
 * <p>Each player may own up to {@link #MAX_BACKPACKS} named backpacks. Each
 * backpack holds up to {@link #BACKPACK_SIZE} item stacks.
 * Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class BackpackManager {

    /** Maximum backpacks a player may store. */
    public static final int MAX_BACKPACKS = 9;

    /** Number of slots in each backpack. */
    public static final int BACKPACK_SIZE = 27;

    private static final BackpackManager INSTANCE = new BackpackManager();

    /** playerId → (backpackName → contents[BACKPACK_SIZE]) */
    private final Map<UUID, Map<String, ItemStack[]>> backpacks = new HashMap<>();

    private BackpackManager() {}

    /**
     * Returns the single shared {@code BackpackManager} instance.
     *
     * @return the singleton instance
     */
    public static BackpackManager getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new empty backpack with the given name for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the backpack name, must not be null or blank
     * @return {@code true} if created, {@code false} if the player already has
     *         {@link #MAX_BACKPACKS} backpacks or a backpack with that name exists
     */
    public boolean createBackpack(UUID playerId, String name) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        Map<String, ItemStack[]> packs = backpacks.computeIfAbsent(playerId, id -> new HashMap<>());
        if (packs.containsKey(name)) {
            return false;
        }
        if (packs.size() >= MAX_BACKPACKS) {
            return false;
        }
        packs.put(name, new ItemStack[BACKPACK_SIZE]);
        return true;
    }

    /**
     * Returns a copy of the contents of the named backpack, or {@code null} if
     * it does not exist.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the backpack name, must not be null
     * @return cloned contents array, or {@code null} if not found
     */
    public ItemStack[] getContents(UUID playerId, String name) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        Map<String, ItemStack[]> packs = backpacks.get(playerId);
        if (packs == null) {
            return null;
        }
        ItemStack[] stored = packs.get(name);
        if (stored == null) {
            return null;
        }
        ItemStack[] copy = new ItemStack[BACKPACK_SIZE];
        for (int i = 0; i < BACKPACK_SIZE; i++) {
            copy[i] = stored[i] != null ? stored[i].clone() : null;
        }
        return copy;
    }

    /**
     * Saves (overwrites) the contents of the named backpack.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the backpack name, must not be null
     * @param contents the item stacks to store
     * @return {@code true} if saved, {@code false} if the backpack does not exist
     */
    public boolean saveContents(UUID playerId, String name, ItemStack[] contents) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(contents, "contents");
        Map<String, ItemStack[]> packs = backpacks.get(playerId);
        if (packs == null || !packs.containsKey(name)) {
            return false;
        }
        ItemStack[] snapshot = new ItemStack[BACKPACK_SIZE];
        for (int i = 0; i < BACKPACK_SIZE; i++) {
            snapshot[i] = (i < contents.length && contents[i] != null) ? contents[i].clone() : null;
        }
        packs.put(name, snapshot);
        return true;
    }

    /**
     * Deletes the named backpack for the player.
     *
     * @param playerId the player's UUID, must not be null
     * @param name     the backpack name, must not be null
     * @return {@code true} if the backpack existed and was removed
     */
    public boolean deleteBackpack(UUID playerId, String name) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(name, "name");
        Map<String, ItemStack[]> packs = backpacks.get(playerId);
        if (packs == null) {
            return false;
        }
        return packs.remove(name) != null;
    }

    /**
     * Returns an unmodifiable view of the backpack names owned by the player.
     *
     * @param playerId the player's UUID, must not be null
     * @return set of backpack names; empty if none exist
     */
    public Set<String> getBackpackNames(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        Map<String, ItemStack[]> packs = backpacks.get(playerId);
        if (packs == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(packs.keySet());
    }

    /** Removes all stored backpack data. */
    public void clear() {
        backpacks.clear();
    }
}
