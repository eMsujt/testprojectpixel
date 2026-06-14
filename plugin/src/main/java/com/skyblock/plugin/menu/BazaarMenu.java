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

    private static final class Category {
        final int slot;
        final Material material;
        final String displayName;

        Category(int slot, Material material, String displayName) {
            this.slot = slot;
            this.material = material;
            this.displayName = displayName;
        }
    }

    private static final Category[] CATEGORIES = {
        new Category(19, Material.WHEAT,          "§aFarming Supplies"),
        new Category(20, Material.STONE_PICKAXE,  "§aMining Supplies"),
        new Category(21, Material.IRON_SWORD,     "§aCombat Supplies"),
        new Category(22, Material.OAK_SAPLING,    "§aForaging Supplies"),
        new Category(23, Material.COD,            "§aFishing Supplies"),
        new Category(24, Material.ENCHANTED_BOOK, "§aEnchanting"),
        new Category(25, Material.NETHER_STAR,    "§aMiscellaneous"),
    };

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
        for (Category cat : CATEGORIES) {
            inventory.setItem(cat.slot, makeItem(cat.material, cat.displayName));
        }
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
