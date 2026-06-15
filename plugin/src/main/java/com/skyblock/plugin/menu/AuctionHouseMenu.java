package com.skyblock.plugin.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.AuctionHouseMenu} instead.
 */
@Deprecated
public final class AuctionHouseMenu implements InventoryHolder, Listener {

    /** @deprecated Use {@link com.skyblock.core.menu.AuctionHouseMenu} instead. */
    @Deprecated
    public AuctionHouseMenu() {}

    public void open(Player player) {
        new com.skyblock.core.menu.AuctionHouseMenu().open(player);
    }

    @Override
    public Inventory getInventory() {
        return new com.skyblock.core.menu.AuctionHouseMenu().getInventory();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof AuctionHouseMenu) {
            event.setCancelled(true);
        }
    }
}
