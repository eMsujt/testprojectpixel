package com.skyblock.core.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class InventoryListener implements Listener {

    private static final InventoryListener INSTANCE = new InventoryListener();

    private InventoryListener() {}

    public static InventoryListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
