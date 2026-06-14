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
        Inventory inv = Bukkit.createInventory(null, 54, "§eCollections");

        // Category selector — 10 icons centered across two rows
        inv.setItem(20, makeItem(Material.WHEAT,        "§aFarming"));
        inv.setItem(21, makeItem(Material.COBBLESTONE,  "§aMining"));
        inv.setItem(22, makeItem(Material.ROTTEN_FLESH, "§aCombat"));
        inv.setItem(23, makeItem(Material.OAK_LOG,      "§aForaging"));
        inv.setItem(24, makeItem(Material.COD,          "§aFishing"));
        inv.setItem(29, makeItem(Material.GUNPOWDER,    "§aRift"));
        inv.setItem(30, makeItem(Material.IRON_SWORD,   "§aSlayer"));
        inv.setItem(31, makeItem(Material.STONE_BRICKS, "§aDungeoneering"));
        inv.setItem(32, makeItem(Material.SUGAR_CANE,   "§aAlchemy"));
        inv.setItem(33, makeItem(Material.ENCHANTING_TABLE, "§aEnchanting"));

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
