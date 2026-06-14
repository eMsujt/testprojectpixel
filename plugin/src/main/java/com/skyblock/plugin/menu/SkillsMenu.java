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
        Inventory inv = Bukkit.createInventory(null, 54, "§bSkills");

        // The ten skills, laid out from slot 1
        inv.setItem(1,  makeItem(Material.GOLDEN_HOE,        "§bFarming"));
        inv.setItem(2,  makeItem(Material.STONE_PICKAXE,     "§bMining"));
        inv.setItem(3,  makeItem(Material.STONE_SWORD,       "§bCombat"));
        inv.setItem(4,  makeItem(Material.JUNGLE_SAPLING,    "§bForaging"));
        inv.setItem(5,  makeItem(Material.FISHING_ROD,       "§bFishing"));
        inv.setItem(6,  makeItem(Material.ENCHANTING_TABLE,  "§bEnchanting"));
        inv.setItem(7,  makeItem(Material.BREWING_STAND,     "§bAlchemy"));
        inv.setItem(8,  makeItem(Material.BONE,              "§bTaming"));
        inv.setItem(9,  makeItem(Material.CRAFTING_TABLE,    "§bCarpentry"));
        inv.setItem(10, makeItem(Material.MAGMA_CREAM,       "§bRunecrafting"));

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
