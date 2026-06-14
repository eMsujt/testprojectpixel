package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class StorageMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    public StorageMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§9Ender Chest");
        build(player);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build(Player player) {
        // Mirror the player's existing ender chest contents into this viewer.
        ItemStack[] contents = player.getEnderChest().getContents();
        for (int slot = 0; slot < contents.length && slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, contents[slot]);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof StorageMenu)) return;
        event.setCancelled(true);
    }
}
