package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class WardrobeMenu {

    /** Column-2 slots (rows 2–5) showing the equipped armour set, helmet → boots top-to-bottom. */
    private static final int[] ARMOR_SLOTS = {10, 19, 28, 37};

    public void open(Player player) {
        player.openInventory(buildMenu(player));
    }

    private Inventory buildMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Wardrobe");

        // getArmorContents() is ordered boots, leggings, chestplate, helmet; show helmet on top.
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < ARMOR_SLOTS.length && i < armor.length; i++) {
            ItemStack piece = armor[armor.length - 1 - i];
            if (piece != null) {
                inv.setItem(ARMOR_SLOTS[i], piece);
            }
        }

        return inv;
    }
}
