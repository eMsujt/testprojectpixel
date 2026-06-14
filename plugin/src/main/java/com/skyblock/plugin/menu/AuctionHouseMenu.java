package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class AuctionHouseMenu {

    public void open(Player player) {
        player.openInventory(buildMenu());
    }

    private Inventory buildMenu() {
        Inventory inv = Bukkit.createInventory(null, 54, "§eAuction House");

        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                inv.setItem(slot, pane);
            }
        }

        // Top row — controls
        inv.setItem(1, makeItem(Material.NAME_TAG,          "§aSearch"));
        inv.setItem(3, makeItem(Material.GOLD_INGOT,        "§6Manage Auctions"));
        inv.setItem(4, makeItem(Material.GOLDEN_HORSE_ARMOR,"§eAuctions Browser"));
        inv.setItem(5, makeItem(Material.HOPPER,            "§eSort: §aHigh Bid"));
        inv.setItem(7, makeItem(Material.OAK_SIGN,          "§aView Bids"));

        // Category selectors
        inv.setItem(46, makeItem(Material.GOLDEN_SWORD,     "§cWeapons"));
        inv.setItem(47, makeItem(Material.GOLDEN_CHESTPLATE,"§bArmor"));
        inv.setItem(48, makeItem(Material.DIAMOND_PICKAXE,  "§eTools"));
        inv.setItem(49, makeItem(Material.ENCHANTED_BOOK,   "§dEnchanted Books"));
        inv.setItem(50, makeItem(Material.WHEAT,            "§aConsumables"));
        inv.setItem(51, makeItem(Material.DIRT,             "§6Blocks"));
        inv.setItem(52, makeItem(Material.BARRIER,          "§7Misc"));

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
