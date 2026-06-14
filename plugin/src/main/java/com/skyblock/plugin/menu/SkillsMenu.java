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
        Inventory inv = Bukkit.createInventory(null, 54, "§bYour Skills");

        // Row 1: gathering skills
        inv.setItem(11, makeItem(Material.GOLDEN_HOE,        "§aFarming"));
        inv.setItem(12, makeItem(Material.STONE_PICKAXE,     "§aMining"));
        inv.setItem(13, makeItem(Material.STONE_SWORD,       "§aCombat"));
        inv.setItem(14, makeItem(Material.JUNGLE_SAPLING,    "§aForaging"));
        inv.setItem(15, makeItem(Material.FISHING_ROD,       "§aFishing"));

        // Row 2: crafting / utility skills
        inv.setItem(20, makeItem(Material.ENCHANTING_TABLE,  "§aEnchanting"));
        inv.setItem(21, makeItem(Material.BREWING_STAND,     "§aAlchemy"));
        inv.setItem(22, makeItem(Material.LEAD,              "§aTaming"));
        inv.setItem(23, makeItem(Material.CRAFTING_TABLE,    "§aCarpentry"));
        inv.setItem(24, makeItem(Material.MAGMA_CREAM,       "§aRunecrafting"));

        // Row 3: social skill
        inv.setItem(31, makeItem(Material.EMERALD,           "§aSocial"));

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
