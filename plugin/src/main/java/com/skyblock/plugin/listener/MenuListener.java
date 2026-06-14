package com.skyblock.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Cancels item movement inside the plugin's chest-GUI menus.
 *
 * <p>A single shared listener registered once in
 * {@link com.skyblock.plugin.SkyBlockPlugin#onEnable()} so individual menu
 * classes in {@code com.skyblock.plugin.menu} do not each need to implement
 * {@link Listener}.</p>
 */
public final class MenuListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder != null && holder.getClass().getName().startsWith("com.skyblock.plugin.menu.")) {
            event.setCancelled(true);
        }
    }
}
