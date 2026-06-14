package com.skyblock.plugin.economy;

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

import java.util.Arrays;
import java.util.List;

public final class BazaarMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    public BazaarMenu(Player player) {
        this.inventory = Bukkit.createInventory(this, 54, "§eBazaar");
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
        // Top row — one icon per Bazaar category (slots 0–4)
        inventory.setItem(0, makeCategoryItem(Material.WHEAT,       "§6Farming Ingredients", "farming ingredients"));
        inventory.setItem(1, makeCategoryItem(Material.COBBLESTONE, "§6Mining",              "mining"));
        inventory.setItem(2, makeCategoryItem(Material.IRON_SWORD,  "§6Combat",              "combat"));
        inventory.setItem(3, makeCategoryItem(Material.OAK_LOG,     "§6Woods and Fishes",    "woods and fishes"));
        inventory.setItem(4, makeCategoryItem(Material.REDSTONE,    "§6Odds and Ends",       "odds and ends"));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BazaarMenu)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;

        // Map each top-row category icon to its per-category listing.
        switch (event.getRawSlot()) {
            case 0 -> new BazaarCategoryMenu("Farming Ingredients", Arrays.asList(
                    "WHEAT", "CARROT", "POTATO", "PUMPKIN", "MELON", "SUGAR_CANE")).open(player);
            case 1 -> new BazaarCategoryMenu("Mining", Arrays.asList(
                    "COBBLESTONE", "COAL", "IRON_INGOT", "GOLD_INGOT", "DIAMOND", "EMERALD")).open(player);
            case 2 -> new BazaarCategoryMenu("Combat", Arrays.asList(
                    "ROTTEN_FLESH", "BONE", "STRING", "SPIDER_EYE", "GUNPOWDER", "ENDER_PEARL")).open(player);
            case 3 -> new BazaarCategoryMenu("Woods and Fishes", Arrays.asList(
                    "OAK_LOG", "BIRCH_LOG", "SPRUCE_LOG", "COD", "SALMON", "PUFFERFISH")).open(player);
            case 4 -> new BazaarCategoryMenu("Odds and Ends", Arrays.asList(
                    "REDSTONE", "FLINT", "FEATHER", "LEATHER", "SLIME_BALL")).open(player);
            default -> { /* empty slot — already cancelled */ }
        }
    }

    private ItemStack makeCategoryItem(Material material, String name, String label) {
        return makeItem(material, name, Arrays.asList(
                "§7Click to browse " + label + " items."
        ));
    }

    private ItemStack makeItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
