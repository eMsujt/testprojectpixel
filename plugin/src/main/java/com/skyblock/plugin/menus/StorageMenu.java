package com.skyblock.plugin.menus;

import com.skyblock.plugin.profile.PlayerProfile;
import com.skyblock.plugin.profile.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * The Ender Chest storage menu.
 *
 * <p>A 45-slot (5-row) interactive chest titled {@code §bEnder Chest} backed by
 * the player's {@link PlayerProfile} via {@link ProfileManager}. Unlike the
 * read-only display menus, this inventory is fully interactive: the player may
 * freely move items in and out.</p>
 *
 * <p>Contents are persisted on the profile as a per-slot {@code ItemStack[]}
 * snapshot rather than a live Bukkit {@link Inventory}: a fresh inventory is
 * populated from the snapshot each time the chest is opened, and the snapshot is
 * rewritten from the inventory when the player closes it (see
 * {@link #onClose(InventoryCloseEvent)}).</p>
 */
public final class StorageMenu implements Listener {

    /** The inventory title (supports colour codes). */
    public static final String TITLE = "§bEnder Chest";

    /** The number of storage slots (5 rows). */
    public static final int SIZE = 45;

    /**
     * Opens the player's Ender Chest, populating a fresh inventory from the
     * contents snapshot persisted on their profile.
     *
     * @param player the player to show the storage to
     */
    public static void open(Player player) {
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(player, SIZE, TITLE);
        if (profile.getEnderChestContents() != null) {
            inventory.setContents(profile.getEnderChestContents());
        }
        player.openInventory(inventory);
    }

    /**
     * Persists the Ender Chest contents back onto the player's profile when the
     * menu is closed, so edits survive the inventory being discarded.
     *
     * @param event the inventory close event
     */
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!TITLE.equals(event.getView().getTitle())) return;
        HumanEntity closer = event.getPlayer();
        if (!(closer instanceof Player player)) return;
        PlayerProfile profile = ProfileManager.getInstance().getOrCreate(player.getUniqueId());
        profile.setEnderChestContents(event.getInventory().getContents());
    }
}
