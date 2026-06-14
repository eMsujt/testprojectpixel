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

public final class StorageMenu implements InventoryHolder, Listener {

    // Slots 0-44 hold storage contents; the bottom row is reserved for navigation.
    private static final int CONTENT_SIZE = 45;
    private static final int PREV_SLOT = 45;
    private static final int NEXT_SLOT = 53;

    private final Inventory inventory;
    private final ItemStack[] contents;
    private final int pages;
    private int page;

    public StorageMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§aStorage");
        this.contents = player.getEnderChest().getContents();
        this.pages = Math.max(1, (int) Math.ceil((double) contents.length / CONTENT_SIZE));
        this.page = 0;
        render();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void render() {
        inventory.clear();
        int start = page * CONTENT_SIZE;
        for (int i = 0; i < CONTENT_SIZE; i++) {
            int source = start + i;
            if (source < contents.length) {
                inventory.setItem(i, contents[source]);
            }
        }
        if (page > 0) {
            inventory.setItem(PREV_SLOT, makeItem(Material.ARROW, "§aPrevious Page"));
        }
        if (page < pages - 1) {
            inventory.setItem(NEXT_SLOT, makeItem(Material.ARROW, "§aNext Page"));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof StorageMenu)) return;
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot == PREV_SLOT && page > 0) {
            page--;
            render();
        } else if (slot == NEXT_SLOT && page < pages - 1) {
            page++;
            render();
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
