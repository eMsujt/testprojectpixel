package com.skyblock.plugin.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.BazaarMenu} instead.
 */
@Deprecated
public final class BazaarMenu implements InventoryHolder {

    private com.skyblock.core.menu.BazaarMenu delegate;

    public void open(Player player) {
        new com.skyblock.core.menu.BazaarMenu(player).open(player);
    }

    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException("Use open(Player) to show BazaarMenu.");
    }
}
