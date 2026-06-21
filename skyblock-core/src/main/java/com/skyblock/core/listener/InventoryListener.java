package com.skyblock.core.listener;

import com.skyblock.core.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

/**
 * Handles general {@link InventoryClickEvent} cases not covered by
 * {@link com.skyblock.core.menu.listener.MenuListener}: prevents players from
 * moving items out of non-player, non-crafting inventories that are not SkyBlock
 * {@link Menu} instances (e.g. raw chest/hopper views).
 */
public final class InventoryListener implements Listener {

    private static final InventoryListener INSTANCE = new InventoryListener();

    private InventoryListener() {}

    public static InventoryListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        InventoryHolder holder = event.getInventory().getHolder();

        // Menu instances are handled by MenuListener — skip them here.
        if (holder instanceof Menu) {
            return;
        }

        InventoryType type = event.getInventory().getType();
        // Allow clicks in the player's own inventory and crafting grid.
        if (type == InventoryType.PLAYER || type == InventoryType.CRAFTING) {
            return;
        }

        // Cancel all other inventory interactions to prevent item duplication
        // or unintended access to world containers.
        event.setCancelled(true);
    }
}
