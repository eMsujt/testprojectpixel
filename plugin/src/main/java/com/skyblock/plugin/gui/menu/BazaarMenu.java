package com.skyblock.plugin.gui.menu;

import com.skyblock.plugin.bazaar.BazaarManager.Product;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

/**
 * @deprecated Use {@link com.skyblock.core.menu.BazaarMenu} instead.
 */
@Deprecated
public class BazaarMenu implements InventoryHolder {

    private final com.skyblock.core.menu.BazaarMenu delegate;

    public BazaarMenu(Player player, List<Product> products) {
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
