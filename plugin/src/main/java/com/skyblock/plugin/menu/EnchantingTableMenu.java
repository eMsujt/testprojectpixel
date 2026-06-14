package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class EnchantingTableMenu {

    /** Slot that accepts the item being enchanted; left empty for player input. */
    private static final int INPUT_SLOT = 22;

    public void open(Player player) {
        player.openInventory(buildMenu());
    }

    private Inventory buildMenu() {
        Inventory inv = Bukkit.createInventory(null, 54, "§5Enchanting Table");

        // Slot 22 is left empty so the player can drop in the item to enchant.
        inv.setItem(INPUT_SLOT, null);

        return inv;
    }
}
