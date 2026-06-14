package com.skyblock.plugin.menu;

import com.skyblock.plugin.accessories.AccessoryBagManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class AccessoryBagMenu {

    /** Inner slots (between the border), one per accessory. */
    private static final int[] SLOTS = {
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    public void open(Player player) {
        player.openInventory(buildMenu(player));
    }

    private Inventory buildMenu(Player player) {
        List<String> accessories = AccessoryBagManager.getInstance().getEquipped(player.getUniqueId());

        int totalPages = Math.max(1, (accessories.size() + SLOTS.length - 1) / SLOTS.length);
        Inventory inv = Bukkit.createInventory(null, 54, "§5Accessory Bag §7(1/" + totalPages + ")");

        ItemStack pane = makeItem(Material.PURPLE_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                inv.setItem(slot, pane);
            }
        }

        for (int i = 0; i < accessories.size() && i < SLOTS.length; i++) {
            inv.setItem(SLOTS[i], makeItem(Material.GOLD_NUGGET, "§6" + accessories.get(i)));
        }

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
