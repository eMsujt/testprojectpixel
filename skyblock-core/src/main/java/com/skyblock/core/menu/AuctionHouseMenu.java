package com.skyblock.core.menu;

import com.skyblock.core.auction.manager.AuctionHouseManager;
import com.skyblock.core.auction.manager.AuctionHouseManager.AuctionListing;
import com.skyblock.core.util.SkyblockUtils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Canonical 54-slot Auction House menu. Category filter icons occupy the top
 * row (slots 0–8); gray-pane separators sit at slots 9 and 44; 28 listing
 * slots fill the middle four rows (10–43); a gray-pane footer spans the bottom
 * row (45–53). An empty-state barrier appears at slot 22 when no listings are
 * active.
 */
public final class AuctionHouseMenu extends Menu {

    static final int[] LISTING_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private enum CategoryFilter {
        WEAPONS(0,    Material.DIAMOND_SWORD,       "§aWeapons",         "§7Swords, bows and more."),
        ARMOR(1,      Material.DIAMOND_CHESTPLATE,  "§aArmor",           "§7Helmets, chestplates and boots."),
        ACCESSORIES(2, Material.NETHER_STAR,        "§aAccessories",     "§7Talismans and rings."),
        CONSUMABLES(3, Material.POTION,             "§aConsumables",     "§7Potions and food."),
        BLOCKS(4,     Material.STONE,               "§aBlocks",          "§7Building and resource blocks."),
        TOOLS(5,      Material.DIAMOND_PICKAXE,     "§aTools",           "§7Pickaxes, axes and hoes."),
        ENCHANTED(6,  Material.ENCHANTED_BOOK,      "§aEnchanted Books", "§7Enchantments for your gear."),
        PETS(7,       Material.BONE,                "§aPets",            "§7Companions to fight beside you."),
        MISC(8,       Material.PAPER,               "§aMisc",            "§7Everything else.");

        final int slot;
        final Material icon;
        final String displayName;
        final String lore;

        CategoryFilter(int slot, Material icon, String displayName, String lore) {
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
        for (CategoryFilter cat : CategoryFilter.values()) {
            setItem(cat.slot, new ItemBuilder(cat.icon)
                    .displayName(cat.displayName)
                    .lore(cat.lore)
                    .build());
        }

        ItemStack pane = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).displayName("§r").build();
        setItem(9, pane);
        setItem(44, pane);
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }

        AuctionHouseManager manager = AuctionHouseManager.getInstance();
        List<AuctionListing> listings = manager.getActiveListings().stream()
                .map(manager::getListing)
                .collect(Collectors.toList());

        for (int i = 0; i < listings.size() && i < LISTING_SLOTS.length; i++) {
            AuctionListing listing = listings.get(i);
            String name = itemDisplayName(listing.item());
            ItemStack icon = new ItemBuilder(listing.item())
                    .displayName("§e" + name)
                    .lore(
                            "§7" + listing.type().getDisplayName() + ": §6" + (long) listing.startingBid() + " coins",
                            "§7Category: §f" + listing.category().getDisplayName(),
                            "§eClick to purchase!")
                    .build();
            setItem(LISTING_SLOTS[i], icon,
                    event -> event.getWhoClicked().sendMessage(
                            "§e" + name + " §7is listed for §6" + (long) listing.startingBid() + " coins§7."));
        }

        if (listings.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Auctions Available")
                    .lore("§7There are no active listings right now.")
                    .build());
        }
    }

    private static String itemDisplayName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        String name = item.getType().name().replace('_', ' ');
        StringBuilder sb = new StringBuilder();
        for (String word : name.split(" ")) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) sb.append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
