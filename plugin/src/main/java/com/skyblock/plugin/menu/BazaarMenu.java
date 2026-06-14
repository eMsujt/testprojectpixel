package com.skyblock.plugin.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class BazaarMenu implements InventoryHolder, Listener {

    private final Inventory inventory = Bukkit.createInventory(this, 54, "§aBazaar");

    public BazaarMenu() {
        build(inventory);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void build(Inventory inventory) {
        // Border
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int column = slot % 9;
            if (slot < 9 || slot >= 45 || column == 0 || column == 8) {
                inventory.setItem(slot, pane);
            }
        }

        // Title item
        inventory.setItem(4, makeItem(Material.GOLD_INGOT, "§aBazaar"));

        // Category selector — Hypixel's Bazaar groupings
        inventory.setItem(19, makeItem(Material.WHEAT,          "§aFarming"));
        inventory.setItem(20, makeItem(Material.STONE_PICKAXE,  "§aMining"));
        inventory.setItem(21, makeItem(Material.IRON_SWORD,     "§aCombat"));
        inventory.setItem(22, makeItem(Material.OAK_SAPLING,    "§aWoods & Fishes"));
        inventory.setItem(23, makeItem(Material.QUARTZ,         "§aOdds & Ends"));
        inventory.setItem(24, makeItem(Material.MAP,            "§aSpecial Items"));
        inventory.setItem(25, makeItem(Material.ENCHANTED_BOOK, "§aEnchantments"));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof BazaarMenu) {
            event.setCancelled(true);
        }
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
