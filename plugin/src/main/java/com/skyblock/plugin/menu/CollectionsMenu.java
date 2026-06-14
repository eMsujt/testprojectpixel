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

        // Category selector
        inv.setItem(11, makeItem(Material.WHEAT,        "§aFarming"));
        inv.setItem(12, makeItem(Material.COBBLESTONE,  "§aMining"));
        inv.setItem(13, makeItem(Material.ROTTEN_FLESH, "§aCombat"));
        inv.setItem(14, makeItem(Material.OAK_LOG,      "§aForaging"));
        inv.setItem(15, makeItem(Material.COD,          "§aFishing"));
        inv.setItem(16, makeItem(Material.GUNPOWDER,    "§aRift"));

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
