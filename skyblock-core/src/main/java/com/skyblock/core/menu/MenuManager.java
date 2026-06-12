package com.skyblock.core.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton that manages open {@link SkyBlockMenu} instances for each player.
 */
public final class MenuManager {

    /**
     * Abstract base for all SkyBlock GUI menus.
     *
     * <p>Subclasses must implement {@link #open(Player)} to build and display
     * the inventory, and {@link #onClick(InventoryClickEvent)} to handle slot
     * clicks within that inventory.</p>
     */
    public static abstract class SkyBlockMenu {

        /**
         * Opens this menu for {@code player}, creating and displaying the
         * backing {@link Inventory}.
         *
         * @param player the player to show the menu to, must not be null
         */
        public abstract void open(Player player);

        /**
         * Called when a player clicks inside this menu's inventory.
         *
         * @param event the click event, must not be null
         */
        public abstract void onClick(InventoryClickEvent event);
    }

    private static final MenuManager INSTANCE = new MenuManager();

    /** Maps player UUID → the SkyBlockMenu currently open for that player. */
    private final Map<UUID, SkyBlockMenu> openMenus = new HashMap<>();

    private MenuManager() {}

    /**
     * Returns the single shared {@code MenuManager} instance.
     *
     * @return the singleton instance
     */
    public static MenuManager getInstance() {
        return INSTANCE;
    }

    /**
     * Opens {@code menu} for {@code player} and records it as the active menu.
     *
     * @param player the player opening the menu, must not be null
     * @param menu   the menu to open, must not be null
     */
    public void openMenu(Player player, SkyBlockMenu menu) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(menu, "menu");
        openMenus.put(player.getUniqueId(), menu);
        menu.open(player);
    }

    /**
     * Returns the active menu for {@code player}, or {@code null} if none.
     *
     * @param player the player to query
     * @return the open menu, or {@code null}
     */
    public SkyBlockMenu getOpenMenu(Player player) {
        Objects.requireNonNull(player, "player");
        return openMenus.get(player.getUniqueId());
    }

    /** Removes the active menu record for {@code player} without closing the inventory. */
    public void closeMenu(Player player) {
        Objects.requireNonNull(player, "player");
        openMenus.remove(player.getUniqueId());
    }
}
