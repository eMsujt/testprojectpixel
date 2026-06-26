package com.skyblock.core.listener;

import com.skyblock.core.manager.BankManager;
import com.skyblock.core.menu.Menu;
import com.skyblock.core.util.ChatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public final class PlayerEventListener implements Listener {

    private static final PlayerEventListener INSTANCE = new PlayerEventListener();

    private final BankManager bankManager = BankManager.getInstance();

    private PlayerEventListener() {}

    public static PlayerEventListener getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Menu menu)) return;
        // Menus are click-locked by default; a menu may opt specific slots into
        // free item placement (Menu.isInteractiveSlot). Only a plain left/right
        // place-or-pickup in the TOP inventory on such a slot is left un-cancelled,
        // so shift-click / number-key / drag / double-click can't dupe or smuggle
        // items into locked slots.
        boolean interactive = event.getClickedInventory() != null
                && event.getClickedInventory().equals(event.getView().getTopInventory())
                && menu.isInteractiveSlot(event.getSlot())
                && (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT);
        event.setCancelled(!interactive);
        menu.handleClick(event);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Menu menu)) return;
        // Cancel any drag touching a non-interactive slot in the top inventory.
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
            // Lets interactive menus (crafting grid, reforge) return leftover items.
            menu.onClose(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        long purse = bankManager.getPurseBalance(uuid);
        long penalty = (long) (purse * 0.05);
        if (penalty > 0) {
            bankManager.removeFromPurse(uuid, penalty);
            ChatUtil.sendError(player, "You lost §6" + penalty + " coins §cfrom your purse on death.");
        }
    }
}
