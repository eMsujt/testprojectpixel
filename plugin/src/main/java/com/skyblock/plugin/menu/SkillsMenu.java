package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class SkillsMenu {

    public void open(Player player) {
        player.openInventory(buildMenu());
    }

    private Inventory buildMenu() {
        Inventory inv = Bukkit.createInventory(null, 54, "§aSkills");

        // Row 2: the seven core skills, centered in slots 10-16
        inv.setItem(10, makeItem(Material.GOLDEN_HOE,        "§aFarming"));
        inv.setItem(11, makeItem(Material.STONE_PICKAXE,     "§aMining"));
        inv.setItem(12, makeItem(Material.STONE_SWORD,       "§aCombat"));
        inv.setItem(13, makeItem(Material.JUNGLE_SAPLING,    "§aForaging"));
        inv.setItem(14, makeItem(Material.FISHING_ROD,       "§aFishing"));
        inv.setItem(15, makeItem(Material.ENCHANTING_TABLE,  "§aEnchanting"));
        inv.setItem(16, makeItem(Material.BREWING_STAND,     "§aAlchemy"));

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
