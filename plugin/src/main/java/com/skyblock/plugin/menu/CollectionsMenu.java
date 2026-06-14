package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CollectionsMenu {

    public void open(Player player) {
        player.openInventory(buildMenu());
    }

    private Inventory buildMenu() {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Collections");

        // Six category icons centered in row 3
        inv.setItem(20, makeItem(Material.WHEAT,        "§6Farming"));
        inv.setItem(21, makeItem(Material.COBBLESTONE,  "§6Mining"));
        inv.setItem(22, makeItem(Material.ROTTEN_FLESH, "§6Combat"));
        inv.setItem(23, makeItem(Material.OAK_LOG,      "§6Foraging"));
        inv.setItem(24, makeItem(Material.COD,          "§6Fishing"));
        inv.setItem(25, makeItem(Material.ENDER_PEARL,  "§6Rift"));

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
