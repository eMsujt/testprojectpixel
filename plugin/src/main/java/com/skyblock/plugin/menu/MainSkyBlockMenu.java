package com.skyblock.plugin.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.SkyBlockMainMenu} instead.
 */
@Deprecated
public final class MainSkyBlockMenu implements InventoryHolder {

    /** @deprecated Use {@link com.skyblock.core.menu.SkyBlockMainMenu} instead. */
    @Deprecated
    public MainSkyBlockMenu() {}

    public void open(Player player) {
        new com.skyblock.core.menu.SkyBlockMainMenu(player).open(player);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
