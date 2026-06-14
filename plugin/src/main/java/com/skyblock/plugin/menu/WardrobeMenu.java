package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class WardrobeMenu {

    /** Slots showing the player's four equipped armour pieces (boots → helmet). */
    private static final int[] ARMOR_SLOTS = {10, 11, 12, 13};

    public void open(Player player) {
        player.openInventory(buildMenu(player));
    }

    private Inventory buildMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§6Wardrobe");

        // getArmorContents() is ordered boots, leggings, chestplate, helmet.
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < ARMOR_SLOTS.length && i < armor.length; i++) {
            if (armor[i] != null) {
                inv.setItem(ARMOR_SLOTS[i], armor[i]);
            }
        }

        return inv;
    }
}
