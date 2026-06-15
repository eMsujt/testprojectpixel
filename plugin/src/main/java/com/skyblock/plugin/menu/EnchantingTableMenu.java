package com.skyblock.plugin.menu;

import org.bukkit.entity.Player;

/**
 * @deprecated Use {@link com.skyblock.core.menu.EnchantingMenu} instead.
 */
@Deprecated
public final class EnchantingTableMenu {

    public void open(Player player) {
        new com.skyblock.core.menu.EnchantingMenu(player.getUniqueId()).open(player);
    }
}
