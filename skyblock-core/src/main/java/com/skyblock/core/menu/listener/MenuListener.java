package com.skyblock.core.menu.listener;

import com.skyblock.core.menu.Menu;
import com.skyblock.core.menu.manager.MenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Routes inventory click and close events to every {@link Menu} instance,
 * whether opened via {@link MenuManager} or directly via {@link Menu#open}.
 */
public final class MenuListener implements Listener {

    private final MenuManager menuManager;

    public MenuListener(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Menu menu)) {
            return;
        }
        // By default every click is cancelled (menus are click-locked). A menu may
        // opt specific slots into free item placement; only a plain left/right
        // place-or-pickup in the TOP inventory on such a slot is left un-cancelled,
        // so shift-click / number-key / drag / double-click can't dupe or smuggle
        // items into non-interactive slots.
        boolean interactive = event.getClickedInventory() != null
                && event.getClickedInventory().equals(event.getView().getTopInventory())
                && menu.isInteractiveSlot(event.getSlot())
                && isSafeClick(event.getClick());
        event.setCancelled(!interactive);
        menu.handleClick(event);
    }

    /** Only simple place/pickup clicks are allowed on interactive slots. */
    private static boolean isSafeClick(ClickType click) {
        return click == ClickType.LEFT || click == ClickType.RIGHT;
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Menu menu)) {
            return;
        }
        // Cancel any drag that touches a non-interactive slot in the top inventory.
        int topSize = event.getView().getTopInventory().getSize();
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < topSize && !menu.isInteractiveSlot(rawSlot)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Menu menu && event.getPlayer() instanceof Player player) {
            menu.onClose(player);
        }
        if (event.getPlayer() instanceof Player player) {
            menuManager.closeMenu(player);
        }
    }
}
