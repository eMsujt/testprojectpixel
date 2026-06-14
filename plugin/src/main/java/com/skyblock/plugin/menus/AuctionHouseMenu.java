package com.skyblock.plugin.menus;

import com.skyblock.plugin.auction.AuctionManager;
import com.skyblock.plugin.gui.ItemBuilder;
import com.skyblock.plugin.gui.Menu;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The Auction House menu.
 *
 * <p>A 54-slot (6-row) menu titled {@code §6Auction House}. Slots 0-8 hold the
 * category filter icons in Hypixel's category order; slots 9-44 display the
 * active auction listings one icon each, paged in document order; the bottom row
 * (45-53) is a {@code GRAY_STAINED_GLASS_PANE} footer. Clicking a listing tells
 * the player its current price.</p>
 */
public class AuctionHouseMenu extends Menu {

    /** First slot available for auction listings (slots 10-43). */
    private static final int FIRST_LISTING_SLOT = 10;

    /** Number of listing slots available between the category bar and footer. */
    private static final int LISTING_SLOTS = 34;

    /** An Auction House category filter: its icon, display name, and slot. */
    private enum Category {
        WEAPONS(0, Material.DIAMOND_SWORD, "§aWeapons", "§7Swords, bows and more."),
        ARMOR(1, Material.DIAMOND_CHESTPLATE, "§aArmor", "§7Helmets, chestplates and boots."),
        ACCESSORIES(2, Material.NETHER_STAR, "§aAccessories", "§7Talismans and rings."),
        CONSUMABLES(3, Material.POTION, "§aConsumables", "§7Potions and food."),
        BLOCKS(4, Material.STONE, "§aBlocks", "§7Building and resource blocks."),
        TOOLS(5, Material.DIAMOND_PICKAXE, "§aTools", "§7Pickaxes, axes and hoes."),
        ENCHANTED(6, Material.ENCHANTED_BOOK, "§aEnchanted Books", "§7Enchantments for your gear."),
        PETS(7, Material.BONE, "§aPets", "§7Companions to fight beside you."),
        MISC(8, Material.PAPER, "§aMisc", "§7Everything else.");

        private final int slot;
        private final Material icon;
        private final String displayName;
        private final String lore;

        Category(int slot, Material icon, String displayName, String lore) {
            this.slot = slot;
            this.icon = icon;
            this.displayName = displayName;
            this.lore = lore;
        }
    }

    public AuctionHouseMenu() {
        super("§6Auction House", 6);
    }

    @Override
    protected void build() {
        fillCategories();
        fillBorder();
        fillFooter();

        List<AuctionManager.AuctionListing> listings =
                new ArrayList<>(AuctionManager.getInstance().getListings());
        for (int i = 0; i < listings.size() && i < LISTING_SLOTS; i++) {
            AuctionManager.AuctionListing listing = listings.get(i);
            setItem(FIRST_LISTING_SLOT + i, new ItemBuilder(Material.PAPER)
                            .displayName("§a" + listing.itemName())
                            .lore(
                                    "§7Price: §6" + listing.price() + " coins",
                                    "§7Click to view")
                            .build(),
                    event -> event.getWhoClicked().sendMessage(
                            "§a" + listing.itemName() + " §7is going for §6"
                                    + listing.price() + " coins§7."));
        }
    }

    /** Fills slots 9 and 44 with gray glass panes to frame the listing area. */
    private void fillBorder() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        setItem(9, pane);
        setItem(44, pane);
    }

    /** Places the category filter icons across the menu's top row. */
    private void fillCategories() {
        for (Category category : Category.values()) {
            setItem(category.slot, new ItemBuilder(category.icon)
                    .displayName(category.displayName)
                    .lore(category.lore)
                    .build());
        }
    }

    /** Fills the menu's bottom row with gray glass panes, matching Hypixel. */
    private void fillFooter() {
        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .displayName("§r")
                .build();
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }
    }
}
