package com.skyblock.plugin.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * @deprecated Use {@link com.skyblock.core.menu.BankMenu} instead.
 * Clicks are now routed via {@link com.skyblock.core.menu.MenuListener}.
 */
@Deprecated
public final class BankMenu implements Listener {

    /** No-arg constructor kept for legacy listener registration; does nothing. */
    BankMenu() {}

    public BankMenu(Player player) {}

    public void open(Player player) {
        new com.skyblock.core.menu.BankMenu(player).open(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        // no-op — canonical BankMenu handles clicks via MenuListener
    }
}
