package com.skyblock.plugin.economy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.BazaarMenu} instead.
 */
@Deprecated
public final class BazaarMenu implements InventoryHolder {

    private final com.skyblock.core.menu.BazaarMenu delegate;

    public BazaarMenu(Player player) {
        this.delegate = new com.skyblock.core.menu.BazaarMenu(player);
    }

    public void open(Player player) {
        delegate.open(player);
    }

    @Override
    public Inventory getInventory() {
        return delegate.getInventory();
    }
}
