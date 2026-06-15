package com.skyblock.plugin.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.BazaarMenu} instead.
 */
@Deprecated
public class BazaarMenu implements InventoryHolder {

    public void open(Player player) {
        new com.skyblock.core.menu.BazaarMenu(player).open(player);
    }

    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException("Use open(Player) to show BazaarMenu.");
    }
}
