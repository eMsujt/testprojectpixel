package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class BazaarMenu {

    public void open(Player player) {
        player.openInventory(buildMenu());
    }

    private Inventory buildMenu() {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Bazaar");

        // Title item
        inv.setItem(4, makeItem(Material.GOLD_INGOT, "§6Bazaar"));

        // Category selector — Hypixel's Bazaar groupings
        inv.setItem(10, makeItem(Material.GOLDEN_HOE,     "§aFarming"));
        inv.setItem(11, makeItem(Material.STONE_PICKAXE,  "§aMining"));
        inv.setItem(12, makeItem(Material.IRON_SWORD,     "§aCombat"));
        inv.setItem(13, makeItem(Material.OAK_SAPLING,    "§aWoods & Fishes"));
        inv.setItem(14, makeItem(Material.QUARTZ,         "§aOdds & Ends"));
        inv.setItem(15, makeItem(Material.MAP,            "§aSpecial Items"));
        inv.setItem(16, makeItem(Material.ENCHANTED_BOOK, "§aEnchantments"));

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
