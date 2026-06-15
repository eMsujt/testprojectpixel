package com.skyblock.core.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Abstract base for all SkyBlock GUI menus managed by {@link MenuManager}.
 *
 * <p>Subclasses implement {@link #open(Player)} to build and display the
 * inventory, and {@link #handleClick(InventoryClickEvent)} to handle slot
 * clicks within that inventory.</p>
 */
public abstract class Menu {

    /**
     * Opens this menu for {@code player}, creating and displaying the
     * backing {@link org.bukkit.inventory.Inventory}.
     *
     * @param player the player to show the menu to, must not be null
     */
    public abstract void open(Player player);

    /**
     * Called when a player clicks inside this menu's inventory.
     *
     * @param event the click event, must not be null
     */
    public abstract void handleClick(InventoryClickEvent event);
}
