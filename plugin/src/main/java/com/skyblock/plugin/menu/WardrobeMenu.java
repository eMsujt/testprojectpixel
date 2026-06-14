package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class WardrobeMenu implements InventoryHolder {

    /** Column-2 slots (rows 2–5) showing the equipped armour set, helmet → boots top-to-bottom. */
    private static final int[] ARMOR_SLOTS = {10, 19, 28, 37};

    private final Inventory inventory;

    public WardrobeMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§6Wardrobe");
        build(player);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build(Player player) {
        // getArmorContents() is ordered boots, leggings, chestplate, helmet; show helmet on top.
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < ARMOR_SLOTS.length && i < armor.length; i++) {
            ItemStack piece = armor[armor.length - 1 - i];
            if (piece != null) {
                inventory.setItem(ARMOR_SLOTS[i], piece);
            }
        }
    }
}
