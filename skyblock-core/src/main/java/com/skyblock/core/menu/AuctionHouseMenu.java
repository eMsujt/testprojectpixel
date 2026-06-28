package com.skyblock.core.menu;

import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.manager.AuctionHouseManager.AuctionCategory;
import com.skyblock.core.manager.AuctionHouseManager.AuctionListing;
import com.skyblock.core.manager.AuctionHouseManager.ItemRarity;
import com.skyblock.core.model.Rarity;
import com.skyblock.core.util.SignInput;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * The "Auctions Browser", laid out 1:1 with Hypixel: category tabs run down the
 * left column (Weapons 0, Armor 9, Accessories 18, Consumables 27, Blocks 36,
 * Misc 45 — Misc also shows Minions), active listings fill the inner grid
 * (10–43), and the bottom row holds paging plus the Search/Sort/Rarity/BIN
 * control bar. Sort and BIN-only are functional; Search and Rarity are display.
 */
public final class AuctionHouseMenu extends AbstractSkyBlockMenu {

    // Listings fill cols 3-8 (rows 2-5); col 2 (10/19/28/37) is a filler pane on Hypixel.
    static final int[] LISTING_SLOTS = {
            11, 12, 13, 14, 15, 16,
            20, 21, 22, 23, 24, 25,
            29, 30, 31, 32, 33, 34,
            38, 39, 40, 41, 42, 43
    };

    private static final int PAGE_SIZE = LISTING_SLOTS.length;

    /** Border panes framing the listing grid (top row + the columns either side). */
    private static final int[] BORDER_SLOTS = {
            1, 2, 3, 4, 5, 6, 7, 8,
            10, 19, 28, 37, 17, 26, 35, 44, 46, 47
    };

    /** Category tabs down the left column; the MISC tab also lists MINIONS auctions. */
    private static final AuctionCategory[] TAB_CATEGORIES = {
            AuctionCategory.WEAPONS, AuctionCategory.ARMOR, AuctionCategory.ACCESSORIES,
            AuctionCategory.CONSUMABLES, AuctionCategory.BLOCKS, AuctionCategory.MISC
    };
    private static final int[] CATEGORY_SLOTS = {0, 9, 18, 27, 36, 45};
    // Icons per the wiki: Weapons=Golden Sword, Armor=Diamond Chestplate, Accessories=Emerald,
    // Consumables=Apple, Blocks=Grass Block, Misc=Stick.
    private static final Material[] CATEGORY_ICONS = {
            Material.GOLDEN_SWORD, Material.DIAMOND_CHESTPLATE, Material.EMERALD,
            Material.APPLE, Material.GRASS_BLOCK, Material.STICK
    };

    /** Sort order for the listing grid. */
    private enum Sort {
        LOWEST("Lowest Price"),
        HIGHEST("Highest Price");

        final String label;

        Sort(String label) {
            this.label = label;
        }

        Sort next() {
            return this == LOWEST ? HIGHEST : LOWEST;
        }
    }

    private final int page;
    private final AuctionCategory category; // null = all categories
    private final Sort sort;
    private final boolean binOnly;
    private final String search;       // null/blank = no name filter
    private final ItemRarity rarity;   // null = any rarity

    public AuctionHouseMenu(Player player) {
        this(player, 0, null, Sort.LOWEST, false);
    }

    public AuctionHouseMenu(Player player, int page) {
        this(player, page, null, Sort.LOWEST, false);
    }

    public AuctionHouseMenu(Player player, int page, AuctionCategory category) {
        this(player, page, category, Sort.LOWEST, false);
    }

    private AuctionHouseMenu(Player player, int page, AuctionCategory category, Sort sort, boolean binOnly) {
        this(player, page, category, sort, binOnly, null, null);
    }

    private AuctionHouseMenu(Player player, int page, AuctionCategory category, Sort sort, boolean binOnly,
                            String search, ItemRarity rarity) {
        super(player, "§6Auctions Browser", 6);
        this.page = Math.max(0, page);
        this.category = category;
        this.sort = sort;
        this.binOnly = binOnly;
        this.search = search;
        this.rarity = rarity;
    }

