package com.skyblock.core.menu;

import com.skyblock.core.manager.AuctionHouseManager;
import com.skyblock.core.manager.AuctionHouseManager.AuctionListing;
import com.skyblock.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 6-row Auction House menu. Active {@link AuctionListing}s fill the 28
 * {@link #LISTING_SLOTS} in rows 2–5; the bottom row (45–53) holds navigation.
 */
public final class AuctionHouseMenu extends AbstractSkyBlockMenu {

    static final int[] LISTING_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private static final int PAGE_SIZE = LISTING_SLOTS.length;

    private final int page;

    public AuctionHouseMenu(Player player) {
        this(player, 0);
    }

    public AuctionHouseMenu(Player player, int page) {
        super(player, "§6Auction House", 6);
        this.page = Math.max(0, page);
    }

    @Override
    protected void populate() {
        AuctionHouseManager manager = AuctionHouseManager.getInstance();
        List<UUID> ids = new ArrayList<>(manager.getActiveListings());

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, ids.size());

        for (int i = start; i < end; i++) {
            UUID id = ids.get(i);
            AuctionListing listing = manager.getListing(id);
            if (listing == null) continue;
            ItemStack icon = new ItemBuilder(listing.item())
                    .displayName("§e" + listing.itemName())
                    .lore(
                            "§7Starting Bid: §6" + (long) listing.startingBid() + " coins",
                            "§7Category: §f" + listing.category().getDisplayName(),
                            "§7Type: §f" + listing.type().getDisplayName(),
                            "§eClick to purchase!")
                    .build();
            int slot = LISTING_SLOTS[i - start];
            setItem(slot, icon,
                    event -> {
                        event.setCancelled(true);
                        if (event.isLeftClick()) {
                            new AuctionConfirmMenu(player, id).open(player);
                        }
                    });
        }

        if (ids.isEmpty()) {
            setItem(22, new ItemBuilder(Material.BARRIER)
                    .displayName("§cNo Auctions Available")
                    .lore("§7There are no active listings right now.")
                    .build());
        }

        ItemStack pane = new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).displayName("§r").build();
        for (int slot = 45; slot < 54; slot++) {
            setItem(slot, pane);
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

        if (end < ids.size()) {
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
