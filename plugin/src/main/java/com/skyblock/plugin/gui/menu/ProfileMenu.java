package com.skyblock.plugin.gui.menu;

import org.bukkit.entity.Player;

/**
 * @deprecated Use {@link com.skyblock.core.menu.ProfileMenu} instead.
 */
@Deprecated
public final class ProfileMenu {

    private final com.skyblock.core.menu.ProfileMenu delegate;

    public ProfileMenu(Player player) {
        this.delegate = new com.skyblock.core.menu.ProfileMenu(player);
    }

    public void open(Player player) {
        delegate.open(player);
    }
}
