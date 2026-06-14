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
        Inventory inv = Bukkit.createInventory(null, 54, "§aWardrobe");

        // 9 armor-set columns, each occupying one of the 9 slots across 4 rows:
        // helmet (row 1), chestplate (row 2), leggings (row 3), boots (row 4).
        for (int column = 0; column < 9; column++) {
            inv.setItem(column,      makeItem(Material.LEATHER_HELMET,     "§aArmor Set " + (column + 1)));
            inv.setItem(column + 9,  makeItem(Material.LEATHER_CHESTPLATE, "§aArmor Set " + (column + 1)));
            inv.setItem(column + 18, makeItem(Material.LEATHER_LEGGINGS,   "§aArmor Set " + (column + 1)));
            inv.setItem(column + 27, makeItem(Material.LEATHER_BOOTS,      "§aArmor Set " + (column + 1)));
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
