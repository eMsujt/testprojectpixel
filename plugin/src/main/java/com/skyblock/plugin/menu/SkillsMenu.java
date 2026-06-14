package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class SkillsMenu implements InventoryHolder {

    private final Inventory inventory;

    public SkillsMenu() {
        this.inventory = Bukkit.createInventory(this, 54, "§bSkills");
        build();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build() {
        // The ten skills, laid out from slot 1
        inventory.setItem(1,  makeItem(Material.GOLDEN_HOE,        "§bFarming"));
        inventory.setItem(2,  makeItem(Material.STONE_PICKAXE,     "§bMining"));
        inventory.setItem(3,  makeItem(Material.STONE_SWORD,       "§bCombat"));
        inventory.setItem(4,  makeItem(Material.JUNGLE_SAPLING,    "§bForaging"));
        inventory.setItem(5,  makeItem(Material.FISHING_ROD,       "§bFishing"));
        inventory.setItem(6,  makeItem(Material.ENCHANTING_TABLE,  "§bEnchanting"));
        inventory.setItem(7,  makeItem(Material.BREWING_STAND,     "§bAlchemy"));
        inventory.setItem(8,  makeItem(Material.BONE,              "§bTaming"));
        inventory.setItem(9,  makeItem(Material.CRAFTING_TABLE,    "§bCarpentry"));
        inventory.setItem(10, makeItem(Material.MAGMA_CREAM,       "§bRunecrafting"));
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
