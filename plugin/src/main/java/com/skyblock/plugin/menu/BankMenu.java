package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class BankMenu {

    public void open(Player player) {
        player.openInventory(buildMenu());
    }

    private Inventory buildMenu() {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Bank");

        inv.setItem(4,  makeItem(Material.GOLD_NUGGET, "§ePurse"));
        inv.setItem(20, makeItem(Material.GOLD_INGOT,  "§aDeposit Coins"));
        inv.setItem(24, makeItem(Material.REDSTONE,    "§cWithdraw Coins"));
        inv.setItem(40, makeItem(Material.BOOK,        "§6Transaction Log"));

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