    @Override
    protected void populate() {
        AuctionHouseManager manager = AuctionHouseManager.getInstance();

        // Border panes frame the listing grid.
        ItemStack border = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(" ").build();
        for (int slot : BORDER_SLOTS) {
            setItem(slot, border);
        }

        // Category tabs (left column).
        for (int i = 0; i < TAB_CATEGORIES.length; i++) {
            final AuctionCategory cat = TAB_CATEGORIES[i];
            boolean selected = cat == category;
            setItem(CATEGORY_SLOTS[i], new ItemBuilder(CATEGORY_ICONS[i])
                    .displayName((selected ? "§a" : "§e") + cat.getDisplayName())
                    .lore(selected ? "§aShowing this category" : "§7Click to view!")
                    .build(),
                    e -> { e.setCancelled(true); new AuctionHouseMenu(player, 0, selected ? null : cat, sort, binOnly, search, rarity).open(player); });
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

        // BIN-only filter, name search, rarity filter, then price sort.
        if (binOnly) {
            listings.removeIf(l -> !l.binListing());
        }
        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            listings.removeIf(l -> !l.itemName().toLowerCase().contains(q));
        }
        if (rarity != null) {
            listings.removeIf(l -> !Rarity.fromItem(l.item(), Rarity.COMMON).name().equals(rarity.name()));
        }
        listings.sort(Comparator.comparingDouble(AuctionListing::startingBid));
        if (sort == Sort.HIGHEST) {
            Collections.reverse(listings);
        }

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, listings.size());
        for (int i = start; i < end; i++) {
            AuctionListing listing = listings.get(i);
            final UUID id = listing.id();
            ItemStack icon = new ItemBuilder(listing.item())
                    .displayName("§e" + listing.itemName())
                    .lore(
                            "§7" + (listing.binListing() ? "Buy it now: " : "Starting bid: ") + "§6"
                                    + (long) listing.startingBid() + " coins",
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

        // Bottom control bar, 1:1 with the wiki Auctions Browser (row 6).
        setItem(48, new ItemBuilder(Material.OAK_SIGN)
                .displayName("§aSearch")
                .lore("§7Find items by name.",
                      "§7Filtered: " + (search == null || search.isBlank() ? "§7None" : "§e" + search),
                      "",
                      "§eClick to edit filter!",
                      "§7Right-click to clear!").build(),
                e -> {
                    e.setCancelled(true);
                    if (e.isRightClick()) {
                        new AuctionHouseMenu(player, 0, category, sort, binOnly, null, rarity).open(player);
                    } else {
                        openSearch();
                    }
                });
        setItem(50, new ItemBuilder(Material.HOPPER)
                .displayName("§aSort: §f" + sort.label)
                .lore("§7Click to change the order.").build(),
                e -> { e.setCancelled(true); new AuctionHouseMenu(player, 0, category, sort.next(), binOnly, search, rarity).open(player); });
        setItem(51, new ItemBuilder(Material.ENDER_EYE)
                .displayName("§aItem Tier")
                .lore("§7Currently: " + (rarity == null ? "§7Any" : "§e" + rarity.getDisplayName()),
                      "",
                      "§eClick to switch rarity!",
                      "§7Right-click to clear!").build(),
                e -> {
                    e.setCancelled(true);
                    ItemRarity next = e.isRightClick() ? null : nextRarity(rarity);
                    new AuctionHouseMenu(player, 0, category, sort, binOnly, search, next).open(player);
                });
        setItem(52, new ItemBuilder(Material.POWERED_RAIL)
                .displayName("§aBIN Filter: " + (binOnly ? "§aBIN Only" : "§fShow All"))
                .lore("§7Show Buy-It-Now listings only.").build(),
                e -> { e.setCancelled(true); new AuctionHouseMenu(player, 0, category, sort, !binOnly, search, rarity).open(player); });

        // Go Back to the Auction House hub (slot 6,5 = 49; the browser has no Close).
        setItem(49, new ItemBuilder(Material.ARROW)
                .displayName("§aGo Back")
                .lore("§7To Auction House").build(),
                e -> { e.setCancelled(true); new AuctionHubMenu(player).open(player); });

        // Pages: Next arrow at slot 6,9 = 53. Left-click forward, right-click back.
        boolean hasNext = end < listings.size();
        boolean hasPrev = page > 0;
        if (hasNext || hasPrev) {
            setItem(53, new ItemBuilder(Material.ARROW)
                    .displayName("§aNext Page")
                    .lore("§7Page " + (page + 1),
                          "",
                          hasNext ? "§eClick to turn page!" : "§8No next page",
                          hasPrev ? "§eRight-click to go back!" : "§8No previous page")
                    .build(),
                    event -> {
                        event.setCancelled(true);
                        if (event.isRightClick() && hasPrev) {
                            new AuctionHouseMenu(player, page - 1, category, sort, binOnly, search, rarity).open(player);
                        } else if (event.isLeftClick() && hasNext) {
                            new AuctionHouseMenu(player, page + 1, category, sort, binOnly, search, rarity).open(player);
                        }
                    });
        }
    }

    /** Prompts for a search term on a sign and reopens the browser filtered to it. */
    private void openSearch() {
        SignInput.request(player, "§8Search auctions", query -> {
            String term = query == null || query.isBlank() ? null : query;
            new AuctionHouseMenu(player, 0, category, sort, binOnly, term, rarity).open(player);
        });
    }

    /** Cycles to the next rarity tier, wrapping past the last back to "any" (null). */
    private static ItemRarity nextRarity(ItemRarity current) {
        ItemRarity[] values = ItemRarity.values();
        if (current == null) {
            return values[0];
        }
        int next = current.ordinal() + 1;
        return next >= values.length ? null : values[next];
    }
}
