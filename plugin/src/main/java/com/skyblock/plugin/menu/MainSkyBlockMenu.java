package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class MainSkyBlockMenu implements InventoryHolder {

    private final Inventory inventory;

    public MainSkyBlockMenu() {
        this.inventory = Bukkit.createInventory(this, 54, "§eSkyBlock Menu");
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
        ItemStack pane = makeItem(Material.BLACK_STAINED_GLASS_PANE, "§7");
        for (int i = 0; i < 9; i++)   inventory.setItem(i,  pane);
        for (int i = 45; i < 54; i++) inventory.setItem(i,  pane);
        for (int row = 1; row <= 4; row++) {
            inventory.setItem(row * 9,     pane);
            inventory.setItem(row * 9 + 8, pane);
        }

        // Row 1: skill / gameplay sub-menus
        inventory.setItem(10, makeItem(Material.DIAMOND_SWORD,      "§aSkills"));
        inventory.setItem(11, makeItem(Material.PAINTING,          "§aCollection"));
        inventory.setItem(12, makeItem(Material.CRAFTING_TABLE,    "§aRecipe Book"));
        inventory.setItem(13, makeItem(Material.LEATHER_CHESTPLATE,"§aWardrobe"));
        inventory.setItem(14, makeItem(Material.ENDER_CHEST,       "§aStorage"));
        inventory.setItem(15, makeItem(Material.LEATHER_BOOTS,     "§aAccessory Bag"));
        inventory.setItem(16, makeItem(Material.BONE,              "§aPets"));

        // Row 2: economy / social / travel sub-menus
        inventory.setItem(19, makeItem(Material.PLAYER_HEAD,       "§aYour SkyBlock Profile"));
        inventory.setItem(20, makeItem(Material.GOLD_INGOT,        "§aAuction House"));
        inventory.setItem(21, makeItem(Material.EMERALD,           "§aBazaar"));
        inventory.setItem(22, makeItem(Material.CLOCK,             "§aCalendar and Events"));
        inventory.setItem(23, makeItem(Material.WRITTEN_BOOK,      "§aQuest Log"));
        inventory.setItem(24, makeItem(Material.ENDER_PEARL,       "§aPersonal Bank"));
        inventory.setItem(25, makeItem(Material.COMPASS,           "§aFast Travel"));

        // Centre: settings
        inventory.setItem(49, makeItem(Material.REDSTONE,          "§aSettings"));
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
