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
        Inventory inv = Bukkit.createInventory(null, 54, "§6Your Collections");

        // Top row: category selector
        inv.setItem(0, makeItem(Material.WHEAT,        "Farming"));
        inv.setItem(1, makeItem(Material.COBBLESTONE,  "Mining"));
        inv.setItem(2, makeItem(Material.ROTTEN_FLESH, "Combat"));
        inv.setItem(3, makeItem(Material.OAK_LOG,      "Foraging"));
        inv.setItem(4, makeItem(Material.COD,          "Fishing"));
        inv.setItem(5, makeItem(Material.GUNPOWDER,    "Rift"));

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
