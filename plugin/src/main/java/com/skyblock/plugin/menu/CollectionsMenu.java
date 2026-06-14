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
        Inventory inv = Bukkit.createInventory(null, 54, "§aCollections");

        // Top row — the five collection categories, centered in slots 2-6
        inv.setItem(2, makeItem(Material.WHEAT,        "§eFarming"));
        inv.setItem(3, makeItem(Material.COBBLESTONE,  "§eMining"));
        inv.setItem(4, makeItem(Material.ROTTEN_FLESH, "§eCombat"));
        inv.setItem(5, makeItem(Material.OAK_LOG,      "§eForaging"));
        inv.setItem(6, makeItem(Material.COD,          "§eFishing"));

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
