package com.skyblock.plugin.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.PetsMenu} instead.
 */
@Deprecated
public final class PetsMenu implements InventoryHolder {

    public PetsMenu(Player player) {}

    public void open(Player player) {
        new com.skyblock.core.menu.PetsMenu(player).open(player);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
