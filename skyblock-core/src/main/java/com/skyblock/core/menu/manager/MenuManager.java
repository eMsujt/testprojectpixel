package com.skyblock.core.menu.manager;

import com.skyblock.core.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Singleton that manages open {@link Menu} instances for each player.
 */
public final class MenuManager {

    private static final MenuManager INSTANCE = new MenuManager();

    /** Maps player UUID → the Menu currently open for that player. */
    private final Map<UUID, Menu> openMenus = new HashMap<>();

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
    public void openMenu(Player player, Menu menu) {
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
    public Menu getOpenMenu(Player player) {
        Objects.requireNonNull(player, "player");
        return openMenus.get(player.getUniqueId());
    }

    /** Removes the active menu record for {@code player} without closing the inventory. */
    public void closeMenu(Player player) {
        Objects.requireNonNull(player, "player");
        openMenus.remove(player.getUniqueId());
    }
}
