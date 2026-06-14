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

import java.util.List;

public final class WardrobeMenu implements InventoryHolder, Listener {

    /** Nine saved-armour wardrobe slots laid out across the central row. */
    private static final int[] SLOTS = {19, 20, 21, 22, 23, 24, 25, 28, 29};

    private final Inventory inventory;

    public WardrobeMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§9Wardrobe");
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
        for (int i = 0; i < SLOTS.length; i++) {
            inventory.setItem(SLOTS[i], makeSlot(i + 1));
        }
    }

    private ItemStack makeSlot(int number) {
        ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§eWardrobe Slot " + number);
            meta.setLore(List.of("§7Click to equip this armour set."));
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof WardrobeMenu) {
            event.setCancelled(true);
        }
    }
}
