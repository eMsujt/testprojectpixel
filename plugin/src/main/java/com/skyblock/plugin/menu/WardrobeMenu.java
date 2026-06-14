package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class WardrobeMenu {

    public void open(Player player) {
        player.openInventory(buildMenu());
    }

    private Inventory buildMenu() {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Wardrobe");

        // 20 armor-set slots, each occupying a single slot, laid out across the
        // three interior rows (slots 9–28) matching Hypixel's wardrobe page.
        for (int set = 0; set < 20; set++) {
            inv.setItem(9 + set, makeItem(Material.LEATHER_CHESTPLATE, "§6Armor Set " + (set + 1)));
        }

        return inv;
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
