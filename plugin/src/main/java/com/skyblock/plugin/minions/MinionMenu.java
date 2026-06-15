package com.skyblock.plugin.minions;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.MinionMenu} instead.
 */
@Deprecated
public class MinionMenu implements InventoryHolder {

    private final com.skyblock.core.menu.MinionsMenu delegate;

    public MinionMenu(Player player) {
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
