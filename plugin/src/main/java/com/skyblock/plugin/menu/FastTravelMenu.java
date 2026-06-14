package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class FastTravelMenu {

    public void open(Player player) {
        player.openInventory(buildMenu());
    }

    private Inventory buildMenu() {
        Inventory inv = Bukkit.createInventory(null, 54, "§bFast Travel");

        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                inv.setItem(slot, pane);
            }
        }

        inv.setItem(13, makeItem(Material.COMPASS,        "§bHub Island"));
        inv.setItem(20, makeItem(Material.HAY_BLOCK,      "§bThe Farming Islands"));
        inv.setItem(22, makeItem(Material.JUNGLE_SAPLING, "§bThe Park"));
        inv.setItem(24, makeItem(Material.STRING,         "§bSpider's Den"));
        inv.setItem(29, makeItem(Material.ENDER_PEARL,    "§bThe End"));
        inv.setItem(31, makeItem(Material.NETHERRACK,     "§bCrimson Isle"));
        inv.setItem(33, makeItem(Material.COBBLESTONE,    "§bDeep Caverns"));

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
