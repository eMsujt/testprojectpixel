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

    public BazaarMenu() {
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(this, 54, "§6Bazaar");
        build(inventory);
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(this, 54, "§6Bazaar");
    }

    private void build(Inventory inventory) {
        // Title item
        inventory.setItem(4, makeItem(Material.GOLD_INGOT, "§6Bazaar"));

        // Category selector — Hypixel's Bazaar groupings
        inventory.setItem(10, makeItem(Material.GOLDEN_HOE,     "§aFarming"));
        inventory.setItem(11, makeItem(Material.STONE_PICKAXE,  "§aMining"));
        inventory.setItem(12, makeItem(Material.IRON_SWORD,     "§aCombat"));
        inventory.setItem(13, makeItem(Material.OAK_SAPLING,    "§aWoods & Fishes"));
        inventory.setItem(14, makeItem(Material.QUARTZ,         "§aOdds & Ends"));
        inventory.setItem(15, makeItem(Material.MAP,            "§aSpecial Items"));
        inventory.setItem(16, makeItem(Material.ENCHANTED_BOOK, "§aEnchantments"));
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
