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

public final class AuctionHouseMenu implements InventoryHolder, Listener {

    private final Inventory inventory = Bukkit.createInventory(this, 54, "§6Auction House");

    public void open(Player player) {
        player.openInventory(build());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private Inventory build() {
        Inventory inv = inventory;

        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inv.setItem(slot, pane);
            }
        }

        // Top row — controls
        inv.setItem(1, makeItem(Material.NAME_TAG,           "§aSearch"));
        inv.setItem(3, makeItem(Material.GOLD_INGOT,         "§6Manage Auctions"));
        inv.setItem(4, makeItem(Material.GOLDEN_HORSE_ARMOR, "§eAuctions Browser"));
        inv.setItem(5, makeItem(Material.HOPPER,             "§eSort: §aHigh Bid"));
        inv.setItem(7, makeItem(Material.OAK_SIGN,           "§aView Bids"));

        // Category selectors
        inv.setItem(46, makeItem(Material.GOLDEN_SWORD,      "§cWeapons"));
        inv.setItem(47, makeItem(Material.GOLDEN_CHESTPLATE, "§bArmor"));
        inv.setItem(48, makeItem(Material.DIAMOND_PICKAXE,   "§eTools"));
        inv.setItem(49, makeItem(Material.ENCHANTED_BOOK,    "§dEnchanted Books"));
        inv.setItem(50, makeItem(Material.WHEAT,             "§aConsumables"));
        inv.setItem(51, makeItem(Material.DIRT,              "§6Blocks"));
        inv.setItem(52, makeItem(Material.BARRIER,           "§7Misc"));

        return inv;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof AuctionHouseMenu) {
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
