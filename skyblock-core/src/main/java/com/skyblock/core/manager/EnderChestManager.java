package com.skyblock.core.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Singleton tracking each player's SkyBlock Ender Chest storage.
 *
 * <p>Each player owns a single 54-slot chest inventory, created lazily the first
 * time it is requested and retained for the lifetime of the manager so its
 * contents persist across re-opens.</p>
 *
 * <p>Not thread-safe; synchronize externally if accessed from multiple threads.</p>
 */
public final class EnderChestManager {

    /** Number of slots in a player's Ender Chest (a double chest). */
    public static final int CHEST_SIZE = 54;

    /** Title shown on the Ender Chest inventory. */
    public static final String CHEST_TITLE = "Ender Chest";

    private static final EnderChestManager INSTANCE = new EnderChestManager();

    private final Map<UUID, Inventory> chests = new HashMap<>();

    private EnderChestManager() {}

    /**
     * Returns the single shared {@code EnderChestManager} instance.
     *
     * @return the singleton instance
     */
    public static EnderChestManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the Ender Chest inventory for a player, creating it lazily on first
     * access.
     *
     * @param playerId the player's unique id
     * @return the player's persistent Ender Chest inventory
     */
    public Inventory getChest(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        return chests.computeIfAbsent(playerId,
                id -> Bukkit.createInventory(null, CHEST_SIZE, CHEST_TITLE));
    }

    /**
     * Opens a player's Ender Chest, creating it lazily if needed.
     *
     * @param player the player to open the chest for
     */
    public void open(Player player) {
        Objects.requireNonNull(player, "player");
        player.openInventory(getChest(player.getUniqueId()));
    }

    /**
     * Returns whether an Ender Chest has been created for the given player.
     *
     * @param playerId the player's unique id
     * @return {@code true} if a chest exists for the player
     */
    public boolean hasChest(UUID playerId) {
        return chests.containsKey(playerId);
    }

    /**
     * Discards a player's cached Ender Chest, typically on logout to free memory.
     *
     * @param playerId the player's unique id
     */
    public void unload(UUID playerId) {
        chests.remove(playerId);
    }
}
