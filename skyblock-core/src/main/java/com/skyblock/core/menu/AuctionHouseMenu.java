package com.skyblock.core.menu;

import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.manager.AuctionHouseManager.AuctionCategory;
import com.skyblock.core.manager.AuctionHouseManager.AuctionListing;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The "Auctions Browser", laid out 1:1 with Hypixel: category tabs run down the
 * left column (Weapons 0, Armor 9, Accessories 18, Consumables 27, Blocks 36,
 * Misc 45 — Misc also shows Minions), active listings fill the inner grid
 * (10–43), and the bottom row holds paging plus the Search/Sort/Rarity/BIN
 * control bar.
 */
public final class AuctionHouseMenu extends AbstractSkyBlockMenu {

    static final int[] LISTING_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private static final int PAGE_SIZE = LISTING_SLOTS.length;

    /** Category tabs down the left column; the MISC tab also lists MINIONS auctions. */
    private static final AuctionCategory[] TAB_CATEGORIES = {
            AuctionCategory.WEAPONS, AuctionCategory.ARMOR, AuctionCategory.ACCESSORIES,
            AuctionCategory.CONSUMABLES, AuctionCategory.BLOCKS, AuctionCategory.MISC
    };
    private static final int[] CATEGORY_SLOTS = {0, 9, 18, 27, 36, 45};
    private static final Material[] CATEGORY_ICONS = {
            Material.DIAMOND_SWORD, Material.DIAMOND_CHESTPLATE, Material.NETHER_STAR,
            Material.COOKED_BEEF, Material.GRASS_BLOCK, Material.CHEST
    };

    private final int page;
    private final AuctionCategory category; // null = all categories

    public AuctionHouseMenu(Player player) {
        this(player, 0, null);
    }

    public AuctionHouseMenu(Player player, int page) {
        this(player, page, null);
    }

    public AuctionHouseMenu(Player player, int page, AuctionCategory category) {
        super(player, "§6Auctions Browser", 6);
        this.page = Math.max(0, page);
        this.category = category;
    }

    @Override
    protected void populate() {
        AuctionHouseManager manager = AuctionHouseManager.getInstance();

        // Category tabs (left column).
        for (int i = 0; i < TAB_CATEGORIES.length; i++) {
            final AuctionCategory cat = TAB_CATEGORIES[i];
            boolean selected = cat == category;
            setItem(CATEGORY_SLOTS[i], new ItemBuilder(CATEGORY_ICONS[i])
                    .displayName((selected ? "§a" : "§e") + cat.getDisplayName())
                    .lore(selected ? "§aShowing this category" : "§7Click to view!")
                    .build(),
                    e -> { e.setCancelled(true); new AuctionHouseMenu(player, 0, selected ? null : cat).open(player); });
        }

        // Resolve the listing set for the active filter.
        List<AuctionListing> listings = new ArrayList<>();
        if (category == null) {
            for (UUID id : manager.getActiveListings()) {
                AuctionListing l = manager.getListing(id);
                if (l != null) listings.add(l);
            }
        } else {
            listings.addAll(manager.getListingsByCategory(category));
            if (category == AuctionCategory.MISC) {
                listings.addAll(manager.getListingsByCategory(AuctionCategory.MINIONS));
            }
        }

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, listings.size());
        for (int i = start; i < end; i++) {
            AuctionListing listing = listings.get(i);
            final UUID id = listing.id();
            ItemStack icon = new ItemBuilder(listing.item())
                    .displayName("§e" + listing.itemName())
                    .lore(
                            "§7Starting Bid: §6" + (long) listing.startingBid() + " coins",
                            "§7Category: §f" + listing.category().getDisplayName(),
                            "§7Type: §f" + listing.type().getDisplayName(),
                            "§eClick to purchase!")
                    .build();
            setItem(LISTING_SLOTS[i - start], icon,
                    event -> {
                        event.setCancelled(true);
                        if (event.isLeftClick()) {
                            new AuctionConfirmMenu(player, id).open(player);
                        }
                    });
        }

        if (listings.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Auctions Available")
                    .lore("§7There are no active listings here right now.")
                    .build());
        }

        // Bottom control bar (Search/Sort/Rarity/BIN are display-only for now).
        setItem(48, new ItemBuilder(Material.OAK_SIGN)
                .displayName("§aSearch Auctions")
                .lore("§7Search for a specific item.").build());
        setItem(50, new ItemBuilder(Material.HOPPER)
                .displayName("§aSort")
                .lore("§7Change the listing order.").build());
        setItem(51, new ItemBuilder(Material.ENDER_EYE)
                .displayName("§aRarity Filter")
                .lore("§7Filter by item rarity.").build());
        setItem(52, new ItemBuilder(Material.POWERED_RAIL)
                .displayName("§aBIN Only")
                .lore("§7Show Buy-It-Now listings only.").build());

        if (page > 0) {
            setItem(46, new ItemBuilder(Material.ARROW)
                    .displayName("§ePrevious Page")
                    .lore("§7Page " + page)
                    .build(),
                    event -> { event.setCancelled(true); new AuctionHouseMenu(player, page - 1, category).open(player); });
        }
        if (end < listings.size()) {
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§eNext Page")
                    .lore("§7Page " + (page + 2))
                    .build(),
                    event -> { event.setCancelled(true); new AuctionHouseMenu(player, page + 1, category).open(player); });
        }
    }
}
