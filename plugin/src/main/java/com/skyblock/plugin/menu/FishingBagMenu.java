package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class FishingBagMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    public FishingBagMenu() {
        this.inventory = Bukkit.createInventory(this, 45, "§9Fishing Bag");
        build();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build() {
        // Rows 1-4 (slots 0-35) hold the player's fishing items and are left empty.
        // The final row (slots 36-44) is the controls row.
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 36; slot < 45; slot++) {
            inventory.setItem(slot, pane);
        }

        inventory.setItem(36, makeItem(Material.ARROW, "§aGo Back"));
        inventory.setItem(44, makeItem(Material.BARRIER, "§cClose"));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof FishingBagMenu) {
            event.setCancelled(true);
        }
    }

    private ItemStack makeItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
