package com.skyblock.plugin.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.CollectionsMenu} instead.
 */
@Deprecated
public final class CollectionMenu implements InventoryHolder {

    private final com.skyblock.core.menu.CollectionsMenu delegate;

    public CollectionMenu(Player player) {
        this.delegate = new com.skyblock.core.menu.CollectionsMenu(player.getUniqueId());
    }

    public void open(Player player) {
        delegate.open(player);
    }

    @Override
    public Inventory getInventory() {
        return delegate.getInventory();
    }
}
