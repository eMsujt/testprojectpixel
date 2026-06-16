package com.skyblock.core.menu.manager;

import com.skyblock.core.menu.SkyBlockMainMenu;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Singleton façade for opening the central SkyBlock menu.
 *
 * <p>Delegates inventory tracking to the shared {@link MenuManager}
 * and always opens a fresh {@link SkyBlockMainMenu} instance.</p>
 */
public final class SkyBlockMenuManager {

    private static final SkyBlockMenuManager INSTANCE = new SkyBlockMenuManager();

    private final MenuManager menuManager;

    private SkyBlockMenuManager() {
        this.menuManager = MenuManager.getInstance();
    }

    public static SkyBlockMenuManager getInstance() {
        return INSTANCE;
    }

    /**
     * Opens the SkyBlock main menu for {@code player}.
     *
     * @param player the player to show the menu to, must not be null
     */
    public void openMainMenu(Player player) {
        Objects.requireNonNull(player, "player");
        menuManager.openMenu(player, new SkyBlockMainMenu(player));
    }
}
