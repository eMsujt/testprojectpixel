package com.skyblock.core.menu;

import com.skyblock.core.manager.AuctionManager;
import com.skyblock.core.manager.AuctionManager.Listing;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Canonical 54-slot Auction House menu. Category filter icons occupy the top
 * row (slots 0–8); yellow-pane separators sit at slots 9 and 44; 28 listing
 * slots fill the middle four rows (10–43); a yellow-pane footer spans the
 * bottom row (45–53) with a prev-page button at slot 45 and next-page at 53.
 */
public final class AuctionHouseMenu extends AbstractSkyBlockMenu {

    static final int[] LISTING_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private static final int PAGE_SIZE = LISTING_SLOTS.length;

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

    private final int page;

    public AuctionHouseMenu(Player player) {
        this(player, 0);
    }

    public AuctionHouseMenu(Player player, int page) {
        super(player, "§6§lAuction House", 54);
        this.page = Math.max(0, page);
    }

    @Override
    protected void populate() {
        for (CategoryFilter cat : CategoryFilter.values()) {
            setItem(cat.slot, new ItemBuilder(cat.icon)
                    .displayName(cat.displayName)
                    .lore(cat.lore)
                    .build());
        }

        ItemStack pane = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).displayName("§r").build();
        setItem(9, pane);
        setItem(44, pane);
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
        }

        List<Listing> listings = AuctionManager.getInstance().getListings();

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, listings.size());

        for (int i = start; i < end; i++) {
            Listing listing = listings.get(i);
            ItemStack icon = new ItemBuilder(listing.item())
                    .displayName("§e" + listing.itemName())
                    .lore(
                            "§7BIN: §6" + (long) listing.price() + " coins",
                            "§7Category: §f" + listing.category(),
                            "§eClick to purchase!")
                    .build();
            setItem(LISTING_SLOTS[i - start], icon,
                    event -> event.getWhoClicked().sendMessage(
                            "§e" + listing.itemName() + " §7is listed for §6" + (long) listing.price() + " coins§7."));
        }

        if (listings.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Auctions Available")
                    .lore("§7There are no active listings right now.")
                    .build());
        }

        if (page > 0) {
            setItem(45, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Page " + page)
                    .build(),
                    event -> {
                        event.setCancelled(true);
                        new AuctionHouseMenu(player, page - 1).open(player);
                    });
        }

        if (end < listings.size()) {
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Page " + (page + 2))
                    .build(),
                    event -> {
                        event.setCancelled(true);
                        new AuctionHouseMenu(player, page + 1).open(player);
                    });
        }
    }
}
