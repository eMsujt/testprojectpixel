package com.skyblock.core.listener;

import com.skyblock.core.manager.BankManager;
import com.skyblock.core.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        if (!(holder instanceof Menu)) return;
        event.setCancelled(true);
        ((Menu) holder).handleClick(event);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();
        long purse = bankManager.getPurseBalance(uuid);
        long penalty = (long) (purse * 0.05);
        if (penalty > 0) {
            bankManager.removeFromPurse(uuid, penalty);
            player.sendMessage("§cYou lost §6" + penalty + " coins §cfrom your purse on death.");
        }
    }
}
