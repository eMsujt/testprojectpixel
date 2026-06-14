package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class SkyBlockMenu implements InventoryHolder {

    private final Inventory inventory;

    public SkyBlockMenu() {
        this.inventory = Bukkit.createInventory(this, 54, "§aSkyBlock Menu");
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
        Inventory inv = inventory;

        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§7");
        for (int i = 0; i < 9; i++)  inv.setItem(i,      pane);
        for (int i = 45; i < 54; i++) inv.setItem(i,      pane);
        for (int row = 1; row <= 4; row++) {
            inv.setItem(row * 9,     pane);
            inv.setItem(row * 9 + 8, pane);
        }

        // Row 1: skill / gameplay sub-menus
        inv.setItem(10, makeItem(Material.BOOK,              "§aSkills"));
        inv.setItem(11, makeItem(Material.PAINTING,          "§aCollections"));
        inv.setItem(12, makeItem(Material.CRAFTING_TABLE,    "§aCrafting"));
        inv.setItem(13, makeItem(Material.LEATHER_CHESTPLATE,"§aWardrobe"));
        inv.setItem(14, makeItem(Material.CHEST,             "§aStorage"));
        inv.setItem(15, makeItem(Material.LEATHER_BOOTS,     "§aAccessories"));
        inv.setItem(16, makeItem(Material.BONE,              "§aPets"));

        // Row 2: economy / social / travel sub-menus
        inv.setItem(19, makeItem(Material.PLAYER_HEAD,       "§aProfile"));
        inv.setItem(20, makeItem(Material.GOLD_INGOT,        "§aAuction House"));
        inv.setItem(21, makeItem(Material.EMERALD,           "§aBazaar"));
        inv.setItem(22, makeItem(Material.CLOCK,             "§aCalendar"));
        inv.setItem(23, makeItem(Material.WRITTEN_BOOK,      "§aQuests"));
        inv.setItem(24, makeItem(Material.ENDER_PEARL,       "§aCo-op"));
        inv.setItem(25, makeItem(Material.COMPASS,           "§aFast Travel"));
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
