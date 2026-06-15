package com.skyblock.plugin.economy;

import com.skyblock.economy.CoinManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @deprecated Use {@link com.skyblock.core.menu.BankMenu} instead.
 */
@Deprecated
public final class BankMenu implements InventoryHolder, Listener {

    public BankMenu(Player player) {}

    public BankMenu(Player player, CoinManager coinManager, BankManager bankManager) {}

    public void open(Player player) {
        new com.skyblock.core.menu.BankMenu(player).open(player);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // no-op — canonical BankMenu handles clicks via MenuListener
    }
}
