package com.skyblock.plugin.menu;

import com.skyblock.plugin.auction.AuctionManager;
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

    /** Inner content slots (between the border) used to display active listings. */
    private static final int[] LISTING_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43};

    private final Inventory inventory;

    public AuctionHouseMenu() {
        this.inventory = Bukkit.createInventory(this, 54, "§6Auction House");
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
        ItemStack pane = makeItem(Material.GRAY_STAINED_GLASS_PANE, "§r");
        for (int slot = 0; slot < 54; slot++) {
            int col = slot % 9;
            if (slot < 9 || slot >= 45 || col == 0 || col == 8) {
                inventory.setItem(slot, pane);
            }
        }

        // Top row — controls
        inventory.setItem(1, makeItem(Material.NAME_TAG,           "§aSearch"));
        inventory.setItem(3, makeItem(Material.GOLD_INGOT,         "§6Manage Auctions"));
        inventory.setItem(4, makeItem(Material.GOLDEN_HORSE_ARMOR, "§eAuctions Browser"));
        inventory.setItem(5, makeItem(Material.HOPPER,             "§eSort: §aHigh Bid"));
        inventory.setItem(7, makeItem(Material.OAK_SIGN,           "§aView Bids"));

        // Category selectors
        inventory.setItem(46, makeItem(Material.GOLDEN_SWORD,      "§cWeapons"));
        inventory.setItem(47, makeItem(Material.GOLDEN_CHESTPLATE, "§bArmor"));
        inventory.setItem(48, makeItem(Material.DIAMOND_PICKAXE,   "§eTools"));
        inventory.setItem(49, makeItem(Material.ENCHANTED_BOOK,    "§dEnchanted Books"));
        inventory.setItem(50, makeItem(Material.WHEAT,             "§aConsumables"));
        inventory.setItem(51, makeItem(Material.DIRT,              "§6Blocks"));
        inventory.setItem(52, makeItem(Material.BARRIER,           "§7Misc"));

        // Active listings
        var listings = AuctionManager.getInstance().getListings();
        for (int i = 0; i < listings.size() && i < LISTING_SLOTS.length; i++) {
            AuctionManager.AuctionEntry listing = listings.get(i);
            inventory.setItem(LISTING_SLOTS[i], makeItem(Material.PAPER,
                    "§a" + listing.itemName(),
                    "§7Price: §6" + listing.price() + " coins",
                    "§7Click to view"));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof AuctionHouseMenu) {
            event.setCancelled(true);
        }
    }

    private ItemStack makeItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(java.util.Arrays.asList(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
