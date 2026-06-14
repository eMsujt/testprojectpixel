package com.skyblock.plugin.menus;

import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * The Ender Chest storage menu.
 *
 * <p>A 45-slot (5-row) interactive chest titled {@code §bEnder Chest} backed by
 * the player's {@link PlayerProfile} via {@link ProfileManager}. Unlike the
 * read-only display menus, this inventory is fully interactive: the player may
 * freely move items in and out. The backing inventory is created once per
 * profile and cached, so Bukkit mutates it in place and its contents persist
 * across openings.</p>
 */
public final class StorageMenu {

    /** The inventory title (supports colour codes). */
    public static final String TITLE = "§bEnder Chest";

    /** The number of storage slots (5 rows). */
    public static final int SIZE = 45;

    private StorageMenu() {}

    /**
     * Opens the player's Ender Chest, creating and caching the backing
     * inventory on their profile if one does not exist yet.
     *
     * @param player the player to show the storage to
     */
    public static void open(Player player) {
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        Inventory inventory = profile.getEnderChest();
        if (inventory == null) {
            inventory = Bukkit.createInventory(player, SIZE, TITLE);
            profile.setEnderChest(inventory);
        }
        player.openInventory(inventory);
    }
}
