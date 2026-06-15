package com.skyblock.plugin.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.SkyBlockMainMenu} instead.
 */
@Deprecated
public final class SkyBlockMenu implements InventoryHolder, Listener {

    /** @deprecated Use {@link com.skyblock.core.menu.SkyBlockMainMenu} instead. */
    @Deprecated
    public SkyBlockMenu() {}

    public void open(Player player) {
        new com.skyblock.core.menu.SkyBlockMainMenu(player).open(player);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof SkyBlockMenu) {
            event.setCancelled(true);
        }
    }
}
