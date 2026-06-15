package com.skyblock.plugin.gui.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.MinionsMenu} instead.
 */
@Deprecated
public class MinionsMenu implements InventoryHolder {

    private final com.skyblock.core.menu.MinionsMenu delegate;

    public MinionsMenu(Player player) {
        this.delegate = new com.skyblock.core.menu.MinionsMenu(player);
    }

    public void open(Player player) {
        delegate.open(player);
    }

    @Override
    public Inventory getInventory() {
        return delegate.getInventory();
    }
}
